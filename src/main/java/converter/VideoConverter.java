package converter;

import types.AudioType;
import types.MediaType;
import types.VideoType;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.info.VideoSize;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;

public class VideoConverter implements Converter {
    private VideoType convertTo = null;
    private VideoType convertFrom = null;
    private VideoAttributes videoAttributes = new VideoAttributes();
    private AudioAttributes audioAttributes = new AudioAttributes();
    private EncodingAttributes encodingAttributes = null;
    private MultimediaObject mmsObject = null;

    //Unsupported meaning they cant be decoded by JAVE
    private String[] unsupportedVideoFormats = {"wmv", "mkv", "mpg"};

    public VideoConverter(Path path) {
        mmsObject = new MultimediaObject(path.toFile());
        System.out.println(path.toString());
        convertFrom = VideoType.getEnum(path.toString().substring(path.toString().indexOf('.') + 1).toLowerCase());

        //Test Settings
        setVideoAttributes(192000, 5, 100, new VideoSize(640, 480));
    }

    @Override
    public void convert(String to_type) throws IOException {
        convertTo = VideoType.getEnum(to_type.toLowerCase());
        encodingAttributes = new EncodingAttributes();
        encodingAttributes.setOutputFormat(to_type.toLowerCase());

        encodingAttributes.setAudioAttributes(audioAttributes);
        encodingAttributes.setVideoAttributes(videoAttributes);
    }

    @Override
    public void save(String absolutePath, String fileName) {
        Path path = Path.of(absolutePath + "/" + fileName + "." + convertTo.fileExtension);
        System.out.println(path);
        Encoder encoder = new Encoder();
        try {
            if (!Arrays.stream(unsupportedVideoFormats).toList().contains(convertTo.fileExtension) && convertFrom != convertTo) {
                encoder.encode(mmsObject, path.toFile(), encodingAttributes);
            } else {
                // Creating temp MP4-File with applied settings and changing it to unsupported file-ending
                Path tempPath = Path.of(absolutePath + "/" + fileName + "2.mp4");
                encodingAttributes.setOutputFormat("mp4");

                encoder.encode(mmsObject, tempPath.toFile(), encodingAttributes);

                byte[] fileContent = Files.readAllBytes(tempPath);
                Files.write(path, fileContent, StandardOpenOption.CREATE);
                Files.delete(tempPath);
            }
        } catch (EncoderException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setVideoAttributes(int bitrate, int fps, int quality, VideoSize resolution) {
        this.videoAttributes.setBitRate(bitrate);
        this.videoAttributes.setFrameRate(fps);
        this.videoAttributes.setQuality(quality);
        this.videoAttributes.setSize(resolution);
    }

    public void setAudioAttributes(int bitrate, int channels, int samplingRate) {
        this.audioAttributes.setBitRate(bitrate);
        this.audioAttributes.setChannels(channels);
        this.audioAttributes.setSamplingRate(samplingRate);
    }
}
