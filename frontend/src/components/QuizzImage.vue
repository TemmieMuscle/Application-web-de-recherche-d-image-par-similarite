<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { api } from '../http-api';
import type { ImageType } from '../image';
import Image from './Image.vue';

const imageList = ref<ImageType[]>([]);
const randomImageId = ref<number | null>(null);

const fetchImageList = async () => {
  try {
    imageList.value = await api.getImageList();
    if (imageList.value.length > 0) {
      const randomIndex = Math.floor(Math.random() * (imageList.value.length));
      randomImageId.value = imageList.value[randomIndex].id;
      console.log(imageList.value[randomIndex].id);

    }
  } catch (e) {
    console.error('Error fetching image list:', e);
  }
};

onMounted(fetchImageList);
</script>

<template>
  <div>
    <h3>Random Image</h3>
    <div v-if="randomImageId !== null">
      <Image :id="randomImageId" />
    </div>
    <div v-else>
      <p>Loading image...</p>
    </div>
  </div>
</template>