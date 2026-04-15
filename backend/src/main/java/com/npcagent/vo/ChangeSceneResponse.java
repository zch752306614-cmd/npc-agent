package com.npcagent.vo;

import lombok.Data;

import java.util.List;

@Data
public class ChangeSceneResponse {
    private boolean success;
    private String sceneCode;
    private String sceneName;
    private String description;
    private List<String> npcs;
    private List<ResourcePointResponse> resourcePoints;
}
