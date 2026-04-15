package com.npcagent.vo;

import lombok.Data;

@Data
public class TreasureHuntResponse {
    private boolean success;
    private String location;
    private boolean treasureFound;
    private String reward;
    private String rarity;
    private String message;
    private boolean rewardGranted;
    private InventoryOperationResponse grantDetail;
}
