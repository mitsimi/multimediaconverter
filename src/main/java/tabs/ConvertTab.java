package tabs;

import converter.AudioConverter;
import converter.Converter;
import converter.ImageConverter;
import converter.VideoConverter;
import io.qt.core.QFileInfo;
import io.qt.core.QSize;
import io.qt.gui.QImageReader;
import io.qt.gui.QPixmap;
import io.qt.widgets.*;
import types.*;

import java.io.IOException;
import java.nio.file.Path;

public class ConvertTab {
    private Path path;

    private QLineEdit fieldFilePath;
    private String selectedOption;
    QComboBox dropdownMenu;

    private QFileInfo fileInfo;
    private MediaType mediaType;

    private QLineEdit fieldFileName;
    public QWidget createTabWidget() {
        QWidget tabWidget = new QWidget();

        QVBoxLayout tabLayout = new QVBoxLayout(tabWidget);

        // Add the necessary widgets and layout for the Convert tab
        QLabel labelName = new QLabel("Name", tabWidget);
        fieldFileName = new QLineEdit(tabWidget);
        QLabel labelPath = new QLabel("Path", tabWidget);
        fieldFilePath = new QLineEdit(tabWidget);
        QLabel labelConvert = new QLabel("Convert to", tabWidget);
        dropdownMenu = new QComboBox(tabWidget);
        dropdownMenu.currentIndexChanged.connect(this, "handleDropdownSelection(int)");

        QPushButton fileUpload= new QPushButton("Upload File", tabWidget);
        fileUpload.clicked.connect(this, "openFile()");
        QPushButton fileSave = new QPushButton("Save File", tabWidget);
        fileSave.clicked.connect(this, "saveFile()");

        // ... Add other necessary widgets and connect signals/slots

        tabLayout.addWidget(labelName);
        tabLayout.addWidget(fieldFileName);
        tabLayout.addWidget(labelPath);
        tabLayout.addWidget(fieldFilePath);
        tabLayout.addWidget(fileUpload);
        tabLayout.addWidget(fileSave);
        tabLayout.addWidget(labelConvert);
        tabLayout.addWidget(dropdownMenu);

        return tabWidget;
    }
    private void handleSelectedFile(String filePath) {
        // Handle the selected file path here
        
            path = Path.of(filePath);


    }

    private void handleDropdownSelection(int index) {
        selectedOption = dropdownMenu.itemText(index);
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

           if(path==null){
               return;
           }

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
}
