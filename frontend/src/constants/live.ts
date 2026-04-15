import type { BattleState, ExploreResult, PlayerState, TreasureResult } from '../types/live';

export const SCENE_NAMES: Record<string, string> = {
  village: '青牛镇',
  mountain: '青云山',
  cave: '神秘洞穴'
};

export const NPC_NAMES: Record<string, string> = {
  elder: '老者',
  sectMaster: '掌门',
  mysteriousElder: '神秘老者'
};

export const DEFAULT_PLAYER: PlayerState = {
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
};

export const EMPTY_BATTLE: BattleState = {
  battleId: null,
  playerHealth: 100,
  monsterHealth: 100,
  turn: 1,
  logs: []
};

export const EMPTY_EXPLORE_RESULT: ExploreResult = {
  title: '',
  description: '',
  eventType: '',
  reward: ''
};

export const EMPTY_TREASURE_RESULT: TreasureResult = {
  location: '',
  reward: '',
  rarity: ''
};

export const DEFAULT_DIALOGUE_OPTIONS = [
  { optionId: 'option1', text: '我想拜师修仙', type: 'normal', available: true },
  { optionId: 'option2', text: '请问灵根测试在哪里？', type: 'normal', available: true }
];
