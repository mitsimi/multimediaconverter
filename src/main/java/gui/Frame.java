package gui;

import converter.AudioConverter;
import converter.Converter;
import converter.ImageConverter;
import converter.VideoConverter;
import io.qt.core.*;
import io.qt.widgets.*;
import types.AudioType;
import types.ImageType;
import types.MediaType;
import types.VideoType;

import java.io.File;
import java.io.IOException;

public class Frame extends QThread {
    private String path;
    private QFileInfo fileInfo;
    private MediaType mediaType;

    private QLineEdit fieldFileName;
    private QLineEdit fieldFilePath;

    private QComboBox dropdownMenu;
    private String selectedOption;

    public void run() {

        QMetaObject.invokeMethod(this, "createFrame", Qt.ConnectionType.QueuedConnection);
    }

    private void createFrame() {

        // Frame erstellen
        QFrame frame = new QFrame();
        frame.setFrameStyle(QFrame.Shape.Box.value());
        frame.setLineWidth(4);
        QVBoxLayout frameLayout = new QVBoxLayout(frame);

        QLabel labelName = new QLabel("Name", frame);
        fieldFileName = new QLineEdit(frame);
        QLabel labelLink = new QLabel("Link", frame);
        fieldFilePath = new QLineEdit(frame);
        QLabel labelConvert = new QLabel("Convert to", frame);


        //Button für Upload und Speicherung erstellen
        QPushButton fileUpload = new QPushButton("Upload File", frame);
        fileUpload.clicked.connect(this, "openFile()");
        QPushButton fileSave = new QPushButton("Save File", frame);
        fileSave.clicked.connect(this, "saveFile()");

        //Dropdown Menü erstellen
        dropdownMenu = new QComboBox(frame);


        dropdownMenu.currentIndexChanged.connect(this, "handleDropdownSelection(int)");

        // Layout konfigurieren
        frameLayout.addWidget(labelName);
        frameLayout.addWidget(fieldFileName);
        frameLayout.addWidget(labelLink);
        frameLayout.addWidget(fieldFilePath);
        frameLayout.addWidget(fileUpload);
        frameLayout.addWidget(fileSave);
        frameLayout.addWidget(labelConvert);
        frameLayout.addWidget(dropdownMenu);

        // Frame anzeigen
        frame.show();
    }

    // TODO - Rewrite this method to be more generic
    private void saveFile() throws IOException {
        File file = new File(path);

        Converter converter;

        // Create converter
        if (mediaType.getClass().equals(ImageType.class)) {
            converter = new ImageConverter(file);
        } else if (mediaType.getClass().equals(AudioType.class)) {
            converter = new AudioConverter(file);
        } else if (mediaType.getClass().equals(VideoType.class)) {
            converter = new VideoConverter(file);
        } else { // so that the compiler shuts up
            converter = new Converter() {
                @Override
                public void convert(String to_type) throws IOException {
                }

                @Override
                public void save(String absolutePath, String fileName) throws IOException {
                }
            };
        }

        // Convert into selected format
        converter.convert(selectedOption);

        //Save converted file
        converter.save(fileInfo.absolutePath(), fieldFileName.text());

//         ImageIO.write(convertedImage, "jpg", outputFile);
    }

    private void openFile() {
        QFileDialog fileDialog = new QFileDialog();
        fileDialog.setFileMode(QFileDialog.FileMode.ExistingFile);
        fileDialog.fileSelected.connect(this, "handleSelectedFile(String)");
        fileDialog.exec();

        fileInfo = new QFileInfo(path);
        String fileType = fileInfo.completeSuffix();

        // Set the file name and path based on the selected file
        fieldFileName.setText(fileInfo.baseName());
        fieldFilePath.setText(fileInfo.absoluteFilePath());

        System.out.println(fileType);

        dropdownMenu.clear();
        loadDropdownOptions(fileType);
    }

    private void loadDropdownOptions(String fileType) {
        // Load the dropdown options based on the file type
        // HELP - May need to be changed for a faster approach
        if (AudioType.contains(fileType)) {
            mediaType = AudioType.getEnum(fileType);
            for ( AudioType type : AudioType.values() ) {
                dropdownMenu.addItem(type.toString());
            }
        } else if (VideoType.contains(fileType)) {
            mediaType = VideoType.getEnum(fileType);
            for ( VideoType type : VideoType.values() ) {
                dropdownMenu.addItem(type.toString());
            }
        } else if (ImageType.contains(fileType)) {
            mediaType = ImageType.getEnum(fileType);
            for ( ImageType type : ImageType.values() ) {
                dropdownMenu.addItem(type.toString());
            }
            selectedOption = ImageType.JPG.toString();
        }
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
