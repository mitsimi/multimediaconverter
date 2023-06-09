package filters;

import io.qt.core.Qt;
import io.qt.gui.QPixmap;
import io.qt.gui.QTransform;

public class SpiegelFilter {
    public static QPixmap spiegelPixmap(QPixmap pixmap) {
        QTransform transform = new QTransform();
        transform.scale(-1,1);
        pixmap = pixmap.transformed(transform, Qt.TransformationMode.SmoothTransformation);
        return pixmap;
    }
}
