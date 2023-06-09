package filters;

import io.qt.gui.QColor;
import io.qt.gui.QImage;
import io.qt.gui.QPainter;
import io.qt.gui.QPixmap;

public class InvertFilter implements Filter{
    public static QPixmap apply(QPixmap pixmap){
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

                //Inverts the red, blue and green color of a pixel
                QColor newColor = new QColor(255 - red, 255 - green, 255 - blue);

                painter.setPen(newColor);
                painter.drawPoint(x, y);
            }

        }
        painter.end();
        return pixmap;
    }
}
