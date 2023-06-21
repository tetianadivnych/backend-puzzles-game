package com.divnych.puzzlesgame.converter;

import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
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

    public static BufferedImage convertFileToImage(File file) {
        try {
            return ImageIO.read(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
