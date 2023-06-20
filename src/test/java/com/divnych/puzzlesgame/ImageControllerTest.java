package com.divnych.puzzlesgame;

import com.divnych.puzzlesgame.controller.ImageController;
import com.divnych.puzzlesgame.playload.ImageUrlRequest;
import com.divnych.puzzlesgame.service.ImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
public class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private ImageService imageService;


    @Test
    @DisplayName("Should return a list of images decoded in strings")
    void testSplit() throws Exception {
            // Create a sample ImageUrlRequest
            ImageUrlRequest request = new ImageUrlRequest();
            request.setImageUrl("http://example.com/image.jpg");

            // Create a sample list of strings representing the result
            List<String> expectedResult = Arrays.asList("string1", "string2", "string3");

            // Mock the behavior of the imageService.split() method
            when(imageService.getEncodedImages(any())).thenReturn(expectedResult);

            // Convert the request object to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String requestJson = objectMapper.writeValueAsString(request);

            // Perform the POST request and capture the response
            MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/images/split")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andReturn();

            // Extract the response body
            String responseBody = mvcResult.getResponse().getContentAsString();

            // Convert the response JSON to a list of strings
            List<String> actualResult = objectMapper.readValue(responseBody, List.class);

            // Assert the expected result with the actual result
            assertEquals(expectedResult, actualResult);
    }

}
