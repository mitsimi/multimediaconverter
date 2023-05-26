package filters;

import io.qt.gui.QColor;
import io.qt.gui.QImage;
import io.qt.gui.QPainter;
import io.qt.gui.QPixmap;

public class InvertFilter {
    public static QPixmap invertPixmap(QPixmap pixmap){
        QImage image = pixmap.toImage();
        QPainter painter = new QPainter(pixmap);
        int width = pixmap.width();
        int height = pixmap.height();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                QColor color = new QColor(image.pixel(x, y));
                int red = color.red();
                int green = color.green();
                int blue = color.blue();

                QColor newColor = new QColor(255 - green, 255 - blue, 255 - red);

                // Zeichnen Sie den Pixel mit den neuen Farbwerten
                painter.setPen(newColor);
                painter.drawPoint(x, y);
            }

        }

        painter.end();
        return pixmap;
    }
}
