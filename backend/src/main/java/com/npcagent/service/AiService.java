package com.npcagent.service;

import com.npcagent.rag.CharacterSetting;
import java.util.List;

/**
 * AI服务接口
 *
 * 定义AI对话生成的核心功能：
 * 1. 基于角色设定生成对话回复
 * 2. 保持对话上下文连贯性
 * 3. 支持不同的AI后端（OpenAI、Ollama等）
 *
 * 实现说明：
 * - 使用Spring AI作为底层框架
 * - 支持Prompt Engineering
 * - 支持对话历史管理
 */
public interface AiService {

    /**
     * 生成NPC对话回复
     *
     * @param characterSetting 角色设定
     * @param playerInput 玩家输入
     * @param dialogueHistory 对话历史
     * @return NPC回复内容
     */
    String generateResponse(CharacterSetting characterSetting, String playerInput, 
                           List<com.npcagent.rag.DialogueHistory> dialogueHistory);

    /**
     * 生成通用回复（无角色设定）
     *
     * @param npcCode NPC代码
     * @param playerInput 玩家输入
     * @param dialogueHistory 对话历史
     * @return NPC回复内容
     */
    String generateGenericResponse(String npcCode, String playerInput,
                                   List<com.npcagent.rag.DialogueHistory> dialogueHistory);

    /**
     * 检查AI服务是否可用
     *
     * @return true if available
     */
    boolean isAvailable();
}
