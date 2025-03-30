<script setup lang="js">

import {onMounted, ref} from "vue";
import GameEngine from "../game/Engine.js"

let gameInstance = ref(null);

onMounted(() => {
  gameInstance.value = new GameEngine("gameId");
  gameInstance.value.initialise();
})

</script>

<template>
  <div id="gameId"></div>
</template>

<style scoped>

</style>