<script setup>
import { ref, onMounted } from 'vue';

// 游戏状态
const gameState = ref({
  scene: 'village',
  player: {
    name: '凡人',
    level: 1,
    cultivation: '练气期',
    hp: 100,
    mp: 50,
    inventory: []
  },
  npc: {
    name: '老者',
    dialogues: [],
    currentDialogue: 0
  },
  freeInput: '',
  dialogueHistory: [],
  loading: false,
  showExploreResult: false,
  showStatus: false,
  showInventory: false,
  exploreResult: {
    title: '',
    description: '',
    items: []
  }
});

// API基础URL
const API_BASE_URL = 'http://localhost:8080/api';

// 加载NPC对话
const loadNpcDialogues = async () => {
  console.log('开始加载NPC对话');
  try {
    const url = `${API_BASE_URL}/npcs/${gameState.value.npc.name}/dialogues`;
    console.log('加载对话URL:', url);
    const response = await fetch(url);
    console.log('加载对话响应状态:', response.status);
    if (response.ok) {
      const dialogues = await response.json();
      console.log('加载对话响应数据:', dialogues);
      gameState.value.npc.dialogues = dialogues;
      // 初始化对话历史
      if (dialogues.length > 0) {
        gameState.value.dialogueHistory.push({
          speaker: 'npc',
          text: dialogues[0].text
        });
        console.log('初始化对话历史:', dialogues[0].text);
      }
    } else {
      console.log('加载对话失败:', response.statusText);
      //  fallback to local dialogues
      gameState.value.npc.dialogues = [
        {
          id: 1,
          text: '年轻人，你是第一次来青牛镇吧？',
          options: [
            '是的，前辈',
            '我是来拜师学艺的',
            '请问这里有修仙门派吗？'
          ]
        }
      ];
      gameState.value.dialogueHistory.push({
        speaker: 'npc',
        text: '年轻人，你是第一次来青牛镇吧？'
      });
    }
  } catch (error) {
    console.error('Failed to load dialogues:', error);
    //  fallback to local dialogues
    gameState.value.npc.dialogues = [
      {
        id: 1,
        text: '年轻人，你是第一次来青牛镇吧？',
        options: [
          '是的，前辈',
          '我是来拜师学艺的',
          '请问这里有修仙门派吗？'
        ]
      }
    ];
    gameState.value.dialogueHistory.push({
      speaker: 'npc',
      text: '年轻人，你是第一次来青牛镇吧？'
    });
  }
};

// 选择对话选项
const selectOption = async (option) => {
  console.log('选择选项:', option);
  try {
    // 创建新的对话历史，包含玩家的新消息
    const newMessage = {
      speaker: 'player',
      text: option
    };
    
    // 准备发送给后端的对话历史
    const requestHistory = [...gameState.value.dialogueHistory, newMessage];
    
    // 将玩家消息添加到对话历史
    gameState.value.dialogueHistory.push(newMessage);
    
    gameState.value.loading = true;
    console.log('设置loading为true');
    
    console.log('开始调用API');
    // 调用后端API获取回复
    const url = `${API_BASE_URL}/npcs/${gameState.value.npc.name}/chat`;
    console.log('API URL:', url);
    
    const requestData = {
      input: option,
      history: requestHistory
    };
    console.log('请求数据:', requestData);
    
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(requestData)
    });
    
    console.log('API响应状态:', response.status);
    
    if (response.ok) {
      const data = await response.json();
      console.log('API响应数据:', data);
      gameState.value.dialogueHistory.push({
        speaker: 'npc',
        text: data.response
      });
    } else {
      console.log('API响应失败:', response.statusText);
      //  fallback response
      gameState.value.dialogueHistory.push({
        speaker: 'npc',
        text: '不错，年轻人有志向。青牛镇附近有一个青云门，你可以去那里试试。'
      });
    }
  } catch (error) {
    console.error('Chat error:', error);
    //  fallback response
    gameState.value.dialogueHistory.push({
      speaker: 'npc',
      text: '不错，年轻人有志向。青牛镇附近有一个青云门，你可以去那里试试。'
    });
  } finally {
    console.log('设置loading为false');
    gameState.value.loading = false;
    gameState.value.npc.currentDialogue++;
    console.log('对话索引递增:', gameState.value.npc.currentDialogue);
    
    // 如果对话结束，循环回第一条对话
    if (gameState.value.npc.currentDialogue >= gameState.value.npc.dialogues.length) {
      gameState.value.npc.currentDialogue = 0;
      console.log('对话循环回第一条');
    }
  }
};

// 获取场景名称
const getSceneName = (sceneKey) => {
  const scenes = {
    'village': '青牛镇',
    'mountain': '青云山',
    'cave': '神秘洞穴'
  };
  return scenes[sceneKey] || '未知地点';
};

// 探索当前区域
const exploreArea = () => {
  const sceneData = {
    village: {
      title: '青牛镇探索',
      description: '你仔细观察了青牛镇的环境。这里是一个宁静的小山村，青石板铺成的小路两旁是古朴的民居。远处可以看到一位老者在路边休息，镇子东边隐约有一座废弃的石台微微发光...',
      items: ['发现：聚灵台线索']
    },
    mountain: {
      title: '青云山探索',
      description: '你站在青云山脚下，云雾缭绕，山势险峻。山路上不时有身着道袍的修士来往。隐约可以看见山顶有一座宏伟的宫殿...',
      items: []
    },
    cave: {
      title: '神秘洞穴探索',
      description: '洞穴内部幽暗深邃，墙壁上刻满了古老的符文。传说这里藏有上古传承，但也危机四伏...',
      items: []
    }
  };
  
  const currentScene = sceneData[gameState.value.scene] || sceneData.village;
  gameState.value.exploreResult = currentScene;
  gameState.value.showExploreResult = true;
};

// 查看角色状态
const viewStatus = () => {
  gameState.value.showStatus = true;
};

// 查看背包
const viewInventory = () => {
  gameState.value.showInventory = true;
};

// 切换场景
const changeScene = () => {
  const scenes = [
    { key: 'village', name: '青牛镇' },
    { key: 'mountain', name: '青云山' },
    { key: 'cave', name: '神秘洞穴' }
  ];
  
  const currentIndex = scenes.findIndex(s => s.key === gameState.value.scene);
  const nextIndex = (currentIndex + 1) % scenes.length;
  const nextScene = scenes[nextIndex];
  
  gameState.value.scene = nextScene.key;
  
  // 更新当前NPC
  if (nextScene.key === 'village') {
    gameState.value.npc.name = '老者';
  } else if (nextScene.key === 'mountain') {
    gameState.value.npc.name = '掌门';
  } else {
    gameState.value.npc.name = '神秘老人';
  }
  
  // 重置对话状态
  gameState.value.npc.currentDialogue = 0;
  gameState.value.dialogueHistory = [];
  loadNpcDialogues();
};

// 发送自由对话
const sendFreeInput = async () => {
  if (gameState.value.freeInput.trim()) {
    gameState.value.dialogueHistory.push({
      speaker: 'player',
      text: gameState.value.freeInput
    });
    
    gameState.value.loading = true;
    try {
      // 调用后端API获取AI回复
      const response = await fetch(`${API_BASE_URL}/npcs/${gameState.value.npc.name}/chat`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          input: gameState.value.freeInput,
          history: gameState.value.dialogueHistory
        })
      });
      
      if (response.ok) {
        const data = await response.json();
        gameState.value.dialogueHistory.push({
          speaker: 'npc',
          text: data.response
        });
      } else {
        //  fallback response
        gameState.value.dialogueHistory.push({
          speaker: 'npc',
          text: '你的问题很有趣。不过现在最重要的是找到修仙的门路，青云门会是你不错的选择。'
        });
      }
    } catch (error) {
      console.error('Chat error:', error);
      //  fallback response
      gameState.value.dialogueHistory.push({
        speaker: 'npc',
        text: '你的问题很有趣。不过现在最重要的是找到修仙的门路，青云门会是你不错的选择。'
      });
    } finally {
      gameState.value.loading = false;
      gameState.value.freeInput = '';
    }
  }
};

// 场景背景图片
const sceneBackgrounds = {
  village: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=ancient%20chinese%20village%20with%20traditional%20houses%2C%20mountain%20background%2C%20peaceful%20atmosphere&image_size=landscape_16_9',
  mountain: 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=misty%20mountain%20with%20ancient%20temple%2C%20chinese%20style%2C%20ethereal%20atmosphere&image_size=landscape_16_9'
};

onMounted(() => {
  loadNpcDialogues();
});
</script>

<template>
  <div class="game-container">
    <!-- 场景展示 -->
    <div class="scene" :style="{ backgroundImage: `url(${sceneBackgrounds[gameState.scene]})` }"></div>
    
    <!-- 角色状态 -->
    <div class="player-status">
      <div class="status-item">
        <span class="label">姓名：</span>
        <span class="value">{{ gameState.player.name }}</span>
      </div>
      <div class="status-item">
        <span class="label">境界：</span>
        <span class="value">{{ gameState.player.cultivation }}</span>
      </div>
      <div class="status-item">
        <span class="label">等级：</span>
        <span class="value">{{ gameState.player.level }}</span>
      </div>
      <div class="status-item">
        <span class="label">气血：</span>
        <span class="value">{{ gameState.player.hp }}/100</span>
      </div>
      <div class="status-item">
        <span class="label">法力：</span>
        <span class="value">{{ gameState.player.mp }}/50</span>
      </div>
    </div>
    
    <!-- 对话区域 -->
    <div class="dialogue-container">
      <div class="dialogue-history">
        <div 
          v-for="(msg, index) in gameState.dialogueHistory" 
          :key="index"
          :class="['dialogue-message', msg.speaker]"
        >
          <div class="speaker">{{ msg.speaker === 'npc' ? gameState.npc.name : gameState.player.name }}：</div>
          <div class="text">{{ msg.text }}</div>
        </div>
        <div v-if="gameState.loading" class="loading">
          <div class="loading-spinner"></div>
          <span>正在思考...</span>
        </div>
      </div>
      
      <!-- 固定选项 -->
      <div class="dialogue-options" v-if="gameState.npc.currentDialogue < gameState.npc.dialogues.length">
        <button 
          v-for="(option, index) in gameState.npc.dialogues[gameState.npc.currentDialogue].options" 
          :key="index"
          class="option-button"
          @click="selectOption(option)"
          :disabled="gameState.loading"
        >
          {{ option }}
        </button>
      </div>
      
      <!-- 自由对话输入 -->
      <div class="free-input" v-else>
        <input 
          type="text" 
          v-model="gameState.freeInput"
          placeholder="输入你想说的话..."
          @keyup.enter="sendFreeInput"
          :disabled="gameState.loading"
        />
        <button class="send-button" @click="sendFreeInput" :disabled="gameState.loading">
          {{ gameState.loading ? '发送中...' : '发送' }}
        </button>
      </div>
    </div>
    
    <!-- 游戏操作区 -->
    <div class="game-actions">
      <button class="action-btn" @click="exploreArea" title="探索当前区域">
        <span class="icon">🔍</span> 探索
      </button>
      <button class="action-btn" @click="viewStatus" title="查看角色状态">
        <span class="icon">📊</span> 状态
      </button>
      <button class="action-btn" @click="viewInventory" title="查看背包">
        <span class="icon">🎒</span> 背包
      </button>
      <button class="action-btn" @click="changeScene" title="切换场景">
        <span class="icon">🚶</span> 移动
      </button>
    </div>
    
    <!-- 探索结果弹窗 -->
    <div v-if="showExploreResult" class="modal" @click="showExploreResult = false">
      <div class="modal-content" @click.stop>
        <h3>{{ exploreResult.title }}</h3>
        <p>{{ exploreResult.description }}</p>
        <div v-if="exploreResult.items && exploreResult.items.length > 0">
          <h4>发现物品：</h4>
          <ul>
            <li v-for="(item, index) in exploreResult.items" :key="index">{{ item }}</li>
          </ul>
        </div>
        <button class="close-btn" @click="showExploreResult = false">关闭</button>
      </div>
    </div>
    
    <!-- 状态查看弹窗 -->
    <div v-if="showStatus" class="modal" @click="showStatus = false">
      <div class="modal-content" @click.stop>
        <h3>角色状态</h3>
        <div class="status-detail">
          <p><strong>姓名：</strong>{{ gameState.player.name }}</p>
          <p><strong>境界：</strong>{{ gameState.player.cultivation }}</p>
          <p><strong>等级：</strong>{{ gameState.player.level }}</p>
          <p><strong>气血：</strong>{{ gameState.player.hp }}/100</p>
          <p><strong>法力：</strong>{{ gameState.player.mp }}/50</p>
          <p><strong>当前场景：</strong>{{ getSceneName(gameState.scene) }}</p>
        </div>
        <button class="close-btn" @click="showStatus = false">关闭</button>
      </div>
    </div>
    
    <!-- 背包弹窗 -->
    <div v-if="showInventory" class="modal" @click="showInventory = false">
      <div class="modal-content" @click.stop>
        <h3>背包</h3>
        <div v-if="gameState.player.inventory && gameState.player.inventory.length > 0">
          <ul>
            <li v-for="(item, index) in gameState.player.inventory" :key="index">
              {{ item.name }} x {{ item.count }}
            </li>
          </ul>
        </div>
        <p v-else>背包是空的</p>
        <button class="close-btn" @click="showInventory = false">关闭</button>
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
  font-family: 'SimSun', serif;
}

.scene {
  width: 100%;
  height: 70%;
  background-size: cover;
  background-position: center;
  position: relative;
}

.player-status {
  position: absolute;
  top: 20px;
  left: 20px;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  padding: 15px;
  border-radius: 8px;
  font-size: 14px;
}

.status-item {
  margin-bottom: 8px;
}

.label {
  font-weight: bold;
  margin-right: 10px;
}

.dialogue-container {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 30%;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.9), rgba(0, 0, 0, 0.5));
  color: #fff;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.dialogue-history {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 15px;
}

.dialogue-message {
  margin-bottom: 10px;
  display: flex;
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
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
  border: 1px solid rgba(255, 255, 255, 0.3);
  padding: 10px 15px;
  border-radius: 5px;
  cursor: pointer;
  text-align: left;
  transition: background 0.3s;
}

.option-button:hover {
  background: rgba(255, 255, 255, 0.3);
}

.free-input {
  display: flex;
  gap: 10px;
}

.free-input input {
  flex: 1;
  padding: 10px;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 5px;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
}

.send-button {
  background: #4CAF50;
  color: #fff;
  border: none;
  padding: 0 20px;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;
}

.send-button:hover {
  background: #45a049;
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

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 游戏操作按钮 */
.game-actions {
  position: absolute;
  top: 20px;
  right: 20px;
  display: flex;
  gap: 10px;
}

.action-btn {
  background: rgba(76, 175, 80, 0.8);
  color: #fff;
  border: none;
  padding: 10px 15px;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 5px;
  transition: background 0.3s, transform 0.2s;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.3);
}

.action-btn:hover {
  background: rgba(69, 160, 69, 1);
  transform: translateY(-2px);
}

.action-btn .icon {
  font-size: 16px;
}

/* 弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  color: #fff;
  padding: 30px;
  border-radius: 15px;
  max-width: 500px;
  width: 90%;
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

.modal-content ul {
  list-style: none;
  padding: 0;
}

.modal-content li {
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 5px;
  margin-bottom: 8px;
}

.status-detail p {
  padding: 8px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.close-btn {
  background: #4CAF50;
  color: #fff;
  border: none;
  padding: 10px 30px;
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
</style>
