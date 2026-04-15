import httpClient from '../../../services/http';
import type { DialogueMessage } from '../../../types/live';

export function getDialogueOptions(npcCode: string, playerId: string) {
  return httpClient.get('/dialogue/options', { params: { npcCode, playerId } });
}

export function submitFixedDialogueOption(npcCode: string, optionId: string, playerId: string) {
  return httpClient.post('/dialogue/fixed', null, { params: { npcCode, optionId, playerId } });
}

export function submitFreeDialogue(
  npcCode: string,
  input: string,
  history: DialogueMessage[],
  playerId: string
) {
  return httpClient.post('/dialogue/free', { npcCode, input, history, playerId });
}

export function changeScene(playerId: string, sceneCode: string) {
  return httpClient.post('/exploration/change-scene', null, { params: { playerId, sceneCode } });
}

export function exploreScene(playerId: string, sceneCode: string, positionX: number, positionY: number) {
  return httpClient.post('/exploration/explore', { playerId, sceneCode, positionX, positionY });
}

export function startCombat(playerId: string, monsterId: string) {
  return httpClient.post('/combat/start', null, { params: { playerId, monsterId } });
}

export function executeCombatTurn(battleId: string, action: string) {
  return httpClient.post('/combat/turn', { battleId, action });
}

export function endCombat(battleId: string, winner: string) {
  return httpClient.post('/combat/end', null, { params: { battleId, winner } });
}

export function createTreasureHunt(playerId: string, location: string) {
  return httpClient.post('/treasure-hunt/start', null, { params: { playerId, location } });
}

export function getInventory(playerId: string) {
  return httpClient.get('/inventory', { params: { playerId } });
}

export function useInventoryItem(playerId: string, itemId: string) {
  return httpClient.post('/inventory/use', null, { params: { playerId, itemId } });
}

export function getStoryProgress(playerId: string) {
  return httpClient.get('/story/progress', { params: { playerId } });
}
