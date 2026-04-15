package com.npcagent.service;

import com.npcagent.model.*;
import com.npcagent.common.exception.BusinessException;
import com.npcagent.controller.dto.DialogueMessageRequest;
import com.npcagent.controller.dto.FreeDialogueRequest;
import com.npcagent.mapper.DialogueHistoryMapper;
import com.npcagent.rag.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private static final Logger logger = LoggerFactory.getLogger(DialogueService.class);
    private static final String SPEAKER_PLAYER = "player";
    private static final String SPEAKER_NPC = "npc";

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
        validateRequiredText(npcCode, "npcCode 不能为空");
        validateRequiredText(playerId, "playerId 不能为空");
        logger.info("Load dialogue options, npcCode={}, playerId={}", npcCode, playerId);
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
        validateRequiredText(npcCode, "npcCode 不能为空");
        validateRequiredText(optionId, "optionId 不能为空");
        validateRequiredText(playerId, "playerId 不能为空");
        logger.info("Handle fixed dialogue, npcCode={}, optionId={}, playerId={}", npcCode, optionId, playerId);
        DialogueResult result = ragStoryManager.processFixedOption(npcCode, optionId, playerId);
        saveDialogueEntry(playerId, npcCode, SPEAKER_NPC, result.getNpcResponse());
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
    public DialogueResult handleFreeInput(FreeDialogueRequest request) {
        validateFreeDialogueRequest(request);
        logger.info("Handle free dialogue, npcCode={}, playerId={}", request.getNpcCode(), request.getPlayerId());

        RAGResult ragResult = ragStoryManager.processDialogue(request.getNpcCode(), request.getInput(), request.getPlayerId());
        DialogueResult result = buildDialogueResult(ragResult);

        saveDialogueEntry(request.getPlayerId(), request.getNpcCode(), SPEAKER_PLAYER, request.getInput());
        saveDialogueEntry(request.getPlayerId(), request.getNpcCode(), SPEAKER_NPC, result.getNpcResponse());
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
        validateRequiredText(playerId, "playerId 不能为空");
        validateRequiredText(npcCode, "npcCode 不能为空");
        logger.info("Query dialogue history, playerId={}, npcCode={}", playerId, npcCode);

        QueryWrapper<DialogueHistoryEntity> query = new QueryWrapper<>();
        query.eq("player_id", playerId).eq("npc_code", npcCode).orderByAsc("id");
        return toDialogueHistoryFromEntity(dialogueHistoryMapper.selectList(query));
    }

    /**
     * 保存对话历史
     *
     * @param playerId 玩家ID
     * @param npcCode NPC代码
     * @param dialogueHistory 对话历史
     */
    public void saveDialogueHistory(String playerId, String npcCode, List<DialogueHistory> dialogueHistory) {
        validateRequiredText(playerId, "playerId 不能为空");
        validateRequiredText(npcCode, "npcCode 不能为空");
        if (dialogueHistory == null || dialogueHistory.isEmpty()) {
            return;
        }

        for (DialogueHistory history : dialogueHistory) {
            if (SPEAKER_PLAYER.equals(history.getSpeaker()) && history.getPlayerInput() != null) {
                saveDialogueEntry(playerId, npcCode, SPEAKER_PLAYER, history.getPlayerInput());
            } else if (SPEAKER_NPC.equals(history.getSpeaker()) && history.getNpcResponse() != null) {
                saveDialogueEntry(playerId, npcCode, SPEAKER_NPC, history.getNpcResponse());
            }
        }
    }

    public List<DialogueHistory> toDialogueHistory(List<DialogueMessageRequest> historyRequests) {
        if (historyRequests == null || historyRequests.isEmpty()) {
            return Collections.emptyList();
        }

        List<DialogueHistory> dialogueHistoryList = new ArrayList<>();
        for (DialogueMessageRequest message : historyRequests) {
            if (message == null || message.getText() == null || message.getText().isBlank()) {
                continue;
            }
            DialogueHistory item = new DialogueHistory();
            item.setSpeaker(message.getSpeaker());
            if (SPEAKER_PLAYER.equals(message.getSpeaker())) {
                item.setPlayerInput(message.getText());
            } else {
                item.setNpcResponse(message.getText());
            }
            dialogueHistoryList.add(item);
        }
        return dialogueHistoryList;
    }

    private List<DialogueHistory> toDialogueHistoryFromEntity(List<DialogueHistoryEntity> entities) {
        List<DialogueHistory> result = new ArrayList<>();
        for (DialogueHistoryEntity entity : entities) {
            DialogueHistory item = new DialogueHistory();
            item.setSpeaker(entity.getSpeaker());
            if (SPEAKER_PLAYER.equals(entity.getSpeaker())) {
                item.setPlayerInput(entity.getText());
            } else {
                item.setNpcResponse(entity.getText());
            }
            result.add(item);
        }
        return result;
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

    private DialogueResult buildDialogueResult(RAGResult ragResult) {
        DialogueResult result = new DialogueResult();
        result.setNpcResponse(ragResult.getResponseContent());
        result.setTriggeredNodeId(ragResult.getTriggeredNodeId());
        result.setDialogueType(ragResult.getResponseType());
        result.setStoryAdvance(ragResult.isStoryAdvance());
        return result;
    }

    private void validateFreeDialogueRequest(FreeDialogueRequest request) {
        if (request == null) {
            throw BusinessException.badRequest("请求体不能为空");
        }
        validateRequiredText(request.getNpcCode(), "npcCode 不能为空");
        validateRequiredText(request.getInput(), "input 不能为空");
        validateRequiredText(request.getPlayerId(), "playerId 不能为空");
    }

    private void validateRequiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw BusinessException.badRequest(message);
        }
    }
}
