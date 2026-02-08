package com.example.warehouse.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ItemResponse {
    private Long id;
    private String name;
    private String description;
    private List<VariantResponse> variants;
}
