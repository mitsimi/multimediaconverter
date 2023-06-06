package tabs;

import io.qt.core.QUrl;
import io.qt.multimedia.QMediaPlayer;
import io.qt.multimedia.widgets.QVideoWidget;
import io.qt.widgets.QWidget;

public class EditVideoTab {
    public QWidget createTabWidget() {
        QVideoWidget videoWidget = new QVideoWidget();

        QMediaPlayer mediaPlayer = new QMediaPlayer();

        mediaPlayer.setVideoOutput(videoWidget);
        mediaPlayer.setSource(new QUrl("D:/Test.mp4"));
        mediaPlayer.play();

        return videoWidget;
    }
}
