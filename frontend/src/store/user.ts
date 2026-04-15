import { defineStore } from 'pinia';
import { ref } from 'vue';
import { DEFAULT_PLAYER } from '../constants/live';

export const useUserStore = defineStore('user', () => {
  const playerId = ref('player_001');
  const player = ref({ ...DEFAULT_PLAYER });

  function updateScene(sceneCode: string) {
    player.value.currentScene = sceneCode;
  }

  return {
    playerId,
    player,
    updateScene
  };
});
