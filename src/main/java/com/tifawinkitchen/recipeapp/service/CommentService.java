package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.CommentDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;

import java.util.List;

public interface CommentService {
    List<CommentDto> getCommentsByRecipeId(Long recipeId, int page, int size);
    CommentDto createComment(CommentDto commentDto, Long userId) throws ResourceNotFoundException;
    CommentDto updateComment(Long commentId, CommentDto commentDto, Long userId) throws ResourceNotFoundException;
    void deleteComment(Long commentId, Long userId) throws ResourceNotFoundException;
}