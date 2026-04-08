<script setup lang="ts">

import {ColorSetting} from "../../../../util/types";

const props = defineProps<{
    setting: ColorSetting,
    update: () => void
}>()

function onInput(e: Event) {
    props.setting.color = parseInt((e.target as HTMLInputElement).value.slice(1), 16);
    props.update();
}

function color() {
    const r = ((props.setting.color >> 16) & 0xFF).toString(16).padStart(2, '0');
    const g = ((props.setting.color >> 8) & 0xFF).toString(16).padStart(2, '0');
    const b = (props.setting.color & 0xFF).toString(16).padStart(2, '0');

    return `#${r}${g}${b}`
}
</script>
<template>
    <div :class="$style.settingHeader">
        {{ setting.name }}
    </div>
    <div :class="$style.wrapper">
        <input
            type="color"
            :class="$style.color"
            :value="color()"
            @input="onInput"
        />
    </div>
</template>
<style module>
.settingHeader {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 2px 12px;
    font-size: 11px;
    color: var(--fg-primary);
}

.wrapper {
    position: relative;
    margin: 4px 8px 6px;
    height: 14px;
    display: flex;
    align-items: center;
}

.color {
    position: absolute;
    left: 0;
    right: 0;
    width: 100%;
    appearance: none;
    background: transparent;
    cursor: pointer;
    outline: none;
    margin: 0;
}
</style>
