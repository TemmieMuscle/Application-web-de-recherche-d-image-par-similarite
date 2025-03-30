<script setup lang="js">

import {onMounted} from "vue";
import GameEngine from "../game/Engine.js"

let gameInstance = null;

onMounted(() => {
  gameInstance= new GameEngine("gameId");
  gameInstance.initialise();
})

</script>

<template>
  <div id="gameId"></div>
</template>

<style scoped>

</style>