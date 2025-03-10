<script setup lang="ts">
import { ref, watch } from 'vue'
import { api } from '../http-api';
import type { ImageType } from '../image';
import Image from './Image.vue';

const selectedId = ref<number | null>(null);
const imageList = ref<ImageType[]>([]);
const similarImages = ref<ImageType[]>([]);

// Récupère la liste des images disponibles au chargement
api.getImageList()
  .then((data) => {
    imageList.value = data;
  })
  .catch(e => {
    console.log(e.message);
  });

// Fonction pour aller chercher les images similaires
const fetchSimilarImages = async (id: number) => {
  similarImages.value = await api.getSimilarImages(id);
};

// Détecte quand une image est sélectionnée pour afficher l'image + les similaires
watch(selectedId, async (id) => {
  if (id !== null) {
    await fetchSimilarImages(id);
  } else {
    similarImages.value = [];
  }
});
</script>

<template>
  <div>
    <h3>Choose an image</h3>
    <div>
      <select v-model="selectedId">
        <option v-for="image in imageList" :value="image.id" :key="image.id">{{ image.name }}</option>
      </select>
    </div>

    <!-- Affiche l'image sélectionnée -->
    <div v-if="selectedId !== null" style="margin-top: 20px;">
      <h4>Selected Image</h4>
      <Image :id="selectedId" />
    </div>

    <!-- Affiche les images similaires -->
    <div v-if="similarImages.length > 0" style="margin-top: 20px;">
      <h4>Similar Images</h4>
      <div style="display: flex; flex-wrap: wrap; gap: 10px;">
        <Image
          v-for="image in similarImages"
          :key="image.id"
          :id="image.id"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
select {
  margin-bottom: 10px;
}

h4 {
  margin-bottom: 10px;
}

div {
  margin-bottom: 20px;
}
</style>
