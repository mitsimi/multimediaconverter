package tabs.gui;

import converter.AudioConverter;
import converter.Converter;
import converter.ImageConverter;
import converter.VideoConverter;
import io.qt.core.*;
import io.qt.gui.*;
import io.qt.widgets.*;
import tabs.ConvertTab;
import tabs.EditAudioTab;
import tabs.EditPictureTab;
import tabs.EditVideoTab;
import types.*;

import java.io.IOException;
import java.nio.file.Path;

public class Frame extends QThread {
    private QFrame frame;

    public void run() {
        QMetaObject.invokeMethod(this, "createFrame", Qt.ConnectionType.QueuedConnection);
    }

    private void createFrame() {

        // Frame erstellen
        frame = new QFrame();
        frame.setFrameStyle(QFrame.Shape.Box.value());
        frame.setLineWidth(0);
        QVBoxLayout frameLayout = new QVBoxLayout(frame);

        QTabWidget tabWidget = new QTabWidget();

        // Tabs erzeugen
        ConvertTab convertTab = new ConvertTab();
        QWidget tab0 = convertTab.createTabWidget();
        tabWidget.addTab(tab0, "Convert");

        EditPictureTab pictureTab = new EditPictureTab();
        QWidget tab1 = pictureTab.createTabWidget();
        tabWidget.addTab(tab1, "Edit: Picture");

        EditAudioTab audioTab = new EditAudioTab();
        QWidget tab2 = audioTab.createTabWidget();
        tabWidget.addTab(tab2, "Edit: Audio");

        EditVideoTab videoTab = new EditVideoTab();
        QWidget tab3 = videoTab.createTabWidget();
        tabWidget.addTab(tab3, "Edit: Video");

        // Tabs hinzufügen
        frameLayout.addWidget(tabWidget);

        // Frame anzeigen
        frame.show();
    }
}
