package filters;

import io.qt.core.Qt;
import io.qt.gui.QPixmap;
import io.qt.gui.QTransform;

public class TurnFilter implements Filter{
    public static QPixmap apply(QPixmap pixmap) {
        QTransform transform = new QTransform();
        //Rotate picture 90 degrees
        transform.rotate(90);
        pixmap = pixmap.transformed(transform, Qt.TransformationMode.SmoothTransformation);
        return pixmap;
    }
}
