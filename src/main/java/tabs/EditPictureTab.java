package tabs;

import filters.*;
import io.qt.core.QFileInfo;
import io.qt.core.QSize;
import io.qt.core.Qt;
import io.qt.gui.*;
import io.qt.widgets.*;

import java.io.IOException;
import java.nio.file.Path;

public class EditPictureTab {
    private QLabel showPicture;
    private QSlider slider;
    private int oldBrightness = 0;

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
        QHBoxLayout bottomLayout = new QHBoxLayout();
        QHBoxLayout brightnessLayout = new QHBoxLayout();

        tabLayout.addLayout(northLayout);
        tabLayout.addLayout(centerLayout);
        tabLayout.addLayout(southLayout);
        tabLayout.addLayout(bottomLayout);
        tabLayout.addLayout(brightnessLayout);


        QPushButton invert = new QPushButton("Invert", tabWidget);
        invert.clicked.connect(() -> filterPicture("Invert"));

        QPushButton sharp = new QPushButton("Sharping", tabWidget);
        sharp.clicked.connect(() -> filterPicture("Sharp"));

        QPushButton bit = new QPushButton("8-Bit", tabWidget);
        bit.clicked.connect(() -> filterPicture("Bit"));

        QPushButton dithering = new QPushButton("Dithering", tabWidget);
        dithering.clicked.connect(() -> filterPicture("Dithering"));

        QPushButton watermark = new QPushButton("Watermark", tabWidget);
        watermark.clicked.connect(() -> filterPicture("Watermark"));

        QPushButton turn = new QPushButton("Turn", tabWidget);
        turn.clicked.connect(() -> filterPicture("Turn"));

        QPushButton mirror = new QPushButton("Mirror", tabWidget);
        mirror.clicked.connect(() -> filterPicture("Mirror"));

        QLabel labelHeight = new QLabel("Height [100 <= px => 800]", tabWidget);
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

        QLabel labelBright = new QLabel("Brightness", tabWidget);
        slider = new QSlider(Qt.Orientation.Horizontal);
        slider.setMinimum(-100);
        slider.setMaximum(100);
        slider.setSingleStep(10);
        slider.setTickInterval(10);
        slider.setTickPosition(QSlider.TickPosition.TicksBothSides);
        slider.sliderReleased.connect(this, "updateValue()");

        northLayout.addWidget(invert);
        northLayout.addWidget(sharp);
        northLayout.addWidget(bit);
        northLayout.addWidget(dithering);
        northLayout.addWidget(watermark);
        northLayout.addWidget(turn);
        northLayout.addWidget(mirror);

        centerLayout.addWidget(showPicture);
        centerLayout.setAlignment(Qt.AlignmentFlag.AlignCenter);
        southLayout.addWidget(pictureUpload);
        southLayout.addWidget(reset);
        southLayout.addWidget(save);
        bottomLayout.addWidget(labelHeight);
        bottomLayout.addWidget(setHeight);
        bottomLayout.addWidget(resize);
        brightnessLayout.addWidget(labelBright);
        brightnessLayout.addWidget(slider);
        tabLayout.setAlignment(Qt.AlignmentFlag.AlignCenter);

        return tabWidget;
    }

    private void savePicture() throws IOException {
        if(pixmap != null)
        {
            QImage image = pixmap.toImage();
            System.out.print(fileInfo.absolutePath());
            image.save(fileInfo.absolutePath()+fileInfo.baseName()+"new."+fileInfo.completeSuffix().toLowerCase());
        }


    }



    private void resizePicture(int height) {
    if(height >= 100 && height <= 800)
    {
        resetPicture();
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
            case "Sharp" -> SharpenFilter.apply(pixmap);
            case "Bit" -> Bit8Filter.apply(pixmap);
            case "Dithering" -> DitheringFilter.apply(pixmap);
            case "Watermark" -> WatermarkFilter.apply(pixmap);
            case "Turn" -> TurnFilter.apply(pixmap);
            case "Mirror" -> MirrorFilter.apply(pixmap);
            default -> throw new IllegalStateException("Unexpected value: " + filter);
        };

        showPicture.setPixmap(pixmap);
    }
    private void updateValue()
    {
        if(pixmap != null)
        {
            int newBrightness = slider.value();
            newBrightness = newBrightness - oldBrightness;

            pixmap = BrightnessFilter.apply(pixmap, newBrightness);
            showPicture.setPixmap(pixmap);
            oldBrightness = newBrightness;
        }


    }
}
