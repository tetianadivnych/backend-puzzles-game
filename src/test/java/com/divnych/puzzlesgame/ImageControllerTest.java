package com.divnych.puzzlesgame;

import com.divnych.puzzlesgame.controller.ImageController;
import com.divnych.puzzlesgame.service.ImageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(ImageController.class)
public class ImageControllerTest {
    
    @MockBean
    private ImageService imageService;

    @Test
    @DisplayName("Should return a list of images decoded in strings")
    void testSplit() throws Exception {

    }
}
