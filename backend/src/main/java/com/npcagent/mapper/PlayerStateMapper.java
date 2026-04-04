package com.npcagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.npcagent.model.PlayerState;
import org.apache.ibatis.annotations.Param;

/**
 * 玩家状态Mapper
 *
 * 使用MyBatis-Plus实现数据库操作
 */
public interface PlayerStateMapper extends BaseMapper<PlayerState> {

    /**
     * 根据玩家ID查询玩家状态
     *
     * @param playerId 玩家ID
     * @return 玩家状态
     */
    PlayerState selectByPlayerId(@Param("playerId") String playerId);
}
