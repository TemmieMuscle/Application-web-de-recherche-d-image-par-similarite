<script setup lang="ts">
import { ref } from 'vue';
import { api } from '../http-api';
import router from '../router';
import type { ImageType } from '../image';

const selectedId = ref(-1);
const imageList = ref<ImageType[]>([]);
getImageList();

function getImageList() {
  api.getImageList().then((data) => {
    imageList.value = data;
  }).catch(e => {
    console.log(e.message);
  });
}

function supprFile() {
  api.deleteImage(selectedId.value);
}


</script>

<template>
  <div>
    <h3>Choose an image</h3>
    <div>
      <select v-model="selectedId">
        <option v-for="image in imageList" :value="image.id" :key="image.id">{{ image.name }}</option>
      </select>
      <div>
        <button @click="supprFile">Submit</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
</style>
