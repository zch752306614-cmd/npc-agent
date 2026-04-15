package com.npcagent.vo;

import lombok.Data;

@Data
public class InventoryOperationResponse {
    private boolean success;
    private Long itemId;
    private String itemName;
    private Integer quantity;
    private Integer remain;
    private String effect;
    private String message;
}
