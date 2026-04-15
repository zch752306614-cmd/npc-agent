import { computed, onMounted, ref } from 'vue';
import { DEFAULT_DIALOGUE_OPTIONS, EMPTY_BATTLE, SCENE_NAMES } from '../../../constants/live';
import { useLiveStore } from '../../../store/live';
import { usePermissionStore } from '../../../store/permission';
import { useUserStore } from '../../../store/user';
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
  const localLoading = ref(false);

  const currentSceneName = computed(() => SCENE_NAMES[userStore.player.currentScene] || '未知地点');
  const isLoading = computed(() => localLoading.value || isHttpLoading.value > 0);

  async function runWithLoading(task: () => Promise<void>) {
    localLoading.value = true;
    try {
      await task();
    } catch (error) {
      // 统一入口做兜底，避免页面层散落 try/catch。
      console.error(error);
      window.alert(error instanceof Error ? error.message : '操作失败，请重试');
    } finally {
      localLoading.value = false;
    }
  }

  async function loadDialogueOptions() {
    await runWithLoading(async () => {
      const options = await getDialogueOptions(liveStore.currentNpc.code, userStore.playerId);
      liveStore.setDialogueOptions(options || DEFAULT_DIALOGUE_OPTIONS);
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
    const text = liveStore.freeInputText.trim();
    if (!text) return;

    await runWithLoading(async () => {
      liveStore.dialogueHistory.push({ speaker: 'player', text });
      const result = await submitFreeDialogue(
        liveStore.currentNpc.code,
        text,
        liveStore.dialogueHistory,
        userStore.playerId
      );
      liveStore.dialogueHistory.push({ speaker: 'npc', text: result.npcResponse });
      if (result.storyAdvance && result.nextOptions) {
        liveStore.setDialogueOptions(result.nextOptions);
      }
      liveStore.freeInputText = '';
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
      loadDialogueOptions();
    }
  });

  return {
    sceneNames: SCENE_NAMES,
    player: userStore.player,
    currentNpc: liveStore.currentNpc,
    dialogueOptions: liveStore.dialogueOptions,
    dialogueHistory: liveStore.dialogueHistory,
    freeInputText: liveStore.freeInputText,
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
    isLoading,
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
