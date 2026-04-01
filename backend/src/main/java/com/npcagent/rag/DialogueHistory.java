package com.npcagent.rag;

/**
 * 对话历史记录类
 *
 * 用于保持对话上下文，让AI能够理解对话的连贯性
 * - speaker: 说话者（"player"表示玩家，NPC名称表示NPC）
 * - text: 对话内容
 *
 * 在AI生成回复时，最近的10条对话历史会被包含在Prompt中，
 * 帮助AI理解当前对话的上下文和话题。
 */
public class DialogueHistory {
    private String speaker;
    private String text;

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
