package converter;

import types.ImageType;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;

public class ImageConverter implements Converter {
    private final BufferedImage image;
    private ImageType type;

    /**
     * @param path to original image
     * @throws IOException
     */
    public ImageConverter(Path path) throws IOException {
        image = ImageIO.read(path.toFile());
    }

    /**
     * @param to_type type to which the image have to be covnerted
     */
    @Override
    public void convert(String to_type) {
        type = ImageType.valueOf(to_type);
    }

    /**
     * Saves the converted image into the specified path with specified file name.
     * @param absolutePath where the new image should be saved
     * @param fileName naming of the file WITHOUT extension
     * @throws IOException
     */
    @Override
    public void save(String absolutePath, String fileName) throws IOException {
        Path path = getValidPath(Path.of(absolutePath, fileName + "." + type.getFileExtension()));

        BufferedImage outputImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
        outputImage.createGraphics().drawImage(image, 0, 0, Color.WHITE, null);

        ImageIO.write(outputImage, type.getFileExtension(), path.toFile());
    }

    /**
     * Returns the valid path for the image.
     * If the file name already exists, _x will be attached. x stands for a number.
     *
     * @param path for the image which should be saved
     * @return valid path for the image
     */
    private static Path getValidPath(Path path) {
        int x = 1;
        while (path.toFile().exists()) {
            path = Path.of(path.toFile().getParent() + "/" + path.toFile().getName().replace(".", "_" + x + "."));
            x++;
        }

        return path;
    }
}
