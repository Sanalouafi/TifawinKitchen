package com.tifawinkitchen.recipeapp.service;

import com.tifawinkitchen.recipeapp.dto.IngredientQuantityDto;
import com.tifawinkitchen.recipeapp.dto.ShoppingListDto;
import com.tifawinkitchen.recipeapp.exception.ResourceNotFoundException;
import com.tifawinkitchen.recipeapp.model.*;
import com.tifawinkitchen.recipeapp.repository.*;
import com.tifawinkitchen.recipeapp.service.impl.ShoppingListServiceImpl;
import com.tifawinkitchen.recipeapp.util.MapperUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShoppingListServiceImplTest {

    @Mock
    private ShoppingListRepository shoppingListRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private IngredientRepository ingredientRepository;
    @Mock
    private MapperUtil mapperUtil;

    @InjectMocks
    private ShoppingListServiceImpl shoppingListService;

    private ShoppingList shoppingList;
    private ShoppingListDto shoppingListDto;
    private User user;
    private Recipe recipe;
    private Ingredient ingredient;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        ingredient = new Ingredient();
        ingredient.setId(1L);
        ingredient.setName("Flour");

        recipe = new Recipe();
        recipe.setId(1L);
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setQuantity("2 cups");
        recipe.setRecipeIngredients(Collections.singletonList(recipeIngredient));

        ShoppingListItem item = new ShoppingListItem();
        item.setId(1L);
        item.setIngredient(ingredient);
        item.setQuantity("1 cup");
        item.setUnit("cup");

        // Use ArrayList instead of Collections.singletonList to allow modifications
        shoppingList = new ShoppingList();
        shoppingList.setId(1L);
        shoppingList.setUser(user);
        shoppingList.setName("Weekly Shopping");
        shoppingList.setItems(new ArrayList<>(Collections.singletonList(item)));
        shoppingList.setGeneratedFromRecipes(new HashSet<>(Collections.singleton(recipe)));

        shoppingListDto = new ShoppingListDto();
        shoppingListDto.setId(1L);
        shoppingListDto.setName("Weekly Shopping");
    }

    @Test
    void getUserShoppingLists_Success() {
        when(shoppingListRepository.findByUserId(1L)).thenReturn(Collections.singletonList(shoppingList));
        when(mapperUtil.mapShoppingListToDto(any(ShoppingList.class))).thenReturn(shoppingListDto);

        List<ShoppingListDto> result = shoppingListService.getUserShoppingLists(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getShoppingListById_Success() throws ResourceNotFoundException {
        when(shoppingListRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shoppingList));
        when(mapperUtil.mapShoppingListToDto(any(ShoppingList.class))).thenReturn(shoppingListDto);

        ShoppingListDto result = shoppingListService.getShoppingListById(1L, 1L);

        assertNotNull(result);
    }

    @Test
    void getShoppingListById_NotFound_ThrowsException() {
        when(shoppingListRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> shoppingListService.getShoppingListById(1L, 1L));
    }

    @Test
    void createShoppingList_Success() throws ResourceNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(shoppingListRepository.save(any(ShoppingList.class))).thenReturn(shoppingList);
        when(mapperUtil.mapShoppingListToDto(any(ShoppingList.class))).thenReturn(shoppingListDto);

        ShoppingListDto result = shoppingListService.createShoppingList(shoppingListDto, 1L);

        assertNotNull(result);
    }

    @Test
    void generateFromRecipes_Success() throws ResourceNotFoundException {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(shoppingListRepository.save(any(ShoppingList.class))).thenReturn(shoppingList);
        when(mapperUtil.mapShoppingListToDto(any(ShoppingList.class))).thenReturn(shoppingListDto);

        ShoppingListDto result = shoppingListService.generateFromRecipes(
                Collections.singleton(1L), "Weekly Shopping", 1L);

        assertNotNull(result);
    }

    @Test
    void updateShoppingList_Success() throws ResourceNotFoundException {
        when(shoppingListRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shoppingList));
        when(shoppingListRepository.save(any(ShoppingList.class))).thenReturn(shoppingList);
        when(mapperUtil.mapShoppingListToDto(any(ShoppingList.class))).thenReturn(shoppingListDto);

        ShoppingListDto result = shoppingListService.updateShoppingList(1L, shoppingListDto, 1L);

        assertNotNull(result);
    }

    @Test
    void deleteShoppingList_Success() throws ResourceNotFoundException {
        when(shoppingListRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shoppingList));

        shoppingListService.deleteShoppingList(1L, 1L);

        verify(shoppingListRepository, times(1)).delete(any(ShoppingList.class));
    }

    @Test
    void addItemToShoppingList_Success() throws ResourceNotFoundException {
        when(shoppingListRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shoppingList));
        when(ingredientRepository.findById(anyLong())).thenReturn(Optional.of(ingredient));
        when(shoppingListRepository.save(any(ShoppingList.class))).thenReturn(shoppingList);
        when(mapperUtil.mapShoppingListToDto(any(ShoppingList.class))).thenReturn(shoppingListDto);

        IngredientQuantityDto itemDto = new IngredientQuantityDto();
        itemDto.setIngredientId(1L);
        itemDto.setQuantity("1");
        itemDto.setUnit("cup");

        ShoppingListDto result = shoppingListService.addItemToShoppingList(1L, itemDto, 1L);

        assertNotNull(result);
    }

    @Test
    void removeItemFromShoppingList_Success() throws ResourceNotFoundException {
        when(shoppingListRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shoppingList));
        when(shoppingListRepository.save(any(ShoppingList.class))).thenReturn(shoppingList);
        when(mapperUtil.mapShoppingListToDto(any(ShoppingList.class))).thenReturn(shoppingListDto);

        ShoppingListDto result = shoppingListService.removeItemFromShoppingList(1L, 1L, 1L);

        assertNotNull(result);
    }

    @Test
    void updateShoppingListItem_Success() throws ResourceNotFoundException {
        when(shoppingListRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(shoppingList));
        when(ingredientRepository.findById(anyLong())).thenReturn(Optional.of(ingredient));
        when(shoppingListRepository.save(any(ShoppingList.class))).thenReturn(shoppingList);
        when(mapperUtil.mapShoppingListToDto(any(ShoppingList.class))).thenReturn(shoppingListDto);

        IngredientQuantityDto itemDto = new IngredientQuantityDto();
        itemDto.setIngredientId(1L);
        itemDto.setQuantity("2");
        itemDto.setUnit("cups");

        ShoppingListDto result = shoppingListService.updateShoppingListItem(1L, 1L, itemDto, 1L);

        assertNotNull(result);
    }
}