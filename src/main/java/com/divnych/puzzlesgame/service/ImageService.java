package com.divnych.puzzlesgame.service;

import com.divnych.puzzlesgame.converter.ImageConverter;
import com.divnych.puzzlesgame.exceptions.*;
import com.divnych.puzzlesgame.playload.ImageUrlRequest;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ImageService {

    public List<File> split(URL imageUrl) {
        System.setProperty("http.agent", "Chrome");
        InputStream inputStream;
        try {
            inputStream = imageUrl.openStream();
        } catch (IOException e) {
            throw new FailedToOpenStreamException("Cannot open stream for URL" + imageUrl);
        }
        BufferedImage inputImage;
        try {
            inputImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new FailedToReadImageException("Cannot read input image");
        }
        int rows = 4;
        int columns = 4;
        BufferedImage[] puzzleImages = new BufferedImage[16];
        int width = inputImage.getWidth() / columns;
        int height = inputImage.getHeight() / rows;
        int currentImage = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                puzzleImages[currentImage] = new BufferedImage(width, height, inputImage.getType());
                Graphics2D img_creator = puzzleImages[currentImage].createGraphics();
                int src_first_x = width * j;
                int src_first_y = height * i;
                int dst_corner_x = width * j + width;
                int dst_corner_y = height * i + height;
                img_creator.drawImage(inputImage, 0, 0, width, height, src_first_x, src_first_y, dst_corner_x, dst_corner_y, null);
                currentImage++;
            }
        }
        List<File> puzzleFiles = new ArrayList<>();
        String sourcePuzzles = "./puzzles/";
        Path directoryPath = Paths.get(sourcePuzzles);
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new FailedToCreateDirectoryException("Cannot create directory" + sourcePuzzles);
            }
        }
        for (int i = 0; i < 16; i++) {
            File outputFile = new File(sourcePuzzles + "img" + i + ".jpg");
            try {
                ImageIO.write(puzzleImages[i], "jpg", outputFile);
            } catch (IOException e) {
                throw new FailedToWriteImageException("Cannot write puzzle image");
            }
            puzzleFiles.add(outputFile);
        }
        System.out.println("Puzzles have been created.");
        return puzzleFiles;
    }

    public boolean verify(List<String> puzzles) {
        File[] files = new File("./puzzles").listFiles();
        File[] sortedArray = Arrays.asList(files).stream()
                .sorted(Comparator.comparing(file -> {
                    Pattern pattern = Pattern.compile("\\d+");
                    Matcher matcher = pattern.matcher(file.getName());
                    return matcher.find() ? Integer.parseInt(matcher.group()) : -1;
                })).toArray(File[]::new);
        for (int i = 0; i < sortedArray.length; i++) {
            File file = sortedArray[i];
            String stringPuzzle = puzzles.get(i);
            boolean equal = isEqual(stringPuzzle, file);
            if (!equal) {
                return false;
            }
        }
        return true;
    }

    private boolean isEqual(String stringPuzzle, File file) {
        BufferedImage bufferedPuzzle = ImageConverter.convertString(stringPuzzle);
        BufferedImage bufferedFile = ImageConverter.convertFileToImage(file);
        byte[] filePixels = getPixels(bufferedFile);
        byte[] puzzlePixels = getPixels(bufferedPuzzle);
        return Arrays.equals(filePixels, puzzlePixels);
    }

    private byte[] getPixels(BufferedImage bufferedFile) {
        return ((DataBufferByte) bufferedFile.getRaster().getDataBuffer()).getData();
    }


    public List<String> getEncodedImages(ImageUrlRequest request) {
        String stringUrl = request.getImageUrl();
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            throw new InvalidImageUrlException("Input image Url is not valid");
        }
        List<File> imageFiles = split(url);
        return ImageConverter.convertFilesToStrings(imageFiles);
    }
}
