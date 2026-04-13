package com.npcagent.service;

import com.npcagent.model.*;
import com.npcagent.mapper.DialogueHistoryMapper;
import com.npcagent.rag.*;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.List;
import java.util.ArrayList;

/**
 * 对话服务
 *
 * 实现双模式对话系统：
 * 1. 固定选项模式：提供预设对话选项，玩家选择后推进剧情
 * 2. 自由输入模式：玩家自由输入，通过RAG系统生成回复
 *
 * 核心功能：
 * - 获取NPC对话选项
 * - 处理固定选项选择
 * - 处理自由输入
 * - 生成NPC回复
 * - 管理对话历史和上下文
 */
@Service
public class DialogueService {

    private final RAGStoryManager ragStoryManager;
    private final DialogueHistoryMapper dialogueHistoryMapper;

    public DialogueService(RAGStoryManager ragStoryManager, DialogueHistoryMapper dialogueHistoryMapper) {
        this.ragStoryManager = ragStoryManager;
        this.dialogueHistoryMapper = dialogueHistoryMapper;
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
        DialogueResult result = ragStoryManager.processFixedOption(npcCode, optionId, playerId);
        // 固定选项文本由RAG侧处理，这里记录NPC回复，保持接口可追溯
        saveDialogueEntry(playerId, npcCode, "npc", result.getNpcResponse());
        return result;
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
        // 直接调用RAGStoryManager的processDialogue方法
        RAGResult ragResult = ragStoryManager.processDialogue(npcCode, playerInput, playerId);
        
        // 转换为DialogueResult
        DialogueResult result = new DialogueResult();
        result.setNpcResponse(ragResult.getResponseContent());
        result.setTriggeredNodeId(ragResult.getTriggeredNodeId());
        result.setDialogueType(ragResult.getResponseType());
        result.setStoryAdvance(ragResult.isStoryAdvance());

        saveDialogueEntry(playerId, npcCode, "player", playerInput);
        saveDialogueEntry(playerId, npcCode, "npc", result.getNpcResponse());

        return result;
    }

    /**
     * 获取对话历史
     *
     * @param playerId 玩家ID
     * @param npcCode NPC代码
     * @return 对话历史
     */
    public List<DialogueHistory> getDialogueHistory(String playerId, String npcCode) {
        QueryWrapper<DialogueHistoryEntity> query = new QueryWrapper<>();
        query.eq("player_id", playerId)
                .eq("npc_code", npcCode)
                .orderByAsc("id");
        List<DialogueHistoryEntity> rows = dialogueHistoryMapper.selectList(query);
        List<DialogueHistory> result = new ArrayList<>();
        for (DialogueHistoryEntity row : rows) {
            DialogueHistory item = new DialogueHistory();
            item.setSpeaker(row.getSpeaker());
            if ("player".equals(row.getSpeaker())) {
                item.setPlayerInput(row.getText());
            } else {
                item.setNpcResponse(row.getText());
            }
            result.add(item);
        }
        return result;
    }

    /**
     * 保存对话历史
     *
     * @param playerId 玩家ID
     * @param npcCode NPC代码
     * @param dialogueHistory 对话历史
     */
    public void saveDialogueHistory(String playerId, String npcCode, List<DialogueHistory> dialogueHistory) {
        for (DialogueHistory history : dialogueHistory) {
            if ("player".equals(history.getSpeaker()) && history.getPlayerInput() != null) {
                saveDialogueEntry(playerId, npcCode, "player", history.getPlayerInput());
            } else if ("npc".equals(history.getSpeaker()) && history.getNpcResponse() != null) {
                saveDialogueEntry(playerId, npcCode, "npc", history.getNpcResponse());
            }
        }
    }

    private void saveDialogueEntry(String playerId, String npcCode, String speaker, String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        DialogueHistoryEntity entity = new DialogueHistoryEntity();
        entity.setPlayerId(playerId);
        entity.setNpcCode(npcCode);
        entity.setSpeaker(speaker);
        entity.setText(text);
        dialogueHistoryMapper.insert(entity);
    }
}
