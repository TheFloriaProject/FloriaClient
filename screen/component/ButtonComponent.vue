<script lang="ts" setup>
defineProps<{
  onClick: () => void,
  title: string
}>()
</script>

<template>
  <button :class="$style.button" @click="onClick">{{ title }}</button>
</template>

<style module>
.button {
  width: 16vw;
  height: 8vh;
  font-size: 3vh;
  background-color: var(--fg-primary);
  color: var(--bg-color);
  outline: none;
  border: none;
  border-radius: var(--radius-primary);
  transition: background-color .2s;
  cursor: pointer;

  &:hover {
    background-color: var(--fg-secondary);
  }
}
</style>
