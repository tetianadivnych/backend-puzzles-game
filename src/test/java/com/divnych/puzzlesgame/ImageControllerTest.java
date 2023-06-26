package com.divnych.puzzlesgame;

import com.divnych.puzzlesgame.controller.ImageController;
import com.divnych.puzzlesgame.playload.ImageUrlRequest;
import com.divnych.puzzlesgame.service.ImageService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.io.UnsupportedEncodingException;
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
    void testSplit() {
        ImageUrlRequest request = new ImageUrlRequest();
        request.setImageUrl("https://i.imgur.com/EfVO4jw.jpeg");
        List<String> expectedResult = Arrays.asList("string1", "string2", "string3");
        when(imageService.getEncodedImages(any())).thenReturn(expectedResult);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = encodeImagesToStrings(request, objectMapper);
        MvcResult mvcResult = createImageFiles(requestJson);
        String responseBody = convertImageFilesToStrings(mvcResult);
        List<String> actualResult = readStrings(objectMapper, responseBody);
        assertEquals(expectedResult, actualResult);
    }

    private String encodeImagesToStrings(ImageUrlRequest request, ObjectMapper objectMapper) {
        String requestJson;
        try {
            requestJson = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return requestJson;
    }

    private MvcResult createImageFiles(String requestJson) {
        MvcResult mvcResult;
        try {
            mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/images/split")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return mvcResult;
    }

    private String convertImageFilesToStrings(MvcResult mvcResult) {
        String responseBody;
        try {
            responseBody = mvcResult.getResponse().getContentAsString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return responseBody;
    }

    private List<String> readStrings(ObjectMapper objectMapper, String responseBody) {
        List<String> actualResult;
        try {
            actualResult = objectMapper.readValue(responseBody, List.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return actualResult;
    }

}
