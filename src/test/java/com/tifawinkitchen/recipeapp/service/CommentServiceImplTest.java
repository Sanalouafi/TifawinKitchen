package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.CommentDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.Comment;
import com.tifawinkitchen.recipeapp.model.Recipe;
import com.tifawinkitchen.recipeapp.model.User;
import com.tifawinkitchen.recipeapp.repository.CommentRepository;
import com.tifawinkitchen.recipeapp.repository.RecipeRepository;
import com.tifawinkitchen.recipeapp.repository.UserRepository;
import com.tifawinkitchen.recipeapp.service.impl.CommentServiceImpl;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private MapperUtil mapperUtil;

    @InjectMocks
    private CommentServiceImpl commentService;

    private CommentDto commentDto;
    private Comment comment;
    private User user;
    private Recipe recipe;

    @BeforeEach
    void setUp() {
        commentDto = new CommentDto();
        commentDto.setRecipeId(1L);
        commentDto.setText("Test comment");

        user = new User();
        user.setId(1L);

        recipe = new Recipe();
        recipe.setId(1L);

        comment = new Comment();
        comment.setId(1L);
        comment.setUser(user);
        comment.setRecipe(recipe);
        comment.setText("Test comment");
        comment.setTimestamp(LocalDateTime.now());
    }

    @Test
    void getCommentsByRecipeId_Success() {
        Page<Comment> page = new PageImpl<>(Collections.singletonList(comment));
        when(commentRepository.findByRecipeId(anyLong(), any(Pageable.class))).thenReturn(page);
        when(mapperUtil.mapCommentToDto(any(Comment.class))).thenReturn(commentDto);

        List<CommentDto> result = commentService.getCommentsByRecipeId(1L, 0, 10);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(commentRepository, times(1)).findByRecipeId(anyLong(), any(Pageable.class));
    }

    @Test
    void createComment_Success() throws ResourceNotFoundException {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(mapperUtil.mapCommentToDto(any(Comment.class))).thenReturn(commentDto);

        CommentDto result = commentService.createComment(commentDto, 1L);

        assertNotNull(result);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void createComment_UserNotFound_ThrowsException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.createComment(commentDto, 1L));
    }

    @Test
    void updateComment_Success() throws ResourceNotFoundException {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(mapperUtil.mapCommentToDto(any(Comment.class))).thenReturn(commentDto);

        CommentDto result = commentService.updateComment(1L, commentDto, 1L);

        assertNotNull(result);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void updateComment_UnauthorizedUser_ThrowsException() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(1L, commentDto, 2L));
    }

    @Test
    void deleteComment_Success() throws ResourceNotFoundException {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L, 1L);

        verify(commentRepository, times(1)).delete(any(Comment.class));
    }
}
