package com.divnych.puzzlesgame.controller;

import com.divnych.puzzlesgame.playload.ImageUrlRequest;
import com.divnych.puzzlesgame.service.ImageService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/split")
    public List<String> split(@RequestBody ImageUrlRequest request) throws IOException {
        return imageService.getEncodedImages(request);
    }

    @PostMapping("/verify")
    public boolean verify(@RequestBody List<String> puzzles) {
        return imageService.verify(puzzles);
    }

}


