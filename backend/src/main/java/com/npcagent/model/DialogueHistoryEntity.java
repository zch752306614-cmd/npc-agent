package com.npcagent.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("dialogue_history")
public class DialogueHistoryEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String playerId;

    private String npcCode;

    private String speaker;

    private String text;

    private LocalDateTime createdAt;
}
