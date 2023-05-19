package types;

import java.util.Objects;

//https://guides.lib.umich.edu/c.php?g=282942&p=1885348
public enum ImageType implements MediaType {
    JPEG("jpeg"),
    JPG("jpg"),
    PNG("png"),
    APNG("apng"),
    TIFF("tiff"),
    TIF("tif"),
    BMP("bmp"),
    WBMP("wbmp"),
    RAW("raw"),
    //    CR2("cr2"),
//    NEF("nef"),
//    ORF("orf"),
//    SR2("sr2"),
//    WEBP("webp"),
    ICO("ico");

    public final String fileExtension;

    ImageType(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public static ImageType getEnum(String type) {
        for (ImageType value : ImageType.values()) {
            if (Objects.equals(value.fileExtension, type)) return value;
        }
        return null;
    }

    public static boolean contains(String type) {
        for (ImageType value : ImageType.values()) {
            if (Objects.equals(value.fileExtension, type)) return true;
        }
        return false;
    }

    @Override
    public String getFileExtension() {
        return fileExtension;
    }
}
