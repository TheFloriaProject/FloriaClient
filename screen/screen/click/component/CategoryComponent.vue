<script lang="ts" setup>
import {ref} from "vue";
import ModuleComponent from "./ModuleComponent.vue";
import {ModuleGroup} from "../../../util/types";
import {go} from "../../../util/functions";

const props = defineProps<{
    category: ModuleGroup,
    scale: number
}>();

const isDragging = ref(false);

async function onHeaderPointerDown(event: PointerEvent) {
    if (event.button === 2) {
        await collapse()
        return;
    }
    if (event.button !== 0) return;

    const startX = props.category.category.x * 15;
    const startY = props.category.category.y * 15;
    const dragOffsetX = event.clientX / props.scale - startX;
    const dragOffsetY = event.clientY / props.scale - startY;

    (event.target as HTMLElement).setPointerCapture(event.pointerId);

    function onMove(event: PointerEvent) {
        isDragging.value = true;

        const rawX = event.clientX / props.scale - dragOffsetX;
        const rawY = event.clientY / props.scale - dragOffsetY;

        props.category.category.x = Math.round(rawX / 15);
        props.category.category.y = Math.round(rawY / 15);
    }

    async function onUp() {
        isDragging.value = false;

        window.removeEventListener("pointermove", onMove);
        window.removeEventListener("pointerup", onUp);

        await go("position", props.category.category)
    }

    window.addEventListener("pointermove", onMove);
    window.addEventListener("pointerup", onUp);
}

async function collapse() {
    props.category.collapsed = !props.category.collapsed;

    await go("collapse", {
        category: props.category.category.id,
        collapsed: props.category.collapsed
    })
}
</script>
<template>
    <div :class="$style.panel">
        <div :class="$style.panelHeader" @pointerdown="onHeaderPointerDown($event)">
            {{ category.collapsed ? "▶" : "▼" }} {{ category.category.name }}
        </div>
        <ModuleComponent v-for="module in category.modules" v-if="!category.collapsed" :module="module"/>
    </div>
</template>
<style module>
.panel {
    position: absolute;
    z-index: 3;
    width: 180px;
    background: rgba(0, 0, 0, 0.5);
    border-radius: var(--radius-primary);
    user-select: none;
}

.panelHeader {
    padding: 8px 12px;
    cursor: grab;
    background: var(--bg-color);
    border-radius: 3px 3px 0 0;
    font-size: 13px;
    font-weight: 600;
    touch-action: none;
    color: var(--fg-primary);
}

.panelHeader:active {
    cursor: grabbing;
}
</style>
