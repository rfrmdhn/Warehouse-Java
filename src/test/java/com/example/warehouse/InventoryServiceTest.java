package com.example.warehouse.service;

import com.example.warehouse.domain.Item;
import com.example.warehouse.domain.Variant;
import com.example.warehouse.exception.OutOfStockException;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.repository.ItemRepository;
import com.example.warehouse.repository.VariantRepository;
import com.example.warehouse.service.impl.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private VariantRepository variantRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Item item;
    private Variant variant;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .build();

        variant = Variant.builder()
                .id(1L)
                .name("Test Variant")
                .price(BigDecimal.TEN)
                .stockQuantity(10)
                .item(item)
                .build();
    }

    @Test
    void createItem_ShouldReturnSavedItem() {
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        Item createdItem = inventoryService.createItem(item);

        assertNotNull(createdItem);
        assertEquals("Test Item", createdItem.getName());
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void sellItem_ShouldReduceStock_WhenStockIsSufficient() {
        when(variantRepository.reduceStock(1L, 5)).thenReturn(1);

        inventoryService.sellItem(1L, 5);

        verify(variantRepository, times(1)).reduceStock(1L, 5);
        verify(variantRepository, never()).save(any(Variant.class));
    }

    @Test
    void sellItem_ShouldThrowException_WhenStockIsInsufficient() {
        when(variantRepository.reduceStock(1L, 15)).thenReturn(0);
        when(variantRepository.existsById(1L)).thenReturn(true);

        assertThrows(OutOfStockException.class, () -> inventoryService.sellItem(1L, 15));
        verify(variantRepository, never()).save(any(Variant.class));
    }

    @Test
    void sellItem_ShouldThrowException_WhenVariantNotFound() {
        when(variantRepository.reduceStock(1L, 5)).thenReturn(0);
        when(variantRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.sellItem(1L, 5));
    }

    @Test
    void sellItem_ShouldReduceStockToZero_WhenStockIsExact() {
        when(variantRepository.reduceStock(1L, 10)).thenReturn(1); // Assuming starting stock was 10
        
        inventoryService.sellItem(1L, 10);

        verify(variantRepository, times(1)).reduceStock(1L, 10);
    }

    @Test
    void getAllItems_ShouldReturnItemList() {
        when(itemRepository.findAll()).thenReturn(List.of(item));

        List<Item> items = inventoryService.getAllItems();

        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Test Item", items.get(0).getName());
    }

    @Test
    void getItemById_ShouldReturnItem_WhenFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        Item foundItem = inventoryService.getItemById(1L);

        assertNotNull(foundItem);
        assertEquals("Test Item", foundItem.getName());
    }

    @Test
    void getItemById_ShouldThrowException_WhenNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.getItemById(1L));
    }

    @Test
    void addVariant_ShouldReturnSavedVariant_WhenItemExists() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(variantRepository.save(any(Variant.class))).thenReturn(variant);

        Variant createdVariant = inventoryService.addVariant(1L, variant);

        assertNotNull(createdVariant);
        assertEquals("Test Variant", createdVariant.getName());
        verify(itemRepository, times(1)).findById(1L);
        verify(variantRepository, times(1)).save(variant);
    }

    @Test
    void addVariant_ShouldThrowException_WhenItemNotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.addVariant(1L, variant));
        verify(variantRepository, never()).save(any(Variant.class));
    }

    @Test
    void updateVariantStock_ShouldUpdateStock_WhenVariantExists() {
        when(variantRepository.findById(1L)).thenReturn(Optional.of(variant));
        when(variantRepository.save(any(Variant.class))).thenReturn(variant);

        Variant updatedVariant = inventoryService.updateVariantStock(1L, 50);

        assertNotNull(updatedVariant);
        assertEquals(50, variant.getStockQuantity()); // Since object is modified by reference
        verify(variantRepository, times(1)).save(variant);
    }

    @Test
    void updateVariantStock_ShouldThrowException_WhenVariantNotFound() {
        when(variantRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> inventoryService.updateVariantStock(1L, 50));
        verify(variantRepository, never()).save(any(Variant.class));
    }
}
