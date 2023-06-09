package tabs;

import io.qt.core.QFileInfo;
import io.qt.core.QUrl;
import io.qt.core.Qt;
import io.qt.multimedia.QAudioDevice;
import io.qt.multimedia.QAudioOutput;
import io.qt.multimedia.QMediaPlayer;
import io.qt.multimedia.widgets.QVideoWidget;
import io.qt.widgets.*;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.info.AudioInfo;
import ws.schild.jave.info.VideoSize;

import java.io.File;
import java.nio.file.Path;

public class EditAudioTab {

    private QMediaPlayer mediaPlayer;
    private Path path;
    private QFileInfo fileInfo;
    private QWidget widget;
    private QAudioOutput audioOutput;
    private QSlider timeSlider;
    private QPushButton encodeButton, audioSave;
    private QLineEdit setSampleRate, setBitRate, setVolume;
    private QComboBox dropDown;

    private final static String TEMPFILE_PATH = ".temp.mp3";

    public QWidget createTabWidget() {
        widget = new QWidget();

        QVBoxLayout layout = new QVBoxLayout(widget);

        audioOutput = new QAudioOutput();
        audioOutput.setVolume(50);


        mediaPlayer = new QMediaPlayer();
        mediaPlayer.setAudioOutput(audioOutput);
        mediaPlayer.positionChanged.connect(this, "setSlider()");

        QPushButton audioUpload = new QPushButton("Upload Audio", widget);
        audioUpload.clicked.connect(this, "openAudio()");
        audioSave = new QPushButton("Save Audio", widget);
        audioSave.clicked.connect(this, "saveAudio()");
        audioSave.setEnabled(false);

        QPushButton playAudio = new QPushButton("Play Audio", widget);
        playAudio.clicked.connect(this, "playAudio()");
        QPushButton stopAudio = new QPushButton("Pause Audio", widget);
        stopAudio.clicked.connect(this, "pauseAudio()");

        QLabel volumeLabel = new QLabel("Volume:", widget);
        setVolume = new QLineEdit(widget);
        setVolume.setPlaceholderText("S: 30");

        QLabel sampleRateLabel = new QLabel("SampleRate:", widget);
        setSampleRate = new QLineEdit(widget);
        setSampleRate.setPlaceholderText("SampleRate");

        QLabel bitRateLabel = new QLabel("BitRate:", widget);
        setBitRate = new QLineEdit(widget);
        setBitRate.setPlaceholderText("BitRate");

        dropDown = new QComboBox();
        dropDown.addItem("Mono");
        dropDown.addItem("Stereo");

        encodeButton = new QPushButton("Encode", widget);
        encodeButton.clicked.connect(this, "encode()");

        timeSlider = new QSlider(Qt.Orientation.Horizontal, widget);
        //timeSlider.sliderPressed.connect(this, "pauseVideo()");
        //timeSlider.sliderMoved.connect(this, "setVideo()");
        //timeSlider.sliderReleased.connect(this,"setVideo()");
        timeSlider.actionTriggered.connect(this, "setAudio()");
        //QSlider slider2 = new QSlider(Qt.Orientation.Horizontal, widget);
        QStackedWidget sliders = new QStackedWidget(widget);
        sliders.insertWidget(0, timeSlider);
        //slider2.setSliderPosition(5);
        //sliders.insertWidget(1,slider2);
        sliders.setMaximumHeight(15);

        QHBoxLayout hBox2 = new QHBoxLayout(widget);
        QHBoxLayout hBox3 = new QHBoxLayout(widget);
        QHBoxLayout hBox4 = new QHBoxLayout(widget);
        QHBoxLayout hBox5 = new QHBoxLayout(widget);

        //hBox1.addWidget(cutBefore);
        //hBox1.addWidget(cutAfter);
        //layout.addLayout(hBox1);


        hBox2.addWidget(sliders);
        layout.addLayout(hBox2);

        hBox3.addWidget(audioUpload);
        hBox3.addWidget(playAudio);
        hBox3.addWidget(stopAudio);
        hBox3.addWidget(audioSave);
        layout.addLayout(hBox3);

        hBox4.addWidget(dropDown);
        hBox4.addWidget(sampleRateLabel);
        hBox4.addWidget(setSampleRate);
        hBox4.addWidget(volumeLabel);
        hBox4.addWidget(setVolume);
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

    private void openAudio()
    {
        QFileDialog fileDialog = new QFileDialog();
        fileDialog.setFileMode(QFileDialog.FileMode.ExistingFile);
        fileDialog.fileSelected.connect(this, "handleSelectedFile(String)");
        fileDialog.exec();
        openAudio(path);
    }

    private void openAudio(Path path) {
        if(path != null)
        {
            fileInfo = new QFileInfo(path.toFile().getPath());
            if (!fileInfo.filePath().isEmpty() && isAudioFile(fileInfo.filePath())) {


                mediaPlayer.setSource(new QUrl(path.toString().replace("\\", "/")));

                MultimediaObject audio = new MultimediaObject(path.toFile());
                try {
                    setSampleRate.setText(audio.getInfo().getAudio().getSamplingRate()+"");
                    setBitRate.setText(audio.getInfo().getAudio().getBitRate()+"");
                    dropDown.setCurrentIndex(audio.getInfo().getAudio().getChannels()-1);
                    setVolume.setText("30");
                } catch (EncoderException e) {
                    e.printStackTrace();
                }

            } else {
                QMessageBox.warning(widget, "Invalid Audio", "Selected file is not a valid audio.");
            }
        }
    }

    private boolean isAudioFile(String filePath) {
        return true;
    }

    private void encode() {
        try {
            File source = path.toFile();
            MultimediaObject audio = new MultimediaObject(source);
            File target = new File(TEMPFILE_PATH);

            AudioInfo audioInfo = audio.getInfo().getAudio();
            AudioAttributes audioAtr = new AudioAttributes();
            audioAtr.setBitRate(Integer.parseInt(setBitRate.getText()));
            audioAtr.setSamplingRate(Integer.parseInt(setSampleRate.getText()));
            audioAtr.setChannels(dropDown.getCurrentIndex()+1);
            audioAtr.setVolume(Integer.parseInt(setVolume.getText()));

            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setAudioAttributes(audioAtr);
            attrs.setOutputFormat(audio.getInfo().getFormat());

            Encoder encoder = new Encoder();
            encoder.encode(audio, target, attrs);

            audioSave.setEnabled(true);
            openAudio(target.toPath());
        } catch (Exception e) {
            e.printStackTrace();
            QMessageBox.warning(widget, "Encoding Failed", "Something went wrong while encoding.");
        }

    }

    private void saveAudio() {
        File temp = new File(TEMPFILE_PATH);
        if(!temp.exists()) {
            return;
        }

        System.out.println("Save");
        path.toFile().delete();
        temp.renameTo(path.toFile());

    }

    private void setAudio() {
        if (mediaPlayer.isPlaying()) pauseAudio();
        int x = timeSlider.getValue();
        mediaPlayer.setPosition(x * mediaPlayer.getDuration() / 100);
    }

    private void setSlider() {
        long x = mediaPlayer.position() * 100 / mediaPlayer.getDuration();
        //System.out.println(mediaPlayer.position() +" "+mediaPlayer.getDuration());
        timeSlider.setValue((int) x);
    }

    private void pauseAudio() {
        System.out.println("Pause");
        mediaPlayer.pause();
    }

    private void playAudio() {
        System.out.println("Play");
        mediaPlayer.play();
    }

    private void stopAudio() {
        System.out.println("Stop");
        mediaPlayer.pause();
    }
}
