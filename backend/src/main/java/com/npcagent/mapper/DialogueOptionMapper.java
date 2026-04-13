package com.npcagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.npcagent.model.DialogueOption;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 对话选项Mapper
 *
 * 使用MyBatis-Plus实现数据库操作
 */
@Mapper
public interface DialogueOptionMapper extends BaseMapper<DialogueOption> {

    /**
     * 根据NPC代码查询对话选项列表
     *
     * @param npcCode NPC代码
     * @return 对话选项列表
     */
    @Select("SELECT option_id, text, npc_code, node_id, next_node_id, response_template, order_index, enabled " +
            "FROM dialogue_option WHERE npc_code = #{npcCode} AND enabled = TRUE ORDER BY order_index ASC")
    List<DialogueOption> selectByNpcCode(@Param("npcCode") String npcCode);

    /**
     * 根据剧情节点ID查询对话选项列表
     *
     * @param nodeId 剧情节点ID
     * @return 对话选项列表
     */
    @Select("SELECT option_id, text, npc_code, node_id, next_node_id, response_template, order_index, enabled " +
            "FROM dialogue_option WHERE node_id = #{nodeId} AND enabled = TRUE ORDER BY order_index ASC")
    List<DialogueOption> selectByNodeId(@Param("nodeId") String nodeId);
}
