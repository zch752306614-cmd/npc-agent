/**
 * API 服务
 * 统一管理所有与后端的API交互
 */

const API_BASE_URL = 'http://localhost:8080/api/game';

/**
 * 对话系统API
 */
export const dialogueApi = {
  /**
   * 获取NPC对话选项
   * @param {string} npcCode - NPC代码
   * @param {string} playerId - 玩家ID
   * @returns {Promise<Array>} 对话选项列表
   */
  getDialogueOptions: async (npcCode, playerId) => {
    const response = await fetch(
      `${API_BASE_URL}/dialogue/options?npcCode=${npcCode}&playerId=${playerId}`
    );
    if (!response.ok) throw new Error('Failed to get dialogue options');
    return response.json();
  },

  /**
   * 处理固定选项选择
   * @param {string} npcCode - NPC代码
   * @param {string} optionId - 选项ID
   * @param {string} playerId - 玩家ID
   * @returns {Promise<Object>} 对话结果
   */
  handleFixedOption: async (npcCode, optionId, playerId) => {
    const response = await fetch(
      `${API_BASE_URL}/dialogue/fixed?npcCode=${npcCode}&optionId=${optionId}&playerId=${playerId}`,
      { method: 'POST' }
    );
    if (!response.ok) throw new Error('Failed to handle fixed option');
    return response.json();
  },

  /**
   * 处理自由输入
   * @param {string} npcCode - NPC代码
   * @param {string} playerInput - 玩家输入
   * @param {Array} history - 对话历史
   * @param {string} playerId - 玩家ID
   * @returns {Promise<Object>} 对话结果
   */
  handleFreeInput: async (npcCode, playerInput, history, playerId) => {
    const response = await fetch(`${API_BASE_URL}/dialogue/free`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        npcCode,
        input: playerInput,
        history,
        playerId
      })
    });
    if (!response.ok) throw new Error('Failed to handle free input');
    return response.json();
  },

  /**
   * 获取对话历史
   * @param {string} playerId - 玩家ID
   * @param {string} npcCode - NPC代码
   * @returns {Promise<Array>} 对话历史
   */
  getDialogueHistory: async (playerId, npcCode) => {
    const response = await fetch(
      `${API_BASE_URL}/dialogue/history?playerId=${playerId}&npcCode=${npcCode}`
    );
    if (!response.ok) throw new Error('Failed to get dialogue history');
    return response.json();
  }
};

/**
 * 探索系统API
 */
export const explorationApi = {
  /**
   * 切换场景
   * @param {string} playerId - 玩家ID
   * @param {string} sceneCode - 场景代码
   * @returns {Promise<Object>} 切换结果
   */
  changeScene: async (playerId, sceneCode) => {
    const response = await fetch(
      `${API_BASE_URL}/exploration/change-scene?playerId=${playerId}&sceneCode=${sceneCode}`,
      { method: 'POST' }
    );
    if (!response.ok) throw new Error('Failed to change scene');
    return response.json();
  },

  /**
   * 探索场景
   * @param {string} playerId - 玩家ID
   * @param {string} sceneCode - 场景代码
   * @param {number} positionX - X坐标
   * @param {number} positionY - Y坐标
   * @returns {Promise<Object>} 探索结果
   */
  exploreScene: async (playerId, sceneCode, positionX, positionY) => {
    const response = await fetch(`${API_BASE_URL}/exploration/explore`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        playerId,
        sceneCode,
        positionX,
        positionY
      })
    });
    if (!response.ok) throw new Error('Failed to explore scene');
    return response.json();
  }
};

/**
 * 战斗系统API
 */
export const combatApi = {
  /**
   * 开始战斗
   * @param {string} playerId - 玩家ID
   * @param {string} monsterId - 怪物ID
   * @returns {Promise<Object>} 战斗初始化信息
   */
  startCombat: async (playerId, monsterId) => {
    const response = await fetch(
      `${API_BASE_URL}/combat/start?playerId=${playerId}&monsterId=${monsterId}`,
      { method: 'POST' }
    );
    if (!response.ok) throw new Error('Failed to start combat');
    return response.json();
  },

  /**
   * 执行战斗回合
   * @param {string} battleId - 战斗ID
   * @param {string} action - 玩家行动
   * @returns {Promise<Object>} 战斗结果
   */
  executeTurn: async (battleId, action) => {
    const response = await fetch(`${API_BASE_URL}/combat/turn`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ battleId, action })
    });
    if (!response.ok) throw new Error('Failed to execute turn');
    return response.json();
  },

  /**
   * 结束战斗
   * @param {string} battleId - 战斗ID
   * @param {string} winner - 胜利者
   * @returns {Promise<Object>} 战斗结算结果
   */
  endCombat: async (battleId, winner) => {
    const response = await fetch(
      `${API_BASE_URL}/combat/end?battleId=${battleId}&winner=${winner}`,
      { method: 'POST' }
    );
    if (!response.ok) throw new Error('Failed to end combat');
    return response.json();
  }
};

/**
 * 寻宝系统API
 */
export const treasureHuntApi = {
  /**
   * 开始寻宝
   * @param {string} playerId - 玩家ID
   * @param {string} location - 寻宝地点
   * @returns {Promise<Object>} 寻宝结果
   */
  startTreasureHunt: async (playerId, location) => {
    const response = await fetch(
      `${API_BASE_URL}/treasure-hunt/start?playerId=${playerId}&location=${location}`,
      { method: 'POST' }
    );
    if (!response.ok) throw new Error('Failed to start treasure hunt');
    return response.json();
  }
};

/**
 * 背包系统API
 */
export const inventoryApi = {
  /**
   * 获取背包
   * @param {string} playerId - 玩家ID
   * @returns {Promise<Array>} 背包物品列表
   */
  getInventory: async (playerId) => {
    const response = await fetch(
      `${API_BASE_URL}/inventory?playerId=${playerId}`
    );
    if (!response.ok) throw new Error('Failed to get inventory');
    return response.json();
  },

  /**
   * 使用物品
   * @param {string} playerId - 玩家ID
   * @param {number} itemId - 物品ID
   * @returns {Promise<Object>} 使用结果
   */
  useItem: async (playerId, itemId) => {
    const response = await fetch(
      `${API_BASE_URL}/inventory/use?playerId=${playerId}&itemId=${itemId}`,
      { method: 'POST' }
    );
    if (!response.ok) throw new Error('Failed to use item');
    return response.json();
  },

  /**
   * 添加物品
   * @param {string} playerId - 玩家ID
   * @param {Object} item - 物品信息
   * @returns {Promise<Object>} 添加结果
   */
  addItem: async (playerId, item) => {
    const response = await fetch(
      `${API_BASE_URL}/inventory/add?playerId=${playerId}`,
      {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(item)
      }
    );
    if (!response.ok) throw new Error('Failed to add item');
    return response.json();
  },

  /**
   * 移除物品
   * @param {string} playerId - 玩家ID
   * @param {number} itemId - 物品ID
   * @param {number} quantity - 数量
   * @returns {Promise<Object>} 移除结果
   */
  removeItem: async (playerId, itemId, quantity) => {
    const response = await fetch(
      `${API_BASE_URL}/inventory/remove?playerId=${playerId}&itemId=${itemId}&quantity=${quantity}`,
      { method: 'POST' }
    );
    if (!response.ok) throw new Error('Failed to remove item');
    return response.json();
  }
};

/**
 * 剧情系统API
 */
export const storyApi = {
  /**
   * 获取剧情进度
   * @param {string} playerId - 玩家ID
   * @returns {Promise<Object>} 剧情进度信息
   */
  getStoryProgress: async (playerId) => {
    const response = await fetch(
      `${API_BASE_URL}/story/progress?playerId=${playerId}`
    );
    if (!response.ok) throw new Error('Failed to get story progress');
    return response.json();
  },

  /**
   * 解锁剧情节点
   * @param {string} playerId - 玩家ID
   * @param {string} nodeId - 节点ID
   * @returns {Promise<Object>} 解锁结果
   */
  unlockNode: async (playerId, nodeId) => {
    const response = await fetch(
      `${API_BASE_URL}/story/unlock-node?playerId=${playerId}&nodeId=${nodeId}`,
      { method: 'POST' }
    );
    if (!response.ok) throw new Error('Failed to unlock node');
    return response.json();
  },

  /**
   * 完成剧情节点
   * @param {string} playerId - 玩家ID
   * @param {string} nodeId - 节点ID
   * @returns {Promise<Object>} 完成结果
   */
  completeNode: async (playerId, nodeId) => {
    const response = await fetch(
      `${API_BASE_URL}/story/complete-node?playerId=${playerId}&nodeId=${nodeId}`,
      { method: 'POST' }
    );
    if (!response.ok) throw new Error('Failed to complete node');
    return response.json();
  },

  /**
   * 选择剧情分支
   * @param {string} playerId - 玩家ID
   * @param {string} nodeId - 节点ID
   * @param {string} branch - 分支选择
   * @returns {Promise<Object>} 选择结果
   */
  chooseBranch: async (playerId, nodeId, branch) => {
    const response = await fetch(
      `${API_BASE_URL}/story/choose-branch?playerId=${playerId}&nodeId=${nodeId}&branch=${branch}`,
      { method: 'POST' }
    );
    if (!response.ok) throw new Error('Failed to choose branch');
    return response.json();
  }
};
