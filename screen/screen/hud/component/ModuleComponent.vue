<script lang="ts" setup>
import {Module} from "../../../util/types";

defineProps<{
    module: Module
}>()
</script>

<template>
    <div v-if="module.toggled" :class="$style.module">
        <p :class="$style.name">{{ module.name }}</p>
    </div>
</template>

<style module>
.module {
    display: flex;
    align-items: center;
    gap: 1vh;
    font-size: 1vw;
    background-color: #00000044;
    width: max-content;
    height: 2vw;
    padding-inline: 1vw;
    border-radius: 0 var(--radius-secondary) var(--radius-secondary) 0;
    border-left: solid .5vh var(--bg-color);

    .name {
        font-size: 1vw;
        color: var(--fg-primary);
    }
}
</style>
