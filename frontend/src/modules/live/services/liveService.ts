import httpClient from '../../../services/http';
import type { DialogueMessage } from '../../../types/live';

export function getDialogueOptions(npcCode: string, playerId: string) {
  console.log('调用 getDialogueOptions:', { npcCode, playerId });
  return httpClient.get('/dialogue/options', { params: { npcCode, playerId } }).then(options => {
    console.log('API 返回的 options:', options);
    // 为每个选项添加 available 属性
    const processedOptions = (options || []).map((option: any) => ({
      ...option,
      available: true
    }));
    console.log('处理后的 options:', processedOptions);
    return processedOptions;
  }).catch(error => {
    console.error('获取对话选项失败:', error);
    return [];
  });
}

export function submitFixedDialogueOption(npcCode: string, optionId: string, playerId: string) {
  return httpClient.post('/dialogue/fixed', null, { params: { npcCode, optionId, playerId } }).then(result => {
    // 为 nextOptions 添加 available 属性
    if (result.nextOptions) {
      result.nextOptions = result.nextOptions.map((option: any) => ({
        ...option,
        available: true
      }));
    }
    return result;
  });
}

export function submitFreeDialogue(
  npcCode: string,
  input: string,
  history: DialogueMessage[],
  playerId: string
) {
  console.log('调用 submitFreeDialogue:', { npcCode, input, playerId });
  return httpClient.post('/dialogue/free', { npcCode, input, history, playerId }).then(result => {
    console.log('API 返回的 result:', result);
    return result;
  }).catch(error => {
    console.error('提交自由对话失败:', error);
    throw error;
  });
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
