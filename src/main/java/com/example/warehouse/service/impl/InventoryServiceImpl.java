package com.example.warehouse.service.impl;

import com.example.warehouse.domain.Item;
import com.example.warehouse.domain.Variant;
import com.example.warehouse.exception.OutOfStockException;
import com.example.warehouse.exception.ResourceNotFoundException;
import com.example.warehouse.repository.ItemRepository;
import com.example.warehouse.repository.VariantRepository;
import com.example.warehouse.service.InventoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ItemRepository itemRepository;
    private final VariantRepository variantRepository;

    @Override
    @Transactional
    public Item createItem(Item item) {
        // If variants are provided, ensure they are linked to the item
        if (item.getVariants() != null) {
            item.getVariants().forEach(variant -> variant.setItem(item));
        }
        return itemRepository.save(item);
    }

    @Override
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found with id: " + id));
    }

    @Override
    @Transactional
    public Variant addVariant(Long itemId, Variant variant) {
        Item item = getItemById(itemId);
        variant.setItem(item);
        return variantRepository.save(variant);
    }

    @Override
    @Transactional
    public Variant updateVariantStock(Long variantId, Integer quantity) {
        Variant variant = variantRepository.findById(variantId)
                .orElseThrow(() -> new ResourceNotFoundException("Variant not found with id: " + variantId));
        variant.setStockQuantity(quantity);
        return variantRepository.save(variant);
    }

    @Override
    @Transactional
    public void sellItem(Long variantId, Integer quantity) {
        int updatedRows = variantRepository.reduceStock(variantId, quantity);
        if (updatedRows == 0) {
            // Check if variant exists to throw correct exception
            boolean exists = variantRepository.existsById(variantId);
            if (!exists) {
                throw new ResourceNotFoundException("Variant not found with id: " + variantId);
            }
            throw new OutOfStockException("Insufficient stock for variant id: " + variantId);
        }
    }
}
