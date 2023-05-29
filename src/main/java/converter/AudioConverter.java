package converter;

import java.nio.file.Files;
import java.nio.file.Path;

import types.AudioType;
import types.MediaType;
import types.VideoType;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;


public class AudioConverter implements Converter {

    private AudioType convertTo = null;
    private AudioType convertFrom = null;
    private AudioAttributes audioAttributes = new AudioAttributes();
    private EncodingAttributes encodingAttributes = null;
    private MultimediaObject mmsObject = null;

    //Unsupported meaning they cant be decoded by JAVE
    private String[] unsupportedAudioFormats = {"aac", "ogg", "m4a"};

    public AudioConverter(Path path) {
        mmsObject = new MultimediaObject(path.toFile());
        convertFrom = AudioType.getEnum(path.toString().substring(path.toString().indexOf('.') + 1).toLowerCase());

        //Test audio settings
        setAudioAttributes(3000, 1, 44100);
    }

    @Override
    public void convert(String to_type) {
        convertTo = AudioType.getEnum(to_type.toLowerCase());

        encodingAttributes = new EncodingAttributes();
        encodingAttributes.setOutputFormat(to_type.toLowerCase());
        encodingAttributes.setAudioAttributes(audioAttributes);
    }

    @Override
    public void save(String absolutePath, String fileName) {
        Path path = Path.of(absolutePath + "/" + fileName + "." + convertTo.fileExtension);
        System.out.println(path);
        Encoder encoder = new Encoder();
        try {
            if (!Arrays.stream(unsupportedAudioFormats).toList().contains(convertTo.fileExtension) && convertFrom != convertTo) {
                encoder.encode(mmsObject, path.toFile(), encodingAttributes);
            } else {
                // Creating temp MP3-File with applied settings and changing it to unsupported file-ending
                Path tempPath = Path.of(absolutePath + "/" + fileName + "2.mp3");
                encodingAttributes.setOutputFormat("mp3");

                encoder.encode(mmsObject, tempPath.toFile(), encodingAttributes);

                byte[] fileContent = Files.readAllBytes(tempPath);
                Files.write(path, fileContent, StandardOpenOption.CREATE);
                Files.delete(tempPath);
            }
        } catch (EncoderException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setAudioAttributes(int bitrate, int channels, int samplingRate) {
        this.audioAttributes.setBitRate(bitrate);
        this.audioAttributes.setChannels(channels);
        this.audioAttributes.setSamplingRate(samplingRate);
    }
}
