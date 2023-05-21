package types;

import java.util.Objects;

public enum AudioType implements MediaType {
    MP3("mp3"),
    M4A("m4a"),
    AAC("aac"),
    FLAC("flac"),
    WAV("wav"),
    AIFF("aiff"),
    OGG("ogg");

    public final String fileExtension;

    AudioType(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public static AudioType getEnum(String type) {
        for (AudioType value : AudioType.values()) {
            if (Objects.equals(value.fileExtension, type)) return value;
        }
        return null;
    }

    public static boolean contains(String type) {
        for (AudioType value : AudioType.values()) {
            if (Objects.equals(value.fileExtension, type)) return true;
        }
        return false;
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }
}

