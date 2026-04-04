package com.npcagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.npcagent.model.PlayerStoryProgress;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 玩家剧情进度Mapper
 *
 * 使用MyBatis-Plus实现数据库操作
 */
public interface PlayerStoryProgressMapper extends BaseMapper<PlayerStoryProgress> {

    /**
     * 根据玩家ID查询剧情进度列表
     *
     * @param playerId 玩家ID
     * @return 剧情进度列表
     */
    List<PlayerStoryProgress> selectByPlayerId(@Param("playerId") String playerId);

    /**
     * 根据玩家ID和节点ID查询剧情进度
     *
     * @param playerId 玩家ID
     * @param nodeId   节点ID
     * @return 剧情进度
     */
    PlayerStoryProgress selectByPlayerIdAndNodeId(@Param("playerId") String playerId, @Param("nodeId") String nodeId);

    /**
     * 查询玩家已完成的剧情节点
     *
     * @param playerId 玩家ID
     * @return 已完成的节点ID列表
     */
    List<String> selectCompletedNodesByPlayerId(@Param("playerId") String playerId);

    /**
     * 查询玩家已解锁的剧情节点
     *
     * @param playerId 玩家ID
     * @return 已解锁的节点ID列表
     */
    List<String> selectUnlockedNodesByPlayerId(@Param("playerId") String playerId);
}
