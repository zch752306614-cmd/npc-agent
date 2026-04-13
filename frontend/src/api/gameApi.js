// 游戏API调用
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:59999/api/game';

// 统一的请求方法
async function request(endpoint, options = {}) {
  try {
    const response = await fetch(`${API_BASE_URL}${endpoint}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...options.headers
      }
    });
    
    const data = await response.json();
    
    // 检查返回格式
    if (data.code === 200) {
      return data.data;
    } else {
      throw new Error(data.message || '请求失败');
    }
  } catch (error) {
    console.error('API请求失败:', error);
    throw error;
  }
}

// 对话相关API
const dialogueApi = {
  // 获取对话选项
  getDialogueOptions: async (npcCode, playerId) => {
    return request(`/dialogue/options?npcCode=${npcCode}&playerId=${playerId}`);
  },
  
  // 处理固定选项
  handleFixedOption: async (npcCode, optionId, playerId) => {
    return request(`/dialogue/fixed?npcCode=${npcCode}&optionId=${optionId}&playerId=${playerId}`, {
      method: 'POST'
    });
  },
  
  // 处理自由输入
  handleFreeInput: async (npcCode, input, history, playerId) => {
    return request('/dialogue/free', {
      method: 'POST',
      body: JSON.stringify({
        npcCode,
        input,
        history,
        playerId
      })
    });
  },
  
  // 获取对话历史
  getDialogueHistory: async (playerId, npcCode) => {
    return request(`/dialogue/history?playerId=${playerId}&npcCode=${npcCode}`);
  }
};

// 探索相关API
const explorationApi = {
  // 切换场景
  changeScene: async (playerId, sceneCode) => {
    return request(`/exploration/change-scene?playerId=${playerId}&sceneCode=${sceneCode}`, {
      method: 'POST'
    });
  },
  
  // 探索场景
  exploreScene: async (playerId, sceneCode, positionX, positionY) => {
    return request('/exploration/explore', {
      method: 'POST',
      body: JSON.stringify({
        playerId,
        sceneCode,
        positionX,
        positionY
      })
    });
  }
};

// 战斗相关API
const combatApi = {
  // 开始战斗
  startCombat: async (playerId, monsterId) => {
    return request(`/combat/start?playerId=${playerId}&monsterId=${monsterId}`, {
      method: 'POST'
    });
  },
  
  // 执行战斗回合
  executeTurn: async (battleId, action) => {
    return request('/combat/turn', {
      method: 'POST',
      body: JSON.stringify({
        battleId,
        action
      })
    });
  },
  
  // 结束战斗
  endCombat: async (battleId, winner) => {
    return request(`/combat/end?battleId=${battleId}&winner=${winner}`, {
      method: 'POST'
    });
  }
};

// 寻宝相关API
const treasureHuntApi = {
  // 开始寻宝
  startTreasureHunt: async (playerId, location) => {
    return request(`/treasure-hunt/start?playerId=${playerId}&location=${location}`, {
      method: 'POST'
    });
  }
};

// 背包相关API
const inventoryApi = {
  // 获取背包
  getInventory: async (playerId) => {
    return request(`/inventory?playerId=${playerId}`);
  },
  
  // 使用物品
  useItem: async (playerId, itemId) => {
    return request(`/inventory/use?playerId=${playerId}&itemId=${itemId}`, {
      method: 'POST'
    });
  },
  
  // 添加物品
  addItem: async (playerId, item) => {
    return request(`/inventory/add?playerId=${playerId}`, {
      method: 'POST',
      body: JSON.stringify(item)
    });
  },
  
  // 移除物品
  removeItem: async (playerId, itemId, quantity) => {
    return request(`/inventory/remove?playerId=${playerId}&itemId=${itemId}&quantity=${quantity}`, {
      method: 'POST'
    });
  }
};

// 剧情相关API
const storyApi = {
  // 获取剧情进度
  getStoryProgress: async (playerId) => {
    return request(`/story/progress?playerId=${playerId}`);
  }
};

export {
  dialogueApi,
  explorationApi,
  combatApi,
  treasureHuntApi,
  inventoryApi,
  storyApi
};
