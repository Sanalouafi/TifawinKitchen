package com.tifawinkitchen.recipeapp.controller;

import com.tifawinkitchen.recipeapp.dto.RatingDto;
import com.tifawinkitchen.recipeapp.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @GetMapping("/recipe/{recipeId}/user/{userId}")
    public ResponseEntity<RatingDto> getUserRatingForRecipe(
            @PathVariable Long recipeId,
            @PathVariable Long userId) {
        RatingDto ratingDto = ratingService.getUserRatingForRecipe(recipeId, userId);
        return ResponseEntity.ok(ratingDto);
    }

    @PostMapping
    public ResponseEntity<RatingDto> rateRecipe(@RequestBody RatingDto ratingDto, @RequestParam Long userId) {
        RatingDto savedRating = ratingService.rateRecipe(ratingDto, userId);
        return ResponseEntity.ok(savedRating);
    }

    @DeleteMapping("/recipe/{recipeId}/user/{userId}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable Long recipeId,
            @PathVariable Long userId) {
        ratingService.deleteRating(recipeId, userId);
        return ResponseEntity.noContent().build();
    }
}