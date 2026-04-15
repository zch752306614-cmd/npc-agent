package com.npcagent.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.npcagent.mapper.PlayerMapper;
import com.npcagent.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CombatRewardService {

    private static final Logger logger = LoggerFactory.getLogger(CombatRewardService.class);

    private final PlayerMapper playerMapper;
    private final InventoryService inventoryService;

    public CombatRewardService(PlayerMapper playerMapper, InventoryService inventoryService) {
        this.playerMapper = playerMapper;
        this.inventoryService = inventoryService;
    }

    public void grantWinRewards(String playerId, int exp) {
        grantExperience(playerId, exp);
        inventoryService.addItemByName(playerId, "灵草", 2);
        inventoryService.addItemByName(playerId, "灵石", 10);
    }

    private void grantExperience(String playerId, int exp) {
        try {
            Player player = findPlayer(playerId);
            if (player == null) {
                return;
            }
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

    private Player findPlayer(String playerId) {
        QueryWrapper<Player> query = new QueryWrapper<>();
        query.eq("player_id", playerId);
        return playerMapper.selectOne(query);
    }
}
