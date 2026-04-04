<script lang="ts" setup>
import {Undo} from "@vicons/fa";
import {onMounted, ref} from "vue";
import {go} from "../util/functions";

const username = ref("")
const avatar = ref("")

onMounted(async () => {
    await update()
})

async function update() {
    const res = await fetch("/api/account")
    const data = await res.json()

    username.value = data.username
    avatar.value = data.avatar
}
</script>
<template>
    <div :class="$style.account">
        <img :class="$style.avatar" :src="avatar" alt=""/>
        <span>{{ username }}</span>
        <button :class="$style.option" @click="async () => {
      go('alt').then(async () => {
        await update()
      })
    }">
            <Undo :class="$style.icon"/>
        </button>
    </div>
</template>
<style module>
.account {
    width: 18vw;
    height: 8vh;
    position: absolute;
    bottom: 1vw;
    left: 1vw;
    font-size: 3vh;
    background-color: var(--fg-primary);
    color: var(--bg-color);
    outline: none;
    border: none;
    border-radius: var(--radius-primary);
    transition: background-color .2s;
    display: flex;
    align-items: center;
    z-index: 3;

    .avatar {
        height: 60%;
        margin-left: 1vw;
        border-radius: var(--radius-secondary);
        overflow: hidden;
    }

    span {
        padding-left: 1vw;
        white-space: nowrap;
        overflow: hidden;
        font-size: clamp(1vh, 3vw, 3vh);
        min-width: 0;
        flex-shrink: 1;
        max-width: calc(18vw - 2.5vw - 2vw - 1vw);
    }
}

.option {
    width: 2.5vw;
    aspect-ratio: 1;
    position: absolute;
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
        width: 70%;
    }

    &:hover {
        background-color: var(--bg-color);
    }
}
</style>
