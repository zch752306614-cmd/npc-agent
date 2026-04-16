package com.npcagent.vo;

import lombok.Data;

import java.util.List;

@Data
public class StoryProgressResponse {
    private String playerId;
    private String currentNode;
    private List<String> completedNodes;
    private List<String> unlockedNodes;
    private int totalNodes;
}
