<script lang="ts" setup>
import {onMounted, onUnmounted, ref} from "vue";
import OverlayComponent from "../../component/OverlayComponent.vue";
import CategoryComponent from "./component/CategoryComponent.vue";
import {ModuleGroup} from "../../util/types";
import {go} from "../../util/functions";

const categories = ref<ModuleGroup[]>([]);
const scale = ref(1);

let observer: ResizeObserver | null = null;

function updateScale() {
    scale.value = Math.max(window.innerWidth, window.innerHeight) / 1500;
}

onMounted(async () => {
    updateScale()
    observer = new ResizeObserver(updateScale);
    observer.observe(document.documentElement);
    await update()
})

onUnmounted(() => observer?.disconnect());

async function update() {
    const res = await fetch("/api/categories")
    const data: ModuleGroup[] = await res.json()

    let cursor = 0;
    for (const category of data) {
        if (category.category.x === -1 && category.category.y === -1) {
            category.category.x = cursor;
            category.category.y = 0;
            cursor += 12;

            await go("position", category.category)
        }
    }

    categories.value = data
}
</script>
<template>
    <OverlayComponent/>
    <div :class="$style.viewport">
        <div
            ref="canvasRef"
            :class="$style.canvas"
            :style="{ width: '1500px', height: '1500px', transform: `scale(${scale})` }"
        >
            <CategoryComponent
                v-for="category in categories"
                :category="category"
                :scale="scale"
                :style="{ left: category.category.x * 15 + 'px', top: category.category.y * 15 + 'px' }"
                @contextmenu.prevent
            />
        </div>
    </div>
</template>
<style module>
.viewport {
    position: fixed;
    inset: 0;
    z-index: 3;
}

.canvas {
    position: absolute;
    top: 0;
    left: 0;
    transform-origin: top left;
    z-index: 3;
}
</style>
