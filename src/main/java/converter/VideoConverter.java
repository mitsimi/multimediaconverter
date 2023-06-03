package converter;

import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import types.AudioType;
import types.MediaType;
import types.VideoType;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import ws.schild.jave.encode.VideoAttributes;
import ws.schild.jave.info.AudioInfo;
import ws.schild.jave.info.VideoInfo;
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
    private String pathFrom;
    private String pathTo;
    private VideoType convertFrom = null;
    private VideoAttributes videoAttributes = new VideoAttributes();
    private AudioAttributes audioAttributes = new AudioAttributes();
    private EncodingAttributes encodingAttributes = null;
    private MultimediaObject mmsObject = null;

    //Unsupported meaning they cant be decoded by JAVE
    private String[] unsupportedVideoFormats = {"wmv", "mkv", "mpg"};

    public VideoConverter(Path path) {
        mmsObject = new MultimediaObject(path.toFile());
        setStandardParams();
        convertFrom = VideoType.getEnum(path.toString().substring(path.toString().indexOf('.') + 1).toLowerCase());

        pathFrom = path.toAbsolutePath().toString();
        //Test Settings
        //setVideoAttributes(4000000, 10, 0, new VideoSize(300, 480));

        //setAudioAttributes(...);
    }

    @Override
    public void convert(String to_type) {
        convertTo = VideoType.getEnum(to_type.toLowerCase());
        encodingAttributes = new EncodingAttributes();
        encodingAttributes.setOutputFormat(convertTo.fileExtension);
        encodingAttributes.setAudioAttributes(audioAttributes);
        encodingAttributes.setVideoAttributes(videoAttributes);
    }

    @Override
    public void save(String absolutePath, String fileName) {
        boolean fileTypesEqual = (convertTo == convertFrom);

        String tempFileName = fileName;
        if (fileTypesEqual) {
            tempFileName += fileName.hashCode();
        }
        Path path = Path.of(absolutePath + "/" + tempFileName + "." + convertTo.fileExtension);
        System.out.println(path);
        Encoder encoder = new Encoder();
        try {
            if (!Arrays.stream(unsupportedVideoFormats).toList().contains(convertTo.fileExtension)) {
                encoder.encode(mmsObject, path.toFile(), encodingAttributes);
            } else {
                switch (VideoType.getEnum(convertTo.fileExtension)) {
                    case WMV -> {
                        encodingAttributes.setOutputFormat("asf");
                        encoder.encode(mmsObject, path.toFile(), encodingAttributes);

                        mmsObject.getFile().renameTo(path.toFile());
                    }

                    case MPG -> {
                        encodingAttributes.setOutputFormat("mpeg2video");
                        encoder.encode(mmsObject, path.toFile(), encodingAttributes);
                    }

                    case MKV -> {
                        encodingAttributes.setOutputFormat("matroska");
                        encoder.encode(mmsObject, path.toFile(), encodingAttributes);
                    }
                }

                if (fileTypesEqual) {
                    File hashFile = path.toFile();
                    File destFile = new File(absolutePath + "/" + fileName + "." + convertTo.fileExtension);
                    System.out.println(hashFile.getAbsolutePath() + " " + destFile.getAbsolutePath());
                    destFile.delete();
                    hashFile.renameTo(destFile);
                }


            }
        } catch (EncoderException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the Video-attributes with the given settings.
     *
     * @param bitrate    The bitrate of the Video (bit/s)
     * @param fps        The FPS of the Video
     * @param quality    The quality of the Video (1 - 100)
     * @param resolution The resolution-Object of the Video
     */
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

    private void setStandardParams() {
        try {
            VideoInfo vi = mmsObject.getInfo().getVideo();

            if (vi.getBitRate() != -1) {
                this.videoAttributes.setBitRate(vi.getBitRate());
            } else {
                this.videoAttributes.setBitRate(40000000);
            }

            if (vi.getFrameRate() != -1) {
                this.videoAttributes.setFrameRate((int) vi.getFrameRate());
            } else {
                this.videoAttributes.setBitRate(30);
            }

            if (vi.getSize() != null) {
                this.videoAttributes.setSize(vi.getSize());
            } else {
                this.videoAttributes.setSize(new VideoSize(1920, 1080));
            }


            AudioInfo ai = mmsObject.getInfo().getAudio();

            if (ai.getBitRate() != -1) {
                this.audioAttributes.setBitRate(ai.getBitRate());
            } else {
                this.audioAttributes.setBitRate(256000);
            }

            if (ai.getChannels() != -1) {
                this.audioAttributes.setChannels(ai.getChannels());
            } else {
                this.audioAttributes.setChannels(2);
            }

            if (ai.getSamplingRate() != -1) {
                this.audioAttributes.setSamplingRate(ai.getSamplingRate());
            } else {
                this.audioAttributes.setSamplingRate(48000);
            }
        } catch (EncoderException e) {
            throw new RuntimeException(e);
        }
    }
}
