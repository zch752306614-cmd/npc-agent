package com.npcagent.controller.dto;

import lombok.Data;

@Data
public class DialogueMessageRequest {
    private String speaker;
    private String text;
}
