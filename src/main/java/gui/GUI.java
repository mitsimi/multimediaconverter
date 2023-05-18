package gui;

import io.qt.core.QThread;
import io.qt.widgets.QApplication;
import io.qt.widgets.QMessageBox;


public class GUI {

    public GUI(String[] args) {
        QApplication.initialize(args);

        Frame frameThread = new Frame();
        frameThread.start();

        QApplication.exec();
    }
}
