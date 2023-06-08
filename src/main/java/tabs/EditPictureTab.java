package tabs;

import filters.Bit8Filter;
import filters.DitheringFilter;
import filters.InvertFilter;
import filters.SchaerfeFilter;
import io.qt.core.QFileInfo;
import io.qt.core.QSize;
import io.qt.core.Qt;
import io.qt.gui.*;
import io.qt.widgets.*;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class EditPictureTab {
    private QLabel showPicture;
    QWidget tabWidget;
    private QPixmap pixmap;
    private QPixmap originalPixmap;
    private Path path;

    private QFileInfo fileInfo;

    public QWidget createTabWidget() {
        tabWidget = new QWidget();

        QVBoxLayout tabLayout = new QVBoxLayout(tabWidget);

        QPushButton pictureUpload = new QPushButton("Upload Picture", tabWidget);
        pictureUpload.clicked.connect(this, "openPicture()");
        showPicture = new QLabel(tabWidget);

        QPushButton reset = new QPushButton("Reset Picture", tabWidget);
        reset.clicked.connect(() -> resetPicture());
        QPushButton save = new QPushButton("Save Picture", tabWidget);
        save.clicked.connect(() -> savePicture());

        QHBoxLayout northLayout = new QHBoxLayout();
        QHBoxLayout centerLayout = new QHBoxLayout();
        QHBoxLayout southLayout = new QHBoxLayout();
        QHBoxLayout eastLayout = new QHBoxLayout();

        tabLayout.addLayout(northLayout);
        tabLayout.addLayout(centerLayout);
        tabLayout.addLayout(southLayout);
        tabLayout.addLayout(eastLayout);


        QPushButton invert = new QPushButton("Invert", tabWidget);
        invert.clicked.connect(() -> filterPicture("Invert"));

        QPushButton sharp = new QPushButton("Sharping", tabWidget);
        sharp.clicked.connect(() -> filterPicture("Sharp"));

        QPushButton bit = new QPushButton("8-Bit", tabWidget);
        bit.clicked.connect(() -> filterPicture("Bit"));

        QPushButton dithering = new QPushButton("Dithering", tabWidget);
        dithering.clicked.connect(() -> filterPicture("Dithering"));

        QLabel labelHeight = new QLabel("Height [100 < px > 800]", tabWidget);
        QLineEdit setHeight = new QLineEdit(tabWidget);
        setHeight.setValidator(new QIntValidator(setHeight));
        QPushButton resize = new QPushButton("Resize", tabWidget);
        resize.clicked.connect(() -> {
            String heightText = setHeight.text();
            if (!heightText.isEmpty()) {
                int height = Integer.parseInt(heightText);
                resizePicture(height);
            }
        });

        northLayout.addWidget(invert);
        northLayout.addWidget(sharp);
        northLayout.addWidget(bit);
        northLayout.addWidget(dithering);

        centerLayout.addWidget(showPicture);
        southLayout.addWidget(pictureUpload);
        southLayout.addWidget(reset);
        southLayout.addWidget(save);
        eastLayout.addWidget(labelHeight);
        eastLayout.addWidget(setHeight);
        eastLayout.addWidget(resize);
        tabLayout.setAlignment(Qt.AlignmentFlag.AlignCenter);

        return tabWidget;
    }

    private void savePicture() throws IOException {
        QImage image = pixmap.toImage();
        System.out.print(fileInfo.absolutePath());
        image.save(fileInfo.absolutePath()+fileInfo.baseName()+"new."+fileInfo.completeSuffix().toLowerCase());

    }



    private void resizePicture(int height) {
    if(height > 100 && height < 800)
    {
        QSize newSize = new QSize((pixmap.width() * height) / pixmap.height(),height);
        pixmap = pixmap.scaled(newSize);
        showPicture.setPixmap(pixmap);
    }

    }

    private void resetPicture() {
        if(pixmap != null)
        {
            pixmap = new QPixmap(fileInfo.filePath());
            int height = 500;
            int width = (pixmap.width() * height) / pixmap.height();
            QSize newSize = new QSize(width,height);
            pixmap = pixmap.scaled(newSize);
            showPicture.setPixmap(pixmap);

        }

    }

    private void openPicture()
    {
        QFileDialog fileDialog = new QFileDialog();
        fileDialog.setFileMode(QFileDialog.FileMode.ExistingFile);
        fileDialog.fileSelected.connect(this, "handleSelectedFile(String)");
        fileDialog.exec();
        if(path != null)
        {
            fileInfo = new QFileInfo(path.toFile().getPath());
            if (!fileInfo.filePath().isEmpty() && isImageFile(fileInfo.filePath())) {
                pixmap = new QPixmap(fileInfo.filePath());
                int height = 500;
                int width = (pixmap.width() * height) / pixmap.height();
                QSize newSize = new QSize(width,height);
                pixmap = pixmap.scaled(newSize);

                showPicture.setPixmap(pixmap);
            } else {
                QMessageBox.warning(tabWidget, "Invalid Image", "Selected file is not a valid image.");
            }
        }
    }
    private boolean isImageFile(String filePath) {
        QImageReader imageReader = new QImageReader(filePath);
        return imageReader.canRead();
    }
    private void handleSelectedFile(String filePath) {
        // Handle the selected file path here
        path = Path.of(filePath);
    }

    private void filterPicture(String filter)
    {
        if (pixmap == null) {
            return;
        }

        pixmap =  switch (filter) {
            case "Invert" -> InvertFilter.apply(pixmap);
            case "Sharp" -> SchaerfeFilter.apply(pixmap);
            case "Bit" -> Bit8Filter.apply(pixmap);
            case "Dithering" -> DitheringFilter.apply(pixmap);
            default -> throw new IllegalStateException("Unexpected value: " + filter);
        };

        showPicture.setPixmap(pixmap);
    }
}
