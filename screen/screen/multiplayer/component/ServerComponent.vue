<script lang="ts" setup>
import {ServerInfo} from "../../../util/types";
import {go} from "../../../util/functions";
import {AngleRight, Pen, Trash} from "@vicons/fa";
import {computed, ref, watch} from "vue";
import TextComponentComponent from "../../../component/TextComponentComponent.vue";
import EditServerModal from "./EditServerModal.vue";

const props = defineProps<{
    server: ServerInfo
}>()

const iconSrc = computed(() => {
    if (!props.server.favicon) return null

    const bytes = new Uint8Array(props.server.favicon)
    const binary = bytes.reduce((acc, b) => acc + String.fromCharCode(b), '')

    return `data:image/png;base64,${btoa(binary)}`
})

const canvasRef = ref<HTMLCanvasElement | null>(null)
const showEditServerModal = ref(false)

watch([iconSrc, canvasRef], (values) => {
    const src = values[0] as string | null
    const canvas = values[1] as HTMLCanvasElement | null

    if (!src || !canvas) return

    const img = new Image()

    img.onload = () => {
        const ctx = canvas.getContext('2d')!
        ctx.imageSmoothingEnabled = false
        canvas.width = 32
        canvas.height = 32
        ctx.drawImage(img, 0, 0, 32, 32)
    }
    img.src = src
}, {immediate: true})

async function updateServer(name: string, address: string) {
    await go('update', {
        old: props.server,
        name,
        address
    })
    showEditServerModal.value = false
}
</script>
<template>
    <EditServerModal v-if="showEditServerModal" :server="server" :on-save="updateServer" :on-close="() => showEditServerModal = false" />
    <div :class="$style.server">
        <canvas v-if="iconSrc" ref="canvasRef" :class="$style.favicon"/>
        <p :class="$style.name">{{ server.name }}</p>
        <TextComponentComponent v-if="server.label" :class="$style.label" :text-component="server.label"/>
        <p v-if="server.players" :class="$style.players">{{ server.players.online }}/{{ server.players.max }}</p>
        <p v-else :class="$style.players">Offline</p>
        <div :class="$style.options">
            <button :class="$style.option" @click="go('join', server)">
                <AngleRight :class="$style.icon"/>
            </button>
            <button :class="$style.option" @click="() => showEditServerModal = true">
                <Pen :class="$style.icon"/>
            </button>
            <button :class="$style.option" @click="go('delete', server)">
                <Trash :class="$style.icon"/>
            </button>
        </div>
    </div>
</template>
<style module>
.server {
    position: relative;
    width: 36vw;
    height: 8vh;
    background-color: var(--fg-primary);
    color: var(--fg-secondary);
    border-radius: var(--radius-primary);
    transition: background-color .2s;
    flex-shrink: 0;
}

.favicon {
    position: absolute;
    image-rendering: pixelated;
    height: 100%;
}

.name {
    position: absolute;
    font-size: 2.5vh;
    top: -2vh;
    left: 9vh;
}

.label {
    position: absolute;
    font-size: 1.5vh;
    top: 4vh;
    left: 9vh;
    max-width: 20vw;
    line-height: 1vw;
}

.players {
    position: absolute;
    right: 1vh;
    top: -1.5vh;
    font-size: 1vw;
}

.options {
    position: absolute;
    top: 3.5vh;
    right: 1vh;
    display: flex;
    align-items: center;
    gap: .5vh;
}

.option {
    width: 2vw;
    aspect-ratio: 1;
    right: 1vw;
    display: flex;
    align-items: center;
    justify-content: center;
    outline: none;
    border: none;
    border-radius: 4px;
    background-color: var(--fg-secondary);
    color: var(--fg-primary);
    cursor: pointer;
    transition: background-color .2s;

    .icon {
        width: 50%;
    }

    &:hover {
        background-color: var(--bg-color);
    }
}
</style>
