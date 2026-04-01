package com.npcagent.service;

import com.npcagent.model.Scene;
import com.npcagent.model.Player;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 探索服务
 *
 * 实现游戏的探索系统：
 * 1. 场景切换
 * 2. 场景探索
 * 3. 随机事件触发
 * 4. 资源点交互
 */
@Service
public class ExplorationService {

    /**
     * 切换场景
     *
     * @param playerId 玩家ID
     * @param sceneCode 目标场景代码
     * @return 切换结果
     */
    public Map<String, Object> changeScene(String playerId, String sceneCode) {
        // 检查场景是否存在
        // 检查玩家是否满足场景进入条件
        // 更新玩家位置
        // 返回场景信息
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("sceneCode", sceneCode);
        result.put("sceneName", getSceneName(sceneCode));
        result.put("description", getSceneDescription(sceneCode));
        result.put("npcs", getSceneNpcs(sceneCode));
        result.put("resourcePoints", getSceneResourcePoints(sceneCode));
        
        return result;
    }

    /**
     * 探索场景
     *
     * @param playerId 玩家ID
     * @param sceneCode 当前场景代码
     * @param positionX X坐标
     * @param positionY Y坐标
     * @return 探索结果
     */
    public Map<String, Object> exploreScene(String playerId, String sceneCode, int positionX, int positionY) {
        // 检查位置是否有效
        // 触发随机事件
        // 发现资源点
        // 遇到怪物
        
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("success", true);
        result.put("position", Map.of("x", positionX, "y", positionY));
        
        // 模拟随机事件
        double random = Math.random();
        if (random < 0.3) {
            result.put("eventType", "resource");
            result.put("eventDescription", "你发现了一处灵草生长地");
            result.put("reward", "获得：灵草 x3");
        } else if (random < 0.6) {
            result.put("eventType", "monster");
            result.put("eventDescription", "一只妖兽出现在你面前");
            result.put("monster", "妖兽：青狼");
        } else {
            result.put("eventType", "exploration");
            result.put("eventDescription", "你在探索中发现了一条小路");
        }
        
        return result;
    }

    /**
     * 获取场景名称
     *
     * @param sceneCode 场景代码
     * @return 场景名称
     */
    private String getSceneName(String sceneCode) {
        Map<String, String> sceneNames = Map.of(
                "village", "青牛镇",
                "mountain", "青云山",
                "cave", "神秘洞穴"
        );
        return sceneNames.getOrDefault(sceneCode, "未知场景");
    }

    /**
     * 获取场景描述
     *
     * @param sceneCode 场景代码
     * @return 场景描述
     */
    private String getSceneDescription(String sceneCode) {
        Map<String, String> sceneDescriptions = Map.of(
                "village", "一个宁静的山村，位于青云山脚下，是通往青云门的必经之路",
                "mountain", "云雾缭绕的修仙圣地，青云门所在地，山巅有千年古刹",
                "cave", "青云山后山的一处隐秘洞穴，据说藏有上古传承"
        );
        return sceneDescriptions.getOrDefault(sceneCode, "未知场景");
    }

    /**
     * 获取场景中的NPC
     *
     * @param sceneCode 场景代码
     * @return NPC列表
     */
    private List<String> getSceneNpcs(String sceneCode) {
        Map<String, List<String>> sceneNpcs = Map.of(
                "village", List.of("elder", "villagerA", "pharmacyOwner"),
                "mountain", List.of("sectMaster", "seniorBrother", "seniorSister"),
                "cave", List.of("mysteriousElder")
        );
        return sceneNpcs.getOrDefault(sceneCode, List.of());
    }

    /**
     * 获取场景中的资源点
     *
     * @param sceneCode 场景代码
     * @return 资源点列表
     */
    private List<Map<String, Object>> getSceneResourcePoints(String sceneCode) {
        Map<String, List<Map<String, Object>>> sceneResources = Map.of(
                "village", List.of(
                        Map.of("name", "药田", "type", "herb", "description", "种植着各种灵草的田地"),
                        Map.of("name", "水井", "type", "water", "description", "清澈的井水，据说有灵气")
                ),
                "mountain", List.of(
                        Map.of("name", "灵石矿", "type", "mineral", "description", "蕴含灵气的矿石"),
                        Map.of("name", "瀑布", "type", "water", "description", "飞流直下的瀑布，水花四溅")
                ),
                "cave", List.of(
                        Map.of("name", "宝藏", "type", "treasure", "description", "散发着金光的宝藏"),
                        Map.of("name", "传承", "type", "artifact", "description", "古老的传承印记")
                )
        );
        return sceneResources.getOrDefault(sceneCode, List.of());
    }
}
