package com.example.warehouse.controller;

import com.example.warehouse.domain.Item;
import com.example.warehouse.domain.Variant;
import com.example.warehouse.dto.*;
import com.example.warehouse.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @PostMapping("/items")
    public ResponseEntity<ItemResponse> createItem(@RequestBody ItemRequest request) {
        Item item = Item.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
        Item savedItem = inventoryService.createItem(item);
        return new ResponseEntity<>(mapToItemResponse(savedItem), HttpStatus.CREATED);
    }

    @GetMapping("/items")
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        List<Item> items = inventoryService.getAllItems();
        List<ItemResponse> response = items.stream()
                .map(this::mapToItemResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<ItemResponse> getItemById(@PathVariable Long id) {
        Item item = inventoryService.getItemById(id);
        return ResponseEntity.ok(mapToItemResponse(item));
    }

    @PostMapping("/items/{itemId}/variants")
    public ResponseEntity<VariantResponse> addVariant(@PathVariable Long itemId, @RequestBody VariantRequest request) {
        Variant variant = Variant.builder()
                .name(request.getName())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build();
        Variant savedVariant = inventoryService.addVariant(itemId, variant);
        return new ResponseEntity<>(mapToVariantResponse(savedVariant), HttpStatus.CREATED);
    }

    @PatchMapping("/variants/{variantId}/stock")
    public ResponseEntity<VariantResponse> updateStock(@PathVariable Long variantId, @RequestBody Integer quantity) {
        Variant updatedVariant = inventoryService.updateVariantStock(variantId, quantity);
        return ResponseEntity.ok(mapToVariantResponse(updatedVariant));
    }

    @PostMapping("/orders")
    public ResponseEntity<Void> sellItem(@RequestBody SellRequest sellRequest) {
        inventoryService.sellItem(sellRequest.getVariantId(), sellRequest.getQuantity());
        return ResponseEntity.ok().build();
    }

    // Helper Mappers
    private ItemResponse mapToItemResponse(Item item) {
        List<VariantResponse> variantResponses = item.getVariants() != null ?
                item.getVariants().stream().map(this::mapToVariantResponse).collect(Collectors.toList()) :
                Collections.emptyList();

        return ItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .variants(variantResponses)
                .build();
    }

    private VariantResponse mapToVariantResponse(Variant variant) {
        return VariantResponse.builder()
                .id(variant.getId())
                .name(variant.getName())
                .price(variant.getPrice())
                .stockQuantity(variant.getStockQuantity())
                .build();
    }
}
