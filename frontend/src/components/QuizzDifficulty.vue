<script setup lang="ts">

// Au début, nous avions mis cette page à l'adresse /QuizzImage/0, mais il y avait un problème : quand nous choisissions un niveau, l'image ne s'affichait jamais.
</script>
<template>
    <div>
        <p>Quel niveau va tu choisir cher joueur ?</p>
        <button><router-link to="/QuizzImage/1">Facile</router-link></button>
        <button><router-link to="/QuizzImage/2">Moyen</router-link></button>
        <button><router-link to="/QuizzImage/3">Dur</router-link></button>
    </div>
</template>

<style></style>