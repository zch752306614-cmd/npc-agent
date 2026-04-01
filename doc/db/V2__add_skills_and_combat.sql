-- =====================================================
-- 增量更新脚本 - 版本 V2
-- 描述: 添加玩家技能系统和战斗记录
-- =====================================================

-- 创建玩家技能表
CREATE TABLE IF NOT EXISTS `player_skill` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `player_id` VARCHAR(64) NOT NULL COMMENT '玩家ID',
    `skill_name` VARCHAR(100) NOT NULL COMMENT '技能名称',
    `skill_level` INT DEFAULT 1 COMMENT '技能等级',
    `skill_type` VARCHAR(50) DEFAULT 'active' COMMENT '技能类型（active、passive）',
    `damage` INT DEFAULT 0 COMMENT '技能伤害',
    `cost` INT DEFAULT 0 COMMENT '灵力消耗',
    `cooldown` INT DEFAULT 0 COMMENT '冷却时间（秒）',
    `description` TEXT COMMENT '技能描述',
    `learned_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '学习时间',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX `idx_player_id` (`player_id`),
    INDEX `idx_skill_name` (`skill_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家技能表';

-- 创建战斗记录表
CREATE TABLE IF NOT EXISTS `combat_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `player_id` VARCHAR(64) NOT NULL COMMENT '玩家ID',
    `battle_id` VARCHAR(64) NOT NULL COMMENT '战斗ID',
    `monster_name` VARCHAR(100) COMMENT '怪物名称',
    `result` VARCHAR(20) NOT NULL COMMENT '战斗结果（win、lose、escape）',
    `player_health_before` INT COMMENT '玩家战斗前生命值',
    `player_health_after` INT COMMENT '玩家战斗后生命值',
    `monster_health_before` INT COMMENT '怪物战斗前生命值',
    `monster_health_after` INT COMMENT '怪物战斗后生命值',
    `rewards` TEXT COMMENT '奖励（JSON格式）',
    `experience_gained` INT DEFAULT 0 COMMENT '获得经验',
    `combat_time` INT COMMENT '战斗时长（秒）',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '战斗时间',
    INDEX `idx_player_id` (`player_id`),
    INDEX `idx_battle_id` (`battle_id`),
    INDEX `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='战斗记录表';

-- 为玩家表添加灵石字段
ALTER TABLE `player` 
ADD COLUMN `spirit_stones` INT DEFAULT 0 COMMENT '灵石数量' AFTER `experience`;

-- 为道具表添加技能类型
ALTER TABLE `item` 
ADD COLUMN `skill_name` VARCHAR(100) COMMENT '关联技能名称（如果是技能书）' AFTER `max_stack`;

-- 插入初始技能数据
INSERT INTO `player_skill` (`player_id`, `skill_name`, `skill_level`, `skill_type`, `damage`, `cost`, `cooldown`, `description`) VALUES
('default', '基础吐纳', 1, 'passive', 0, 0, 0, '修仙基础功法，缓慢恢复灵力'),
('default', '火球术', 1, 'active', 30, 10, 5, '基础攻击法术，对敌人造成伤害');

-- 插入技能书道具
INSERT INTO `item` (`name`, `type`, `rarity`, `description`, `skill_name`, `stackable`) VALUES
('基础吐纳诀', 'consumable', 2, '修仙基础功法，学习后可缓慢恢复灵力', '基础吐纳', FALSE),
('火球术秘籍', 'consumable', 3, '基础攻击法术秘籍，学习后可使用火球术', '火球术', FALSE);
