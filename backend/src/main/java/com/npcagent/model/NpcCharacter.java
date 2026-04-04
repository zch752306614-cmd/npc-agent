package com.npcagent.model;

import lombok.Data;

/**
 * NPC角色模型
 *
 * 存储NPC的基本信息、性格特点和说话风格
 */
@Data
public class NpcCharacter {

    /**
     * NPC代码
     */
    private String code;

    /**
     * NPC名称
     */
    private String name;

    /**
     * 性格特点
     */
    private String personality;

    /**
     * 说话风格
     */
    private String speakingStyle;

    /**
     * 所属场景
     */
    private String sceneCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 初始剧情节点ID
     */
    private String initialNodeId;

    /**
     * 是否启用
     */
    private Boolean enabled;
}
