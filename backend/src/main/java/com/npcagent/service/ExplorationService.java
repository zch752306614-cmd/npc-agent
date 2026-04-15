package com.npcagent.service;

import com.npcagent.common.exception.BusinessException;
import com.npcagent.vo.ChangeSceneResponse;
import com.npcagent.vo.ExploreSceneResponse;
import com.npcagent.vo.PositionResponse;
import com.npcagent.vo.ResourcePointResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;

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

    private static final Logger logger = LoggerFactory.getLogger(ExplorationService.class);
    private static final double RESOURCE_EVENT_THRESHOLD = 0.3;
    private static final double MONSTER_EVENT_THRESHOLD = 0.6;
    private final Random random = new Random();

    /**
     * 切换场景
     *
     * @param playerId 玩家ID
     * @param sceneCode 目标场景代码
     * @return 切换结果
     */
    public ChangeSceneResponse changeScene(String playerId, String sceneCode) {
        validateRequired(playerId, "playerId 不能为空");
        validateRequired(sceneCode, "sceneCode 不能为空");
        logger.info("Change scene, playerId={}, sceneCode={}", playerId, sceneCode);

        ChangeSceneResponse result = new ChangeSceneResponse();
        result.setSuccess(true);
        result.setSceneCode(sceneCode);
        result.setSceneName(getSceneName(sceneCode));
        result.setDescription(getSceneDescription(sceneCode));
        result.setNpcs(getSceneNpcs(sceneCode));
        result.setResourcePoints(getSceneResourcePoints(sceneCode));
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
    public ExploreSceneResponse exploreScene(String playerId, String sceneCode, int positionX, int positionY) {
        validateRequired(playerId, "playerId 不能为空");
        validateRequired(sceneCode, "sceneCode 不能为空");
        logger.info("Explore scene, playerId={}, sceneCode={}, x={}, y={}", playerId, sceneCode, positionX, positionY);

        ExploreSceneResponse result = new ExploreSceneResponse();
        result.setSuccess(true);
        PositionResponse position = new PositionResponse();
        position.setX(positionX);
        position.setY(positionY);
        result.setPosition(position);
        appendRandomEvent(result);
        return result;
    }

    private void appendRandomEvent(ExploreSceneResponse result) {
        double eventRoll = random.nextDouble();
        if (eventRoll < RESOURCE_EVENT_THRESHOLD) {
            result.setEventType("resource");
            result.setEventDescription("你发现了一处灵草生长地");
            result.setReward("获得：灵草 x3");
            return;
        }

        if (eventRoll < MONSTER_EVENT_THRESHOLD) {
            result.setEventType("monster");
            result.setEventDescription("一只妖兽出现在你面前");
            result.setMonster("妖兽：青狼");
            return;
        }

        result.setEventType("exploration");
        result.setEventDescription("你在探索中发现了一条小路");
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
    private List<ResourcePointResponse> getSceneResourcePoints(String sceneCode) {
        Map<String, List<ResourcePointResponse>> sceneResources = Map.of(
                "village", List.of(
                        buildResourcePoint("药田", "herb", "种植着各种灵草的田地"),
                        buildResourcePoint("水井", "water", "清澈的井水，据说有灵气")
                ),
                "mountain", List.of(
                        buildResourcePoint("灵石矿", "mineral", "蕴含灵气的矿石"),
                        buildResourcePoint("瀑布", "water", "飞流直下的瀑布，水花四溅")
                ),
                "cave", List.of(
                        buildResourcePoint("宝藏", "treasure", "散发着金光的宝藏"),
                        buildResourcePoint("传承", "artifact", "古老的传承印记")
                )
        );
        return sceneResources.getOrDefault(sceneCode, List.of());
    }

    private ResourcePointResponse buildResourcePoint(String name, String type, String description) {
        ResourcePointResponse response = new ResourcePointResponse();
        response.setName(name);
        response.setType(type);
        response.setDescription(description);
        return response;
    }

    private void validateRequired(String value, String message) {
        if (value == null || value.isBlank()) {
            throw BusinessException.badRequest(message);
        }
    }
}
