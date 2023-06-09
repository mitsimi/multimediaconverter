package filters;

import io.qt.gui.*;

public class WatermarkFilter implements Filter{
    public static QPixmap apply(QPixmap pixmap) {
        QImage image = pixmap.toImage();

        String text = "Watermark";
        QFont font  = new QFont("Arial", 50);
        QFontMetrics fontMetrics = new QFontMetrics(font);
        int textwidth = fontMetrics.horizontalAdvance(text);


        // Pixmap erstellen

        QPainter textPainter = new QPainter(image);
        textPainter.setFont(new QFont("Arial", 60));
        textPainter.setPen(QColor.fromRgb(255, 0, 0, 160));
        textPainter.drawText(0,(pixmap.height()+fontMetrics.height())/2,text);
        textPainter.end();
        return QPixmap.fromImage(image);
    }
}
