package filters;

import io.qt.gui.QColor;
import io.qt.gui.QImage;
import io.qt.gui.QPixmap;

import static io.qt.core.QtGlobal.qBound;

public class SharpenFilter implements Filter {
    public static QPixmap apply(QPixmap pixmap){
        QImage image = pixmap.toImage();
        QImage sharpenedImage = pixmap.toImage();

        int width = pixmap.width();
        int height = pixmap.height();
        //kernel, which travels over the whole picture
        int[][] kernel = {
                {0, -1, 0},
                {-1, 5, -1},
                {0, -1, 0}
        };

        for (int x = 1; x < width - 1; x++) {
            for (int y = 1; y < height - 1; y++) {
                int newRed = 0;
                int newGreen = 0;
                int newBlue = 0;

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        QColor color = new QColor(image.pixel(x + i, y + j));

                        int kernelValue = kernel[i + 1][j + 1];

                        newRed += color.red() * kernelValue;
                        newGreen += color.green() * kernelValue;
                        newBlue += color.blue() * kernelValue;
                    }
                }

                newRed = qBound(0, newRed, 255);
                newGreen = qBound(0, newGreen, 255);
                newBlue = qBound(0, newBlue, 255);

                QColor newColor = new QColor(newRed, newGreen, newBlue);
                sharpenedImage.setPixel(x, y, newColor.rgb());
            }

        }
        return QPixmap.fromImage(sharpenedImage);
    }
}
