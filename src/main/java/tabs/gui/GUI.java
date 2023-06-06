package tabs.gui;

import io.qt.widgets.QApplication;


public class GUI {

    public GUI(String[] args) {
        QApplication.initialize(args);

        Frame frameThread = new Frame();
        frameThread.start();

        QApplication.exec();
    }
}
