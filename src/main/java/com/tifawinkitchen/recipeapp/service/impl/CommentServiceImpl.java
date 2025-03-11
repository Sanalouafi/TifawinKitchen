package com.tifawinkitchen.recipeapp.service.impl;

import com.tifawinkitchen.recipeapp.dto.CommentDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.Comment;
import com.tifawinkitchen.recipeapp.model.Recipe;
import com.tifawinkitchen.recipeapp.model.User;
import com.tifawinkitchen.recipeapp.repository.CommentRepository;
import com.tifawinkitchen.recipeapp.repository.RecipeRepository;
import com.tifawinkitchen.recipeapp.repository.UserRepository;
import com.tifawinkitchen.recipeapp.service.CommentService;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final MapperUtil mapperUtil;

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByRecipeId(Long recipeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<Comment> comments = commentRepository.findByRecipeId(recipeId, pageable);
        return comments.stream()
                .map(mapperUtil::mapCommentToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Recipe recipe = recipeRepository.findById(commentDto.getRecipeId())
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", commentDto.getRecipeId()));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setRecipe(recipe);
        comment.setText(commentDto.getText());
        comment.setTimestamp(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return mapperUtil.mapCommentToDto(savedComment);
    }

    @Override
    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto commentDto, Long userId) throws ResourceNotFoundException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to update this comment");
        }

        comment.setText(commentDto.getText());
        Comment updatedComment = commentRepository.save(comment);

        return mapperUtil.mapCommentToDto(updatedComment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) throws ResourceNotFoundException {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }
}
