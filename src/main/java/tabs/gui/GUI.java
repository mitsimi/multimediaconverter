package tabs.gui;

import io.qt.widgets.QApplication;


public class GUI {

    public GUI(String[] args) {
        QApplication.initialize(args);
        //Create frame and start
        Frame frameThread = new Frame();
        frameThread.start();

        QApplication.exec();
    }
}
