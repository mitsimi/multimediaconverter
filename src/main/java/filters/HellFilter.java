package filters;

import io.qt.gui.QColor;
import io.qt.gui.QImage;
import io.qt.gui.QPainter;
import io.qt.gui.QPixmap;

import static io.qt.core.QtGlobal.qBound;

public class HellFilter {
    public static QPixmap hellPixmap(QPixmap pixmap, int updateBrightness){
        QImage image = pixmap.toImage();
        QPainter painter = new QPainter(pixmap);
        int width = pixmap.width();
        int height = pixmap.height();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                QColor color = new QColor(image.pixel(x, y));
                int bright = 0;
                QColor newColor = null;
                if(updateBrightness >= 0)
                {
                    bright = Math.min(color.value()+updateBrightness,255);
                    newColor = color.lighter(bright);

                }
                else if (updateBrightness < 0){
                    bright = Math.max(color.value()-updateBrightness,0);
                    newColor = color.darker(bright);

                }



                // Zeichnen Sie den Pixel mit den neuen Farbwerten
                painter.setPen(newColor);
                painter.drawPoint(x, y);
            }

        }
        painter.end();
        return pixmap;
    }
}
