package com.npcagent.service;

import com.npcagent.model.StoryNode;
import com.npcagent.rag.PlayerState;
import org.springframework.stereotype.Service;

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

    /**
     * 获取玩家的剧情进度
     *
     * @param playerId 玩家ID
     * @return 剧情进度信息
     */
    public Map<String, Object> getStoryProgress(String playerId) {
        // 从数据库获取玩家剧情进度
        // 这里使用模拟数据
        Map<String, Object> result = new java.util.HashMap<>();
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
        // 检查节点是否存在
        // 检查前置条件是否满足
        // 解锁节点
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("nodeId", nodeId);
        result.put("nodeName", getNodeName(nodeId));
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
        // 检查节点是否已解锁
        // 完成节点
        // 解锁后续节点
        // 发放奖励
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("nodeId", nodeId);
        result.put("nodeName", getNodeName(nodeId));
        result.put("rewards", getNodeRewards(nodeId));
        result.put("nextNodes", getNextNodes(nodeId));
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
        // 检查节点是否支持分支
        // 处理分支选择
        // 解锁对应分支的节点
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("nodeId", nodeId);
        result.put("branch", branch);
        result.put("nextNode", getBranchNextNode(nodeId, branch));
        result.put("message", "分支选择成功！");
        
        return result;
    }

    /**
     * 获取节点名称
     *
     * @param nodeId 节点ID
     * @return 节点名称
     */
    private String getNodeName(String nodeId) {
        Map<String, String> nodeNames = Map.of(
                "village_001", "老者引导",
                "village_002", "灵根测试",
                "village_003", "功法传授",
                "village_004", "鼓励坚持",
                "village_005", "选择放弃",
                "village_006", "前往青云门"
        );
        return nodeNames.getOrDefault(nodeId, "未知节点");
    }

    /**
     * 获取节点奖励
     *
     * @param nodeId 节点ID
     * @return 奖励列表
     */
    private List<String> getNodeRewards(String nodeId) {
        Map<String, List<String>> nodeRewards = Map.of(
                "village_001", List.of("获得：修仙入门知识"),
                "village_002", List.of("获得：灵根测试结果"),
                "village_003", List.of("获得：基础吐纳诀"),
                "village_004", List.of("获得：修炼建议"),
                "village_006", List.of("获得：青云门通行令")
        );
        return nodeRewards.getOrDefault(nodeId, List.of());
    }

    /**
     * 获取后续节点
     *
     * @param nodeId 节点ID
     * @return 后续节点列表
     */
    private List<String> getNextNodes(String nodeId) {
        Map<String, List<String>> nextNodes = Map.of(
                "village_001", List.of("village_002"),
                "village_002", List.of("village_003", "village_004", "village_005"),
                "village_003", List.of("village_006"),
                "village_004", List.of("village_006"),
                "village_006", List.of("mountain_001")
        );
        return nextNodes.getOrDefault(nodeId, List.of());
    }

    /**
     * 获取分支对应的下一个节点
     *
     * @param nodeId 节点ID
     * @param branch 分支选择
     * @return 下一个节点ID
     */
    private String getBranchNextNode(String nodeId, String branch) {
        if ("village_002".equals(nodeId)) {
            switch (branch) {
                case "天赋路线":
                    return "village_003";
                case "努力路线":
                    return "village_004";
                case "放弃修仙":
                    return "village_005";
                default:
                    return "village_003";
            }
        }
        return null;
    }
}
