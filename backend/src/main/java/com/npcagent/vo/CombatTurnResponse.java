package com.npcagent.vo;

import lombok.Data;

@Data
public class CombatTurnResponse {
    private boolean success;
    private String battleId;
    private int turn;
    private String currentTurn;
    private String playerAction;
    private int playerDamage;
    private String monsterAction;
    private int monsterDamage;
    private int playerHealth;
    private int monsterHealth;
    private String battleStatus;
}
