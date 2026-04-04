<script lang="ts" setup>
import {ref} from "vue";
import {EnumSetting} from "../../../../util/types";

const props = defineProps<{
    setting: EnumSetting,
    update: () => void
}>()

const open = ref(false);

function toggle() {
    open.value = !open.value;
}

function selectEnum(setting: EnumSetting, index: number) {
    setting.index = index;
    open.value = false;
    props.update()
}
</script>

<template>
    <div :class="$style.settingHeader">{{ setting.name }}</div>
    <div :class="$style.dropdownWrapper">
        <div :class="$style.dropdownSelected" @click="toggle">
            {{ setting.options[setting.index] }}
            <span :class="$style.dropdownArrow">{{ open ? "▴" : "▾" }}</span>
        </div>
        <div v-if="open" :class="$style.dropdownList">
            <div v-for="(val, i) in setting.options" :key="i"
                 :class="[$style.dropdownOption, { [$style.dropdownOptionSelected]: setting.index === i }]"
                 @click="selectEnum(setting, i)">
                {{ val }}
            </div>
        </div>
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

.dropdownWrapper {
    position: relative;
    margin: 4px 8px 6px;
}

.dropdownSelected {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 2px 8px;
    font-size: 11px;
    background: rgba(0, 0, 0, 0.4);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-radius: 2px;
    cursor: pointer;
    color: var(--fg-primary);

    :hover {
        background: rgba(255, 255, 255, 0.05);
    }
}

.dropdownArrow {
    font-size: 11px;
}

.dropdownList {
    position: absolute;
    top: 100%;
    left: 0;
    right: 0;
    z-index: 4;
    background: rgba(10, 10, 15, 0.97);
    border: 1px solid rgba(255, 255, 255, 0.1);
    border-top: none;
    border-radius: 0 0 2px 2px;
}

.dropdownOption {
    padding: 3px 8px;
    font-size: 11px;
    cursor: pointer;
    color: var(--fg-primary);

    .dropdownOptionSelected {
        color: #6060ff;
    }

    :hover {
        background: rgba(255, 255, 255, 0.05);
    }
}
</style>
