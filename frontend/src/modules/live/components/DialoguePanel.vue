<script setup lang="ts">
import { type Ref } from 'vue';
import type { DialogueMessage, DialogueOption } from '../../../types/live';

const props = defineProps<{
  loading: Ref<boolean>;
  options: DialogueOption[];
  history: DialogueMessage[];
  npcName: string;
  playerName: string;
  freeInputText: string | Ref<string>; // 接受字符串或 Ref<string>
}>();

// 添加调试信息
console.log('DialoguePanel 组件加载，props:', props);
console.log('对话选项数量:', props.options.length);
console.log('对话选项内容:', props.options);
console.log('loading 状态:', props.loading);

const emit = defineEmits<{
  selectOption: [optionId: string];
  sendInput: [];
  'update:freeInputText': [value: string];
}>();

function updateInput(value: string) {
  emit('update:freeInputText', value);
}

// 添加发送按钮点击的调试信息
function handleSendInput() {
  console.log('点击发送按钮');
  console.log('当前输入文本:', props.freeInputText);
  console.log('loading 状态:', props.loading.value);
  emit('sendInput');
}
</script>

<template>
  <div class="dialogue-container">
    <div class="dialogue-history">
      <div v-for="(msg, index) in history" :key="index" class="dialogue-message" :class="msg.speaker">
        <strong>{{ msg.speaker === 'npc' ? npcName : playerName }}：</strong>
        <span>{{ msg.text }}</span>
      </div>
      <div v-if="loading.value" class="loading">正在思考...</div>
    </div>

    <div class="dialogue-input-area">
      <div class="dialogue-options">
        <button
          v-for="option in options"
          :key="option.optionId"
          :disabled="loading.value || !option.available"
          class="option-button"
          @click="emit('selectOption', option.optionId)"
        >
          {{ option.text }}
        </button>
      </div>
      <div class="free-input">
        <input
          :value="typeof freeInputText === 'object' ? freeInputText.value : freeInputText"
          :disabled="loading.value"
          placeholder="输入你想说的话..."
          @input="(e) => updateInput((e.target as HTMLInputElement).value)"
          @keyup.enter="handleSendInput"
        />
        <button :disabled="loading.value || !((typeof freeInputText === 'object' ? freeInputText.value : freeInputText) || '').trim()" @click="handleSendInput">发送</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.dialogue-container {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  height: 38%;
  padding: 16px;
  color: #fff;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.95), rgba(0, 0, 0, 0.7));
}

.dialogue-history {
  height: 56%;
  overflow-y: auto;
}

.dialogue-message {
  margin: 8px 0;
}

.dialogue-message.npc strong {
  color: #4caf50;
}

.dialogue-message.player strong {
  color: #4da3ff;
}

.dialogue-input-area {
  margin-top: 12px;
  display: grid;
  gap: 8px;
}

.dialogue-options {
  display: grid;
  gap: 8px;
}

.option-button,
.free-input button {
  border: 0;
  border-radius: 8px;
  padding: 10px 12px;
  cursor: pointer;
  color: #fff;
  background: rgba(76, 175, 80, 0.8);
}

.free-input {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 8px;
}

.free-input input {
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  background: rgba(0, 0, 0, 0.4);
  color: #fff;
  padding: 10px 12px;
}

.loading {
  color: #d3d3d3;
}
</style>
