<script setup lang="ts">

const win_cond = defineProps<{ id: number }>()


</script>

<template>
  <div>
    <p v-if="win_cond.id === 0">
    <h1 class="win">Victoire</h1>
    </p>
    <p v-else-if="win_cond.id === 1">
    <h1 class="loose">Défaite</h1>
    </p>
  </div>
  <div v-if="win_cond.id === 0">
    <p>Tu peux maintenant accéder au jeux avec le bonus de ton choix :</p>
    <button><router-link to="/ImageQuest">Bonus 1</router-link></button>
    <button><router-link to="/ImageQuest">Bonus 2</router-link></button>
    <button><router-link to="/ImageQuest">Bonus 3</router-link></button>
    <p>Ou tu peux rejouer au jeux :</p>
  </div>
  <div v-else-if="win_cond.id === 1">
    <p>Tu peux maintenant accéder au jeux sans bonus &#128542; :</p>
    <button><router-link to="/ImageQuest">Aller au jeu</router-link></button>
    <p>Ou tu peux retenter ta chance pour un bonus :</p>
  </div>
  <div>
    <button><router-link to="/QuizzImage/1">Quizz niveau facile</router-link></button>
    <button><router-link to="/QuizzImage/2">Quizz niveau moyen</router-link></button>
    <button><router-link to="/QuizzImage/3">Quizz niveau dur</router-link></button>
  </div>
</template>

<style>
.win {
  color: green;
  font-weight: bold;
}

.loose {
  color: red;
  font-weight: bold;
}
</style>