package com.npcagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.npcagent.model.PlayerState;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 玩家状态Mapper
 *
 * 使用MyBatis-Plus实现数据库操作
 */
@Mapper
public interface PlayerStateMapper extends BaseMapper<PlayerState> {

    /**
     * 根据玩家ID查询玩家状态
     *
     * @param playerId 玩家ID
     * @return 玩家状态
     */
    @Select("SELECT player_id, current_scene, current_level, cultivation_level, experience, created_at, updated_at " +
            "FROM player_state WHERE player_id = #{playerId} LIMIT 1")
    PlayerState selectByPlayerId(@Param("playerId") String playerId);
}
