package filters;

import io.qt.gui.QColor;
import io.qt.gui.QImage;
import io.qt.gui.QPainter;
import io.qt.gui.QPixmap;

public class Bit8Filter {
    public static QPixmap colorizePixmap(QPixmap pixmap){
        QImage image = pixmap.toImage();
        QPainter painter = new QPainter(pixmap);
        int width = pixmap.width();
        int height = pixmap.height();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int bitColor = EightBit.fromColor(new QColor(image.pixel(x, y)));

                painter.setPen(EightBit.toColor(bitColor));
                painter.drawPoint(x, y);
            }

        }

        painter.end();
        return pixmap;
    }

    abstract class EightBit {
        public static int fromColor(QColor c) {
            return ((c.alpha() >> 6) << 6)
                    + ((c.red()   >> 6) << 4)
                    + ((c.green() >> 6) << 2)
                    +  (c.blue()  >> 6);
        }
        public static QColor toColor(int i) {
            return new QColor(((i >> 4) % 4) * 64,
                    ((i >> 2) % 4) * 64,
                    (i       % 4) * 64,
                    (i >> 6)      * 64);
        }
    }
}
