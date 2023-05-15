package picture;

import java.awt.image.BufferedImage;

public interface PictureConverter {

    public BufferedImage convertJPEG(BufferedImage image);

    public BufferedImage convertPNG(BufferedImage image);

    public BufferedImage convertGIF(BufferedImage image);

    public BufferedImage convertBMP(BufferedImage image);

    public BufferedImage convertTIFF(BufferedImage image);
}
