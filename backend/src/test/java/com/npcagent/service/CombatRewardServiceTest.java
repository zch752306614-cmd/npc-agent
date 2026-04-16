package com.npcagent.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.npcagent.mapper.PlayerMapper;
import com.npcagent.model.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CombatRewardServiceTest {

    @Mock
    private PlayerMapper playerMapper;

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private CombatRewardService combatRewardService;

    @Test
    void grantWinRewards_shouldUpgradePlayerAndGrantItems() {
        Player player = new Player();
        player.setPlayerId("player_001");
        player.setLevel(1);
        player.setCultivationLevel(0);
        player.setSpiritualPower(10);

        when(playerMapper.selectOne(org.mockito.ArgumentMatchers.<Wrapper<Player>>any())).thenReturn(player);

        combatRewardService.grantWinRewards("player_001", 50);

        ArgumentCaptor<Player> updatedPlayer = ArgumentCaptor.forClass(Player.class);
        verify(playerMapper).updateById(updatedPlayer.capture());
        assertEquals(2, updatedPlayer.getValue().getLevel());
        assertEquals(1, updatedPlayer.getValue().getCultivationLevel());
        assertEquals(15, updatedPlayer.getValue().getSpiritualPower());

        verify(inventoryService).addItemByName("player_001", "灵草", 2);
        verify(inventoryService).addItemByName("player_001", "灵石", 10);
    }
}
