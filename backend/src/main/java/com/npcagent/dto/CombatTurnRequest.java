package com.npcagent.dto;

import lombok.Data;

@Data
public class CombatTurnRequest {
    private String battleId;
    private String action;
}
