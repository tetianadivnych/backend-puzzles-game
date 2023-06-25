package com.divnych.puzzlesgame;

import com.divnych.puzzlesgame.service.ImageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.net.URL;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

    @InjectMocks
    ImageService imageService;

    @Test
    @DisplayName("Should split image and return puzzles")
    void testSplit() throws Exception {
        URL imageUrl = new URL("https://i.imgur.com/EfVO4jw.jpeg");
        List<File> puzzles = imageService.split(imageUrl);
        assertEquals(puzzles.size(), puzzles.size());
        for (File file : puzzles) {
            Assertions.assertTrue(file.exists());
            Assertions.assertTrue(file.isFile());
            Assertions.assertEquals(".jpg", getFileExtension(file));
            Assertions.assertEquals("puzzles", file.getParentFile().getName());
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf(".");
        if (lastDotIndex != -1) {
            return name.substring(lastDotIndex);
        }
        return "";
    }

}
