package com.npcagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.npcagent.model.StoryNode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 剧情节点Mapper
 *
 * 使用MyBatis-Plus实现数据库操作
 */
@Mapper
public interface StoryNodeMapper extends BaseMapper<StoryNode> {

    /**
     * 根据NPC代码查询剧情节点列表
     *
     * @param npcCode NPC代码
     * @return 剧情节点列表
     */
    @Select("SELECT node_id, title, description, content, npc_code, prerequisite_node_id, next_node_id, is_initial, enabled " +
            "FROM story_node WHERE npc_code = #{npcCode} AND (enabled = TRUE OR enabled IS NULL)")
    List<StoryNode> selectByNpcCode(@Param("npcCode") String npcCode);

    /**
     * 查询初始剧情节点
     *
     * @param npcCode NPC代码
     * @return 初始剧情节点
     */
    @Select("SELECT node_id, title, description, content, npc_code, prerequisite_node_id, next_node_id, is_initial, enabled " +
            "FROM story_node WHERE npc_code = #{npcCode} AND is_initial = TRUE AND (enabled = TRUE OR enabled IS NULL) LIMIT 1")
    StoryNode selectInitialNode(@Param("npcCode") String npcCode);
}
