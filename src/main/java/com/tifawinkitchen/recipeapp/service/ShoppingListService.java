package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.ShoppingListDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;

import java.util.List;
import java.util.Set;

public interface ShoppingListService {
    List<ShoppingListDto> getUserShoppingLists(Long userId);
    ShoppingListDto getShoppingListById(Long listId, Long userId) throws ResourceNotFoundException;
    ShoppingListDto createShoppingList(ShoppingListDto shoppingListDto, Long userId) throws ResourceNotFoundException;
    ShoppingListDto generateFromRecipes(Set<Long> recipeIds, String listName, Long userId) throws ResourceNotFoundException;
    ShoppingListDto updateShoppingList(Long listId, ShoppingListDto shoppingListDto, Long userId) throws ResourceNotFoundException;
    void deleteShoppingList(Long listId, Long userId) throws ResourceNotFoundException;
}