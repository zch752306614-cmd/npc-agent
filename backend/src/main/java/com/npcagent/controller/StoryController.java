package com.npcagent.controller;

import com.npcagent.model.StoryData;
import com.npcagent.service.DialogueManager;
import com.npcagent.rag.RAGStoryManager.PlayerState;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class StoryController {
    private final StoryData storyData;
    private final DialogueManager dialogueManager;

    public StoryController(StoryData storyData, DialogueManager dialogueManager) {
        this.storyData = storyData;
        this.dialogueManager = dialogueManager;
    }

    @GetMapping("/scenes")
    public StoryData.Scenes getScenes() {
        return storyData.getScenes();
    }

    @GetMapping("/npcs/{npcName}/dialogues")
    public List<StoryData.Dialogue> getNpcDialogues(@PathVariable String npcName) {
        return dialogueManager.getNpcDialogues(npcName);
    }

    @PostMapping("/npcs/{npcName}/chat")
    public Map<String, String> chatWithNpc(@PathVariable String npcName, @RequestBody Map<String, Object> data) {
        String playerInput = (String) data.get("input");
        List<Map<String, String>> dialogueHistory = (List<Map<String, String>>) data.get("history");
        String playerId = (String) data.getOrDefault("player_id", "default");

        String response = dialogueManager.generateResponse(npcName, playerInput, dialogueHistory, playerId);
        return Map.of("response", response);
    }

    @GetMapping("/player/state")
    public PlayerState getPlayerState(@RequestParam(defaultValue = "default") String player_id) {
        return dialogueManager.getPlayerState(player_id);
    }
}