package gui;

import io.qt.widgets.QApplication;
import io.qt.widgets.QMessageBox;


public class GUI {

    public GUI(String[] args) {
        QApplication.initialize(args);
        QMessageBox.information(null, "QtJambi", "Hello World!");
        QApplication.shutdown();
    }
}
