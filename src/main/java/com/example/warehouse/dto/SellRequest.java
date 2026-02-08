package com.example.warehouse.dto;

import lombok.Data;

@Data
public class SellRequest {
    private Long variantId;
    private Integer quantity;
}
