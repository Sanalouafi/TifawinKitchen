package com.tifawinkitchen.recipeapp.service.impl;

import com.tifawinkitchen.recipeapp.dto.IngredientQuantityDto;
import com.tifawinkitchen.recipeapp.dto.ShoppingListDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.*;
import com.tifawinkitchen.recipeapp.repository.*;
import com.tifawinkitchen.recipeapp.service.ShoppingListService;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ShoppingListServiceImpl implements ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final MapperUtil mapperUtil;

    public ShoppingListServiceImpl(ShoppingListRepository shoppingListRepository, UserRepository userRepository,
                                   RecipeRepository recipeRepository, IngredientRepository ingredientRepository,
                                   MapperUtil mapperUtil) {
        this.shoppingListRepository = shoppingListRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.mapperUtil = mapperUtil;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShoppingListDto> getUserShoppingLists(Long userId) {
        List<ShoppingList> shoppingLists = shoppingListRepository.findByUserId(userId);
        return shoppingLists.stream()
                .map(mapperUtil::mapShoppingListToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ShoppingListDto getShoppingListById(Long listId, Long userId) throws ResourceNotFoundException {
        ShoppingList shoppingList = shoppingListRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));
        return mapperUtil.mapShoppingListToDto(shoppingList);
    }

    @Override
    @Transactional
    public ShoppingListDto createShoppingList(ShoppingListDto shoppingListDto, Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setUser(user);
        shoppingList.setName(shoppingListDto.getName());
        shoppingList.setCreatedAt(LocalDateTime.now());
        shoppingList.setUpdatedAt(LocalDateTime.now());

        if (shoppingListDto.getItems() != null && !shoppingListDto.getItems().isEmpty()) {
            List<ShoppingListItem> items = new ArrayList<>();

            for (IngredientQuantityDto itemDto : shoppingListDto.getItems()) {
                Ingredient ingredient = ingredientRepository.findById(itemDto.getIngredientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found: " + itemDto.getIngredientId()));

                ShoppingListItem item = new ShoppingListItem();
                item.setIngredient(ingredient);
                item.setQuantity(itemDto.getQuantity());
                item.setUnit(itemDto.getUnit());
                item.setShoppingList(shoppingList);
                items.add(item);
            }

            shoppingList.setItems(items);
        }

        if (shoppingListDto.getRecipeIds() != null && !shoppingListDto.getRecipeIds().isEmpty()) {
            Set<Recipe> recipes = shoppingListDto.getRecipeIds().stream()
                    .map(recipeId -> recipeRepository.findById(recipeId)
                            .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + recipeId)))
                    .collect(Collectors.toSet());

            shoppingList.setGeneratedFromRecipes(recipes);
        }

        ShoppingList savedList = shoppingListRepository.save(shoppingList);
        return mapperUtil.mapShoppingListToDto(savedList);
    }

    @Override
    @Transactional
    public ShoppingListDto generateFromRecipes(Set<Long> recipeIds, String listName, Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<Recipe> recipes = recipeIds.stream()
                .map(recipeId -> recipeRepository.findById(recipeId)
                        .orElseThrow(() -> new ResourceNotFoundException("Recipe not found: " + recipeId)))
                .collect(Collectors.toSet());

        ShoppingList shoppingList = new ShoppingList();
        shoppingList.setUser(user);
        shoppingList.setName(listName);
        shoppingList.setCreatedAt(LocalDateTime.now());
        shoppingList.setUpdatedAt(LocalDateTime.now());
        shoppingList.setGeneratedFromRecipes(recipes);

        Map<Ingredient, ShoppingListItem> ingredientMap = new HashMap<>();

        for (Recipe recipe : recipes) {
            for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
                Ingredient ingredient = recipeIngredient.getIngredient();

                if (ingredientMap.containsKey(ingredient)) {
                    ShoppingListItem existingItem = ingredientMap.get(ingredient);
                    existingItem.setQuantity(existingItem.getQuantity() + " + " + recipeIngredient.getQuantity());
                } else {
                    ShoppingListItem item = new ShoppingListItem();
                    item.setIngredient(ingredient);
                    item.setQuantity(recipeIngredient.getQuantity());
                    item.setUnit(recipeIngredient.getUnit());
                    item.setShoppingList(shoppingList);
                    ingredientMap.put(ingredient, item);
                }
            }
        }

        shoppingList.setItems(new ArrayList<>(ingredientMap.values()));
        ShoppingList savedList = shoppingListRepository.save(shoppingList);

        return mapperUtil.mapShoppingListToDto(savedList);
    }

    @Override
    @Transactional
    public ShoppingListDto updateShoppingList(Long listId, ShoppingListDto shoppingListDto, Long userId) throws ResourceNotFoundException {
        ShoppingList shoppingList = shoppingListRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        shoppingList.setName(shoppingListDto.getName());
        shoppingList.setUpdatedAt(LocalDateTime.now());

        if (shoppingListDto.getItems() != null) {
            shoppingList.getItems().clear();

            List<ShoppingListItem> items = new ArrayList<>();

            for (IngredientQuantityDto itemDto : shoppingListDto.getItems()) {
                Ingredient ingredient = ingredientRepository.findById(itemDto.getIngredientId())
                        .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found: " + itemDto.getIngredientId()));

                ShoppingListItem item = new ShoppingListItem();
                item.setIngredient(ingredient);
                item.setQuantity(itemDto.getQuantity());
                item.setUnit(itemDto.getUnit());
                item.setShoppingList(shoppingList);
                items.add(item);
            }

            shoppingList.setItems(items);
        }

        ShoppingList updatedList = shoppingListRepository.save(shoppingList);
        return mapperUtil.mapShoppingListToDto(updatedList);
    }

    @Override
    @Transactional
    public void deleteShoppingList(Long listId, Long userId) throws ResourceNotFoundException {
        ShoppingList shoppingList = shoppingListRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Shopping list not found"));

        shoppingListRepository.delete(shoppingList);
    }
    @Override
    @Transactional
    public ShoppingListDto addItemToShoppingList(Long listId, IngredientQuantityDto itemDto, Long userId)
            throws ResourceNotFoundException {
        validateShoppingListItem(itemDto);

        ShoppingList shoppingList = shoppingListRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingList", "id", listId));

        Ingredient ingredient = ingredientRepository.findById(itemDto.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", itemDto.getIngredientId()));

        ShoppingListItem item = new ShoppingListItem();
        item.setShoppingList(shoppingList);
        item.setIngredient(ingredient);
        item.setQuantity(itemDto.getQuantity());
        item.setUnit(itemDto.getUnit());

        shoppingList.getItems().add(item);
        shoppingList.setUpdatedAt(LocalDateTime.now());

        ShoppingList updatedList = shoppingListRepository.save(shoppingList);
        return mapperUtil.mapShoppingListToDto(updatedList);
    }

    @Override
    @Transactional
    public ShoppingListDto removeItemFromShoppingList(Long listId, Long itemId, Long userId)
            throws ResourceNotFoundException {
        ShoppingList shoppingList = shoppingListRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingList", "id", listId));

        boolean removed = shoppingList.getItems().removeIf(item -> item.getId().equals(itemId));
        if (!removed) {
            throw new ResourceNotFoundException("ShoppingListItem", "id", itemId);
        }

        shoppingList.setUpdatedAt(LocalDateTime.now());
        ShoppingList updatedList = shoppingListRepository.save(shoppingList);
        return mapperUtil.mapShoppingListToDto(updatedList);
    }

    @Override
    @Transactional
    public ShoppingListDto updateShoppingListItem(Long listId, Long itemId, IngredientQuantityDto itemDto, Long userId)
            throws ResourceNotFoundException {
        validateShoppingListItem(itemDto);

        ShoppingList shoppingList = shoppingListRepository.findByIdAndUserId(listId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingList", "id", listId));

        ShoppingListItem item = shoppingList.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("ShoppingListItem", "id", itemId));

        Ingredient ingredient = ingredientRepository.findById(itemDto.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", "id", itemDto.getIngredientId()));

        item.setIngredient(ingredient);
        item.setQuantity(itemDto.getQuantity());
        item.setUnit(itemDto.getUnit());

        shoppingList.setUpdatedAt(LocalDateTime.now());
        ShoppingList updatedList = shoppingListRepository.save(shoppingList);
        return mapperUtil.mapShoppingListToDto(updatedList);
    }

    private void validateShoppingListItem(IngredientQuantityDto itemDto) {
        if (itemDto.getIngredientId() == null) {
            throw new IllegalArgumentException("Ingredient ID is required");
        }
        if (itemDto.getQuantity() == null || itemDto.getQuantity().isEmpty()) {
            throw new IllegalArgumentException("Quantity is required");
        }
        if (itemDto.getUnit() == null || itemDto.getUnit().isEmpty()) {
            throw new IllegalArgumentException("Unit is required");
        }
    }
}
