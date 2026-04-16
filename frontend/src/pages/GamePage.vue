<script setup lang="ts">
import CombatModal from '../modules/live/components/CombatModal.vue';
import DialoguePanel from '../modules/live/components/DialoguePanel.vue';
import ExploreResultModal from '../modules/live/components/ExploreResultModal.vue';
import GameActions from '../modules/live/components/GameActions.vue';
import InventoryModal from '../modules/live/components/InventoryModal.vue';
import PlayerStatusBar from '../modules/live/components/PlayerStatusBar.vue';
import PlayerStatusModal from '../modules/live/components/PlayerStatusModal.vue';
import SceneSwitcher from '../modules/live/components/SceneSwitcher.vue';
import StoryProgressModal from '../modules/live/components/StoryProgressModal.vue';
import TreasureResultModal from '../modules/live/components/TreasureResultModal.vue';
import { useLiveRoom } from '../modules/live/hooks/useLiveRoom';

const liveRoom = useLiveRoom();
</script>

<template>
  <div class="game-container">
    <div class="scene-background" :class="liveRoom.player.currentScene">
      <div class="scene-overlay" />
    </div>

    <PlayerStatusBar :player="liveRoom.player" :current-scene-name="liveRoom.currentSceneName" />

    <GameActions
      :disabled="liveRoom.isLoading"
      @explore="liveRoom.handleExploreScene"
      @status="liveRoom.showStatusModal = true"
      @inventory="liveRoom.handleLoadInventory"
      @treasure="liveRoom.handleTreasureHunt"
      @story="liveRoom.handleLoadStoryProgress"
    />

    <SceneSwitcher
      :scene-names="liveRoom.sceneNames"
      :current-scene="liveRoom.player.currentScene"
      :disabled="liveRoom.isLoading"
      @change="liveRoom.handleChangeScene"
    />

    <DialoguePanel
      :loading="liveRoom.isLoading"
      :options="liveRoom.dialogueOptions"
      :history="liveRoom.dialogueHistory"
      :npc-name="liveRoom.currentNpc.name"
      :player-name="liveRoom.player.name"
      :free-input-text="liveRoom.freeInputText"
      @select-option="liveRoom.handleSelectFixedOption"
      @send-input="liveRoom.handleSendFreeInput"
      @update:free-input-text="liveRoom.freeInputText = $event"
    />

    <ExploreResultModal
      :visible="liveRoom.showExploreModal"
      :result="liveRoom.exploreResult"
      @close="liveRoom.showExploreModal = false"
    />

    <PlayerStatusModal
      :visible="liveRoom.showStatusModal"
      :player="liveRoom.player"
      @close="liveRoom.showStatusModal = false"
    />

    <InventoryModal
      :visible="liveRoom.showInventoryModal"
      :items="liveRoom.inventory"
      @close="liveRoom.showInventoryModal = false"
      @use-item="liveRoom.handleUseItem"
    />

    <TreasureResultModal
      :visible="liveRoom.showTreasureModal"
      :result="liveRoom.treasureResult"
      @close="liveRoom.showTreasureModal = false"
    />

    <StoryProgressModal
      :visible="liveRoom.showStoryModal"
      :progress="liveRoom.storyProgress"
      @close="liveRoom.showStoryModal = false"
    />

    <CombatModal
      :visible="liveRoom.showCombatModal"
      :battle-state="liveRoom.battleState"
      @action="liveRoom.handleExecuteCombatTurn"
    />
  </div>
</template>

<style scoped>
.game-container {
  position: relative;
  width: 100%;
  height: 100%;
  overflow: hidden;
}

.scene-background {
  width: 100%;
  height: 64%;
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
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.32);
}
</style>
