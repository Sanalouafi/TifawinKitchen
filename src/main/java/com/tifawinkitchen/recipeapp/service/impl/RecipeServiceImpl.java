package com.tifawinkitchen.recipeapp.service.impl;


import com.tifawinkitchen.recipeapp.dto.*;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.*;
import com.tifawinkitchen.recipeapp.model.enums.*;
import com.tifawinkitchen.recipeapp.repository.*;
import com.tifawinkitchen.recipeapp.service.RecipeService;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final IngredientRepository ingredientRepository;
    private final RatingRepository ratingRepository;
    private final SearchHistoryRepository searchHistoryRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final MapperUtil mapperUtil;

    public RecipeServiceImpl(RecipeRepository recipeRepository,
                             UserRepository userRepository,
                             IngredientRepository ingredientRepository,
                             RatingRepository ratingRepository,
                             SearchHistoryRepository searchHistoryRepository,
                             RecipeIngredientRepository recipeIngredientRepository,
                             MapperUtil mapperUtil) {
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.ingredientRepository = ingredientRepository;
        this.ratingRepository = ratingRepository;
        this.searchHistoryRepository = searchHistoryRepository;
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RecipeDto> getAllRecipes(int page, int size, String sortBy, boolean ascending) {
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
    }

    @Override
    @Transactional(readOnly = true)
    public RecipeDto getRecipeById(Long id) throws ResourceNotFoundException {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", id));

        Double avgRating = ratingRepository.findAverageRatingByRecipeId(recipe.getId());
        Integer totalRatings = recipe.getRatings() != null ? recipe.getRatings().size() : 0;

        return mapperUtil.mapRecipeToDto(recipe, avgRating, totalRatings);
    }

    @Override
    @Transactional
    public RecipeDto createRecipe(RecipeCreateDto recipeCreateDto, Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Recipe recipe = new Recipe();
        recipe.setName(recipeCreateDto.getName());
        recipe.setDescription(recipeCreateDto.getDescription());
        recipe.setSteps(recipeCreateDto.getSteps());
        recipe.setDishType(recipeCreateDto.getDishType());
        recipe.setPreparationTime(recipeCreateDto.getPreparationTime());
        recipe.setComplexity(recipeCreateDto.getComplexity());
        recipe.setImageURL(recipeCreateDto.getImageURL());
        recipe.setDietTypes(recipeCreateDto.getDietTypes() != null ?
                recipeCreateDto.getDietTypes() : new HashSet<>());
        recipe.setCreatedBy(user);
        recipe.setCreatedAt(LocalDateTime.now());
        recipe.setUpdatedAt(LocalDateTime.now());

        Recipe savedRecipe = recipeRepository.save(recipe);

        if (recipeCreateDto.getIngredients() != null) {
            List<RecipeIngredient> recipeIngredients = new ArrayList<>();

            for (IngredientQuantityDto ingredientDto : recipeCreateDto.getIngredients()) {
                Ingredient ingredient;

                if (ingredientDto.getIngredientId() != null) {
                    ingredient = ingredientRepository.findById(ingredientDto.getIngredientId())
                            .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", ingredientDto.getIngredientId()));
                } else {
                    Optional<Ingredient> existingIngredient = ingredientRepository.findByNameIgnoreCase(ingredientDto.getName());

                    if (existingIngredient.isPresent()) {
                        ingredient = existingIngredient.get();
                    } else {
                        ingredient = new Ingredient();
                        ingredient.setName(ingredientDto.getName());
                        ingredient.setCategory(IngredientCategory.CONDIMENT); // Default category
                        ingredient = ingredientRepository.save(ingredient);
                    }
                }

                RecipeIngredient recipeIngredient = new RecipeIngredient();
                recipeIngredient.setRecipe(savedRecipe);
                recipeIngredient.setIngredient(ingredient);
                recipeIngredient.setQuantity(ingredientDto.getQuantity());
                recipeIngredient.setUnit(ingredientDto.getUnit());

                recipeIngredients.add(recipeIngredientRepository.save(recipeIngredient));
            }

            savedRecipe.setRecipeIngredients(recipeIngredients);
        }

        Double avgRating = 0.0;
        Integer totalRatings = 0;

        return mapperUtil.mapRecipeToDto(savedRecipe, avgRating, totalRatings);
    }

    @Override
    @Transactional
    public RecipeDto updateRecipe(Long id, RecipeCreateDto recipeUpdateDto) throws ResourceNotFoundException {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", id));

        recipe.setName(recipeUpdateDto.getName());
        recipe.setDescription(recipeUpdateDto.getDescription());
        recipe.setSteps(recipeUpdateDto.getSteps());
        recipe.setDishType(recipeUpdateDto.getDishType());
        recipe.setPreparationTime(recipeUpdateDto.getPreparationTime());
        recipe.setComplexity(recipeUpdateDto.getComplexity());
        recipe.setImageURL(recipeUpdateDto.getImageURL());
        recipe.setDietTypes(recipeUpdateDto.getDietTypes() != null ?
                recipeUpdateDto.getDietTypes() : new HashSet<>());
        recipe.setUpdatedAt(LocalDateTime.now());

        if (recipeUpdateDto.getIngredients() != null) {
            if (recipe.getRecipeIngredients() != null) {
                recipeIngredientRepository.deleteAll(recipe.getRecipeIngredients());
                recipe.getRecipeIngredients().clear();
            }

            List<RecipeIngredient> recipeIngredients = new ArrayList<>();

            for (IngredientQuantityDto ingredientDto : recipeUpdateDto.getIngredients()) {
                Ingredient ingredient;

                if (ingredientDto.getIngredientId() != null) {
                    ingredient = ingredientRepository.findById(ingredientDto.getIngredientId())
                            .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", ingredientDto.getIngredientId()));
                } else {
                    Optional<Ingredient> existingIngredient = ingredientRepository.findByNameIgnoreCase(ingredientDto.getName());

                    if (existingIngredient.isPresent()) {
                        ingredient = existingIngredient.get();
                    } else {
                        ingredient = new Ingredient();
                        ingredient.setName(ingredientDto.getName());
                        ingredient.setCategory(IngredientCategory.CONDIMENT); // Default category
                        ingredient = ingredientRepository.save(ingredient);
                    }
                }

                RecipeIngredient recipeIngredient = new RecipeIngredient();
                recipeIngredient.setRecipe(recipe);
                recipeIngredient.setIngredient(ingredient);
                recipeIngredient.setQuantity(ingredientDto.getQuantity());
                recipeIngredient.setUnit(ingredientDto.getUnit());

                recipeIngredients.add(recipeIngredientRepository.save(recipeIngredient));
            }

            recipe.setRecipeIngredients(recipeIngredients);
        }

        Recipe updatedRecipe = recipeRepository.save(recipe);

        Double avgRating = ratingRepository.findAverageRatingByRecipeId(recipe.getId());
        Integer totalRatings = recipe.getRatings() != null ? recipe.getRatings().size() : 0;

        return mapperUtil.mapRecipeToDto(updatedRecipe, avgRating, totalRatings);
    }

    @Override
    @Transactional
    public void deleteRecipe(Long id) throws ResourceNotFoundException {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", "id", id));

        if (recipe.getRecipeIngredients() != null) {
            recipeIngredientRepository.deleteAll(recipe.getRecipeIngredients());
        }

        recipeRepository.delete(recipe);
    }

    @Override
    @Transactional
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

        Pageable pageable = PageRequest.of(0, 1000); // Using a large size as default
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
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

            SearchHistory searchHistory = new SearchHistory();
            searchHistory.setUser(user);
            searchHistory.setSearchQuery(searchDto.getKeyword());
            searchHistory.setTimestamp(LocalDateTime.now());
            searchHistoryRepository.save(searchHistory);
        }

        return recipeDtos;
    }
}