package tabs;

import io.qt.NonNull;
import io.qt.core.QFileInfo;
import io.qt.core.QSize;
import io.qt.core.Qt;
import io.qt.gui.*;
import io.qt.widgets.*;

import java.nio.file.Path;

public class EditPictureTab {
    private QLabel showPicture;
    QWidget tabWidget;
    private QPixmap pixmap;

    private Path path;

    private QFileInfo fileInfo;

    public QWidget createTabWidget() {
        tabWidget = new QWidget();

        QVBoxLayout tabLayout = new QVBoxLayout(tabWidget);

        QPushButton pictureUpload = new QPushButton("Upload Picture", tabWidget);
        pictureUpload.clicked.connect(this, "openPicture()");
        showPicture = new QLabel(tabWidget);

        QHBoxLayout northLayout = new QHBoxLayout();
        QHBoxLayout centerLayout = new QHBoxLayout();
        QHBoxLayout southLayout = new QHBoxLayout();

        tabLayout.addLayout(northLayout);
        tabLayout.addLayout(centerLayout);
        tabLayout.addLayout(southLayout);

        QPushButton invert = new QPushButton("Invert", tabWidget);
        invert.clicked.connect(this, "invertPicture()");

        northLayout.addWidget(invert);
        centerLayout.addWidget(showPicture);
        southLayout.addWidget(pictureUpload);
        tabLayout.setAlignment(Qt.AlignmentFlag.AlignCenter);

        return tabWidget;
    }

    private void openPicture()
    {
        QFileDialog fileDialog = new QFileDialog();
        fileDialog.setFileMode(QFileDialog.FileMode.ExistingFile);
        fileDialog.fileSelected.connect(this, "handleSelectedFile(String)");
        fileDialog.exec();

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
    private boolean isImageFile(String filePath) {
        QImageReader imageReader = new QImageReader(filePath);
        return imageReader.canRead();
    }
    private void handleSelectedFile(String filePath) {
        // Handle the selected file path here
        path = Path.of(filePath);
    }
    private void invertPicture() {
        if (pixmap != null) {

            QImage image = pixmap.toImage();
            QPainter painter = new QPainter(pixmap);
            int width = pixmap.width();
            int height = pixmap.height();
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    QColor color = new QColor(image.pixel(x, y));
                    int red = color.red();
                    int green = color.green();
                    int blue = color.blue();

                    QColor newColor = new QColor(255 - green, 255 - blue, 255 - red);

                    // Zeichnen Sie den Pixel mit den neuen Farbwerten
                    painter.setPen(newColor);
                    painter.drawPoint(x, y);
                }

            }

            showPicture.setPixmap(pixmap);
            painter.end();
        }
    }
}
