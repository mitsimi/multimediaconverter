package types;

import java.util.Objects;

public enum VideoType implements MediaType {
    //all videotypes to convert
    MP4("mp4"),
    MOV("mov"),
    AVI("avi"),
    WMV("wmv"),
    MKV("mkv"),
    MPG("mpg");

    public final String fileExtension;

    VideoType(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public static VideoType getEnum(String type) {
        for (VideoType value : VideoType.values()) {
            if (Objects.equals(value.fileExtension, type)) return value;
        }
        return null;
    }

    public static boolean contains(String type) {
        for (VideoType value : VideoType.values()) {
            if (Objects.equals(value.fileExtension, type)) return true;
        }
        return false;
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }
}
