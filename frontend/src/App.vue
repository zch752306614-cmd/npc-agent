<script setup>
import { ref, onMounted, computed } from 'vue';
import {
  dialogueApi,
  explorationApi,
  combatApi,
  treasureHuntApi,
  inventoryApi,
  storyApi
} from './api/gameApi.js';

// 玩家ID（临时使用默认值）
const playerId = ref('player_001');

// 游戏状态
const gameState = ref({
  player: {
    name: '凡人',
    level: 1,
    cultivationLevel: 0,
    realm: '凡人',
    health: 100,
    maxHealth: 100,
    spiritualPower: 50,
    maxSpiritualPower: 100,
    attack: 10,
    defense: 5,
    experience: 0,
    currentScene: 'village'
  },
  currentNpc: {
    code: 'elder',
    name: '老者'
  },
  dialogueOptions: [],
  dialogueHistory: [],
  dialogueType: 'fixed', // 'fixed' or 'free'
  loading: false,
  currentBattle: null,
  inventory: [],
  storyProgress: null
});

// 弹窗状态
const showModals = ref({
  explore: false,
  status: false,
  inventory: false,
  combat: false,
  treasure: false,
  story: false
});

// 探索结果
const exploreResult = ref({
  title: '',
  description: '',
  eventType: '',
  reward: ''
});

// 战斗状态
const battleState = ref({
  battleId: null,
  playerHealth: 100,
  monsterHealth: 100,
  turn: 1,
  logs: []
});

// 寻宝结果
const treasureResult = ref({
  location: '',
  reward: '',
  rarity: ''
});

// 场景名称映射
const sceneNames = {
  village: '青牛镇',
  mountain: '青云山',
  cave: '神秘洞穴'
};

// 当前场景名称
const currentSceneName = computed(() => {
  return sceneNames[gameState.value.player.currentScene] || '未知地点';
});

// 加载对话选项
const loadDialogueOptions = async () => {
  try {
    gameState.value.loading = true;
    const options = await dialogueApi.getDialogueOptions(
      gameState.value.currentNpc.code,
      playerId.value
    );
    gameState.value.dialogueOptions = options;
  } catch (error) {
    console.error('加载对话选项失败:', error);
    // 使用默认选项
    gameState.value.dialogueOptions = [
      {
        optionId: 'option1',
        text: '我想拜师修仙',
        type: 'normal',
        available: true
      },
      {
        optionId: 'option2',
        text: '请问灵根测试在哪里？',
        type: 'normal',
        available: true
      }
    ];
  } finally {
    gameState.value.loading = false;
  }
};

// 选择固定选项
const selectFixedOption = async (optionId) => {
  try {
    gameState.value.loading = true;
    
    // 添加玩家消息到历史
    const selectedOption = gameState.value.dialogueOptions.find(
      opt => opt.optionId === optionId
    );
    gameState.value.dialogueHistory.push({
      speaker: 'player',
      text: selectedOption.text
    });
    
    // 调用后端API
    const result = await dialogueApi.handleFixedOption(
      gameState.value.currentNpc.code,
      optionId,
      playerId.value
    );
    
    // 添加NPC回复到历史
    gameState.value.dialogueHistory.push({
      speaker: 'npc',
      text: result.npcResponse
    });
    
    // 更新对话选项
    if (result.nextOptions && result.nextOptions.length > 0) {
      gameState.value.dialogueOptions = result.nextOptions;
      gameState.value.dialogueType = 'fixed';
    } else {
      gameState.value.dialogueType = 'free';
    }
    
    // 显示奖励
    if (result.rewards && result.rewards.length > 0) {
      alert(`获得奖励：\n${result.rewards.join('\n')}`);
    }
  } catch (error) {
    console.error('处理固定选项失败:', error);
    alert('对话处理失败，请重试');
  } finally {
    gameState.value.loading = false;
  }
};

// 发送自由输入
const freeInputText = ref('');
const sendFreeInput = async () => {
  if (!freeInputText.value.trim()) return;
  
  try {
    gameState.value.loading = true;
    
    // 添加玩家消息到历史
    gameState.value.dialogueHistory.push({
      speaker: 'player',
      text: freeInputText.value
    });
    
    // 准备对话历史
    const history = gameState.value.dialogueHistory.map(msg => ({
      speaker: msg.speaker,
      text: msg.text
    }));
    
    // 调用后端API
    const result = await dialogueApi.handleFreeInput(
      gameState.value.currentNpc.code,
      freeInputText.value,
      history,
      playerId.value
    );
    
    // 添加NPC回复到历史
    gameState.value.dialogueHistory.push({
      speaker: 'npc',
      text: result.npcResponse
    });
    
    // 清空输入
    freeInputText.value = '';
    
    // 如果触发了剧情节点，切换回固定选项模式
    if (result.storyAdvance && result.nextOptions) {
      gameState.value.dialogueOptions = result.nextOptions;
      gameState.value.dialogueType = 'fixed';
    }
  } catch (error) {
    console.error('发送自由输入失败:', error);
    alert('对话处理失败，请重试');
  } finally {
    gameState.value.loading = false;
  }
};

// 切换场景
const changeScene = async (sceneCode) => {
  try {
    gameState.value.loading = true;
    const result = await explorationApi.changeScene(playerId.value, sceneCode);
    
    if (result.success) {
      gameState.value.player.currentScene = sceneCode;
      
      // 更新NPC
      if (result.npcs && result.npcs.length > 0) {
        gameState.value.currentNpc.code = result.npcs[0];
        // 根据NPC代码设置名称
        const npcNames = {
          elder: '老者',
          sectMaster: '掌门',
          mysteriousElder: '神秘老者'
        };
        gameState.value.currentNpc.name = npcNames[result.npcs[0]] || '未知NPC';
      }
      
      // 重置对话
      gameState.value.dialogueHistory = [];
      gameState.value.dialogueType = 'fixed';
      await loadDialogueOptions();
      
      alert(`已切换到：${result.sceneName}`);
    }
  } catch (error) {
    console.error('切换场景失败:', error);
    alert('切换场景失败，请重试');
  } finally {
    gameState.value.loading = false;
  }
};

// 探索场景
const exploreScene = async () => {
  try {
    gameState.value.loading = true;
    const result = await explorationApi.exploreScene(
      playerId.value,
      gameState.value.player.currentScene,
      Math.floor(Math.random() * 100),
      Math.floor(Math.random() * 100)
    );
    
    if (result.success) {
      exploreResult.value = {
        title: getEventTitle(result.eventType),
        description: result.eventDescription,
        eventType: result.eventType,
        reward: result.reward || ''
      };
      showModals.value.explore = true;
      
      // 如果遇到怪物，自动开始战斗
      if (result.eventType === 'monster') {
        // 这里可以自动触发战斗
        console.log('遇到怪物：', result.monster);
      }
    }
  } catch (error) {
    console.error('探索失败:', error);
    alert('探索失败，请重试');
  } finally {
    gameState.value.loading = false;
  }
};

// 获取事件标题
const getEventTitle = (eventType) => {
  const titles = {
    resource: '发现资源',
    monster: '遭遇怪物',
    exploration: '探索发现'
  };
  return titles[eventType] || '探索结果';
};

// 开始战斗
const startCombat = async (monsterName) => {
  try {
    gameState.value.loading = true;
    const result = await combatApi.startCombat(playerId.value, monsterName);
    
    if (result.success) {
      battleState.value = {
        battleId: result.battleId,
        playerHealth: result.player.health,
        monsterHealth: result.monster.health,
        turn: result.turn,
        logs: [`战斗开始！你遇到了 ${result.monster.name}`]
      };
      showModals.value.combat = true;
    }
  } catch (error) {
    console.error('开始战斗失败:', error);
    alert('开始战斗失败，请重试');
  } finally {
    gameState.value.loading = false;
  }
};

// 执行战斗回合
const executeCombatTurn = async (action) => {
  try {
    gameState.value.loading = true;
    const result = await combatApi.executeTurn(
      battleState.value.battleId,
      action
    );
    
    if (result.success) {
      battleState.value.playerHealth = result.playerHealth;
      battleState.value.monsterHealth = result.monsterHealth;
      battleState.value.turn = result.turn;
      battleState.value.logs.push(result.playerAction);
      battleState.value.logs.push(result.monsterAction);
      
      // 检查战斗是否结束
      if (result.battleStatus === 'ended') {
        await endCombat('player');
      }
    }
  } catch (error) {
    console.error('执行战斗回合失败:', error);
    alert('战斗操作失败，请重试');
  } finally {
    gameState.value.loading = false;
  }
};

// 结束战斗
const endCombat = async (winner) => {
  try {
    gameState.value.loading = true;
    const result = await combatApi.endCombat(
      battleState.value.battleId,
      winner
    );
    
    if (result.success) {
      battleState.value.logs.push(result.message);
      if (result.rewards) {
        battleState.value.logs.push(`获得奖励：${result.rewards.join(', ')}`);
      }
      
      // 延迟关闭战斗窗口
      setTimeout(() => {
        showModals.value.combat = false;
      }, 2000);
    }
  } catch (error) {
    console.error('结束战斗失败:', error);
  } finally {
    gameState.value.loading = false;
  }
};

// 开始寻宝
const startTreasureHunt = async () => {
  try {
    gameState.value.loading = true;
    const result = await treasureHuntApi.startTreasureHunt(
      playerId.value,
      gameState.value.player.currentScene
    );
    
    if (result.success) {
      treasureResult.value = {
        location: result.location,
        reward: result.reward,
        rarity: result.rarity
      };
      showModals.value.treasure = true;
    }
  } catch (error) {
    console.error('寻宝失败:', error);
    alert('寻宝失败，请重试');
  } finally {
    gameState.value.loading = false;
  }
};

// 加载背包
const loadInventory = async () => {
  try {
    gameState.value.loading = true;
    const items = await inventoryApi.getInventory(playerId.value);
    gameState.value.inventory = items;
    showModals.value.inventory = true;
  } catch (error) {
    console.error('加载背包失败:', error);
    alert('加载背包失败，请重试');
  } finally {
    gameState.value.loading = false;
  }
};

// 使用物品
const useItem = async (itemId) => {
  try {
    gameState.value.loading = true;
    const result = await inventoryApi.useItem(playerId.value, itemId);
    
    if (result.success) {
      alert(result.message);
      // 重新加载背包
      await loadInventory();
    }
  } catch (error) {
    console.error('使用物品失败:', error);
    alert('使用物品失败，请重试');
  } finally {
    gameState.value.loading = false;
  }
};

// 加载剧情进度
const loadStoryProgress = async () => {
  try {
    gameState.value.loading = true;
    const progress = await storyApi.getStoryProgress(playerId.value);
    gameState.value.storyProgress = progress;
    showModals.value.story = true;
  } catch (error) {
    console.error('加载剧情进度失败:', error);
    alert('加载剧情进度失败，请重试');
  } finally {
    gameState.value.loading = false;
  }
};

// 初始化
onMounted(() => {
  loadDialogueOptions();
});
</script>

<template>
  <div class="game-container">
    <!-- 场景背景 -->
    <div class="scene-background" :class="gameState.player.currentScene">
      <div class="scene-overlay"></div>
    </div>
    
    <!-- 玩家状态栏 -->
    <div class="player-status-bar">
      <div class="status-item">
        <span class="label">姓名：</span>
        <span class="value">{{ gameState.player.name }}</span>
      </div>
      <div class="status-item">
        <span class="label">境界：</span>
        <span class="value">{{ gameState.player.realm }}</span>
      </div>
      <div class="status-item">
        <span class="label">等级：</span>
        <span class="value">{{ gameState.player.level }}</span>
      </div>
      <div class="status-item">
        <span class="label">生命：</span>
        <span class="value">{{ gameState.player.health }}/{{ gameState.player.maxHealth }}</span>
      </div>
      <div class="status-item">
        <span class="label">灵力：</span>
        <span class="value">{{ gameState.player.spiritualPower }}/{{ gameState.player.maxSpiritualPower }}</span>
      </div>
      <div class="status-item">
        <span class="label">场景：</span>
        <span class="value">{{ currentSceneName }}</span>
      </div>
    </div>
    
    <!-- 游戏操作按钮 -->
    <div class="game-actions">
      <button class="action-btn" @click="exploreScene" :disabled="gameState.loading">
        <span class="icon">🔍</span> 探索
      </button>
      <button class="action-btn" @click="showModals.status = true">
        <span class="icon">📊</span> 状态
      </button>
      <button class="action-btn" @click="loadInventory" :disabled="gameState.loading">
        <span class="icon">🎒</span> 背包
      </button>
      <button class="action-btn" @click="startTreasureHunt" :disabled="gameState.loading">
        <span class="icon">💎</span> 寻宝
      </button>
      <button class="action-btn" @click="loadStoryProgress" :disabled="gameState.loading">
        <span class="icon">📖</span> 剧情
      </button>
    </div>
    
    <!-- 场景切换按钮 -->
    <div class="scene-switcher">
      <button
        v-for="(name, code) in sceneNames"
        :key="code"
        class="scene-btn"
        :class="{ active: gameState.player.currentScene === code }"
        @click="changeScene(code)"
        :disabled="gameState.loading || gameState.player.currentScene === code"
      >
        {{ name }}
      </button>
    </div>
    
    <!-- 对话区域 -->
    <div class="dialogue-container">
      <!-- 对话历史 -->
      <div class="dialogue-history">
        <div
          v-for="(msg, index) in gameState.dialogueHistory"
          :key="index"
          :class="['dialogue-message', msg.speaker]"
        >
          <div class="speaker">
            {{ msg.speaker === 'npc' ? gameState.currentNpc.name : gameState.player.name }}：
          </div>
          <div class="text">{{ msg.text }}</div>
        </div>
        <div v-if="gameState.loading" class="loading">
          <div class="loading-spinner"></div>
          <span>正在思考...</span>
        </div>
      </div>
      
      <!-- 固定选项 -->
      <div v-if="gameState.dialogueType === 'fixed'" class="dialogue-options">
        <button
          v-for="option in gameState.dialogueOptions"
          :key="option.optionId"
          class="option-button"
          @click="selectFixedOption(option.optionId)"
          :disabled="gameState.loading || !option.available"
        >
          {{ option.text }}
        </button>
      </div>
      
      <!-- 自由输入 -->
      <div v-else class="free-input">
        <input
          type="text"
          v-model="freeInputText"
          placeholder="输入你想说的话..."
          @keyup.enter="sendFreeInput"
          :disabled="gameState.loading"
        />
        <button
          class="send-button"
          @click="sendFreeInput"
          :disabled="gameState.loading || !freeInputText.trim()"
        >
          {{ gameState.loading ? '发送中...' : '发送' }}
        </button>
      </div>
    </div>
    
    <!-- 探索结果弹窗 -->
    <div v-if="showModals.explore" class="modal" @click="showModals.explore = false">
      <div class="modal-content" @click.stop>
        <h3>{{ exploreResult.title }}</h3>
        <p>{{ exploreResult.description }}</p>
        <div v-if="exploreResult.reward">
          <h4>获得奖励：</h4>
          <p>{{ exploreResult.reward }}</p>
        </div>
        <button class="close-btn" @click="showModals.explore = false">关闭</button>
      </div>
    </div>
    
    <!-- 状态查看弹窗 -->
    <div v-if="showModals.status" class="modal" @click="showModals.status = false">
      <div class="modal-content" @click.stop>
        <h3>角色状态</h3>
        <div class="status-detail">
          <p><strong>姓名：</strong>{{ gameState.player.name }}</p>
          <p><strong>境界：</strong>{{ gameState.player.realm }}</p>
          <p><strong>等级：</strong>{{ gameState.player.level }}</p>
          <p><strong>修为等级：</strong>{{ gameState.player.cultivationLevel }}</p>
          <p><strong>生命值：</strong>{{ gameState.player.health }}/{{ gameState.player.maxHealth }}</p>
          <p><strong>灵力：</strong>{{ gameState.player.spiritualPower }}/{{ gameState.player.maxSpiritualPower }}</p>
          <p><strong>攻击力：</strong>{{ gameState.player.attack }}</p>
          <p><strong>防御力：</strong>{{ gameState.player.defense }}</p>
          <p><strong>经验值：</strong>{{ gameState.player.experience }}</p>
          <p><strong>当前场景：</strong>{{ currentSceneName }}</p>
        </div>
        <button class="close-btn" @click="showModals.status = false">关闭</button>
      </div>
    </div>
    
    <!-- 背包弹窗 -->
    <div v-if="showModals.inventory" class="modal" @click="showModals.inventory = false">
      <div class="modal-content" @click.stop>
        <h3>背包</h3>
        <div v-if="gameState.inventory && gameState.inventory.length > 0" class="inventory-list">
          <div v-for="item in gameState.inventory" :key="item.id" class="inventory-item">
            <div class="item-info">
              <span class="item-name">{{ item.name }}</span>
              <span class="item-quantity">x {{ item.quantity }}</span>
            </div>
            <button class="use-btn" @click="useItem(item.id)">使用</button>
          </div>
        </div>
        <p v-else>背包是空的</p>
        <button class="close-btn" @click="showModals.inventory = false">关闭</button>
      </div>
    </div>
    
    <!-- 战斗弹窗 -->
    <div v-if="showModals.combat" class="modal combat-modal">
      <div class="modal-content combat-content">
        <h3>战斗中</h3>
        <div class="combat-status">
          <div class="combatant">
            <h4>玩家</h4>
            <div class="health-bar">
              <div class="health-fill" :style="{ width: (battleState.playerHealth / 100 * 100) + '%' }"></div>
            </div>
            <p>{{ battleState.playerHealth }}/100</p>
          </div>
          <div class="combatant">
            <h4>怪物</h4>
            <div class="health-bar">
              <div class="health-fill monster" :style="{ width: (battleState.monsterHealth / 100 * 100) + '%' }"></div>
            </div>
            <p>{{ battleState.monsterHealth }}/100</p>
          </div>
        </div>
        <div class="combat-log">
          <div v-for="(log, index) in battleState.logs" :key="index" class="log-entry">
            {{ log }}
          </div>
        </div>
        <div class="combat-actions">
          <button class="combat-btn" @click="executeCombatTurn('attack')">攻击</button>
          <button class="combat-btn" @click="executeCombatTurn('defend')">防御</button>
          <button class="combat-btn" @click="executeCombatTurn('skill')">技能</button>
        </div>
      </div>
    </div>
    
    <!-- 寻宝弹窗 -->
    <div v-if="showModals.treasure" class="modal" @click="showModals.treasure = false">
      <div class="modal-content" @click.stop>
        <h3>寻宝结果</h3>
        <p><strong>地点：</strong>{{ treasureResult.location }}</p>
        <p><strong>奖励：</strong>{{ treasureResult.reward }}</p>
        <p><strong>稀有度：</strong>{{ treasureResult.rarity }}</p>
        <button class="close-btn" @click="showModals.treasure = false">关闭</button>
      </div>
    </div>
    
    <!-- 剧情进度弹窗 -->
    <div v-if="showModals.story" class="modal" @click="showModals.story = false">
      <div class="modal-content" @click.stop>
        <h3>剧情进度</h3>
        <div v-if="gameState.storyProgress">
          <div class="progress-section">
            <h4>已完成节点</h4>
            <ul>
              <li v-for="node in gameState.storyProgress.completedNodes" :key="node">
                {{ node }}
              </li>
            </ul>
          </div>
          <div class="progress-section">
            <h4>已解锁节点</h4>
            <ul>
              <li v-for="node in gameState.storyProgress.unlockedNodes" :key="node">
                {{ node }}
              </li>
            </ul>
          </div>
          <div class="progress-section">
            <h4>当前节点</h4>
            <p>{{ gameState.storyProgress.currentNode }}</p>
          </div>
        </div>
        <p v-else>暂无剧情进度</p>
        <button class="close-btn" @click="showModals.story = false">关闭</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.game-container {
  width: 100vw;
  height: 100vh;
  position: relative;
  overflow: hidden;
  font-family: 'Microsoft YaHei', 'SimSun', serif;
}

.scene-background {
  width: 100%;
  height: 70%;
  background-size: cover;
  background-position: center;
  position: relative;
}

.scene-background.village {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.scene-background.mountain {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.scene-background.cave {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.scene-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.3);
}

.player-status-bar {
  position: absolute;
  top: 20px;
  left: 20px;
  background: rgba(0, 0, 0, 0.8);
  color: #fff;
  padding: 15px 20px;
  border-radius: 10px;
  font-size: 14px;
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  max-width: 400px;
}

.status-item {
  display: flex;
  gap: 5px;
}

.label {
  font-weight: bold;
  color: #4CAF50;
}

.game-actions {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-btn {
  background: rgba(76, 175, 80, 0.9);
  color: #fff;
  border: none;
  padding: 12px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
}

.action-btn:hover:not(:disabled) {
  background: rgba(69, 160, 69, 1);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4);
}

.action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.action-btn .icon {
  font-size: 16px;
}

.scene-switcher {
  position: absolute;
  top: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
}

.scene-btn {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: 2px solid rgba(255, 255, 255, 0.3);
  padding: 10px 20px;
  border-radius: 20px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s;
}

.scene-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.5);
}

.scene-btn.active {
  background: rgba(76, 175, 80, 0.9);
  border-color: #4CAF50;
}

.scene-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.dialogue-container {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 30%;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.95), rgba(0, 0, 0, 0.7));
  color: #fff;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.dialogue-history {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 15px;
  padding-right: 10px;
}

.dialogue-history::-webkit-scrollbar {
  width: 6px;
}

.dialogue-history::-webkit-scrollbar-track {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
}

.dialogue-history::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}

.dialogue-message {
  margin-bottom: 12px;
  display: flex;
  animation: fadeIn 0.3s ease-in;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.speaker {
  font-weight: bold;
  margin-right: 10px;
  min-width: 80px;
}

.npc .speaker {
  color: #4CAF50;
}

.player .speaker {
  color: #2196F3;
}

.dialogue-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.option-button {
  background: rgba(255, 255, 255, 0.15);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 12px 20px;
  border-radius: 8px;
  cursor: pointer;
  text-align: left;
  transition: all 0.3s;
  font-size: 14px;
}

.option-button:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateX(5px);
}

.option-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.free-input {
  display: flex;
  gap: 10px;
}

.free-input input {
  flex: 1;
  padding: 12px 15px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 14px;
}

.free-input input::placeholder {
  color: rgba(255, 255, 255, 0.5);
}

.send-button {
  background: #4CAF50;
  color: #fff;
  border: none;
  padding: 0 25px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s;
}

.send-button:hover:not(:disabled) {
  background: #45a049;
}

.send-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.loading {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 10px;
  color: #ccc;
}

.loading-spinner {
  width: 16px;
  height: 16px;
  border: 2px solid #ccc;
  border-top: 2px solid #4CAF50;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease-in;
}

.modal-content {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  color: #fff;
  padding: 30px;
  border-radius: 15px;
  max-width: 500px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.5);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.modal-content h3 {
  margin-top: 0;
  color: #4CAF50;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  padding-bottom: 15px;
  margin-bottom: 20px;
}

.modal-content h4 {
  color: #FFD700;
  margin-top: 15px;
}

.modal-content p {
  line-height: 1.6;
  margin-bottom: 15px;
}

.status-detail p {
  padding: 8px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.inventory-list {
  max-height: 300px;
  overflow-y: auto;
}

.inventory-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  margin-bottom: 10px;
}

.item-info {
  display: flex;
  gap: 15px;
}

.item-name {
  font-weight: bold;
}

.item-quantity {
  color: #ccc;
}

.use-btn {
  background: #4CAF50;
  color: #fff;
  border: none;
  padding: 5px 15px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 12px;
}

.use-btn:hover {
  background: #45a049;
}

.close-btn {
  background: #4CAF50;
  color: #fff;
  border: none;
  padding: 12px 30px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 20px;
  width: 100%;
  transition: background 0.3s;
}

.close-btn:hover {
  background: #45a049;
}

.combat-modal {
  background: rgba(0, 0, 0, 0.9);
}

.combat-content {
  max-width: 600px;
}

.combat-status {
  display: flex;
  justify-content: space-around;
  margin-bottom: 20px;
}

.combatant {
  text-align: center;
}

.health-bar {
  width: 150px;
  height: 20px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 10px;
  overflow: hidden;
  margin: 10px auto;
}

.health-fill {
  height: 100%;
  background: #4CAF50;
  transition: width 0.3s;
}

.health-fill.monster {
  background: #f44336;
}

.combat-log {
  max-height: 150px;
  overflow-y: auto;
  background: rgba(0, 0, 0, 0.3);
  padding: 10px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.log-entry {
  padding: 5px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.combat-actions {
  display: flex;
  gap: 10px;
}

.combat-btn {
  flex: 1;
  background: #4CAF50;
  color: #fff;
  border: none;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s;
}

.combat-btn:hover {
  background: #45a049;
}

.progress-section {
  margin-bottom: 20px;
}

.progress-section ul {
  list-style: none;
  padding: 0;
}

.progress-section li {
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 5px;
  margin-bottom: 5px;
}
</style>
