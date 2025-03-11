package com.tifawinkitchen.recipeapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long recipeId;

    @NotBlank(message = "Comment text is required")
    private String text;

    private LocalDateTime timestamp;
}
