package gui;

import converter.AudioConverter;
import converter.Converter;
import converter.ImageConverter;
import converter.VideoConverter;
import io.qt.core.*;
import io.qt.gui.QImageReader;
import io.qt.gui.QPixmap;
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
    private QBoxLayout tab1Layout;
    private QFrame frame;
    private QLabel labelName;
    private QLabel labelPath;
    private QLabel labelConvert;
    private QLabel showPicture;
    private QPushButton fileUpload;
    private QBoxLayout tab3Layout;
    private QPushButton fileSave;


    public void run() {
        QMetaObject.invokeMethod(this, "createFrame", Qt.ConnectionType.QueuedConnection);
    }

    private void createFrame() {

        // Frame erstellen
        frame = new QFrame();
        frame.setFrameStyle(QFrame.Shape.Box.value());
        frame.setLineWidth(0);
        QVBoxLayout frameLayout = new QVBoxLayout(frame);

        labelName = new QLabel("Name", frame);
        fieldFileName = new QLineEdit(frame);
        labelPath = new QLabel("Path", frame);
        fieldFilePath = new QLineEdit(frame);
        labelConvert = new QLabel("Convert to", frame);

        //Button für Upload und Speicherung erstellen

        fileUpload = new QPushButton("Upload File", frame);
        fileUpload.clicked.connect(this, "openFile()");
        fileSave = new QPushButton("Save File", frame);
        fileSave.clicked.connect(this, "saveFile()");

        //Dropdown Menü erstellen
        dropdownMenu = new QComboBox(frame);
        dropdownMenu.currentIndexChanged.connect(this, "handleDropdownSelection(int)");

        QTabWidget tabWidget = new QTabWidget();

        // Tabs erstellen und hinzufügen
        QWidget tab0 = new QWidget();
        QVBoxLayout tab0Layout = new QVBoxLayout(tab0);
        tabWidget.addTab(tab0, "Convert");

        tab0Layout.addWidget(labelName);
        tab0Layout.addWidget(fieldFileName);
        tab0Layout.addWidget(labelPath);
        tab0Layout.addWidget(fieldFilePath);
        tab0Layout.addWidget(fileUpload);
        tab0Layout.addWidget(fileSave);
        tab0Layout.addWidget(labelConvert);
        tab0Layout.addWidget(dropdownMenu);

        QWidget tab1 = new QWidget();
        QVBoxLayout tab1Layout = new QVBoxLayout(tab1);
        tabWidget.addTab(tab1, "Edit: Picture");

        QPushButton pictureUpload = new QPushButton("Upload Picture", frame);
        pictureUpload.clicked.connect(this, "openPicture()");
        showPicture = new QLabel(frame);
        tab1Layout.addWidget(showPicture);
        tab1Layout.addWidget(pictureUpload);
        tab1Layout.setAlignment(Qt.AlignmentFlag.AlignCenter);

        QWidget tab2 = new QWidget();
        QVBoxLayout tab2Layout = new QVBoxLayout(tab2);
        tabWidget.addTab(tab2, "Edit: Audio");

        QWidget tab3 = new QWidget();
        QVBoxLayout tab3Layout = new QVBoxLayout(tab3);
        tabWidget.addTab(tab3, "Edit: Video");

        frameLayout.addWidget(tabWidget);


        // Frame anzeigen
        frame.show();
    }
    private void openPicture()
    {
        QFileDialog fileDialog = new QFileDialog();
        fileDialog.setFileMode(QFileDialog.FileMode.ExistingFile);
        fileDialog.fileSelected.connect(this, "handleSelectedFile(String)");
        fileDialog.exec();

        QFileInfo fileInfo = new QFileInfo(path.toFile().getPath());

        if (!fileInfo.filePath().isEmpty() && isImageFile(fileInfo.filePath())) {
            QPixmap pixmap = new QPixmap(fileInfo.filePath());
            int height = 500;
            int width = (pixmap.width() * height) / pixmap.height();
            QSize newSize = new QSize(width,height);
            pixmap = pixmap.scaled(newSize);
            showPicture.setPixmap(pixmap);
        } else {
            QMessageBox.warning(frame, "Invalid Image", "Selected file is not a valid image.");
        }
    }
    private boolean isImageFile(String filePath) {
        QImageReader imageReader = new QImageReader(filePath);
        return imageReader.canRead();
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
