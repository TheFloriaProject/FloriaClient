<script setup lang="ts">
import {computed} from "vue";
import {BooleanSetting} from "../../../../util/types";

const props = defineProps<{
    setting: BooleanSetting,
    update: () => void
}>()

const checked = computed({
    get: () => props.setting.enabled,
    set: (val) => {
        props.setting.enabled = val
        props.update()
    }
})
</script>
<template>
    <label :class="$style.setting">
        <input type="checkbox" :class="$style.checkbox" v-model="checked"/>
        <span>{{ setting.name }}</span>
    </label>
</template>
<style module>
.setting {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 11px;
    color: var(--fg-primary);
    cursor: pointer;
}

.checkbox {
    appearance: none;
    width: 14px;
    height: 14px;
    border: 2px solid var(--fg-primary);
    border-radius: 4px;
    cursor: pointer;
    display: grid;
    place-content: center;
    flex-shrink: 0;

    &::after {
        content: '';
        width: 5px;
        height: 3px;
        border-left: 2px solid var(--bg-color);
        border-bottom: 2px solid var(--bg-color);
        transform: rotate(-45deg) translateY(-1px);
        opacity: 0;
    }

    &:global(:checked) {
        background: var(--fg-primary);

        &::after {
            opacity: 1;
        }
    }
}
</style>
