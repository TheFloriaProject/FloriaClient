<script setup lang="ts">
import {toast, ToastType} from "vue3-toastify";
import {onMounted, onUnmounted} from "vue";
import {Notification} from "../../util/types";

let events: EventSource | null = null

onMounted(() => {
    events = new EventSource("/api/event")
    events.addEventListener("notification", event => {
        notify(JSON.parse(event.data) as Notification)
    })
})

onUnmounted(() => events?.close())

function notify(notification: Notification) {
    toast(notification.title, {
        content: notification.description,
        type: notification.type.toLowerCase() as ToastType,
        style: {
            width: '20vw',
            height: '2vh',
            fontSize: '2vh'
        }
    })
}
</script>
<template>
    <div class="wrapper"></div>
</template>
<style>
.wrapper {
    position: absolute;
    width: 100vw;
    height: 100vh;
}

.Toastify__toast-icon {
    width: 3vh !important;
    margin-inline-end: 1vh !important;

    > svg {
        width: 3vh !important;
        height: 3vh !important;
    }
}
</style>
