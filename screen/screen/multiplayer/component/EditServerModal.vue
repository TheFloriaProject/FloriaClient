<script setup lang="ts">
import {ref} from "vue";
import {ServerInfo} from "../../../util/types";

const props = defineProps<{
    server: ServerInfo
    onSave: (name: string, address: string) => void
    onClose: () => void
}>()

const name = ref(props.server.name)
const address = ref(props.server.address)
</script>

<template>
    <div :class="$style.wrapper">
        <div :class="$style.modal">
            <input :class="$style.text" type="text" placeholder="Name" v-model="name"/>
            <input :class="$style.text" type="text" placeholder="Address" v-model="address"/>
            <div :class="$style.buttons">
                <button :disabled="address.trim() === ''" :class="$style.button" @click="onSave(name, address)">Save</button>
                <button :class="$style.button" @click="onClose">Close</button>
            </div>
        </div>
    </div>
</template>

<style module>
.wrapper {
    position: absolute;
    width: 100vw;
    height: 100vh;
    background-color: #00000088;
    z-index: 4;
    display: flex;
    justify-content: center;
}

.modal {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    gap: 1vh;
    width: 50vh;
    height: 26vh;
    top: 50%;
    left: 50%;
    position: absolute;
    translate: -50% -50%;
    background-color: #00000088;
    border-radius: var(--radius-primary);
}

.text {
    z-index: 4;
    width: 20vw;
    height: 2vw;
    outline: none;
    border: 0.2vh solid var(--fg-secondary);
    color: var(--fg-primary);
    font-size: 1.1vw;
    background-color: #3a3a3a;
    border-radius: var(--radius-primary);
}

.buttons {
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    width: 20.3vw;
}

.button {
    width: 10vw;
    height: 4vh;
    font-size: 2.2vh;
    background-color: var(--fg-primary);
    color: var(--bg-color);
    outline: none;
    border: none;
    border-radius: var(--radius-primary);
    transition: background-color .2s;
    cursor: pointer;

    &:hover {
        background-color: var(--fg-secondary);
    }
}
</style>
