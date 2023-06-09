package tabs;

import filters.*;
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
        invert.clicked.connect(() -> filterPicture("Invertieren"));

        QPushButton schaerfe = new QPushButton("Schärfe", tabWidget);
        schaerfe.clicked.connect(() -> filterPicture("Schärfen"));

        QPushButton watermark = new QPushButton("Wasserzeichen", tabWidget);
        watermark.clicked.connect(() -> filterPicture("Wasserzeichen"));

        QPushButton drehen = new QPushButton("Drehen", tabWidget);
        drehen.clicked.connect(() -> filterPicture("Drehen"));

        QPushButton spiegeln = new QPushButton("Spiegeln", tabWidget);
        spiegeln.clicked.connect(() -> filterPicture("Spiegeln"));

        QLabel labelHeight = new QLabel("Höhe [100 < px > 800]", tabWidget);
        QLineEdit setHeight = new QLineEdit(tabWidget);
        setHeight.setValidator(new QIntValidator(setHeight));
        QPushButton resize = new QPushButton("Resize", tabWidget);
        resize.clicked.connect(() -> {
            String heightText = setHeight.text();
            if (heightText != null && !heightText.isEmpty()) {
                int height = Integer.parseInt(heightText);
                resizePicture(height);
            }
        });

        slider = new QSlider(Qt.Orientation.Horizontal);
        slider.setMinimum(-100);
        slider.setMaximum(100);
        slider.setSingleStep(10);
        slider.setTickInterval(10);
        slider.setTickPosition(QSlider.TickPosition.TicksBothSides);
        slider.sliderReleased.connect(this, "updateValue()");

        northLayout.addWidget(invert);
        northLayout.addWidget(schaerfe);
        northLayout.addWidget(watermark);
        northLayout.addWidget(drehen);
        northLayout.addWidget(spiegeln);

        centerLayout.addWidget(showPicture);
        centerLayout.setAlignment(Qt.AlignmentFlag.AlignCenter);
        southLayout.addWidget(pictureUpload);
        southLayout.addWidget(reset);
        southLayout.addWidget(save);
        bottomLayout.addWidget(labelHeight);
        bottomLayout.addWidget(setHeight);
        bottomLayout.addWidget(resize);

        brightnessLayout.addWidget(slider);
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
        resetPicture();
        QSize newSize = new QSize((pixmap.width() * height) / pixmap.height(),height);
        pixmap = pixmap.scaled(newSize);
        showPicture.setPixmap(pixmap);
    }

    }

    private void resetPicture() {
        if(pixmap != null)
        {
            oldBrightness = 0;
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
        if (pixmap != null) {
            if (filter.equals("Invertieren")) {
                pixmap = InvertFilter.invertPixmap(pixmap);
            } else if (filter.equals("Schärfen")) {
                pixmap = SchaerfeFilter.schaerfePixmap(pixmap);
            }
         else if (filter.equals("Wasserzeichen")) {
            pixmap = WatermarkFilter.watermarkPixmap(pixmap);
        }
            else if (filter.equals("Drehen")) {
                pixmap = TurnFilter.turnPixmap(pixmap);
            }
            else if (filter.equals("Spiegeln")) {
                pixmap = SpiegelFilter.spiegelPixmap(pixmap);
            }
            showPicture.setPixmap(pixmap);
        }
    }
    private void updateValue()
    {
        if(pixmap != null)
        {
            int newBrightness = slider.value();
            newBrightness = newBrightness - oldBrightness;

            pixmap = HellFilter.hellPixmap(pixmap, newBrightness);
            showPicture.setPixmap(pixmap);
            oldBrightness = newBrightness;
        }


    }
}
