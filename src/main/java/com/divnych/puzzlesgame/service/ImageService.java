package com.divnych.puzzlesgame.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
        int rows = 4;
        int columns = 4;

        // initializing array to hold subimages
        BufferedImage[] imgs = new BufferedImage[16];

        // Equally dividing original image into subimages
        int subimage_Width = image.getWidth() / columns;
        int subimage_Height = image.getHeight() / rows;

        int current_img = 0;

        // iterating over rows and columns for each sub-image
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                // Creating sub image
                imgs[current_img] = new BufferedImage(subimage_Width, subimage_Height, image.getType());
                Graphics2D img_creator = imgs[current_img].createGraphics();

                // coordinates of source image
                int src_first_x = subimage_Width * j;
                int src_first_y = subimage_Height * i;

                // coordinates of sub-image
                int dst_corner_x = subimage_Width * j + subimage_Width;
                int dst_corner_y = subimage_Height * i + subimage_Height;

                img_creator.drawImage(image, 0, 0, subimage_Width, subimage_Height, src_first_x, src_first_y, dst_corner_x, dst_corner_y, null);
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
        for (int i = 0; i < 16; i++) {
            File outputFile = new File(outputDirectory + "img" + i + ".jpg");
            ImageIO.write(imgs[i], "jpg", outputFile);
            puzzles.add(outputFile);
        }
        System.out.println("Sub-images have been created.");
        return puzzles;
    }

}
