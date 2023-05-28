package converter;

import java.nio.file.Path;

import types.AudioType;
import types.MediaType;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;


public class AudioConverter implements Converter {
    
    private byte[] fileData = null;
    AudioType convertTo = null;


    public AudioConverter(Path path) {
        try (FileInputStream fis = new FileInputStream(path.toFile())) {
            fileData = fis.readAllBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void convert(String to_type) throws IOException {
        convertTo = AudioType.getEnum(to_type.toLowerCase());
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
