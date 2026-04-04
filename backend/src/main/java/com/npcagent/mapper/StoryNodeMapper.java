package com.npcagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.npcagent.model.StoryNode;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 剧情节点Mapper
 *
 * 使用MyBatis-Plus实现数据库操作
 */
public interface StoryNodeMapper extends BaseMapper<StoryNode> {

    /**
     * 根据NPC代码查询剧情节点列表
     *
     * @param npcCode NPC代码
     * @return 剧情节点列表
     */
    List<StoryNode> selectByNpcCode(@Param("npcCode") String npcCode);

    /**
     * 查询初始剧情节点
     *
     * @param npcCode NPC代码
     * @return 初始剧情节点
     */
    StoryNode selectInitialNode(@Param("npcCode") String npcCode);
}
