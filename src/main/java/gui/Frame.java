package gui;

import io.qt.core.*;
import io.qt.widgets.*;
import picture.PictureConverter;
import types.AudioType;
import types.ImageType;
import types.VideoType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Frame extends QThread implements PictureConverter {
    public String path;
    QLineEdit textField1;
    QComboBox dropdownMenu;
    String selectedOption;
    public void run() {

        QMetaObject.invokeMethod(this, "createFrame", Qt.ConnectionType.QueuedConnection);
    }
    private void createFrame() {

        // Frame erstellen
        QFrame frame = new QFrame();
        frame.setFrameStyle(QFrame.Shape.Box.value());
        frame.setLineWidth(2);
        QVBoxLayout frameLayout = new QVBoxLayout(frame);

        QLabel label1 = new QLabel("Rename", frame);
        textField1 = new QLineEdit(frame);
        QLabel label3 = new QLabel("Link", frame);
        QLineEdit textField2 = new QLineEdit(frame);
        QLabel label2 = new QLabel("Convert to", frame);


        //Button für Upload und Speicherung erstellen
        QPushButton fileUpload = new QPushButton("Upload File", frame);
        fileUpload.clicked.connect(this, "openFile()");
        QPushButton fileSave = new QPushButton("Save File", frame);
        fileSave.clicked.connect(this, "saveFile()");

        //Dropdown Menü erstellen
        dropdownMenu = new QComboBox(frame);


        dropdownMenu.currentIndexChanged.connect(this, "handleDropdownSelection(int)");

        // Layout konfigurieren
        frameLayout.addWidget(label1);
        frameLayout.addWidget(textField1);
        frameLayout.addWidget(label3);
        frameLayout.addWidget(textField2);
        frameLayout.addWidget(fileUpload);
        frameLayout.addWidget(fileSave);
        frameLayout.addWidget(label2);
        frameLayout.addWidget(dropdownMenu);

        // Frame anzeigen
        frame.show();
    }
    private void saveFile() throws IOException {
        if(selectedOption == null)
        {
            return;
            //TODO Ausgabe, dass Filetype nicht unterstützt wird
        }
        //Create Image
        BufferedImage image = ImageIO.read(new File(path));
        BufferedImage newimage = null;
        //Convert Image
        switch (selectedOption) {
            case "JPEG" -> {
                newimage = convertJPEG(image);
                File outputFile = new File("E:/" + textField1.text() + ".jpg");
                ImageIO.write(newimage, "jpg", outputFile);

            }
            case "PNG" -> {
                newimage = convertPNG(image);
                File outputFile = new File("E:/" + textField1.text() + ".png");
                ImageIO.write(newimage, "png", outputFile);

            }
            case "GIF" -> {
                newimage = convertGIF(image);
                File outputFile = new File("E:/" + textField1.text() + ".gif");
                ImageIO.write(newimage, "gif", outputFile);

            }
            case "BMP" -> {
                newimage = convertBMP(image);
                File outputFile = new File("E:/" + textField1.text() + ".bmp");
                ImageIO.write(newimage, "bmp", outputFile);

            }
            case "TIFF" -> {
                newimage = convertTIFF(image);
                File outputFile = new File("E:/" + textField1.text() + ".tiff");
                ImageIO.write(newimage, "tiff", outputFile);
            }
            default -> System.out.println("not implemented");
        }
    }
    private void openFile() {
        QFileDialog fileDialog = new QFileDialog();
        fileDialog.setFileMode(QFileDialog.FileMode.ExistingFile);
        fileDialog.fileSelected.connect(this, "handleSelectedFile(String)");
        fileDialog.exec();
        //Check if File is Audio, Video, Picture and show possible converts
        QFileInfo fileInfo = new QFileInfo(path);
        String fileType = fileInfo.completeSuffix();
        System.out.println(fileType);
            //dropdownMenu.addItem("OGG");
            //String[] pictureFormat = {"jpg","png","bmp","gif","tiff"};
        String[] audioFormat = {"wav","mp3"};
        String[] videoFormat = {"avi","mp4","wmv"};

        // TODO - Use enums
        if (contains(audioFormat, fileType) ) {
            dropdownMenu.clear();
            for ( AudioType type : AudioType.values() ) {
                dropdownMenu.addItem(type.toString());
            }
        } else if (contains(videoFormat, fileType)) {
            dropdownMenu.clear();
            for ( VideoType type : VideoType.values() ) {
                dropdownMenu.addItem(type.toString());
            }
        } else if (ImageType.contains(fileType)) {
            dropdownMenu.clear();
            for ( ImageType type : ImageType.values() ) {
                dropdownMenu.addItem(type.toString());
            }
            selectedOption = "JPEG";
        } else {
            dropdownMenu.clear();
        }
    }

    @Deprecated
    public static boolean contains(String[] array, String value) {
        for (String element : array) {
            if (element.equals(value)) {
                return true;
            }
        }
        return false;
    }
    private void handleSelectedFile(String filePath) {
        // Handle the selected file path here
        System.out.println("Selected File: " + filePath);
        path = filePath;
    }
    private void handleDropdownSelection(int index) {
        selectedOption = dropdownMenu.itemText(index);
        System.out.println("Selected option: " + selectedOption);
    }
}
