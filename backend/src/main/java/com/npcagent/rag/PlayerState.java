package com.npcagent.rag;

import java.util.ArrayList;
import java.util.List;

/**
 * 玩家状态类
 *
 * 记录玩家的游戏进度和状态：
 * - completedNodes: 已完成的剧情节点列表，用于判断前置条件
 * - inventory: 玩家背包，存储获得的物品
 * - currentScene: 当前所在场景
 *
 * 状态持久化说明：
 * 当前实现为内存存储（playerId -> PlayerState映射），
 * 生产环境应改为数据库存储，确保玩家状态不会丢失。
 */
public class PlayerState {
    private List<String> completedNodes;
    private List<String> inventory;
    private String currentScene;

    public PlayerState() {
        this.completedNodes = new ArrayList<>();
        this.inventory = new ArrayList<>();
        this.currentScene = "village";
    }

    public List<String> getCompletedNodes() {
        return completedNodes;
    }

    public void setCompletedNodes(List<String> completedNodes) {
        this.completedNodes = completedNodes;
    }

    public List<String> getInventory() {
        return inventory;
    }

    public void setInventory(List<String> inventory) {
        this.inventory = inventory;
    }

    public String getCurrentScene() {
        return currentScene;
    }

    public void setCurrentScene(String currentScene) {
        this.currentScene = currentScene;
    }
}
