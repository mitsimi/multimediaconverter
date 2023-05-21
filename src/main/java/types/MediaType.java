package types;

import java.util.Objects;

public interface MediaType {
    public final String fileExtension = null;

    public static MediaType getEnum(String type) {
        return null;
    }


    public static boolean contains(String type) {
        return false;
    }

    public String getFileExtension();
}

