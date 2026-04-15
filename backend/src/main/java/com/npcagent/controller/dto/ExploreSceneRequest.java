package com.npcagent.controller.dto;

import lombok.Data;

@Data
public class ExploreSceneRequest {
    private String playerId;
    private String sceneCode;

    private Integer positionX;
    private Integer positionY;
}
