<script setup lang="ts">
import { ref } from 'vue';
import { api } from '../http-api';
import router from '../router';
import type { ImageType } from '../image';

const selectedId = ref(-1);
const selectedDifficulty = ref(-1);
const imageList = ref<ImageType[]>([]);
getImageList();

function getImageList() {
  api.getImageList().then((data) => {
    imageList.value = data;
  }).catch(e => {
    console.log(e.message);
  });
}

function showImage() {
  router.push({ name: 'image', params: { id: selectedId.value } })
}

function showQuizz() {
  router.push({ name: 'QuizzImage', params: { id: selectedDifficulty.value } })
}
</script>

<template>
  <div>
    <h1>Choose an image</h1>
    <div>
      <select v-model="selectedId" @change="showImage">
        <option v-for="image in imageList" :value="image.id" :key="image.id">{{ image.name }}</option>
      </select>
    </div>
    <div>
      <h1>Choose a difficulty</h1>
      <select v-model="selectedDifficulty" @change="showQuizz">
        <option :value="1">facile</option>
        <option :value="2">pas facile</option>
        <option :value="3">dur dur dur</option>
      </select>
    </div>
  </div>
</template>

<style scoped></style>
