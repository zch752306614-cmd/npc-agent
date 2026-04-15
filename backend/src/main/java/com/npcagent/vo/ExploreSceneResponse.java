package com.npcagent.vo;

import lombok.Data;

@Data
public class ExploreSceneResponse {
    private boolean success;
    private PositionResponse position;
    private String eventType;
    private String eventDescription;
    private String reward;
    private String monster;
}
