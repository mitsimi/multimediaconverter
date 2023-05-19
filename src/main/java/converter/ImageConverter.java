package converter;

import types.ImageType;
import types.MediaType;
import types.UnsupportedFileTypeException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;

public class ImageConverter implements Converter {
    private final BufferedImage image;
    private ImageType type;

    public ImageConverter(File file) throws IOException {
        image = ImageIO.read(file);
    }

    @Override
    public void convert(String to_type) throws IOException {
        type = ImageType.valueOf(to_type);
        switch (type) {
            case JPEG, JPG, PNG, BMP, WBMP -> {
//                return convertFormat(to_type.getFileExtension());
            }
            default -> {
                throw new UnsupportedFileTypeException("Unsupported file type");
            }
        }
    }

    @Override
    public void save(String absolutePath, String fileName) throws IOException {
        // TODO - prep parameters before using it for stream creation
        // absolute Path: remove or add / at the end
        // fileName: convert not allowed letters
        FileOutputStream outputStream = new FileOutputStream(
                absolutePath + "/" + fileName + "." + type.getFileExtension()
        );
        ImageIO.write(image, type.getFileExtension(), outputStream);
    }

    public static void convertFormat(String formatName) throws IOException {
        // reads input image from file
    }
}
