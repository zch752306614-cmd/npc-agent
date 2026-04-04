package com.npcagent.service;

import com.npcagent.model.NpcCharacter;
import com.npcagent.mapper.NpcCharacterMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * NPC角色服务
 *
 * 从数据库加载NPC角色设定，替代硬编码实现
 * 支持动态配置NPC性格和说话风格
 */
@Service
public class NpcService {

    private static final Logger logger = LoggerFactory.getLogger(NpcService.class);

    private final NpcCharacterMapper npcCharacterMapper;

    public NpcService(NpcCharacterMapper npcCharacterMapper) {
        this.npcCharacterMapper = npcCharacterMapper;
    }

    /**
     * 根据代码获取NPC角色
     *
     * @param npcCode NPC代码
     * @return NPC角色
     */
    public NpcCharacter getNpcByCode(String npcCode) {
        try {
            return npcCharacterMapper.selectByCode(npcCode);
        } catch (Exception e) {
            logger.error("Error getting NPC by code: {}", npcCode, e);
            return null;
        }
    }

    /**
     * 获取所有NPC角色
     *
     * @return NPC角色列表
     */
    public List<NpcCharacter> getAllNpcs() {
        try {
            return npcCharacterMapper.selectList(null);
        } catch (Exception e) {
            logger.error("Error getting all NPCs", e);
            return List.of();
        }
    }

    /**
     * 获取场景中的NPC列表
     *
     * @param sceneCode 场景代码
     * @return NPC角色列表
     */
    public List<NpcCharacter> getNpcsByScene(String sceneCode) {
        try {
            return npcCharacterMapper.selectByScene(sceneCode);
        } catch (Exception e) {
            logger.error("Error getting NPCs by scene: {}", sceneCode, e);
            return List.of();
        }
    }
}
