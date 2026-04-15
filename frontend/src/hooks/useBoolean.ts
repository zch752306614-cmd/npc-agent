import { ref } from 'vue';

export function useBoolean(defaultValue = false) {
  const value = ref(defaultValue);
  const setTrue = () => {
    value.value = true;
  };
  const setFalse = () => {
    value.value = false;
  };
  const toggle = () => {
    value.value = !value.value;
  };
  return { value, setTrue, setFalse, toggle };
}
