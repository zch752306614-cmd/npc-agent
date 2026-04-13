package com.npcagent.service;

import com.npcagent.model.Player;
import com.npcagent.mapper.PlayerMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
        Player player = findPlayer(playerId);

        int playerHealth = player != null ? safe(player.getLevel() * 20 + 80, 100) : 100;
        int playerAttack = player != null ? safe(player.getLevel() * 3 + 12, 20) : 20;
        int playerDefense = player != null ? safe(player.getLevel() + 4, 8) : 8;
        int playerSpirit = player != null ? safe(player.getSpiritualPower(), 50) : 50;

        int monsterHealth = 80;
        int monsterAttack = 15;
        int monsterDefense = 5;
        String monsterName = (monsterId == null || monsterId.isBlank()) ? "青狼" : monsterId;

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
                "spiritualPower", 20
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
        BattleState current = activeBattles.get(battleId);
        if (current == null || current.ended()) {
            return Map.of(
                    "success", false,
                    "battleId", battleId,
                    "message", "战斗不存在或已结束"
            );
        }

        int playerDamage;
        String playerAction;
        switch (action) {
            case "skill" -> {
                playerDamage = Math.max(1, current.playerAttack() + 8 - current.monsterDefense());
                playerAction = "玩家施展功法，造成 " + playerDamage + " 点伤害";
            }
            case "defend" -> {
                playerDamage = Math.max(1, current.playerAttack() / 2 - current.monsterDefense());
                playerAction = "玩家进行防御姿态并反击，造成 " + playerDamage + " 点伤害";
            }
            default -> {
                playerDamage = Math.max(1, current.playerAttack() - current.monsterDefense());
                playerAction = "玩家使用普通攻击，造成 " + playerDamage + " 点伤害";
            }
        }

        int newMonsterHealth = Math.max(0, current.monsterHealth() - playerDamage);

        int monsterDamage = 0;
        String monsterAction = "怪物被击败，无法行动";
        int newPlayerHealth = current.playerHealth();

        if (newMonsterHealth > 0) {
            monsterDamage = Math.max(1, current.monsterAttack() - current.playerDefense());
            if ("defend".equals(action)) {
                monsterDamage = Math.max(1, monsterDamage - 5);
            }
            newPlayerHealth = Math.max(0, current.playerHealth() - monsterDamage);
            monsterAction = current.monsterName() + " 反击，造成 " + monsterDamage + " 点伤害";
        }

        boolean ended = newMonsterHealth <= 0 || newPlayerHealth <= 0;
        BattleState next = current.withRoundResult(newPlayerHealth, newMonsterHealth, current.turn() + 1, ended);
        activeBattles.put(battleId, next);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("battleId", battleId);
        result.put("turn", next.turn());
        result.put("currentTurn", "monster");
        result.put("playerAction", playerAction);
        result.put("playerDamage", playerDamage);
        result.put("monsterAction", monsterAction);
        result.put("monsterDamage", monsterDamage);
        result.put("playerHealth", next.playerHealth());
        result.put("monsterHealth", next.monsterHealth());
        result.put("battleStatus", ended ? "ended" : "ongoing");
        
        return result;
    }

    /**
     * 结束战斗
     *
     * @param battleId 战斗ID
     * @param winner 胜利者
     * @return 战斗结算结果
     */
    public Map<String, Object> endCombat(String battleId, String winner) {
        BattleState state = activeBattles.remove(battleId);
        if (state == null) {
            return Map.of(
                    "success", false,
                    "battleId", battleId,
                    "message", "战斗不存在或已结算"
            );
        }

        String finalWinner = winner;
        if (finalWinner == null || finalWinner.isBlank()) {
            finalWinner = state.monsterHealth() <= 0 ? "player" : "monster";
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("battleId", battleId);
        result.put("winner", finalWinner);

        if ("player".equals(finalWinner)) {
            int exp = 50;
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
        } catch (Exception ignored) {
            // 兼容当前多套schema并存场景，经验更新失败不影响战斗主流程
        }
    }

    private int safe(int value, int fallback) {
        return value <= 0 ? fallback : value;
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
}
