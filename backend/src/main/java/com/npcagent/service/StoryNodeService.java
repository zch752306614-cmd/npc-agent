package com.npcagent.service;

import com.npcagent.model.DialogueOption;
import com.npcagent.model.StoryNode;
import com.npcagent.vo.StoryProgressResponse;
import com.npcagent.mapper.StoryNodeMapper;
import com.npcagent.mapper.DialogueOptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 剧情节点服务
 *
 * 从数据库加载剧情节点和对话选项，替代硬编码实现
 * 支持动态配置剧情内容，无需修改代码
 */
@Service
public class StoryNodeService {

    private static final Logger logger = LoggerFactory.getLogger(StoryNodeService.class);

    private final StoryNodeMapper storyNodeMapper;
    private final DialogueOptionMapper dialogueOptionMapper;

    public StoryNodeService(StoryNodeMapper storyNodeMapper, DialogueOptionMapper dialogueOptionMapper) {
        this.storyNodeMapper = storyNodeMapper;
        this.dialogueOptionMapper = dialogueOptionMapper;
    }

    /**
     * 根据ID获取剧情节点
     *
     * @param nodeId 节点ID
     * @return 剧情节点
     */
    public StoryNode getStoryNodeById(String nodeId) {
        try {
            return storyNodeMapper.selectById(nodeId);
        } catch (Exception e) {
            logger.error("Error getting story node by id: {}", nodeId, e);
            return null;
        }
    }

    /**
     * 获取NPC的可用对话选项
     *
     * @param npcCode NPC代码
     * @param playerId 玩家ID
     * @return 对话选项列表
     */
    public List<DialogueOption> getAvailableDialogueOptions(String npcCode, String playerId) {
        try {
            // 初始版本：只返回初始节点的对话选项
            // 后续可以根据玩家进度返回不同节点的选项
            return dialogueOptionMapper.selectByNodeId("node_1_1");
        } catch (Exception e) {
            logger.error("Error getting dialogue options for NPC: {}", npcCode, e);
            return List.of();
        }
    }

    /**
     * 根据ID获取对话选项
     *
     * @param optionId 选项ID
     * @return 对话选项
     */
    public DialogueOption getDialogueOptionById(String optionId) {
        try {
            return dialogueOptionMapper.selectById(optionId);
        } catch (Exception e) {
            logger.error("Error getting dialogue option by id: {}", optionId, e);
            return null;
        }
    }

    /**
     * 根据剧情节点ID获取对话选项
     *
     * @param nodeId 剧情节点ID
     * @return 对话选项列表
     */
    public List<DialogueOption> getDialogueOptionsByNodeId(String nodeId) {
        try {
            return dialogueOptionMapper.selectByNodeId(nodeId);
        } catch (Exception e) {
            logger.error("Error getting dialogue options by node id: {}", nodeId, e);
            return List.of();
        }
    }

    /**
     * 获取所有剧情节点（用于初始化向量库）
     *
     * @return 剧情节点列表
     */
    public List<StoryNode> getAllStoryNodes() {
        try {
            return storyNodeMapper.selectList(null);
        } catch (Exception e) {
            logger.error("Error getting all story nodes", e);
            return List.of();
        }
    }

    /**
     * 获取玩家剧情进度（最小可用实现）
     *
     * 当前版本未接入玩家剧情进度表的完整读写链路，
     * 先基于剧情节点顺序返回可展示的进度结构，避免前后端接口断裂。
     *
     * @param playerId 玩家ID
     * @return 剧情进度信息
     */
    public StoryProgressResponse getStoryProgress(String playerId) {
        List<StoryNode> nodes = getAllStoryNodes();
        List<String> orderedNodeIds = nodes.stream()
                .map(StoryNode::getNodeId)
                .toList();

        StoryProgressResponse result = new StoryProgressResponse();
        result.setPlayerId(playerId);
        result.setCurrentNode(orderedNodeIds.isEmpty() ? "暂无剧情节点" : orderedNodeIds.get(0));
        result.setCompletedNodes(List.of());
        result.setUnlockedNodes(orderedNodeIds);
        result.setTotalNodes(orderedNodeIds.size());
        return result;
    }
}
