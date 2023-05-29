package converter;

import types.AudioType;
import types.MediaType;
import types.VideoType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;

public class VideoConverter implements Converter {

    private byte[] fileData = null;
    VideoType convertTo = null;

    public VideoConverter(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            fileData = fis.readAllBytes();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void convert(String to_type) throws IOException {
        this.convertTo = VideoType.getEnum(to_type.toLowerCase());
    }

    @Override
    public void save(String absolutePath, String fileName) {
        System.out.println(absolutePath + "/" + fileName + "." + convertTo.fileExtension);
        try (FileOutputStream fos = new FileOutputStream(absolutePath + "/" + fileName + "." + convertTo.fileExtension)) {
            fos.write(fileData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
