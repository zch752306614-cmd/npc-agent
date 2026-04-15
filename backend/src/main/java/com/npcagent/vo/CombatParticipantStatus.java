package com.npcagent.vo;

import lombok.Data;

@Data
public class CombatParticipantStatus {
    private String name;
    private int health;
    private int maxHealth;
    private int attack;
    private int defense;
    private int spiritualPower;
}
