package gui;

import io.qt.core.*;
import io.qt.widgets.*;
import picture.PictureConverter;

import javax.imageio.ImageIO;
import java.awt.*;
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

        QLabel label1 = new QLabel("Label 1", frame);
        textField1 = new QLineEdit(frame);

        //Button für Upload und Speicherung erstellen
        QPushButton fileUpload = new QPushButton("Upload File", frame);
        fileUpload.clicked.connect(this, "openFile()");
        QPushButton fileSave = new QPushButton("Save File", frame);
        fileSave.clicked.connect(this, "saveFile()");

        //Dropdown Menü erstellen
        dropdownMenu = new QComboBox(frame);
        dropdownMenu.addItem("JPEG");
        dropdownMenu.addItem("PNG");
        dropdownMenu.addItem("BMP");
        dropdownMenu.addItem("GIF");
        dropdownMenu.addItem("TIFF");
        /*dropdownMenu.addItem("MP4");
        dropdownMenu.addItem("AVI");
        dropdownMenu.addItem("WMV");
        dropdownMenu.addItem("MOV");
        dropdownMenu.addItem("MP3");
        dropdownMenu.addItem("OGG");
        dropdownMenu.addItem("WAV");*/

        dropdownMenu.currentIndexChanged.connect(this, "handleDropdownSelection(int)");

        // Layout konfigurieren
        frameLayout.addWidget(label1);
        frameLayout.addWidget(textField1);
        frameLayout.addWidget(fileUpload);
        frameLayout.addWidget(fileSave);
        frameLayout.addWidget(dropdownMenu);

        // Frame anzeigen
        frame.show();
    }
    private void saveFile() throws IOException {
        //Create Image
        BufferedImage image = ImageIO.read(new File(path));
        BufferedImage newimage = null;
        //Convert Image
        if(selectedOption == "JPEG")
        {
            newimage = convertJPEG(image);
            File outputFile = new File(textField1.text()+"/newimage.jpg");
            ImageIO.write(newimage, "jpg", outputFile);

        } else if (selectedOption == "PNG") {
            newimage = convertPNG(image);
            File outputFile = new File(textField1.text()+"/newimage.png");
            ImageIO.write(newimage, "png", outputFile);

        } else if (selectedOption == "GIF") {
            newimage = convertGIF(image);
            File outputFile = new File(textField1.text()+"/newimage.gif");
            ImageIO.write(newimage, "gif", outputFile);

        } else if (selectedOption == "BMP") {
            newimage = convertBMP(image);
            File outputFile = new File(textField1.text()+"/newimage.bmp");
            ImageIO.write(newimage, "bmp", outputFile);

        } else if (selectedOption == "TIFF") {
            newimage = convertTIFF(image);
            File outputFile = new File(textField1.text()+"/newimage.tiff");
            ImageIO.write(newimage, "tiff", outputFile);
        }

       /* String originalFilePath = path;
        QFileInfo fileInfo = new QFileInfo(path);
        String directoryPath = fileInfo.absolutePath();
        String fileType = fileInfo.completeSuffix();

        //Name,Path and FileType
        String newFilePath = textField1.text()+"/"+newimage.toString();

        QFile originalFile = new QFile(originalFilePath);
        if (originalFile.open(QIODevice.OpenModeFlag.ReadOnly)) {
            QByteArray fileData = originalFile.readAll();
            originalFile.close();

            QFile newFile = new QFile(newFilePath);
            if (newFile.open(QIODevice.OpenModeFlag.WriteOnly)) {
                newFile.write(fileData);
                newFile.close();
                System.out.println("File saved successfully to: " + newFilePath);
            } else {
                System.out.println("Failed to save file: " + newFile.errorString());
            }
        } else {
            System.out.println("Failed to open original file: " + originalFile.errorString());
        }*/
    }
    private void openFile() {
        QFileDialog fileDialog = new QFileDialog();
        fileDialog.setFileMode(QFileDialog.FileMode.ExistingFile);
        fileDialog.fileSelected.connect(this, "handleSelectedFile(String)");
        fileDialog.exec();
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

    @Override
    public BufferedImage convertJPEG(BufferedImage image) {
        return image;
    }

    @Override
    public BufferedImage convertPNG(BufferedImage image) {
        return image;
    }

    @Override
    public BufferedImage convertGIF(BufferedImage image) {
        return image;
    }

    @Override
    public BufferedImage convertBMP(BufferedImage image) {
        return image;
    }

    @Override
    public BufferedImage convertTIFF(BufferedImage image) {
        return image;
    }
}
