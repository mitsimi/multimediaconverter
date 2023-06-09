package filters;

import io.qt.core.Qt;
import io.qt.gui.QPixmap;
import io.qt.gui.QTransform;

public class MirrorFilter implements Filter{
    public static QPixmap apply(QPixmap pixmap) {
        QTransform transform = new QTransform();
        //Mirrors Picture vertically
        transform.scale(-1,1);
        pixmap = pixmap.transformed(transform, Qt.TransformationMode.SmoothTransformation);
        return pixmap;
    }
}
