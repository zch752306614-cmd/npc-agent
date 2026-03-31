package com.npcagent.service;

import com.npcagent.model.StoryData;
import com.npcagent.rag.RAGStoryManager;
import com.npcagent.rag.RAGStoryManager.DialogueHistory;
import com.npcagent.rag.RAGStoryManager.PlayerState;
import com.npcagent.rag.RAGStoryManager.RAGResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DialogueManager {
    private final StoryData storyData;
    private final RAGStoryManager ragManager;
    private final Map<String, PlayerState> playerStates;
    private final RestTemplate restTemplate;

    public DialogueManager(StoryData storyData, RAGStoryManager ragManager) {
        this.storyData = storyData;
        this.ragManager = ragManager;
        this.playerStates = new HashMap<>();
        this.restTemplate = new RestTemplate();
    }

    public List<StoryData.Dialogue> getNpcDialogues(String npcName) {
        switch (npcName) {
            case "老者":
                return storyData.getNpcs().get老者().getDialogues();
            case "村民甲":
                return storyData.getNpcs().get村民甲().getDialogues();
            case "药铺老板":
                return storyData.getNpcs().get药铺老板().getDialogues();
            case "掌门":
                return storyData.getNpcs().get掌门().getDialogues();
            case "师兄":
                return storyData.getNpcs().get师兄().getDialogues();
            case "师姐":
                return storyData.getNpcs().get师姐().getDialogues();
            case "神秘老人":
                return storyData.getNpcs().get神秘老人().getDialogues();
            default:
                return new ArrayList<>();
        }
    }

    public String generateResponse(String npcName, String playerInput, List<Map<String, String>> dialogueHistory, String playerId) {
        PlayerState playerState = playerStates.computeIfAbsent(playerId, id -> new PlayerState());

        List<DialogueHistory> history = new ArrayList<>();
        for (Map<String, String> msg : dialogueHistory) {
            DialogueHistory dh = new DialogueHistory();
            dh.setSpeaker(msg.get("speaker"));
            dh.setText(msg.get("text"));
            history.add(dh);
        }

        RAGResult ragResult = ragManager.processDialogue(npcName, playerInput, history, playerState);

        if ("关键剧情".equals(ragResult.getResponseType()) || "引导剧情".equals(ragResult.getResponseType())) {
            if (ragResult.getNodeId() != null) {
                ragManager.updatePlayerState(playerState, ragResult.getNodeId());
            }

            String response = ragResult.getResponseContent();

            if (ragResult.getRewards() != null && !ragResult.getRewards().isEmpty()) {
                response += "\n\n【系统提示】" + String.join("、", ragResult.getRewards());
            }

            return response;
        }

        return generateFreeResponse(npcName, playerInput, history, ragResult.getCharacterSetting());
    }

    private String generateFreeResponse(String npcName, String playerInput, List<DialogueHistory> dialogueHistory, 
                                      RAGStoryManager.CharacterSetting characterSetting) {
        StringBuilder prompt = new StringBuilder();

        switch (npcName) {
            case "老者":
                prompt.append("你是老者，慈祥、见多识广、乐于助人。青牛镇的老居民，曾经是青云门的外门弟子，因资质有限未能更进一步。\n");
                break;
            case "村民甲":
                prompt.append("你是村民甲，热情、朴实、好奇。青牛镇的普通村民，世代居住于此。\n");
                break;
            case "药铺老板":
                prompt.append("你是药铺老板，精明、势利、见多识广。青牛镇药铺的老板，经常与修仙者打交道。\n");
                break;
            case "掌门":
                prompt.append("你是青云子，威严、公正、看重资质。青云门掌门，金丹期修士，掌管门派已有百年。\n");
                break;
            case "师兄":
                prompt.append("你是林峰，开朗、热心、好胜。青云门内门弟子，筑基期修士，入门已有十年。\n");
                break;
            case "师姐":
                prompt.append("你是林小婉，温柔、善良、细心。青云门内门弟子，筑基期修士，擅长炼丹。\n");
                break;
            case "神秘老人":
                prompt.append("你是神秘老人，神秘、高深、孤傲。隐居在神秘洞穴中的大能，疑似元婴期修士。\n");
                break;
        }

        if (characterSetting != null) {
            prompt.append("性格特点：").append(String.join("、", characterSetting.getPersonality())).append("\n");
            prompt.append("说话风格：").append(characterSetting.getSpeakingStyle()).append("\n");
            prompt.append("常用词汇：").append(String.join("、", characterSetting.getCommonVocabulary())).append("\n");
            if (!characterSetting.getTabooTopics().isEmpty()) {
                prompt.append("禁忌话题：").append(String.join("、", characterSetting.getTabooTopics())).append("\n");
            }
        }

        prompt.append("你正在和一个想要修仙的年轻人对话。\n");
        prompt.append("当前剧情：初入仙途 - 从凡人到修仙者的第一步\n");
        prompt.append("你的任务是：\n");
        prompt.append("1. 保持符合你人格的对话风格\n");
        prompt.append("2. 自然地回答玩家的问题\n");
        prompt.append("3. 使用符合修仙世界的语言和表达方式\n");
        prompt.append("4. 不要说任何现代或不符合修仙世界的内容\n");
        prompt.append("5. 保持对话的趣味性和互动性\n");
        prompt.append("对话历史：\n");

        List<DialogueHistory> recentHistory = dialogueHistory.size() > 10 ? 
                dialogueHistory.subList(dialogueHistory.size() - 10, dialogueHistory.size()) : dialogueHistory;

        for (DialogueHistory msg : recentHistory) {
            if ("player".equals(msg.getSpeaker())) {
                prompt.append("年轻人：").append(msg.getText()).append("\n");
            } else {
                prompt.append(npcName).append("：").append(msg.getText()).append("\n");
            }
        }

        prompt.append("年轻人：").append(playerInput).append("\n");
        prompt.append(npcName).append("：");

        try {
            String ollamaUrl = "http://localhost:11434/api/generate";
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "qwen3.5");
            requestBody.put("prompt", prompt.toString());
            requestBody.put("stream", false);
            requestBody.put("think", false);

            Map<String, Object> options = new HashMap<>();
            options.put("temperature", 0.7);
            options.put("num_predict", 800);
            requestBody.put("options", options);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            Map<String, Object> response = restTemplate.exchange(
                    ollamaUrl, HttpMethod.POST, entity, Map.class).getBody();

            if (response != null && response.containsKey("response")) {
                return response.get("response").toString().trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "抱歉，我暂时无法回答你的问题。";
    }

    public PlayerState getPlayerState(String playerId) {
        return playerStates.computeIfAbsent(playerId, id -> new PlayerState());
    }
}