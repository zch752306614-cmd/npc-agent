package com.npcagent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("player_inventory")
public class PlayerInventory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String playerId;

    private Long itemId;

    private Integer quantity;

    private Boolean equipped;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
