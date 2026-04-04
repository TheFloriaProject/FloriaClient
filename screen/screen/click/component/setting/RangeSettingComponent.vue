<script setup lang="ts">

import {RangeSetting} from "../../../../util/types";

const props = defineProps<{
    setting: RangeSetting,
    update: () => void
}>()

function onInput(e: Event) {
    props.setting.value = parseFloat((e.target as HTMLInputElement).value);
    props.update();
}

function sliderPercent(setting: RangeSetting) {
    return ((setting.value - setting.min) / (setting.max - setting.min)) * 100;
}
</script>
<template>
    <div :class="$style.settingHeader">
        {{ setting.name }}
        <span :class="$style.sliderValue">{{ setting.value.toFixed(2) }}</span>
    </div>
    <div :class="$style.sliderWrapper">
        <div :class="$style.sliderTrack">
            <div :class="$style.sliderFill" :style="{ width: sliderPercent(setting) + '%' }"/>
        </div>
        <input
            type="range"
            :class="$style.slider"
            :min="setting.min"
            :max="setting.max"
            :step="setting.step > 0 ? setting.step : 'any'"
            :value="setting.value"
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

.sliderValue {
    font-size: 11px;
    color: var(--fg-primary);
}

.sliderWrapper {
    position: relative;
    margin: 4px 8px 6px;
    height: 14px;
    display: flex;
    align-items: center;
}

.sliderTrack {
    position: absolute;
    left: 0;
    right: 0;
    height: 6px;
    background: rgba(255, 255, 255, 0.1);
    border-radius: 2px;
    pointer-events: none;
}

.sliderFill {
    height: 100%;
    background: var(--bg-color);
    border-radius: 2px;
}

.slider {
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

::-webkit-slider-thumb {
    appearance: none;
    width: 4px;
    height: 14px;
    border-radius: 2px;
    background: var(--fg-primary);
    cursor: pointer;
}

::-moz-range-thumb {
    width: 8px;
    height: 14px;
    border-radius: 2px;
    background: var(--fg-primary);
    border: none;
    cursor: pointer;
}
</style>
