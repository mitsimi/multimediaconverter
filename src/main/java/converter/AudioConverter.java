package converter;

import types.AudioType;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.info.AudioInfo;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;


public class AudioConverter implements Converter {

    private AudioType convertTo = null;
    private AudioType convertFrom = null;
    private Path sourceFile = null;
    private AudioAttributes audioAttributes = new AudioAttributes();
    private EncodingAttributes encodingAttributes = null;
    private MultimediaObject mmsObject = null;

    //Unsupported meaning they cant be encoded by the standard JAVE-Encoder
    private String[] unsupportedAudioFormats = {"aac", "ogg", "m4a"};

    /**
     * Initializes the audio-converter
     *
     * @param path The path-object of the source-file
     */
    public AudioConverter(Path path) {
        mmsObject = new MultimediaObject(path.toFile());
        setStandardParams();

        convertFrom = AudioType.getEnum(path.toString().substring(path.toString().indexOf('.') + 1).toLowerCase());

        sourceFile = path;

        //Test audio settings
        //setAudioAttributes(3000, 1, 44100);
    }

    /**
     * Sets the audio for the encoding
     *
     * @param to_type The type in which the source-file should get encoded in
     */
    @Override
    public void convert(String to_type) {
        convertTo = AudioType.getEnum(to_type.toLowerCase());

        encodingAttributes = new EncodingAttributes();
        encodingAttributes.setOutputFormat(to_type.toLowerCase());
        encodingAttributes.setAudioAttributes(audioAttributes);
    }

    /**
     * Encodes the files and saves them.
     *
     * @param absolutePath The absolute Path of the destination-file
     * @param fileName     The filename of the destination-file (without file-extension)
     */
    @Override
    public void save(String absolutePath, String fileName) {
        Path destinPath = Path.of(absolutePath + fileName + "." + convertTo.fileExtension);
        boolean fileTypesEqual = (sourceFile.toFile().getAbsolutePath().equals(destinPath.toFile().getAbsolutePath()));
        System.out.println(sourceFile.toFile().getAbsolutePath() + "___" + (destinPath.toFile().getAbsolutePath()) + " " + fileTypesEqual);

        String tempFileName = fileName;
        if (fileTypesEqual) {
            // this creates a tempFile with the files hashcode, because the source-file cannot be overwritten while encoding
            tempFileName += fileName.hashCode();
        }
        Path path = Path.of(absolutePath + "/" + tempFileName + "." + convertTo.fileExtension);
        Encoder encoder = new Encoder();
        try {
            if (!Arrays.stream(unsupportedAudioFormats).toList().contains(convertTo.fileExtension)) {
                encoder.encode(mmsObject, path.toFile(), encodingAttributes);
            } else {
                switch (AudioType.getEnum(convertTo.fileExtension)) {
                    case AAC -> {
                        encodingAttributes.setOutputFormat("adts");
                        encoder.encode(mmsObject, path.toFile(), encodingAttributes);
                    }

                    case OGG -> {
                        encodingAttributes.setOutputFormat("ogg");
                        encoder.encode(mmsObject, path.toFile(), encodingAttributes);
                    }

                    case M4A -> {
                        encodingAttributes.setOutputFormat("mp4");
                        encoder.encode(mmsObject, path.toFile(), encodingAttributes);
                    }
                }
            }
            if (fileTypesEqual) {
                File hashFile = path.toFile();
                File destFile = destinPath.toFile();
                System.out.println(hashFile.getAbsolutePath() + " " + destFile.getAbsolutePath());
                destFile.delete();
                hashFile.renameTo(destFile);
            }

        } catch (EncoderException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the audio-attributes with the given settings.
     *
     * @param bitrate      The bitrate of the Audio (bit/s)
     * @param channels     The channels of the Audio (1=Mono, 2=Stereo)
     * @param samplingRate The sampling-rate of the Audio (Hz)
     */
    public void setAudioAttributes(int bitrate, int channels, int samplingRate) {
        this.audioAttributes.setBitRate(bitrate);
        this.audioAttributes.setChannels(channels);
        this.audioAttributes.setSamplingRate(samplingRate);
    }

    // Sets the encoding parameters to the same as the source-file if they are readable
    private void setStandardParams() {
        try {
            AudioInfo ai = mmsObject.getInfo().getAudio();

            if (ai.getBitRate() != -1) {
                audioAttributes.setBitRate(ai.getBitRate());
            } else {
                audioAttributes.setBitRate(300000);
            }

            if (ai.getChannels() != -1) {
                audioAttributes.setChannels(ai.getChannels());
            } else {
                audioAttributes.setChannels(2);
            }

            if (ai.getSamplingRate() != -1) {
                audioAttributes.setSamplingRate(ai.getSamplingRate());
            } else {
                audioAttributes.setSamplingRate(44100);
            }
        } catch (EncoderException e) {
            e.printStackTrace();
        }
    }
}
