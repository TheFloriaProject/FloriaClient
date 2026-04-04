<script lang="ts" setup>
import {sortModulesByWidth} from "../../util/functions";
import {onMounted, onUnmounted, ref} from "vue";
import {Module} from "../../util/types";
import ModuleComponent from "./component/ModuleComponent.vue";

const modules = ref<Module[]>([])

let events: EventSource | null = null

onMounted(async () => {
    await update()

    events = new EventSource("/api/event")
    events.addEventListener("modules:refetch", async () => {
        await update()
    })
})

onUnmounted(() => events?.close())

async function update() {
    const res = await fetch("/api/modules")
    modules.value = await res.json()
}
</script>
<template>
    <img :class="$style.logo" alt="" src="/logo.png"/>
    <div :class="$style.modules">
        <ModuleComponent v-for="module in sortModulesByWidth(modules)" :key="module.id" :module="module"/>
    </div>
</template>
<style module>
.logo {
    padding: 1vh 0 0 1vh;
    height: 5.5vw;
}

.modules {
    max-height: 94.5vh;
    overflow: hidden;

    &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        height: 6vh;
        background: linear-gradient(to bottom, transparent, black);
        pointer-events: none;
    }
}
</style>
