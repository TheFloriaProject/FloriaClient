<script lang="ts" setup>
import {ref} from "vue";
import {BooleanSetting, EnumSetting, Module, RangeSetting, Setting} from "../../../util/types";
import BooleanSettingComponent from "./setting/BooleanSettingComponent.vue";
import RangeSettingComponent from "./setting/RangeSettingComponent.vue";
import EnumSettingComponent from "./setting/EnumSettingComponent.vue";
import {go} from "../../../util/functions";

const props = defineProps<{
    module: Module
}>()

const expanded = ref(false)

async function onClick() {
    props.module.toggled = !props.module.toggled;

    await go("toggle", props.module)
}

function onRightClick() {
    if (props.module.settings.length === 0) return;
    expanded.value = !expanded.value;
}

async function updateSetting(setting: Setting) {
    await go("setting", {
        setting,
        module: props.module.id,
    })
}
</script>
<template>
    <div :class="[$style.moduleRow, { [$style.enabled]: module.toggled }]" @click="async () => onClick()"
         @contextmenu.prevent.stop="onRightClick()">
        {{ module.name }}
        <span v-if="module.settings.length > 0" :class="$style.expandArrow">{{ expanded ? "▾" : "▸" }}</span>
    </div>
    <div v-for="setting in module.settings" v-if="expanded" :key="setting.id" :class="$style.setting">
        <BooleanSettingComponent v-if="setting.type === 'BOOLEAN'" :setting="setting as BooleanSetting"
                                 :update="async () => await updateSetting(setting)"/>
        <RangeSettingComponent v-if="setting.type === 'RANGE'" :setting="setting as RangeSetting"
                               :update="async () => await updateSetting(setting)"/>
        <EnumSettingComponent v-if="setting.type === 'ENUM'" :setting="setting as EnumSetting"
                              :update="async () => await updateSetting(setting)"/>
    </div>
</template>
<style module>
.moduleRow {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 3px 8px;
    font-size: 12px;
    cursor: pointer;
    color: var(--fg-primary);
}

.moduleRow.enabled {
    background: var(--fg-secondary);
}

.expandArrow {
    font-size: 11px;
}

.setting {
    border-top: 1px solid rgba(255, 255, 255, 0.05);
}
</style>
