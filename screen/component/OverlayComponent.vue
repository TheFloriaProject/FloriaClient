<template>
  <div :class="$style.overlay"></div>
</template>
<style module>
.overlay {
  position: absolute;
  width: 100vw;
  height: 100vh;
  background-color: #00000088;
  z-index: 2;
}
</style>