package filters;

import io.qt.gui.QColor;
import io.qt.gui.QImage;
import io.qt.gui.QPainter;
import io.qt.gui.QPixmap;

public class BrightnessFilter{
    public static QPixmap apply(QPixmap pixmap, int updateBrightness){
        QImage image = pixmap.toImage();
        QPainter painter = new QPainter(pixmap);
        int width = pixmap.width();
        int height = pixmap.height();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                QColor color = new QColor(image.pixel(x, y));
                int bright = 0;
                QColor newColor = null;
                //picture gets brighter
                if(updateBrightness >= 0)
                {
                    bright = Math.min(color.value()+updateBrightness,255);
                    newColor = color.lighter(bright);

                }
                //picture gets darker
                else if (updateBrightness < 0){
                    bright = Math.max(color.value()-updateBrightness,0);
                    newColor = color.darker(bright);

                }

                // Draws Pixel with new color values
                painter.setPen(newColor);
                painter.drawPoint(x, y);
            }

        }
        painter.end();
        return pixmap;
    }
}
