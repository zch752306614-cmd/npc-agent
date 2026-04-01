package com.npcagent.service.impl;

import com.npcagent.rag.CharacterSetting;
import com.npcagent.rag.DialogueHistory;
import com.npcagent.service.AiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring AI服务实现
 *
 * 使用Spring AI框架实现对话生成：
 * 1. 支持OpenAI、Ollama等多种AI后端
 * 2. 使用Prompt Engineering构建角色设定
 * 3. 管理对话历史和上下文
 *
 * 实现说明：
 * - 使用ChatClient进行对话生成
 * - 通过SystemMessage设置角色设定
 * - 通过UserMessage传递玩家输入
 * - 当ChatClient不可用时，使用fallback响应
 */
@Service
public class SpringAiServiceImpl implements AiService {

    private static final Logger logger = LoggerFactory.getLogger(SpringAiServiceImpl.class);

    private final ChatClient chatClient;

    @Autowired
    public SpringAiServiceImpl(@Autowired(required = false) ChatClient chatClient) {
        this.chatClient = chatClient;
        if (chatClient == null) {
            logger.warn("ChatClient is not available, AI service will use fallback responses");
        }
    }

    @Override
    public String generateResponse(CharacterSetting characterSetting, String playerInput, 
                                   List<DialogueHistory> dialogueHistory) {
        if (chatClient == null) {
            return generateFallbackResponse(characterSetting, playerInput);
        }

        try {
            List<Message> messages = new ArrayList<>();

            String systemPrompt = buildSystemPrompt(characterSetting);
            messages.add(new SystemMessage(systemPrompt));

            for (DialogueHistory history : dialogueHistory) {
                if ("player".equals(history.getSpeaker())) {
                    messages.add(new UserMessage(history.getText()));
                } else {
                    messages.add(new org.springframework.ai.chat.messages.AssistantMessage(history.getText()));
                }
            }

            messages.add(new UserMessage(playerInput));

            Prompt prompt = new Prompt(messages);
            String response = chatClient.call(prompt).getResult().getOutput().getContent();

            logger.debug("Generated AI response for character: {}", characterSetting.getCharacterName());
            return response;
        } catch (Exception e) {
            logger.error("Failed to generate AI response: {}", e.getMessage());
            return generateFallbackResponse(characterSetting, playerInput);
        }
    }

    @Override
    public String generateGenericResponse(String npcCode, String playerInput,
                                         List<DialogueHistory> dialogueHistory) {
        if (chatClient == null) {
            return "（若有所思地看着你）这位道友，你的话让我有些困惑。";
        }

        try {
            List<Message> messages = new ArrayList<>();

            String systemPrompt = "你是一个修仙世界的NPC，请用古风、温和的语气回应玩家。";
            messages.add(new SystemMessage(systemPrompt));

            for (DialogueHistory history : dialogueHistory) {
                if ("player".equals(history.getSpeaker())) {
                    messages.add(new UserMessage(history.getText()));
                } else {
                    messages.add(new org.springframework.ai.chat.messages.AssistantMessage(history.getText()));
                }
            }

            messages.add(new UserMessage(playerInput));

            Prompt prompt = new Prompt(messages);
            String response = chatClient.call(prompt).getResult().getOutput().getContent();

            logger.debug("Generated generic AI response for NPC: {}", npcCode);
            return response;
        } catch (Exception e) {
            logger.error("Failed to generate generic AI response: {}", e.getMessage());
            return "（若有所思地看着你）这位道友，你的话让我有些困惑。";
        }
    }

    @Override
    public boolean isAvailable() {
        return chatClient != null;
    }

    private String buildSystemPrompt(CharacterSetting characterSetting) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("你现在是").append(characterSetting.getCharacterName()).append("。\n\n");
        
        prompt.append("性格特点：");
        prompt.append(String.join("、", characterSetting.getPersonality()));
        prompt.append("。\n\n");
        
        prompt.append("说话风格：");
        prompt.append(characterSetting.getSpeakingStyle());
        prompt.append("。\n\n");
        
        prompt.append("常用词汇：");
        prompt.append(String.join("、", characterSetting.getCommonVocabulary()));
        prompt.append("。\n\n");
        
        if (!characterSetting.getTabooTopics().isEmpty()) {
            prompt.append("禁忌话题：你从不谈论");
            prompt.append(String.join("、", characterSetting.getTabooTopics()));
            prompt.append("。\n\n");
        }
        
        prompt.append("请严格按照以上设定进行角色扮演，保持角色的一致性。");
        
        return prompt.toString();
    }

    private String generateFallbackResponse(CharacterSetting characterSetting, String playerInput) {
        List<String> fallbackResponses = List.of(
            "（若有所思地看着你）这位道友，你的话让我有些困惑。",
            "（微微点头）嗯，你说得有道理。",
            "（沉思片刻）这个问题值得深思。",
            "（笑了笑）道友真是有趣。",
            "（摇了摇头）此事说来话长。"
        );
        
        int index = (int) (System.currentTimeMillis() % fallbackResponses.size());
        return fallbackResponses.get(index);
    }
}
