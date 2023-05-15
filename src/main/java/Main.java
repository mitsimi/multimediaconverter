import io.qt.widgets.QApplication;
import io.qt.widgets.QMessageBox;

public class Main {
    public static void main(String[] args) {
        QApplication.initialize(args);
        QMessageBox.information(null, "QtJambi", "Hello World!");
        QApplication.shutdown();
    }

}
