package converter;

import types.MediaType;

import java.io.IOException;

public interface Converter {
    public void convert(String to_type) throws IOException;

    public void save(String absolutePath, String fileName) throws IOException;
}
