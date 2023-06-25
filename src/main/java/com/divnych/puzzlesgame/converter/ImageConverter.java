package com.divnych.puzzlesgame.converter;

import com.divnych.puzzlesgame.exceptions.FailedToWriteImageException;
import org.apache.commons.io.FileUtils;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class ImageConverter {

    public static String convertFiletoString(File file) {
        byte[] fileContent;
        try {
            fileContent = FileUtils.readFileToByteArray(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Base64.getEncoder().encodeToString(fileContent);
    }

    public static List<String> convertFilesToStrings(List<File> files) {
        return files.stream()
                .map(file -> convertFiletoString(file))
                .collect(Collectors.toList());
    }

    public static BufferedImage convertString(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        try {
            return ImageIO.read(new ByteArrayInputStream(decodedBytes));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage convertFileToBufferedImage(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File convertBufferedImageToFile(BufferedImage image, String filePath) throws IOException {
        File outputFile = new File(filePath);
        ImageIO.write(image, "jpg", outputFile);
        return outputFile;
    }

    public static List<File> convertBufferedImagesToFiles(BufferedImage[] bufferedImages, String imagesDirectory) {
        List<File> puzzleFiles = new ArrayList<>();
        for (int i = 0; i < bufferedImages.length; i++) {
            File outputFile = new File(imagesDirectory + "img" + i + ".jpg");
            try {
                ImageIO.write(bufferedImages[i], "jpg", outputFile);
            } catch (IOException e) {
                throw new FailedToWriteImageException("Cannot write puzzle image");
            }
            puzzleFiles.add(outputFile);
        }
        System.out.println("Puzzles have been created.");
        return puzzleFiles;
    }

    public static BufferedImage convertMatToBufferedImage(Mat mat) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] data = new byte[bufferSize];
        mat.get(0, 0, data);

        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);

        return image;
    }



}
