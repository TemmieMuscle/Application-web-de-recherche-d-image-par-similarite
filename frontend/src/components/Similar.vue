<script setup lang="ts">
import { ref, watch } from 'vue'
import { api } from '../http-api';
import type { ImageType } from '../image';
import Image from './Image.vue';

const selectedId = ref<number | null>(null);
const imageList = ref<ImageType[]>([]);
const similarImages = ref<ImageType[]>([]);

const fetchImageList = async () => {
  try {
    imageList.value = await api.getImageList();
  } catch (e) {
    console.error('Error fetching image list:', e);
  }
};

const fetchSimilarImages = async (id: number) => {
  try {
    similarImages.value = await api.getSimilarImages(id);
  } catch (e) {
    console.error('Error fetching similar images:', e);
    similarImages.value = [];
  }
};

watch(selectedId, async (id) => {
  if (id !== null) {
    await fetchSimilarImages(id);
  } else {
    similarImages.value = [];
  }
});

fetchImageList();
</script>

<template>
  <div>
    <h3>Choose an image</h3>
    <div>
      <select v-model="selectedId">
        <option v-for="image in imageList" :value="image.id" :key="image.id">{{ image.name }}</option>
      </select>
    </div>
    <div v-if="selectedId !== null">
      <h4>Selected Image</h4>
      <Image :id="selectedId" />
    </div>
    <div v-if="similarImages.length > 0">
      <h4>Similar Images</h4>
      <div>
        <Image v-for="image in similarImages" :key="image.id" :id="image.id" />
      </div>
    </div>
  </div>
</template>

<style>
img {
  width: 300px;
  height: auto;
}
</style>