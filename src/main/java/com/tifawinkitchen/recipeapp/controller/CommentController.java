package com.tifawinkitchen.recipeapp.controller;

import com.tifawinkitchen.recipeapp.dto.CommentDto;
import com.tifawinkitchen.recipeapp.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/recipe/{recipeId}")
    public ResponseEntity<List<CommentDto>> getCommentsByRecipeId(
            @PathVariable Long recipeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<CommentDto> comments = commentService.getCommentsByRecipeId(recipeId, page, size);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto, @RequestParam Long userId) {
        CommentDto createdComment = commentService.createComment(commentDto, userId);
        return ResponseEntity.ok(createdComment);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentDto commentDto,
            @RequestParam Long userId) {
        CommentDto updatedComment = commentService.updateComment(commentId, commentDto, userId);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @RequestParam Long userId) {
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}