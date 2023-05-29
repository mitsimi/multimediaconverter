package converter;

import java.nio.file.Files;
import java.nio.file.Path;

import types.AudioType;
import types.MediaType;
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
    private AudioAttributes audioAttributes = new AudioAttributes();
    private EncodingAttributes encodingAttributes = null;
    private MultimediaObject mmsObject = null;

    //Unsupported meaning they cant be decoded by JAVE
    private String[] unsupportedAudioFormats = {"aac","ogg","m4a"};

    public AudioConverter(Path path) {
        mmsObject = new MultimediaObject(path.toFile());
    }

    @Override
    public void convert(String to_type) throws IOException {
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
            if (!Arrays.stream(unsupportedAudioFormats).toList().contains(convertTo.fileExtension)) {
                encoder.encode(mmsObject, path.toFile(), encodingAttributes);
            } else {
                // Just changing file-endings
                byte[] fileContent = Files.readAllBytes(mmsObject.getFile().toPath());
                Files.write(path, fileContent, StandardOpenOption.CREATE);
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

    public void setAudioAttributes(AudioAttributes audioAttributes) {
        this.audioAttributes = audioAttributes;
    }
}
