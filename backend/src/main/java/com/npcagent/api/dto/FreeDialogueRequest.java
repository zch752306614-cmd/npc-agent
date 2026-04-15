package com.npcagent.api.dto;

import lombok.Data;

import java.util.List;

@Data
public class FreeDialogueRequest {
    private String npcCode;
    private String input;
    private String playerId;
    private List<DialogueMessageRequest> history;
}
