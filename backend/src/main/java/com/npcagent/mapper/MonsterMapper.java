package com.npcagent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.npcagent.model.Monster;
import org.apache.ibatis.annotations.Mapper;

/**
 * 怪物数据访问层
 *
 * 继承MyBatis-Plus的BaseMapper接口，提供基础的CRUD操作
 * 自定义查询方法可以在此接口中定义
 */
@Mapper
public interface MonsterMapper extends BaseMapper<Monster> {
}
