<script lang="ts" setup>
import {onMounted, onUnmounted, ref} from "vue";
import OverlayComponent from "../../component/OverlayComponent.vue";
import LogoComponent from "../../component/LogoComponent.vue";
import ButtonComponent from "../../component/ButtonComponent.vue";
import {go} from "../../util/functions";
import ServerComponent from "./component/ServerComponent.vue";
import LinksComponent from "../../component/LinksComponent.vue";
import AddServerModal from "./component/AddServerModal.vue";
import {ServerInfo} from "../../util/types";
import ConnectModal from "./component/ConnectModal.vue";

const servers = ref<ServerInfo[]>([])
const showAddServerModal = ref(false)
const showConnectModal = ref(false)

let events: EventSource | null = null

onMounted(async () => {
    await update()

    events = new EventSource("/api/event")
    events.addEventListener("servers:refetch", async () => {
        await update()
    })
})

onUnmounted(() => events?.close())

async function update() {
    const res = await fetch("/api/servers")
    servers.value = await res.json()
}

async function addServer(name: string, address: string) {
    await go('create', {
        name,
        address
    })
    showAddServerModal.value = false
}

async function connect(address: string) {
    await go('connect', {
        address
    })
}
</script>
<template>
    <div class="body">
        <OverlayComponent/>
        <LogoComponent/>
        <LinksComponent/>
        <AddServerModal v-if="showAddServerModal" :on-save="addServer" :on-close="() => showAddServerModal = false"/>
        <ConnectModal v-if="showConnectModal" :on-save="connect" :on-close="() => showConnectModal = false"/>
        <div :class="$style.buttons">
            <ButtonComponent :onClick="() => showConnectModal = true" title="Direct Connection"/>
            <ButtonComponent :onClick="() => showAddServerModal = true" title="Add Server"/>
            <ButtonComponent :onClick="update" title="Refresh"/>
            <ButtonComponent :onClick="() => go('back')" title="Back"/>
        </div>
        <div :class="$style.servers">
            <h1>Servers</h1>
            <div :class="$style.scroll">
                <ServerComponent v-for="server in servers" :server="server"/>
            </div>
        </div>
        <!--        <AccountComponent/>-->
    </div>
</template>
<style module>
.buttons {
    position: absolute;
    right: 1vh;
    display: flex;
    flex-direction: column;
    gap: 1vh;
    top: 50%;
    translate: 0 -50%;
    z-index: 3;
}

.servers {
    position: absolute;
    left: 50%;
    translate: -50% 0;
    z-index: 3;
    top: 5vh;
    width: 46vw;
    background-color: #00000055;
    padding-block: 7vh;
    height: 76vh;
    gap: 1vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    border-radius: var(--radius-secondary);

    h1 {
        font-size: 2vh;
        position: absolute;
        top: 0;
        left: 5vw;
        color: var(--fg-primary);
        background-color: var(--fg-secondary);
        border-radius: var(--radius-secondary);
        display: flex;
        justify-content: center;
        align-items: center;
        width: 36vw;
        height: 4vh;
    }

    .scroll {
        overflow: scroll;
        overflow-x: hidden;
        gap: 1vh;
        width: 37vw;
        display: flex;
        box-sizing: border-box;
        flex-direction: column;
        align-items: center;
        scrollbar-width: thin;
        scrollbar-color: var(--fg-primary) transparent;
        padding-bottom: 0;
        margin-bottom: 0;

        &::-webkit-scrollbar {
            width: 1vh;
        }

        &::-webkit-scrollbar-track {
            background: transparent;
        }

        &::-webkit-scrollbar-thumb {
            background-color: var(--fg-primary);
            border-radius: 9999px;
        }
    }
}
</style>
