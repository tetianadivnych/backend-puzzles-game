package com.divnych.puzzlesgame.service;

import com.divnych.puzzlesgame.converter.ImageConverter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ImageService {

    public List<File> split(URL imageUrl) throws IOException {
        // Setting Chrome as an agent
        System.setProperty("http.agent", "Chrome");

        // reading the file from a URL
        InputStream is = imageUrl.openStream();
        BufferedImage image = ImageIO.read(is);

        // initalizing rows and columns
        int rows = 2;
        int columns = 2;

        // initializing array to hold subimages
        BufferedImage[] imgs = new BufferedImage[16];

        // Equally dividing original image into subimages
        int subimageWidth = image.getWidth() / columns;
        int subimageHeight = image.getHeight() / rows;

        int current_img = 0;

        // iterating over rows and columns for each sub-image
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // Creating sub image
                imgs[current_img] = new BufferedImage(subimageWidth, subimageHeight, image.getType());
                Graphics2D img_creator = imgs[current_img].createGraphics();

                // coordinates of source image
                int src_first_x = subimageWidth * j;
                int src_first_y = subimageHeight * i;

                // coordinates of sub-image
                int dst_corner_x = subimageWidth * j + subimageWidth;
                int dst_corner_y = subimageHeight * i + subimageHeight;

                img_creator.drawImage(image, 0, 0, subimageWidth, subimageHeight, src_first_x, src_first_y, dst_corner_x, dst_corner_y, null);
                current_img++;
            }
        }

        List<File> puzzles = new ArrayList<>();

        //create directory to store files
        String outputDirectory = "./puzzles/"; // Relative path to the subimages folder

        Path directoryPath = Paths.get(outputDirectory);
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        //writing sub-images into image files
        for (int i = 0; i < 4; i++) {
            File outputFile = new File(outputDirectory + "img" + i + ".jpg");
            ImageIO.write(imgs[i], "jpg", outputFile);
            puzzles.add(outputFile);
        }
        System.out.println("Sub-images have been created.");
        return puzzles;
    }

    public boolean verify(List<String> puzzles) {
        File[] files = new File("./puzzles").listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String stringPuzzle = puzzles.get(i);
            boolean equal = isEqual(stringPuzzle, file);
            if (!equal) {
                return false;
            }
        }
        return true;
    }

    private boolean isEqual(String stringPuzzle, File file) {
        //convert String into BufferedImage
        BufferedImage bufferedPuzzle = ImageConverter.convertString(stringPuzzle);

        //convert File into BufferedImage
        BufferedImage bufferedFile = ImageConverter.convertFileToImage(file);

        //get pixels of BufferedFile
        byte[] filePixels = getPixels(bufferedFile);
        byte[] puzzlePixels = getPixels(bufferedPuzzle);
        return Arrays.equals(filePixels, puzzlePixels);
    }

    private byte[] getPixels(BufferedImage bufferedFile) {
        return ((DataBufferByte) bufferedFile.getRaster().getDataBuffer()).getData();
    }


}
