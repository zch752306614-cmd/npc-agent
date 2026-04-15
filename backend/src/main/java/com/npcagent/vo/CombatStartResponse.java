package com.npcagent.vo;

import lombok.Data;

@Data
public class CombatStartResponse {
    private boolean success;
    private String battleId;
    private CombatParticipantStatus player;
    private CombatParticipantStatus monster;
    private int turn;
    private String currentTurn;
}
