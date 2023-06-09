package tabs;

import io.qt.core.QFileInfo;
import io.qt.core.QSize;
import io.qt.core.QUrl;
import io.qt.core.Qt;
import io.qt.gui.QPixmap;
import io.qt.multimedia.*;
import io.qt.multimedia.widgets.QGraphicsVideoItem;
import io.qt.multimedia.widgets.QVideoWidget;
import io.qt.widgets.*;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.VideoProcessor;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.info.AudioInfo;
import ws.schild.jave.info.VideoSize;

import java.io.File;
import java.nio.file.Path;

public class EditVideoTab {

    private QMediaPlayer mediaPlayer;
    private Path path;
    private QFileInfo fileInfo;
    private QWidget widget;
    private QVideoWidget videoWidget;
    private QAudioOutput audioOutput;
    private QSlider timeSlider;
    private QPushButton cutBefore, cutAfter, encodeButton, videoSave;
    private QLineEdit setWidth, setHeight, setFrameRate, setBitRate;

    private final static String TEMPFILE_PATH = ".temp.mp4";

    public QWidget createTabWidget() {
        widget = new QWidget();

        QVBoxLayout layout = new QVBoxLayout(widget);

        videoWidget = new QVideoWidget(widget);
        mediaPlayer = new QMediaPlayer(videoWidget);
        mediaPlayer.setVideoOutput(videoWidget);
        //mediaPlayer.setSource(new QUrl("D:/Test.mp4"));
        //mediaPlayer.play();

        mediaPlayer.videoOutput();

        audioOutput = new QAudioOutput();
        audioOutput.setVolume(50);
        mediaPlayer.setAudioOutput(audioOutput);

        mediaPlayer.positionChanged.connect(this, "setSlider()");

        cutBefore = new QPushButton("Cut Before", widget);
        cutBefore.clicked.connect(this, "doCutBefore()");
        cutAfter = new QPushButton("Cut After", widget);
        cutAfter.clicked.connect(this, "doCutAfter()");

        QPushButton videoUpload = new QPushButton("Upload Video", widget);
        videoUpload.clicked.connect(this, "openVideo()");
        videoSave = new QPushButton("Save Video", widget);
        videoSave.clicked.connect(this, "saveVideo()");
        videoSave.setEnabled(false);

        QPushButton playVideo = new QPushButton("Play Video", widget);
        playVideo.clicked.connect(this, "playVideo()");
        QPushButton stopVideo = new QPushButton("Pause Video", widget);
        stopVideo.clicked.connect(this, "pauseVideo()");

        QLabel sizeLabel = new QLabel("Size:", widget);
        setWidth = new QLineEdit(widget);
        setWidth.setPlaceholderText("Width");
        setHeight = new QLineEdit(widget);
        setHeight.setPlaceholderText("Height");

        QLabel frameRateLabel = new QLabel("FrameRate:", widget);
        setFrameRate = new QLineEdit(widget);
        setFrameRate.setPlaceholderText("FPS");

        QLabel bitRateLabel = new QLabel("BitRate:", widget);
        setBitRate = new QLineEdit(widget);
        setBitRate.setPlaceholderText("BitRate");

        encodeButton = new QPushButton("Encode", widget);
        encodeButton.clicked.connect(this, "encode()");

        timeSlider = new QSlider(Qt.Orientation.Horizontal, widget);
        //timeSlider.sliderPressed.connect(this, "pauseVideo()");
        //timeSlider.sliderMoved.connect(this, "setVideo()");
        //timeSlider.sliderReleased.connect(this,"setVideo()");
        timeSlider.actionTriggered.connect(this, "setVideo()");
        //QSlider slider2 = new QSlider(Qt.Orientation.Horizontal, widget);
        QStackedWidget sliders = new QStackedWidget(widget);
        sliders.insertWidget(0, timeSlider);
        //slider2.setSliderPosition(5);
        //sliders.insertWidget(1,slider2);
        sliders.setMaximumHeight(15);

        QHBoxLayout hBox1 = new QHBoxLayout(widget);
        QHBoxLayout hBox2 = new QHBoxLayout(widget);
        QHBoxLayout hBox3 = new QHBoxLayout(widget);
        QHBoxLayout hBox4 = new QHBoxLayout(widget);
        QHBoxLayout hBox5 = new QHBoxLayout(widget);

        //hBox1.addWidget(cutBefore);
        //hBox1.addWidget(cutAfter);
        //layout.addLayout(hBox1);

        layout.addWidget(videoWidget);

        hBox2.addWidget(sliders);
        layout.addLayout(hBox2);

        hBox3.addWidget(videoUpload);
        hBox3.addWidget(playVideo);
        hBox3.addWidget(stopVideo);
        hBox3.addWidget(videoSave);
        layout.addLayout(hBox3);

        hBox4.addWidget(sizeLabel);
        hBox4.addWidget(setWidth);
        hBox4.addWidget(setHeight);
        hBox4.addWidget(frameRateLabel);
        hBox4.addWidget(setFrameRate);
        layout.addLayout(hBox4);

        hBox5.addWidget(bitRateLabel);
        hBox5.addWidget(setBitRate);
        hBox5.addWidget(encodeButton);
        layout.addLayout(hBox5);


        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                File temp = new File(TEMPFILE_PATH);
                if(temp.exists()) {
                    temp.delete();
                    System.out.println("Temp Deleted");
                }
            }
        }));


        return widget;
    }

    private void handleSelectedFile(String filePath) {
        // Handle the selected file path here
        path = Path.of(filePath);
    }

    private void openVideo()
    {
        QFileDialog fileDialog = new QFileDialog();
        fileDialog.setFileMode(QFileDialog.FileMode.ExistingFile);
        fileDialog.fileSelected.connect(this, "handleSelectedFile(String)");
        fileDialog.exec();
        openVideo(path);
    }

    private void openVideo(Path path) {
        if(path != null)
        {
            fileInfo = new QFileInfo(path.toFile().getPath());
            if (!fileInfo.filePath().isEmpty() && isVideoFile(fileInfo.filePath())) {


                mediaPlayer.setSource(new QUrl(path.toString().replace("\\", "/")));

                MultimediaObject video = new MultimediaObject(path.toFile());
                try {
                    setWidth.setText(video.getInfo().getVideo().getSize().getWidth()+"");
                    setHeight.setText(video.getInfo().getVideo().getSize().getHeight()+"");
                    setFrameRate.setText((int) video.getInfo().getVideo().getFrameRate()+"");
                    setBitRate.setText(video.getInfo().getVideo().getBitRate()+"");
                } catch (EncoderException e) {
                    e.printStackTrace();
                }

                playVideo();
                stopVideo();
                mediaPlayer.setPosition(1);
            } else {
                QMessageBox.warning(widget, "Invalid Video", "Selected file is not a valid video.");
            }
        }
    }

    private void encode() {
        try {
            File source = path.toFile();
            MultimediaObject video = new MultimediaObject(source);
            File target = new File(TEMPFILE_PATH);

            AudioInfo audioInfo = video.getInfo().getAudio();
            AudioAttributes audioAtr = new AudioAttributes();
            //audioAtr.setCodec("aac");
            audioAtr.setBitRate(audioInfo.getBitRate());
            audioAtr.setSamplingRate(audioInfo.getSamplingRate());
            audioAtr.setChannels(audioInfo.getChannels());

            VideoAttributes videoAtr = new VideoAttributes();
            videoAtr.setBitRate(Integer.parseInt(setBitRate.getText()));
            videoAtr.setFrameRate(Integer.parseInt(setFrameRate.getText()));
            videoAtr.setSize(new VideoSize(Integer.parseInt(setWidth.getText()), Integer.parseInt(setHeight.getText())));
            //videoAtr.setCodec(video.getInfo().getVideo().getDecoder());
            //videoAtr.setCodec("mpeg4");

            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setAudioAttributes(audioAtr);
            attrs.setVideoAttributes(videoAtr);
            attrs.setOutputFormat(video.getInfo().getFormat());

            Encoder encoder = new Encoder();
            encoder.encode(video, target, attrs);

            videoSave.setEnabled(true);
            openVideo(target.toPath());
        } catch (Exception e) {
            e.printStackTrace();
            QMessageBox.warning(widget, "Encoding Failed", "Something went wrong while encoding.");
        }

    }

    private void saveVideo() {
        File temp = new File("temp.mp4");
        if(!temp.exists()) {
            return;
        }

        System.out.println("Save");
        path.toFile().delete();
        temp.renameTo(path.toFile());

    }

    private void doCutBefore() {

    }
    private void doCutAfter() {

    }

    private void setVideo() {
        if (mediaPlayer.isPlaying()) pauseVideo();
        int x = timeSlider.getValue();
        mediaPlayer.setPosition(x * mediaPlayer.getDuration() / 100);
    }

    private void setSlider() {
        long x = mediaPlayer.position() * 100 / mediaPlayer.getDuration();
        //System.out.println(mediaPlayer.position() +" "+mediaPlayer.getDuration());
        timeSlider.setValue((int) x);
    }

    private void pauseVideo() {
        System.out.println("Pause");
        cutBefore.setEnabled(true);
        cutAfter.setEnabled(true);
        mediaPlayer.pause();
    }

    private void playVideo() {
        System.out.println("Play");
        cutBefore.setEnabled(false);
        cutAfter.setEnabled(false);
        mediaPlayer.play();
    }

    private void stopVideo() {
        System.out.println("Stop");
        cutBefore.setEnabled(true);
        cutAfter.setEnabled(true);
        mediaPlayer.pause();
    }

    private boolean isVideoFile(String filePath) {
        MultimediaObject video = new MultimediaObject(path.toFile());
        try {
            return video.getInfo().getVideo() != null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
