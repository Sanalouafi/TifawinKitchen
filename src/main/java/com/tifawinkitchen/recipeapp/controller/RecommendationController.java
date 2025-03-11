package com.tifawinkitchen.recipeapp.controller;

import com.tifawinkitchen.recipeapp.dto.RecommendationDto;
import com.tifawinkitchen.recipeapp.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecommendationDto>> getRecommendationsForUser(@PathVariable Long userId) {
        List<RecommendationDto> recommendations = recommendationService.getRecommendationsForUser(userId);
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/generate/user/{userId}")
    public ResponseEntity<Void> generateRecommendations(@PathVariable Long userId) {
        recommendationService.generateRecommendations(userId);
        return ResponseEntity.noContent().build();
    }
}