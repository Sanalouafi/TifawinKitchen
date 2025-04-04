package com.tifawinkitchen.recipeapp.service.impl;

import com.tifawinkitchen.recipeapp.dto.*;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.*;
import com.tifawinkitchen.recipeapp.model.enums.*;
import com.tifawinkitchen.recipeapp.repository.*;
import com.tifawinkitchen.recipeapp.service.*;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final RatingRepository ratingRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final MapperUtil mapperUtil;
    private final FavoriteRecipeService favoriteRecipeService;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public List<RecipeDto> getAllRecipes(int page, int size, String sortBy, boolean ascending) {
        try {
            Sort sort = Sort.by(ascending ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Recipe> recipesPage = recipeRepository.findAll(pageable);

            return recipesPage.getContent().stream()
                    .map(recipe -> {
                        Double avgRating = ratingRepository.findAverageRatingByRecipeId(recipe.getId());
                        Integer totalRatings = recipe.getRatings() != null ? recipe.getRatings().size() : 0;
                        return mapperUtil.mapRecipeToDto(recipe, avgRating, totalRatings);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Failed to fetch recipes", e);
            throw new RuntimeException("Failed to fetch recipes: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public RecipeDto getRecipeById(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));

        try {
            Double avgRating = ratingRepository.findAverageRatingByRecipeId(recipe.getId());
            Integer totalRatings = recipe.getRatings() != null ? recipe.getRatings().size() : 0;
            return mapperUtil.mapRecipeToDto(recipe, avgRating, totalRatings);
        } catch (Exception e) {
            log.error("Failed to fetch recipe details for id: {}", id, e);
            throw new RuntimeException("Failed to fetch recipe details: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public RecipeDto createRecipe(RecipeCreateDto recipeCreateDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        try {
            Recipe recipe = new Recipe();
            recipe.setName(recipeCreateDto.getName());
            recipe.setDescription(recipeCreateDto.getDescription());
            recipe.setSteps(recipeCreateDto.getSteps());
            recipe.setDishType(recipeCreateDto.getDishType());
            recipe.setPreparationTime(recipeCreateDto.getPreparationTime());
            recipe.setComplexity(recipeCreateDto.getComplexity());
            recipe.setDietTypes(recipeCreateDto.getDietTypes() != null ?
                    recipeCreateDto.getDietTypes() : new HashSet<>());
            recipe.setCreatedBy(user);
            recipe.setCreatedAt(LocalDateTime.now());
            recipe.setUpdatedAt(LocalDateTime.now());

            // Handle image upload
            if (recipeCreateDto.getImageFile() != null && !recipeCreateDto.getImageFile().isEmpty()) {
                String fileName = fileStorageService.storeFile(recipeCreateDto.getImageFile());
                recipe.setImageURL("/api/images/" + fileName);
            }

            Recipe savedRecipe = recipeRepository.save(recipe);

            // Handle ingredients
            if (recipeCreateDto.getIngredients() != null && !recipeCreateDto.getIngredients().isEmpty()) {
                List<RecipeIngredient> recipeIngredients = new ArrayList<>();

                for (IngredientQuantityDto ingredientDto : recipeCreateDto.getIngredients()) {
                    Ingredient ingredient = getOrCreateIngredient(
                            ingredientDto.getIngredientId(),
                            ingredientDto.getName(),
                            IngredientCategory.CONDIMENT);

                    RecipeIngredient recipeIngredient = new RecipeIngredient();
                    recipeIngredient.setRecipe(savedRecipe);
                    recipeIngredient.setIngredient(ingredient);
                    recipeIngredient.setQuantity(ingredientDto.getQuantity());
                    recipeIngredient.setUnit(ingredientDto.getUnit());

                    recipeIngredients.add(recipeIngredientRepository.save(recipeIngredient));
                }
                savedRecipe.setRecipeIngredients(recipeIngredients);
            }

            return mapperUtil.mapRecipeToDto(savedRecipe, 0.0, 0);
        } catch (Exception e) {
            log.error("Failed to create recipe", e);
            throw new RuntimeException("Failed to create recipe: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public RecipeDto updateRecipe(Long id, RecipeCreateDto recipeUpdateDto) {
        try {
            Recipe recipe = recipeRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Recipe not found"));

            // Update basic fields
            recipe.setName(recipeUpdateDto.getName());
            recipe.setDescription(recipeUpdateDto.getDescription());
            recipe.setSteps(recipeUpdateDto.getSteps());
            recipe.setDishType(recipeUpdateDto.getDishType());
            recipe.setPreparationTime(recipeUpdateDto.getPreparationTime());
            recipe.setComplexity(recipeUpdateDto.getComplexity());
            recipe.setDietTypes(recipeUpdateDto.getDietTypes() != null ?
                    recipeUpdateDto.getDietTypes() : new HashSet<>());
            recipe.setUpdatedAt(LocalDateTime.now());

            // Handle image update
            if (recipeUpdateDto.getImageFile() != null && !recipeUpdateDto.getImageFile().isEmpty()) {
                String fileName = fileStorageService.storeFile(recipeUpdateDto.getImageFile());
                recipe.setImageURL("/api/images/" + fileName);
            } else if (recipeUpdateDto.getImageURL() != null) {
                recipe.setImageURL(recipeUpdateDto.getImageURL());
            }

            // Handle ingredients update
            if (recipeUpdateDto.getIngredients() != null) {
                updateRecipeIngredients(recipe, recipeUpdateDto.getIngredients());
            }

            Recipe updatedRecipe = recipeRepository.save(recipe);

            Double avgRating = ratingRepository.findAverageRatingByRecipeId(recipe.getId());
            Integer totalRatings = recipe.getRatings() != null ? recipe.getRatings().size() : 0;

            return mapperUtil.mapRecipeToDto(updatedRecipe, avgRating, totalRatings);
        } catch (Exception e) {
            log.error("Failed to update recipe with id: {}", id, e);
            throw new RuntimeException("Failed to update recipe: " + e.getMessage());
        }
    }

    private void updateRecipeIngredients(Recipe recipe, List<IngredientQuantityDto> ingredientDtos) {
        List<RecipeIngredient> ingredientsToRemove = new ArrayList<>(recipe.getRecipeIngredients());
        ingredientsToRemove.forEach(ingredient -> {
            recipe.getRecipeIngredients().remove(ingredient);
            recipeIngredientRepository.delete(ingredient);
        });

        for (IngredientQuantityDto dto : ingredientDtos) {
            Ingredient ingredient = getOrCreateIngredient(
                    dto.getIngredientId(),
                    dto.getName(),
                    IngredientCategory.CONDIMENT);

            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setRecipe(recipe);
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setQuantity(dto.getQuantity());
            recipeIngredient.setUnit(dto.getUnit());

            recipe.getRecipeIngredients().add(recipeIngredient);
            recipeIngredientRepository.save(recipeIngredient);
        }
    }

    @Override
    @Transactional
    public void deleteRecipe(Long id) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + id));

        try {
            if (recipe.getRecipeIngredients() != null && !recipe.getRecipeIngredients().isEmpty()) {
                recipeIngredientRepository.deleteAll(recipe.getRecipeIngredients());
            }

            recipeRepository.delete(recipe);
        } catch (Exception e) {
            log.error("Failed to delete recipe with id: {}", id, e);
            throw new RuntimeException("Failed to delete recipe: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeDto> searchRecipes(RecipeSearchDto searchDto, Long userId) {
        Specification<Recipe> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchDto.getKeyword() != null && !searchDto.getKeyword().isEmpty()) {
                String likeKeyword = "%" + searchDto.getKeyword().toLowerCase() + "%";
                Predicate namePredicate = cb.like(cb.lower(root.get("name")), likeKeyword);
                Predicate descriptionPredicate = cb.like(cb.lower(root.get("description")), likeKeyword);
                predicates.add(cb.or(namePredicate, descriptionPredicate));
            }

            if (searchDto.getDishType() != null) {
                predicates.add(cb.equal(root.get("dishType"), searchDto.getDishType()));
            }

            if (searchDto.getMaxPrepTime() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("preparationTime"), searchDto.getMaxPrepTime()));
            }

            if (searchDto.getComplexity() != null) {
                predicates.add(cb.equal(root.get("complexity"), searchDto.getComplexity()));
            }

            if (searchDto.getDietType() != null) {
                predicates.add(cb.isMember(searchDto.getDietType(), root.get("dietTypes")));
            }

            if (searchDto.getIncludeIngredients() != null && !searchDto.getIncludeIngredients().isEmpty()) {
                for (Long ingredientId : searchDto.getIncludeIngredients()) {
                    jakarta.persistence.criteria.Subquery<Long> subquery = query.subquery(Long.class);
                    jakarta.persistence.criteria.Root<RecipeIngredient> riRoot = subquery.from(RecipeIngredient.class);
                    subquery.select(riRoot.get("recipe").get("id"))
                            .where(
                                    cb.and(
                                            cb.equal(riRoot.get("ingredient").get("id"), ingredientId),
                                            cb.equal(riRoot.get("recipe"), root)
                                    )
                            );
                    predicates.add(cb.exists(subquery));
                }
            }

            if (searchDto.getExcludeIngredients() != null && !searchDto.getExcludeIngredients().isEmpty()) {
                for (Long ingredientId : searchDto.getExcludeIngredients()) {
                    jakarta.persistence.criteria.Subquery<Long> subquery = query.subquery(Long.class);
                    jakarta.persistence.criteria.Root<RecipeIngredient> riRoot = subquery.from(RecipeIngredient.class);
                    subquery.select(riRoot.get("recipe").get("id"))
                            .where(
                                    cb.and(
                                            cb.equal(riRoot.get("ingredient").get("id"), ingredientId),
                                            cb.equal(riRoot.get("recipe"), root)
                                    )
                            );
                    predicates.add(cb.not(cb.exists(subquery)));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(0, 1000);
        Page<Recipe> recipePage = recipeRepository.findAll(spec, pageable);
        List<Recipe> recipes = recipePage.getContent();

        List<RecipeDto> recipeDtos = recipes.stream()
                .map(recipe -> {
                    Double avgRating = ratingRepository.findAverageRatingByRecipeId(recipe.getId());
                    Integer totalRatings = recipe.getRatings() != null ? recipe.getRatings().size() : 0;
                    return mapperUtil.mapRecipeToDto(recipe, avgRating, totalRatings);
                })
                .collect(Collectors.toList());

        if (userId != null && searchDto.getKeyword() != null && !searchDto.getKeyword().isEmpty()) {
            try {
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

                SearchHistory searchHistory = new SearchHistory();
                searchHistory.setUser(user);
                searchHistory.setSearchQuery(searchDto.getKeyword());
                searchHistory.setTimestamp(LocalDateTime.now());
                searchHistoryRepository.save(searchHistory);
            } catch (Exception e) {
                log.error("Failed to save search history", e);
            }
        }

        return recipeDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeDto> getSimilarRecipes(Long recipeId, int count) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe not found with id: " + recipeId));

        List<Recipe> similarRecipes = recipeRepository.findSimilarRecipes(recipeId, recipe.getDishType(), PageRequest.of(0, count));

        return similarRecipes.stream()
                .map(r -> {
                    Double avgRating = ratingRepository.findAverageRatingByRecipeId(r.getId());
                    Integer totalRatings = r.getRatings() != null ? r.getRatings().size() : 0;
                    return mapperUtil.mapRecipeToDto(r, avgRating, totalRatings);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void saveRecipeToUserFavorites(Long userId, Long recipeId) {
        favoriteRecipeService.saveRecipeToFavorites(userId, recipeId);
    }

    @Override
    @Transactional
    public void removeRecipeFromUserFavorites(Long userId, Long recipeId) {
        favoriteRecipeService.removeRecipeFromFavorites(userId, recipeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeDto> getUserFavoriteRecipes(Long userId) {
        return favoriteRecipeService.getUserFavoriteRecipes(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRecipeFavoriteForUser(Long userId, Long recipeId) {
        return favoriteRecipeService.isRecipeFavorite(userId, recipeId);
    }

    private Ingredient getOrCreateIngredient(Long ingredientId, String name, IngredientCategory category) {
        if (ingredientId != null) {
            return ingredientRepository.findById(ingredientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id: " + ingredientId));
        }

        return ingredientRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> {
                    Ingredient newIngredient = new Ingredient();
                    newIngredient.setName(name);
                    newIngredient.setCategory(category);
                    return ingredientRepository.save(newIngredient);
                });
    }
}