package filters;

import io.qt.gui.QPixmap;

public interface Filter {
    public static QPixmap apply(QPixmap pixmap) {
        return pixmap;
    };
}
