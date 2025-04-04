package com.tifawinkitchen.recipeapp.model;

import com.tifawinkitchen.recipeapp.model.enums.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "recipes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "recipe_steps", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "step", columnDefinition = "TEXT")
    @OrderColumn(name = "step_order")
    private List<String> steps = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private DishType dishType;

    @Column(nullable = false)
    private Integer preparationTime;

    @Enumerated(EnumType.STRING)
    private RecipeComplexity complexity;

    private String imageURL;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @ElementCollection(targetClass = DietType.class)
    @CollectionTable(name = "recipe_diet_types", joinColumns = @JoinColumn(name = "recipe_id"))
    @Enumerated(EnumType.STRING)
    private Set<DietType> dietTypes = new HashSet<>();

    // Helper methods for managing bidirectional relationships
    public void addRecipeIngredient(RecipeIngredient recipeIngredient) {
        recipeIngredients.add(recipeIngredient);
        recipeIngredient.setRecipe(this);
    }

    public void removeRecipeIngredient(RecipeIngredient recipeIngredient) {
        recipeIngredients.remove(recipeIngredient);
        recipeIngredient.setRecipe(null);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setRecipe(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setRecipe(null);
    }

    public void addRating(Rating rating) {
        ratings.add(rating);
        rating.setRecipe(this);
    }

    public void removeRating(Rating rating) {
        ratings.remove(rating);
        rating.setRecipe(null);
    }
}