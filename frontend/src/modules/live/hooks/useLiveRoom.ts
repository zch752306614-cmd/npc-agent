import { computed, onMounted, ref } from 'vue';
import { DEFAULT_DIALOGUE_OPTIONS, EMPTY_BATTLE, SCENE_NAMES } from '../../../constants/live';
import { useLiveStore, usePermissionStore, useUserStore } from '../../../store';
import {
  changeScene,
  createTreasureHunt,
  endCombat,
  executeCombatTurn,
  exploreScene,
  getDialogueOptions,
  getInventory,
  getStoryProgress,
  startCombat,
  submitFixedDialogueOption,
  submitFreeDialogue,
  useInventoryItem
} from '../services/liveService';
import { randomInt } from '../../../utils/random';
import { isHttpLoading } from '../../../services/http';

function getEventTitle(eventType: string) {
  const titleMap: Record<string, string> = {
    resource: '发现资源',
    monster: '遭遇怪物',
    exploration: '探索发现'
  };
  return titleMap[eventType] || '探索结果';
}

export function useLiveRoom() {
  const userStore = useUserStore();
  const permissionStore = usePermissionStore();
  const liveStore = useLiveStore();

  // 直接使用 ref 而不是 computed
  const isLoading = ref(false);

  const currentSceneName = computed(() => SCENE_NAMES[userStore.player.currentScene] || '未知地点');

  // 为了保持响应式，使用 computed 包装 isLoading
  const isLoadingComputed = computed(() => isLoading.value);

  // 为了保持响应式，使用 computed 包装 freeInputText
  const freeInputText = computed({
    get: () => liveStore.freeInputText.value,
    set: (value) => { liveStore.freeInputText.value = value; }
  });

  async function runWithLoading(task: () => Promise<void>) {
    isLoading.value = true;
    try {
      await task();
    } catch (error) {
      // 统一入口做兜底，避免页面层散落 try/catch。
      console.error(error);
      window.alert(error instanceof Error ? error.message : '操作失败，请重试');
    } finally {
      isLoading.value = false;
    }
  }

  async function loadDialogueOptions() {
    console.log('开始加载对话选项...');
    console.log('当前NPC:', liveStore.currentNpc.code);
    console.log('当前玩家ID:', userStore.playerId);
    await runWithLoading(async () => {
      try {
        const options = await getDialogueOptions(liveStore.currentNpc.code, userStore.playerId);
        console.log('获取到的对话选项:', options);
        liveStore.setDialogueOptions(options || DEFAULT_DIALOGUE_OPTIONS);
        console.log('设置后的对话选项:', liveStore.dialogueOptions);
      } catch (error) {
        console.error('加载对话选项失败:', error);
        liveStore.setDialogueOptions(DEFAULT_DIALOGUE_OPTIONS);
      }
    });
  }

  async function handleSelectFixedOption(optionId: string) {
    const selectedOption = liveStore.dialogueOptions.find((option) => option.optionId === optionId);
    if (!selectedOption) return;

    await runWithLoading(async () => {
      liveStore.dialogueHistory.push({ speaker: 'player', text: selectedOption.text });
      const result = await submitFixedDialogueOption(liveStore.currentNpc.code, optionId, userStore.playerId);
      liveStore.dialogueHistory.push({ speaker: 'npc', text: result.npcResponse });
      liveStore.setDialogueOptions(result.nextOptions || DEFAULT_DIALOGUE_OPTIONS);
    });
  }

  async function handleSendFreeInput() {
    console.log('调用 handleSendFreeInput');
    const text = (liveStore.freeInputText.value || '').trim(); // 直接从 liveStore 中获取，添加默认值
    console.log('当前输入文本:', text);
    if (!text) return;

    await runWithLoading(async () => {
      console.log('开始处理发送请求');
      liveStore.dialogueHistory.push({ speaker: 'player', text });
      console.log('添加玩家消息到对话历史');
      const result = await submitFreeDialogue(
        liveStore.currentNpc.code,
        text,
        liveStore.dialogueHistory.value, // 传递实际的数组，而不是 ref 对象
        userStore.playerId
      );
      console.log('API 调用成功，返回结果:', result);
      liveStore.dialogueHistory.push({ speaker: 'npc', text: result.npcResponse });
      console.log('添加 NPC 回复到对话历史');
      if (result.storyAdvance && result.nextOptions) {
        liveStore.setDialogueOptions(result.nextOptions);
        console.log('更新对话选项');
      }
      liveStore.freeInputText.value = ''; // 直接操作liveStore清空输入框
      console.log('清空输入框');
      console.log('清空后liveStore.freeInputText.value:', liveStore.freeInputText.value);
    });
  }

  async function handleChangeScene(sceneCode: string) {
    if (sceneCode === userStore.player.currentScene) return;
    await runWithLoading(async () => {
      const result = await changeScene(userStore.playerId, sceneCode);
      userStore.updateScene(sceneCode);
      if (result.npcs?.[0]) {
        liveStore.setNpc(result.npcs[0]);
      }
      liveStore.resetDialogue();
      await loadDialogueOptions();
    });
  }

  async function handleExploreScene() {
    await runWithLoading(async () => {
      const result = await exploreScene(
        userStore.playerId,
        userStore.player.currentScene,
        randomInt(100),
        randomInt(100)
      );
      liveStore.exploreResult = {
        title: getEventTitle(result.eventType),
        description: result.eventDescription,
        eventType: result.eventType,
        reward: result.reward || ''
      };
      liveStore.showExploreModal = true;
      if (result.eventType === 'monster' && result.monster?.id) {
        await handleStartCombat(String(result.monster.id));
      }
    });
  }

  async function handleStartCombat(monsterId: string) {
    await runWithLoading(async () => {
      const result = await startCombat(userStore.playerId, monsterId);
      liveStore.battleState = {
        battleId: result.battleId,
        playerHealth: result.player.health,
        monsterHealth: result.monster.health,
        turn: result.turn,
        logs: [`战斗开始！你遇到了 ${result.monster.name}`]
      };
      liveStore.showCombatModal = true;
    });
  }

  async function handleExecuteCombatTurn(action: string) {
    if (!liveStore.battleState.battleId) return;
    await runWithLoading(async () => {
      const result = await executeCombatTurn(liveStore.battleState.battleId as string, action);
      liveStore.battleState.playerHealth = result.playerHealth;
      liveStore.battleState.monsterHealth = result.monsterHealth;
      liveStore.battleState.turn = result.turn;
      liveStore.battleState.logs.push(result.playerAction, result.monsterAction);
      if (result.battleStatus === 'ended') {
        await handleEndCombat('player');
      }
    });
  }

  async function handleEndCombat(winner: string) {
    if (!liveStore.battleState.battleId) return;
    await runWithLoading(async () => {
      const result = await endCombat(liveStore.battleState.battleId as string, winner);
      liveStore.battleState.logs.push(result.message);
      if (result.rewards?.length) {
        liveStore.battleState.logs.push(`获得奖励：${result.rewards.join(', ')}`);
      }
      setTimeout(() => {
        liveStore.showCombatModal = false;
        liveStore.battleState = { ...EMPTY_BATTLE };
      }, 800);
    });
  }

  async function handleLoadInventory() {
    await runWithLoading(async () => {
      liveStore.inventory = await getInventory(userStore.playerId);
      liveStore.showInventoryModal = true;
    });
  }

  async function handleUseItem(itemId: string) {
    await runWithLoading(async () => {
      const result = await useInventoryItem(userStore.playerId, itemId);
      window.alert(result.message || '使用成功');
      await handleLoadInventory();
    });
  }

  async function handleTreasureHunt() {
    await runWithLoading(async () => {
      const result = await createTreasureHunt(userStore.playerId, userStore.player.currentScene);
      liveStore.treasureResult = {
        location: result.location,
        reward: result.reward,
        rarity: result.rarity
      };
      liveStore.showTreasureModal = true;
    });
  }

  async function handleLoadStoryProgress() {
    await runWithLoading(async () => {
      liveStore.storyProgress = await getStoryProgress(userStore.playerId);
      liveStore.showStoryModal = true;
    });
  }

  onMounted(() => {
    if (permissionStore.canOperateLiveRoom) {
      // 添加开场剧情
      if (liveStore.dialogueHistory.length === 0) {
        // 初始开场白
        liveStore.dialogueHistory.push({
          speaker: 'npc',
          text: '年轻人，你来了。我是镇上的长老，你就是那个被选中的孩子吧？'
        });
      }
      loadDialogueOptions();
    }
  });

  return {
    sceneNames: SCENE_NAMES,
    player: userStore.player,
    currentNpc: liveStore.currentNpc,
    dialogueOptions: liveStore.dialogueOptions,
    dialogueHistory: liveStore.dialogueHistory,
    freeInputText, // 返回计算属性
    inventory: liveStore.inventory,
    storyProgress: liveStore.storyProgress,
    battleState: liveStore.battleState,
    exploreResult: liveStore.exploreResult,
    treasureResult: liveStore.treasureResult,
    showStatusModal: liveStore.showStatusModal,
    showExploreModal: liveStore.showExploreModal,
    showInventoryModal: liveStore.showInventoryModal,
    showCombatModal: liveStore.showCombatModal,
    showTreasureModal: liveStore.showTreasureModal,
    showStoryModal: liveStore.showStoryModal,
    currentSceneName,
    isLoading: isLoadingComputed, // 返回计算属性，确保响应式
    handleSelectFixedOption,
    handleSendFreeInput,
    handleChangeScene,
    handleExploreScene,
    handleExecuteCombatTurn,
    handleLoadInventory,
    handleUseItem,
    handleTreasureHunt,
    handleLoadStoryProgress
  };
}
