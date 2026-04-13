package com.npcagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.npcagent.model.NpcCharacter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * NPC角色Mapper
 *
 * 使用MyBatis-Plus实现数据库操作
 */
@Mapper
public interface NpcCharacterMapper extends BaseMapper<NpcCharacter> {

    /**
     * 根据代码查询NPC
     *
     * @param code NPC代码
     * @return NPC角色
     */
    @Select("SELECT code, name, personality, speaking_style, scene_code, description, initial_node_id, enabled " +
            "FROM npc_character WHERE code = #{code} LIMIT 1")
    NpcCharacter selectByCode(@Param("code") String code);

    /**
     * 根据场景查询NPC列表
     *
     * @param sceneCode 场景代码
     * @return NPC角色列表
     */
    @Select("SELECT code, name, personality, speaking_style, scene_code, description, initial_node_id, enabled " +
            "FROM npc_character WHERE scene_code = #{sceneCode} AND enabled = TRUE")
    List<NpcCharacter> selectByScene(@Param("sceneCode") String sceneCode);
}
