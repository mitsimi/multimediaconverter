package converter;

import types.ImageType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;

public class ImageConverter implements Converter {
    private final BufferedImage image;
    private ImageType type;

    public ImageConverter(Path path) throws IOException {
        image = ImageIO.read(path.toFile());
    }

    @Override
    public void convert(String to_type) {
        type = ImageType.valueOf(to_type);
    }

    @Override
    public void save(String absolutePath, String fileName) throws IOException {
        Path path = Path.of(absolutePath, fileName + "." + type.getFileExtension());
        FileOutputStream outputStream = new FileOutputStream(path.toFile());
        ImageIO.write(image, type.getFileExtension(), outputStream);

        outputStream.close();
    }
}
