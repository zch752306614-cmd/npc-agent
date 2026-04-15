package com.npcagent.vo;

import lombok.Data;

@Data
public class InventoryItemResponse {
    private Long id;
    private String name;
    private String type;
    private Integer quantity;
    private String description;
    private Integer rarity;
}
