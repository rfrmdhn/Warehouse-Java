package com.example.warehouse.service;

import com.example.warehouse.domain.Item;
import com.example.warehouse.domain.Variant;

import java.util.List;

public interface InventoryService {
    Item createItem(Item item);
    List<Item> getAllItems();
    Item getItemById(Long id);
    Variant addVariant(Long itemId, Variant variant);
    Variant updateVariantStock(Long variantId, Integer quantity);
    void sellItem(Long variantId, Integer quantity);
}
