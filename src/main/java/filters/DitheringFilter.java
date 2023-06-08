package filters;

import io.qt.gui.QColor;
import io.qt.gui.QImage;
import io.qt.gui.QPainter;
import io.qt.gui.QPixmap;

import java.awt.*;

public class DitheringFilter implements Filter {
    public static QPixmap apply(QPixmap pixmap){
        QImage image = pixmap.toImage();
        QPainter painter = new QPainter(pixmap);
        int width = pixmap.width();
        int height = pixmap.height();

        Color[] palette = new Color[] {
                new Color(  0,   0,   0), // black
                new Color(  0,   0, 255), // green
                new Color(  0, 255,   0), // blue
                new Color(  0, 255, 255), // cyan
                new Color(255,   0,   0), // red
                new Color(255,   0, 255), // purple
                new Color(255, 255,   0), // yellow
                new Color(255, 255, 255)  // white
        };

        // Creating new pixel array which represents more or less the whole picture
        Color[][] dither = new Color[width][height];

        // Fill the 2-dimensional array with the pixel color information of the original image
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                dither[x][y] = new Color(image.pixelColor(x, y).rgb());
            }
        }

        // Apply dither effect
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {

                Color oldColor = dither[x][y];
                Color newColor = findClosestPaletteColor(oldColor, palette);

                painter.setPen(newColor.toQColor());
                painter.drawPoint(x, y);

                Color err = oldColor.sub(newColor);


                // Add error pixels
                if (x + 1 < width) {
                    dither[x + 1][y] = dither[x + 1][y].add(err.mul(7. / 16));
                }

                if (x - 1 >= 0 && y + 1 < height) {
                    dither[x - 1][y + 1] = dither[x - 1][y + 1].add(err.mul(3. / 16));
                }

                if (y + 1 < height) {
                    dither[x][y + 1] = dither[x][y + 1].add(err.mul(5. / 16));
                }

                if (x + 1 < width && y + 1 < height) {
                    dither[x + 1][y + 1] = dither[x + 1][y + 1].add(err.mul(1. / 16));
                }
            }
        }


        painter.end();
        return pixmap;
    }

    private static Color findClosestPaletteColor(Color c, Color[] palette) {
        Color closest = palette[0];

        for (Color n : palette) {
            if (n.diff(c) < closest.diff(c)) {
                closest = n;
            }
        }

        return closest;
    }

    private static class Color extends QColor {
        int r, g, b;

        public Color(int c) {
            QColor color = new QColor(c);
            r = color.red();
            g = color.green();
            b = color.blue();
        }

        public Color(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public Color add(Color o) {
            return new Color(r + o.r, g + o.g, b + o.b);
        }

        public int clamp(int c) {
            return Math.max(0, Math.min(255, c));
        }

        public int diff(Color o) {
            int Rdiff = o.r - r;
            int Gdiff = o.g - g;
            int Bdiff = o.b - b;
            return Rdiff * Rdiff + Gdiff * Gdiff + Bdiff * Bdiff;
        }

        public Color mul(double d) {
            return new Color((int) (d * r), (int) (d * g), (int) (d * b));
        }

        public Color sub(Color o) {
            return new Color(r - o.r, g - o.g, b - o.b);
        }

        public Color toColor() {
            return new Color(clamp(r), clamp(g), clamp(b));
        }

        public QColor toQColor() {
            return new QColor(clamp(r), clamp(g), clamp(b));
        }

        public int toRGB() {
            return toColor().rgb();
        }
    }
}
