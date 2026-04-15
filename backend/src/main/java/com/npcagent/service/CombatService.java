package com.npcagent.service;

import com.npcagent.common.exception.BusinessException;
import com.npcagent.model.Player;
import com.npcagent.mapper.PlayerMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 战斗服务
 *
 * 实现游戏的战斗系统：
 * 1. 战斗初始化
 * 2. 回合制战斗逻辑
 * 3. 战斗结算
 * 4. 掉落处理
 */
@Service
public class CombatService {
    private static final Logger logger = LoggerFactory.getLogger(CombatService.class);
    private static final int DEFAULT_MONSTER_HEALTH = 80;
    private static final int DEFAULT_MONSTER_ATTACK = 15;
    private static final int DEFAULT_MONSTER_DEFENSE = 5;
    private static final int DEFAULT_MONSTER_SPIRIT = 20;
    private static final int WIN_EXP_REWARD = 50;
    private static final String DEFAULT_MONSTER_NAME = "青狼";
    private static final String BATTLE_STATUS_ENDED = "ended";
    private static final String BATTLE_STATUS_ONGOING = "ongoing";

    private final Map<String, BattleState> activeBattles = new ConcurrentHashMap<>();
    private final PlayerMapper playerMapper;
    private final InventoryService inventoryService;

    public CombatService(PlayerMapper playerMapper, InventoryService inventoryService) {
        this.playerMapper = playerMapper;
        this.inventoryService = inventoryService;
    }

    /**
     * 开始战斗
     *
     * @param playerId 玩家ID
     * @param monsterId 怪物ID
     * @return 战斗初始化信息
     */
    public Map<String, Object> startCombat(String playerId, String monsterId) {
        validateRequired(playerId, "playerId 不能为空");
        logger.info("Start combat, playerId={}, monsterId={}", playerId, monsterId);

        Player player = findPlayer(playerId);

        int playerHealth = player != null ? safe(player.getLevel() * 20 + 80, 100) : 100;
        int playerAttack = player != null ? safe(player.getLevel() * 3 + 12, 20) : 20;
        int playerDefense = player != null ? safe(player.getLevel() + 4, 8) : 8;
        int playerSpirit = player != null ? safe(player.getSpiritualPower(), 50) : 50;

        int monsterHealth = DEFAULT_MONSTER_HEALTH;
        int monsterAttack = DEFAULT_MONSTER_ATTACK;
        int monsterDefense = DEFAULT_MONSTER_DEFENSE;
        String monsterName = (monsterId == null || monsterId.isBlank()) ? DEFAULT_MONSTER_NAME : monsterId;

        String battleId = "battle_" + System.currentTimeMillis();
        BattleState state = new BattleState(
                battleId, playerId, monsterName,
                playerHealth, playerHealth,
                playerAttack, playerDefense, playerSpirit,
                monsterHealth, monsterHealth, monsterAttack, monsterDefense,
                1, false
        );
        activeBattles.put(battleId, state);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("battleId", battleId);
        result.put("player", Map.of(
                "name", "玩家",
                "health", state.playerHealth(),
                "maxHealth", state.playerMaxHealth(),
                "attack", state.playerAttack(),
                "defense", state.playerDefense(),
                "spiritualPower", state.playerSpiritualPower()
        ));
        result.put("monster", Map.of(
                "name", state.monsterName(),
                "health", state.monsterHealth(),
                "maxHealth", state.monsterMaxHealth(),
                "attack", state.monsterAttack(),
                "defense", state.monsterDefense(),
                "spiritualPower", DEFAULT_MONSTER_SPIRIT
        ));
        result.put("turn", state.turn());
        result.put("currentTurn", "player");
        
        return result;
    }

    /**
     * 执行战斗回合
     *
     * @param battleId 战斗ID
     * @param action 玩家行动
     * @return 战斗结果
     */
    public Map<String, Object> executeTurn(String battleId, String action) {
        validateRequired(battleId, "battleId 不能为空");
        validateRequired(action, "action 不能为空");

        BattleState current = activeBattles.get(battleId);
        if (current == null || current.ended()) {
            throw BusinessException.notFound("战斗不存在或已结束");
        }
        logger.info("Execute combat turn, battleId={}, action={}", battleId, action);

        AttackResult playerAttackResult = calculatePlayerAttack(current, action);
        int newMonsterHealth = Math.max(0, current.monsterHealth() - playerAttackResult.damage());
        AttackResult monsterAttackResult = calculateMonsterAttack(current, action, newMonsterHealth);
        int newPlayerHealth = Math.max(0, current.playerHealth() - monsterAttackResult.damage());

        boolean ended = isBattleEnded(newMonsterHealth, newPlayerHealth);
        BattleState next = current.withRoundResult(newPlayerHealth, newMonsterHealth, current.turn() + 1, ended);
        activeBattles.put(battleId, next);
        return buildTurnResult(battleId, next, playerAttackResult, monsterAttackResult, ended);
    }

    /**
     * 结束战斗
     *
     * @param battleId 战斗ID
     * @param winner 胜利者
     * @return 战斗结算结果
     */
    public Map<String, Object> endCombat(String battleId, String winner) {
        validateRequired(battleId, "battleId 不能为空");
        BattleState state = activeBattles.remove(battleId);
        if (state == null) {
            throw BusinessException.notFound("战斗不存在或已结算");
        }

        String finalWinner = winner;
        if (finalWinner == null || finalWinner.isBlank()) {
            finalWinner = state.monsterHealth() <= 0 ? "player" : "monster";
        }
        logger.info("End combat, battleId={}, winner={}", battleId, finalWinner);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("battleId", battleId);
        result.put("winner", finalWinner);

        if ("player".equals(finalWinner)) {
            int exp = WIN_EXP_REWARD;
            grantExperience(state.playerId(), exp);
            inventoryService.addItemByName(state.playerId(), "灵草", 2);
            inventoryService.addItemByName(state.playerId(), "灵石", 10);

            result.put("experience", exp);
            result.put("rewards", List.of("获得：灵草 x2", "获得：灵石 x10", "获得：经验值 +50"));
            result.put("message", "战斗胜利！你获得了奖励。");
        } else {
            result.put("message", "战斗失败！你需要休息一下。");
        }

        return result;
    }

    private Player findPlayer(String playerId) {
        QueryWrapper<Player> query = new QueryWrapper<>();
        query.eq("player_id", playerId);
        return playerMapper.selectOne(query);
    }

    private void grantExperience(String playerId, int exp) {
        try {
            Player player = findPlayer(playerId);
            if (player == null) {
                return;
            }
            // 受限于当前 Player 实体字段，先采用轻量成长：小幅提升境界等级与灵力
            if (exp >= 50) {
                player.setLevel(Math.max(1, player.getLevel()) + 1);
                player.setCultivationLevel(Math.max(0, player.getCultivationLevel()) + 1);
                player.setSpiritualPower(Math.max(0, player.getSpiritualPower()) + 5);
            }
            playerMapper.updateById(player);
        } catch (Exception ex) {
            logger.warn("Grant experience failed, playerId={}, exp={}", playerId, exp, ex);
        }
    }

    private int safe(int value, int fallback) {
        return value <= 0 ? fallback : value;
    }

    private void validateRequired(String value, String message) {
        if (value == null || value.isBlank()) {
            throw BusinessException.badRequest(message);
        }
    }

    private AttackResult calculatePlayerAttack(BattleState state, String action) {
        return switch (action) {
            case "skill" -> new AttackResult(
                    Math.max(1, state.playerAttack() + 8 - state.monsterDefense()),
                    "玩家施展功法，造成 %d 点伤害"
            );
            case "defend" -> new AttackResult(
                    Math.max(1, state.playerAttack() / 2 - state.monsterDefense()),
                    "玩家进行防御姿态并反击，造成 %d 点伤害"
            );
            default -> new AttackResult(
                    Math.max(1, state.playerAttack() - state.monsterDefense()),
                    "玩家使用普通攻击，造成 %d 点伤害"
            );
        };
    }

    private AttackResult calculateMonsterAttack(BattleState state, String action, int monsterHealth) {
        if (monsterHealth <= 0) {
            return new AttackResult(0, "怪物被击败，无法行动");
        }
        int damage = Math.max(1, state.monsterAttack() - state.playerDefense());
        if ("defend".equals(action)) {
            damage = Math.max(1, damage - 5);
        }
        return new AttackResult(damage, state.monsterName() + " 反击，造成 %d 点伤害");
    }

    private boolean isBattleEnded(int monsterHealth, int playerHealth) {
        return monsterHealth <= 0 || playerHealth <= 0;
    }

    private Map<String, Object> buildTurnResult(
            String battleId,
            BattleState next,
            AttackResult playerAttackResult,
            AttackResult monsterAttackResult,
            boolean ended
    ) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("battleId", battleId);
        result.put("turn", next.turn());
        result.put("currentTurn", "monster");
        result.put("playerAction", playerAttackResult.renderedAction());
        result.put("playerDamage", playerAttackResult.damage());
        result.put("monsterAction", monsterAttackResult.renderedAction());
        result.put("monsterDamage", monsterAttackResult.damage());
        result.put("playerHealth", next.playerHealth());
        result.put("monsterHealth", next.monsterHealth());
        result.put("battleStatus", ended ? BATTLE_STATUS_ENDED : BATTLE_STATUS_ONGOING);
        return result;
    }

    private record BattleState(
            String battleId,
            String playerId,
            String monsterName,
            int playerHealth,
            int playerMaxHealth,
            int playerAttack,
            int playerDefense,
            int playerSpiritualPower,
            int monsterHealth,
            int monsterMaxHealth,
            int monsterAttack,
            int monsterDefense,
            int turn,
            boolean ended
    ) {
        private BattleState withRoundResult(int newPlayerHealth, int newMonsterHealth, int newTurn, boolean isEnded) {
            return new BattleState(
                    battleId, playerId, monsterName,
                    newPlayerHealth, playerMaxHealth, playerAttack, playerDefense, playerSpiritualPower,
                    newMonsterHealth, monsterMaxHealth, monsterAttack, monsterDefense,
                    newTurn, isEnded
            );
        }
    }

    private record AttackResult(int damage, String actionTemplate) {
        private String renderedAction() {
            return actionTemplate.formatted(damage);
        }
    }
}
