<script setup lang="ts">
import { ref } from 'vue'
import { api } from '../http-api';
import type { ImageType } from '../image';
import Image from './Image.vue';

const imageList = ref<ImageType[]>([]);

api.getImageList()
  .then((data) => {
    imageList.value = data;
  })
  .catch(e => {
    console.log(e.message);
  });
</script>

<template>
  <div>
    <h3>Gallery</h3>
    <div class="images-gallery">
      <Image v-for="image in imageList" :id="image.id" />
    </div>
  </div>
</template>

<style scoped></style>

<style>
.images-gallery {
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

img {
  width: 300px;
  height: auto;
}
</style>