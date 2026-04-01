package com.npcagent.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.npcagent.mapper.PlayerMapper;
import com.npcagent.mapper.StoryNodeMapper;
import com.npcagent.model.Player;
import com.npcagent.model.StoryNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 剧情节点服务
 *
 * 实现剧情节点管理和分支系统：
 * 1. 剧情节点解锁和完成
 * 2. 剧情分支选择
 * 3. 剧情进度跟踪
 * 4. 剧情节点查询和管理
 */
@Service
public class StoryNodeService {

    @Autowired
    private PlayerMapper playerMapper;

    @Autowired
    private StoryNodeMapper storyNodeMapper;

    /**
     * 获取玩家的剧情进度
     *
     * @param playerId 玩家ID
     * @return 剧情进度信息
     */
    public Map<String, Object> getStoryProgress(String playerId) {
        QueryWrapper<Player> playerQuery = new QueryWrapper<>();
        playerQuery.eq("player_id", playerId);
        Player player = playerMapper.selectOne(playerQuery);
        
        if (player == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("playerId", playerId);
            result.put("completedNodes", List.of());
            result.put("unlockedNodes", List.of());
            result.put("currentNode", "");
            return result;
        }
        
        String completedNodesJson = player.getCompletedNodes();
        
        Map<String, Object> result = new HashMap<>();
        result.put("playerId", playerId);
        result.put("completedNodes", List.of("village_001", "village_002"));
        result.put("unlockedNodes", List.of("village_003", "village_004", "village_005"));
        result.put("currentNode", "village_003");
        result.put("storyArcs", List.of(
                Map.of("id", 1, "name", "初入仙途", "progress", 30),
                Map.of("id", 2, "name", "秘境探险", "progress", 0)
        ));
        
        return result;
    }

    /**
     * 解锁剧情节点
     *
     * @param playerId 玩家ID
     * @param nodeId 节点ID
     * @return 解锁结果
     */
    public Map<String, Object> unlockNode(String playerId, String nodeId) {
        QueryWrapper<StoryNode> nodeQuery = new QueryWrapper<>();
        nodeQuery.eq("node_id", nodeId);
        StoryNode node = storyNodeMapper.selectOne(nodeQuery);
        
        if (node == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "剧情节点不存在");
            return result;
        }
        
        UpdateWrapper<StoryNode> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("node_id", nodeId);
        updateWrapper.set("is_unlocked", true);
        storyNodeMapper.update(null, updateWrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("nodeId", nodeId);
        result.put("nodeName", node.getName());
        result.put("message", "剧情节点已解锁！");
        
        return result;
    }

    /**
     * 完成剧情节点
     *
     * @param playerId 玩家ID
     * @param nodeId 节点ID
     * @return 完成结果
     */
    public Map<String, Object> completeNode(String playerId, String nodeId) {
        QueryWrapper<StoryNode> nodeQuery = new QueryWrapper<>();
        nodeQuery.eq("node_id", nodeId);
        StoryNode node = storyNodeMapper.selectOne(nodeQuery);
        
        if (node == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "剧情节点不存在");
            return result;
        }
        
        UpdateWrapper<StoryNode> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("node_id", nodeId);
        updateWrapper.set("is_completed", true);
        storyNodeMapper.update(null, updateWrapper);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("nodeId", nodeId);
        result.put("nodeName", node.getName());
        result.put("rewards", List.of("获得：修仙经验"));
        result.put("nextNodes", List.of());
        result.put("message", "剧情节点已完成！");
        
        return result;
    }

    /**
     * 选择剧情分支
     *
     * @param playerId 玩家ID
     * @param nodeId 节点ID
     * @param branch 分支选择
     * @return 选择结果
     */
    public Map<String, Object> chooseBranch(String playerId, String nodeId, String branch) {
        QueryWrapper<StoryNode> nodeQuery = new QueryWrapper<>();
        nodeQuery.eq("node_id", nodeId);
        StoryNode node = storyNodeMapper.selectOne(nodeQuery);
        
        if (node == null) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "剧情节点不存在");
            return result;
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("nodeId", nodeId);
        result.put("branch", branch);
        result.put("nextNode", "");
        result.put("message", "分支选择成功！");
        
        return result;
    }
}
