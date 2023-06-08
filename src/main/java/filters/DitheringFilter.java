package filters;

import io.qt.gui.QColor;
import io.qt.gui.QImage;
import io.qt.gui.QPainter;
import io.qt.gui.QPixmap;

import java.awt.*;

// Floyd Steinberg Dithering
public class DitheringFilter {
    public static QPixmap ditherPixmap(QPixmap pixmap){
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

        Color[][] d = new Color[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                d[y][x] = new Color(image.pixelColor(x, y).rgb());
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Color oldColor = d[y][x];
                Color newColor = findClosestPaletteColor(oldColor, palette);

                painter.setPen(newColor.toQColor());
                painter.drawPoint(x, y);

                Color err = oldColor.sub(newColor);

                if (x + 1 < width) {
                    d[y][x + 1] = d[y][x + 1].add(err.mul(7. / 16));
                }

                if (x - 1 >= 0 && y + 1 < height) {
                    d[y + 1][x - 1] = d[y + 1][x - 1].add(err.mul(3. / 16));
                }

                if (y + 1 < height) {
                    d[y + 1][x] = d[y + 1][x].add(err.mul(5. / 16));
                }

                if (x + 1 < width && y + 1 < height) {
                    d[y + 1][x + 1] = d[y + 1][x + 1].add(err.mul(1. / 16));
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
