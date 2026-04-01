package com.npcagent.service;

import com.npcagent.model.*;
import com.npcagent.rag.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 对话服务
 *
 * 实现双模式对话系统：
 * 1. 固定选项模式：提供预设对话选项，玩家选择后推进剧情
 * 2. 自由输入模式：玩家自由输入，通过语义匹配触发剧情
 *
 * 核心功能：
 * - 获取NPC对话选项
 * - 处理固定选项选择
 * - 处理自由输入语义匹配
 * - 生成NPC回复
 * - 管理对话历史和上下文
 */
@Service
public class DialogueService {

    private final RAGStoryManager ragStoryManager;
    private final SemanticMatchingService semanticMatchingService;

    public DialogueService(RAGStoryManager ragStoryManager, SemanticMatchingService semanticMatchingService) {
        this.ragStoryManager = ragStoryManager;
        this.semanticMatchingService = semanticMatchingService;
    }

    /**
     * 获取NPC的对话选项
     *
     * @param npcCode NPC代码
     * @param playerId 玩家ID
     * @return 对话选项列表
     */
    public List<DialogueOption> getNpcDialogueOptions(String npcCode, String playerId) {
        // 从数据库或缓存获取NPC的对话选项
        // 考虑玩家的剧情进度和状态
        return ragStoryManager.getDialogueOptions(npcCode, playerId);
    }

    /**
     * 处理玩家选择固定对话选项
     *
     * @param npcCode NPC代码
     * @param optionId 选项ID
     * @param playerId 玩家ID
     * @return 对话结果
     */
    public DialogueResult handleFixedOption(String npcCode, String optionId, String playerId) {
        // 处理固定选项选择
        // 推进剧情节点
        // 生成NPC回复
        return ragStoryManager.processFixedOption(npcCode, optionId, playerId);
    }

    /**
     * 处理玩家自由输入
     *
     * @param npcCode NPC代码
     * @param playerInput 玩家输入
     * @param dialogueHistory 对话历史
     * @param playerId 玩家ID
     * @return 对话结果
     */
    public DialogueResult handleFreeInput(String npcCode, String playerInput, List<DialogueHistory> dialogueHistory, String playerId) {
        // 1. 进行语义匹配
        SemanticMatchResult matchResult = semanticMatchingService.matchInput(playerInput, npcCode);

        // 2. 根据匹配结果处理
        if (matchResult.isMatch()) {
            // 匹配成功，触发剧情节点
            return ragStoryManager.processSemanticMatch(matchResult, playerId, dialogueHistory);
        } else {
            // 匹配失败，生成通用回复
            return ragStoryManager.generateGenericResponse(npcCode, playerInput, dialogueHistory);
        }
    }

    /**
     * 获取对话历史
     *
     * @param playerId 玩家ID
     * @param npcCode NPC代码
     * @return 对话历史
     */
    public List<DialogueHistory> getDialogueHistory(String playerId, String npcCode) {
        // 从数据库或缓存获取对话历史
        return ragStoryManager.getDialogueHistory(playerId, npcCode);
    }

    /**
     * 保存对话历史
     *
     * @param playerId 玩家ID
     * @param npcCode NPC代码
     * @param dialogueHistory 对话历史
     */
    public void saveDialogueHistory(String playerId, String npcCode, List<DialogueHistory> dialogueHistory) {
        // 保存对话历史到数据库或缓存
        ragStoryManager.saveDialogueHistory(playerId, npcCode, dialogueHistory);
    }
}
