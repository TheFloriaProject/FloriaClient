<script lang="ts" setup>
import LogoComponent from "../../component/LogoComponent.vue";
import ButtonComponent from "../../component/ButtonComponent.vue";
import {go} from "../../util/functions";
import LinksComponent from "../../component/LinksComponent.vue";
</script>
<template>
    <div class="body">
        <LogoComponent/>
        <LinksComponent/>
        <div :class="$style.buttons">
            <ButtonComponent :on-click="() => go('singleplayer')" title="Singleplayer"/>
            <ButtonComponent :on-click="() => go('multiplayer')" title="Multiplayer"/>
            <ButtonComponent :on-click="() => go('options')" title="Options"/>
            <ButtonComponent :on-click="() => go('quit')" title="Quit"/>
        </div>
        <!--        <AccountComponent/>-->
    </div>
</template>
<style module>
.buttons {
    position: absolute;
    right: 2rem;
    display: flex;
    flex-direction: column;
    gap: 1vh;
    top: 50%;
    translate: 0 -50%;
}
</style>
