package types;


import java.io.IOException;

public class UnsupportedFileTypeException extends IOException {
    private final String fileType;

    public UnsupportedFileTypeException() {
        super();
        fileType = "unknown";
    }

    public UnsupportedFileTypeException(String message) {
        super(message);
        fileType = "unknown";
    }

    public UnsupportedFileTypeException(String message, String usedFileType) {
        super(message);
        fileType = usedFileType;
    }

    public String fileType() {
        return fileType;
    }
}
