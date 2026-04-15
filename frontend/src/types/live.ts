export type Speaker = 'player' | 'npc';

export interface PlayerState {
  name: string;
  level: number;
  cultivationLevel: number;
  realm: string;
  health: number;
  maxHealth: number;
  spiritualPower: number;
  maxSpiritualPower: number;
  attack: number;
  defense: number;
  experience: number;
  currentScene: string;
}

export interface DialogueOption {
  optionId: string;
  text: string;
  type?: string;
  available: boolean;
}

export interface DialogueMessage {
  speaker: Speaker;
  text: string;
}

export interface InventoryItem {
  id: string;
  name: string;
  quantity: number;
}

export interface StoryProgress {
  completedNodes: string[];
  unlockedNodes: string[];
  currentNode: string;
}

export interface BattleState {
  battleId: string | null;
  playerHealth: number;
  monsterHealth: number;
  turn: number;
  logs: string[];
}

export interface ExploreResult {
  title: string;
  description: string;
  eventType: string;
  reward: string;
}

export interface TreasureResult {
  location: string;
  reward: string;
  rarity: string;
}
