package com.tifawinkitchen.recipeapp.controller;

import com.tifawinkitchen.recipeapp.dto.ShoppingListDto;
import com.tifawinkitchen.recipeapp.service.ShoppingListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/shopping-lists")
@RequiredArgsConstructor
public class ShoppingListController {

    private final ShoppingListService shoppingListService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ShoppingListDto>> getUserShoppingLists(@PathVariable Long userId) {
        List<ShoppingListDto> shoppingLists = shoppingListService.getUserShoppingLists(userId);
        return ResponseEntity.ok(shoppingLists);
    }

    @GetMapping("/{listId}")
    public ResponseEntity<ShoppingListDto> getShoppingListById(
            @PathVariable Long listId,
            @RequestParam Long userId) {
        ShoppingListDto shoppingListDto = shoppingListService.getShoppingListById(listId, userId);
        return ResponseEntity.ok(shoppingListDto);
    }

    @PostMapping
    public ResponseEntity<ShoppingListDto> createShoppingList(
            @RequestBody ShoppingListDto shoppingListDto,
            @RequestParam Long userId) {
        ShoppingListDto createdList = shoppingListService.createShoppingList(shoppingListDto, userId);
        return ResponseEntity.ok(createdList);
    }

    @PostMapping("/generate")
    public ResponseEntity<ShoppingListDto> generateFromRecipes(
            @RequestParam Set<Long> recipeIds,
            @RequestParam String listName,
            @RequestParam Long userId) {
        ShoppingListDto generatedList = shoppingListService.generateFromRecipes(recipeIds, listName, userId);
        return ResponseEntity.ok(generatedList);
    }

    @PutMapping("/{listId}")
    public ResponseEntity<ShoppingListDto> updateShoppingList(
            @PathVariable Long listId,
            @RequestBody ShoppingListDto shoppingListDto,
            @RequestParam Long userId) {
        ShoppingListDto updatedList = shoppingListService.updateShoppingList(listId, shoppingListDto, userId);
        return ResponseEntity.ok(updatedList);
    }

    @DeleteMapping("/{listId}")
    public ResponseEntity<Void> deleteShoppingList(
            @PathVariable Long listId,
            @RequestParam Long userId) {
        shoppingListService.deleteShoppingList(listId, userId);
        return ResponseEntity.noContent().build();
    }
}