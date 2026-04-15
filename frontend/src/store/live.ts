import { defineStore } from 'pinia';
import { ref } from 'vue';
import {
  DEFAULT_DIALOGUE_OPTIONS,
  EMPTY_BATTLE,
  EMPTY_EXPLORE_RESULT,
  EMPTY_TREASURE_RESULT,
  NPC_NAMES
} from '../constants/live';
import type {
  BattleState,
  DialogueMessage,
  DialogueOption,
  ExploreResult,
  InventoryItem,
  StoryProgress,
  TreasureResult
} from '../types/live';

export const useLiveStore = defineStore('live', () => {
  const currentNpc = ref({ code: 'elder', name: '老者' });
  const dialogueOptions = ref<DialogueOption[]>([]);
  const dialogueHistory = ref<DialogueMessage[]>([]);
  const freeInputText = ref('');
  const inventory = ref<InventoryItem[]>([]);
  const storyProgress = ref<StoryProgress | null>(null);

  const battleState = ref<BattleState>({ ...EMPTY_BATTLE });
  const exploreResult = ref<ExploreResult>({ ...EMPTY_EXPLORE_RESULT });
  const treasureResult = ref<TreasureResult>({ ...EMPTY_TREASURE_RESULT });

  const showStatusModal = ref(false);
  const showExploreModal = ref(false);
  const showInventoryModal = ref(false);
  const showCombatModal = ref(false);
  const showTreasureModal = ref(false);
  const showStoryModal = ref(false);

  function setDialogueOptions(options: DialogueOption[]) {
    dialogueOptions.value = options.length ? options : DEFAULT_DIALOGUE_OPTIONS;
  }

  function setNpc(code: string) {
    currentNpc.value.code = code;
    currentNpc.value.name = NPC_NAMES[code] || '未知NPC';
  }

  function resetDialogue() {
    dialogueHistory.value = [];
  }

  return {
    currentNpc,
    dialogueOptions,
    dialogueHistory,
    freeInputText,
    inventory,
    storyProgress,
    battleState,
    exploreResult,
    treasureResult,
    showStatusModal,
    showExploreModal,
    showInventoryModal,
    showCombatModal,
    showTreasureModal,
    showStoryModal,
    setDialogueOptions,
    setNpc,
    resetDialogue
  };
});
