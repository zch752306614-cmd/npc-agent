package com.npcagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.npcagent.model.NpcCharacter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * NPC角色Mapper
 *
 * 使用MyBatis-Plus实现数据库操作
 */
public interface NpcCharacterMapper extends BaseMapper<NpcCharacter> {

    /**
     * 根据代码查询NPC
     *
     * @param code NPC代码
     * @return NPC角色
     */
    NpcCharacter selectByCode(@Param("code") String code);

    /**
     * 根据场景查询NPC列表
     *
     * @param sceneCode 场景代码
     * @return NPC角色列表
     */
    List<NpcCharacter> selectByScene(@Param("sceneCode") String sceneCode);
}
