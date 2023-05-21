package types;

import java.util.ArrayList;
import java.util.List;

//https://guides.lib.umich.edu/c.php?g=282942&p=1885348
public enum ImageType implements MediaType {
    JPEG("jpg", "jpeg"),
    PNG("png"),
    TIFF("tiff", "tif"),
    BMP("bmp");

    public final ArrayList<String> fileExtension = new ArrayList<>();

    ImageType(String ...extension) {
        fileExtension.addAll(List.of(extension));
    }

    public static ImageType getEnum(String type) {
        for (ImageType value : ImageType.values()) {
            if (value.fileExtension.contains(type)) return value;
        }
        return null;
    }

    public static boolean contains(String type) {
        for (ImageType value : ImageType.values()) {
            if (value.fileExtension.contains(type)) return true;
        }
        return false;
    }

    @Override
    public String getFileExtension() {
        return fileExtension.get(0);
    }
}
