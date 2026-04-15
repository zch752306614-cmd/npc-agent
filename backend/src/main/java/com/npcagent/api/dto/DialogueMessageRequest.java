package com.npcagent.api.dto;

import lombok.Data;

@Data
public class DialogueMessageRequest {
    private String speaker;
    private String text;
}
