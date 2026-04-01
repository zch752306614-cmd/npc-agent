package com.npcagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.npcagent.model.StoryNode;
import org.apache.ibatis.annotations.Mapper;

/**
 * 剧情节点数据访问层
 *
 * 继承MyBatis-Plus的BaseMapper接口，提供基础的CRUD操作
 * 自定义查询方法可以在此接口中定义
 */
@Mapper
public interface StoryNodeMapper extends BaseMapper<StoryNode> {
}
