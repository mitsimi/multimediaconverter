package gui;

import converter.AudioConverter;
import converter.Converter;
import converter.ImageConverter;
import converter.VideoConverter;
import io.qt.core.*;
import io.qt.widgets.*;
import types.*;

import java.io.IOException;
import java.nio.file.Path;

public class Frame extends QThread {
    private Path path;
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
        frame.setLineWidth(0);
        QVBoxLayout frameLayout = new QVBoxLayout(frame);

        QLabel labelName = new QLabel("Name", frame);
        fieldFileName = new QLineEdit(frame);
        QLabel labelPath = new QLabel("Path", frame);
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
        frameLayout.addWidget(labelPath);
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

        Converter converter;

        // Create converter
        if (mediaType.getClass().equals(ImageType.class)) {
            converter = new ImageConverter(path);
        } else if (mediaType.getClass().equals(AudioType.class)) {
            converter = new AudioConverter(path);
        } else if (mediaType.getClass().equals(VideoType.class)) {
            converter = new VideoConverter(path);
        } else { // so that the compiler shuts up
            converter = new Converter() {
                @Override
                public void convert(String to_type) throws IOException {
                    throw new UnsupportedFileTypeException("Unsupported file type");
                }

                @Override
                public void save(String absolutePath, String fileName) throws IOException {
                    throw new UnsupportedFileTypeException("Unsupported file type");
                }
            };
        }

        // Convert into selected format
        converter.convert(selectedOption);

        //Save converted file
        converter.save(fieldFilePath.text(), fieldFileName.text());
    }

    private void openFile() {
        QFileDialog fileDialog = new QFileDialog();
        fileDialog.setFileMode(QFileDialog.FileMode.ExistingFile);
        fileDialog.fileSelected.connect(this, "handleSelectedFile(String)");
        fileDialog.exec();

        fileInfo = new QFileInfo(path.toFile().getPath());
        String fileType = fileInfo.completeSuffix();

        // Set the file name and path based on the selected file
        fieldFileName.setText(fileInfo.baseName());
        fieldFilePath.setText(fileInfo.absoluteDir().absolutePath());

        dropdownMenu.clear();
        loadDropdownOptions();
    }

    /* TODO - other approach needed.
        Converting from raw files not possible because they are not supported for conversion TO them.
     */
    private void loadDropdownOptions() {
        String fileType = fileInfo.completeSuffix().toLowerCase();
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
            selectedOption = String.valueOf(ImageType.JPEG);
        }
    }

    private void handleSelectedFile(String filePath) {
        // Handle the selected file path here
        path = Path.of(filePath);
    }

    private void handleDropdownSelection(int index) {
        selectedOption = dropdownMenu.itemText(index);
    }
}
