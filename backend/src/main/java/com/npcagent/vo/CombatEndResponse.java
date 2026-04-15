package com.npcagent.vo;

import lombok.Data;

import java.util.List;

@Data
public class CombatEndResponse {
    private boolean success;
    private String battleId;
    private String winner;
    private Integer experience;
    private List<String> rewards;
    private String message;
}
