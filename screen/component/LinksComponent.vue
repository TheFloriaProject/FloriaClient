<script lang="ts" setup>
import {Discord, Github} from "@vicons/fa";
import {go} from "../util/functions";
</script>

<template>
    <div :class="$style.links">
        <button :class="$style.link" @click="go('discord')">
            <Discord :class="$style.icon"/>
        </button>
        <button :class="$style.link" @click="go('github')">
            <Github :class="$style.icon"/>
        </button>
    </div>
</template>

<style module>
.links {
    position: absolute;
    display: flex;
    padding: 1vw;
    gap: .5vw;
    z-index: 3;
}

.link {
    aspect-ratio: 1;
    width: 3vw;
    font-size: 3vh;
    background-color: var(--fg-primary);
    color: var(--bg-color);
    outline: none;
    border: none;
    border-radius: var(--radius-primary);
    transition: background-color .2s;
    cursor: pointer;
    display: flex;
    justify-content: center;
    align-items: center;

    .icon {
        width: 70%;
    }

    &:hover {
        background-color: var(--fg-secondary);
    }
}
</style>
