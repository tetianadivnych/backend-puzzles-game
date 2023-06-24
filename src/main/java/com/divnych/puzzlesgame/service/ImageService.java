package com.divnych.puzzlesgame.service;

import com.divnych.puzzlesgame.converter.ImageConverter;
import com.divnych.puzzlesgame.exceptions.*;
import com.divnych.puzzlesgame.playload.ImageUrlRequest;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.boot.CommandLineRunner;
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
import java.util.stream.Collectors;

@Service
public class ImageService implements CommandLineRunner {

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
        int rows = 2;
        int columns = 2;
        BufferedImage[] puzzleImages = new BufferedImage[4];
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
        String sourcePuzzlesDirectoryPath = "./puzzles/";
        createDirectory(sourcePuzzlesDirectoryPath);
        return ImageConverter.convertBufferedImagesToFiles(puzzleImages, sourcePuzzlesDirectoryPath);
    }

    private static void createDirectory(String path) {
        Path directoryPath = Paths.get(path);
        if (!Files.exists(directoryPath)) {
            try {
                Files.createDirectories(directoryPath);
            } catch (IOException e) {
                throw new FailedToCreateDirectoryException("Cannot create directory" + path);
            }
        }
    }

    public boolean verify(List<String> puzzles) {
        File[] files = new File("./puzzles").listFiles();
        File[] sortedArray = Arrays.asList(files)
                .stream().sorted(Comparator.comparing(file -> {
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
        BufferedImage bufferedFile = ImageConverter.convertFileToBufferedImage(file);
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

    /*
    *
    * Core method
    *
    * */
/*
    public void assemblePuzzles() throws MalformedURLException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        //accept list of files
        URL imageUrl = new URL("https://i.imgur.com/EfVO4jw.jpeg");
        List<File> fileList = split(imageUrl);
        File[] puzzleFiles = fileList.toArray(new File[0]);
        try {

            // Step 0. Read images by OpenCV
            List<Mat> imagePieces = loadAndPreprocessImages(puzzleFiles);

            // Step 1. Determine the sequence of image pieces based on edge matching
            List<Mat> assembledPieces = assembleImagePieces(imagePieces);

            // Merge the image pieces
            Mat assembledImage = mergeImagePieces(assembledPieces);

            // Save the assembled image
            String outputImagePath = "./assembled";
            Imgcodecs.imwrite(outputImagePath, assembledImage);

            System.out.println("Image assembled successfully!");
        } catch (Exception e) {
            System.out.println("Error occurred during image assembly: " + e.getMessage());
        }
    }*/

    /*
    *
    *
    * Test of Step 1. Determine the sequence of image pieces based on edge matching.
    * Expected: the sequence of mat files should match the sequence of the source image pieces.
    *
    * */


    public List<BufferedImage> getPiecesOrderedByOpenCV() throws MalformedURLException {

        String nativeLibrariesPath = "native-libraries/";
        System.setProperty("java.library.path", nativeLibrariesPath);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        URL imageUrl = new URL("https://i.imgur.com/EfVO4jw.jpeg");
        List<File> fileList = split(imageUrl);
        File[] puzzleFiles = fileList.toArray(new File[0]);
        List<Mat> assembledPieces = new ArrayList<>();
        try {
            List<Mat> imagePieces = loadAndPreprocessImages(puzzleFiles);
            assembledPieces = assembleImagePieces(imagePieces);
        } catch (Exception e) {
            System.out.println("Error occurred during image assembly: " + e.getMessage());
        }
        // convert list of Mat into list of BufferedImages
        List<BufferedImage> bufferedImages = assembledPieces.stream()
                .map(mat -> ImageConverter.convertMatToBufferedImage(mat))
                .collect(Collectors.toList());
        System.out.println("The list of buffered images ordered by OpenCV have been created");
        return bufferedImages;
    }

    public void saveAssembledImagesToDirectory(List<BufferedImage> bufferedImages) {
        String path = "./assembled/";
        createDirectory(path);
        BufferedImage[] array = bufferedImages.toArray(new BufferedImage[0]);
        ImageConverter.convertBufferedImagesToFiles(array, path);
        System.out.println("the list of image files have been created");
    }



/*    Step 0. Read images by OpenCV */


    private static List<Mat> loadAndPreprocessImages(File[] puzzleFiles) {
        List<Mat> imagePieces = new ArrayList<>();
        if (puzzleFiles != null) {
            for (File puzzleFile : puzzleFiles) {
                if (puzzleFile.isFile() && puzzleFile.getName().endsWith(".jpg")) {
                    Mat image = Imgcodecs.imread(puzzleFile.getAbsolutePath());
                    // Perform any necessary preprocessing on the image (e.g., resize, grayscale, etc.)
                    // ...
                    imagePieces.add(image);
                }
            }
        }

        return imagePieces;
    }

    /*
    *
    *
    * Step 1. Determine the sequence of image pieces based on edge matching.
    *
    *
    * */

    private static List<Mat> assembleImagePieces(List<Mat> imagePieces) {
        List<Mat> assembledPieces = new ArrayList<>();

        // Start with the first image piece
        Mat firstPiece = imagePieces.get(0);
        assembledPieces.add(firstPiece);
        imagePieces.remove(0);

        while (!imagePieces.isEmpty()) {
            Mat previousPiece = assembledPieces.get(assembledPieces.size() - 1);
            Mat bestMatch = null;
            double bestMatchScore = Double.MAX_VALUE;

            // Iterate over the remaining image pieces to find the best matching piece
            for (Mat currentPiece : imagePieces) {
                double score = calculateMatchingScore(previousPiece, currentPiece);

                if (score < bestMatchScore) {
                    bestMatchScore = score;
                    bestMatch = currentPiece;
                }
            }

            if (bestMatch != null) {
                assembledPieces.add(bestMatch);
                imagePieces.remove(bestMatch);
            } else {
                // If no match is found, break the loop
                break;
            }
        }

        return assembledPieces;
    }

    private static double calculateMatchingScore(Mat image1, Mat image2) {
        // Convert images to grayscale
        Mat grayImage1 = new Mat();
        Mat grayImage2 = new Mat();
        Imgproc.cvtColor(image1, grayImage1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(image2, grayImage2, Imgproc.COLOR_BGR2GRAY);

        // Apply Canny edge detection
        Mat edges1 = new Mat();
        Mat edges2 = new Mat();
        Imgproc.Canny(grayImage1, edges1, 50, 150);
        Imgproc.Canny(grayImage2, edges2, 50, 150);

        // Calculate the SSD (Sum of Squared Differences) score

        return calculateSSDScore(edges1, edges2);
    }

    private static double calculateSSDScore(Mat image1, Mat image2) {
        int totalPixels = image1.rows() * image1.cols();

        double sumSquaredDiff = 0;
        for (int row = 0; row < image1.rows(); row++) {
            for (int col = 0; col < image1.cols(); col++) {
                double diff = image1.get(row, col)[0] - image2.get(row, col)[0];
                sumSquaredDiff += Math.pow(diff, 2);
            }
        }

        return sumSquaredDiff / totalPixels;
    }
/*
*
* Final Step.
*
* */

    private static Mat mergeImagePieces(List<Mat> assembledPieces) {
        // Implement the image merging logic here
        // ...

        // Placeholder code: Just concatenate the image pieces horizontally
        Mat assembledImage = new Mat();
        Core.hconcat(assembledPieces, assembledImage);

        return assembledImage;
    }


    @Override
    public void run(String... args) throws Exception {
        List<BufferedImage> piecesOrderedByOpenCV = getPiecesOrderedByOpenCV();
        saveAssembledImagesToDirectory(piecesOrderedByOpenCV);
/*        URL url = new URL("https://i.imgur.com/EfVO4jw.jpeg");
        List<File> fileList = split(url);*/
    }
}
