package com.tifawinkitchen.recipeapp.model;

import com.tifawinkitchen.recipeapp.model.enums.DietType;
import com.tifawinkitchen.recipeapp.model.enums.DishType;
import com.tifawinkitchen.recipeapp.model.enums.RecipeComplexity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}