package com.npcagent.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.npcagent.common.exception.BusinessException;
import com.npcagent.mapper.ItemMapper;
import com.npcagent.mapper.PlayerInventoryMapper;
import com.npcagent.mapper.PlayerMapper;
import com.npcagent.model.Item;
import com.npcagent.model.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private PlayerMapper playerMapper;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private PlayerInventoryMapper playerInventoryMapper;

    @InjectMocks
    private InventoryService inventoryService;

    @Test
    void getInventory_shouldThrowWhenPlayerMissing() {
        when(playerMapper.selectOne(org.mockito.ArgumentMatchers.<Wrapper<Player>>any())).thenReturn(null);
        assertThrows(BusinessException.class, () -> inventoryService.getInventory("missing_player"));
    }

    @Test
    void addItemByName_shouldThrowWhenItemMissing() {
        Player player = new Player();
        player.setPlayerId("player_001");
        when(playerMapper.selectOne(org.mockito.ArgumentMatchers.<Wrapper<Player>>any())).thenReturn(player);
        when(itemMapper.selectOne(org.mockito.ArgumentMatchers.<Wrapper<Item>>any())).thenReturn(null);

        assertThrows(BusinessException.class, () -> inventoryService.addItemByName("player_001", "不存在的道具", 1));
    }
}
