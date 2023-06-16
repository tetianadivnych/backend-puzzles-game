package com.divnych.puzzlesgame;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageSplitter {

    public static List<File> split() throws IOException {
        // Setting Chrome as an agent
        System.setProperty("http.agent", "Chrome");

        // reading the file from a URL
        URL url = new URL("https://i.imgur.com/EfVO4jw.jpeg");
        InputStream is = url.openStream();
        BufferedImage image = ImageIO.read(is);

        // initalizing rows and columns
        int rows = 4;
        int columns = 4;

        // initializing array to hold subimages
        BufferedImage imgs[] = new BufferedImage[16];

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
        //writing sub-images into image files
        for (int i = 0; i < 16; i++) {
            File outputFile = new File("img" + i + ".jpg");
            ImageIO.write(imgs[i], "jpg", outputFile);
            puzzles.add(outputFile);
        }
        System.out.println("Sub-images have been created.");
        return puzzles;
    }

}
