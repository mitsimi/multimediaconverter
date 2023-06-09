package filters;

import io.qt.core.Qt;
import io.qt.gui.*;

public class TurnFilter {
    public static QPixmap turnPixmap(QPixmap pixmap) {
        QTransform transform = new QTransform();
        transform.rotate(90);
        pixmap = pixmap.transformed(transform, Qt.TransformationMode.SmoothTransformation);
        return pixmap;
    }
}
