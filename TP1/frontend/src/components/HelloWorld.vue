<script setup lang="ts">
import { ref, VueElement } from 'vue'
import { defineProps } from 'vue'
import
{
  ImageURL, ListImage, Option, ImageSRC,
  DefautSelect,ErrorMsg,GalerieButton,
  GalerieStyle, GalerieDisplay,InitImages
  ,SelectionImage,Upload
} from './http-api';


InitImages();
defineProps<{ msg: string }>()

const count = ref(0)

</script>
<template>

  <h1>{{ msg }}</h1>



  <div class="card">
    <h1 class="stp">{{ "BANKIMAGE.COM" }}</h1>
    <button type="button" @click="count++">count is {{ count }}</button>
    <div class="ErrorDiv">
      <p v-if="ErrorMsg.bool == true" style="color : red; font: bold">{{ ErrorMsg.text }}</p>
      <br><br>
    </div>
        <label for="FileInput"> Select Image
        <input type="file" id="FileInput"  @change="Upload($event)" placeholder="Post Image On Server">
        </label>
    <br><br>
    <div class="SelectionImageDiv">
      <button :class="GalerieStyle()" v-on:click="GalerieDisplay()">Galerie</button>
        <select v-model="Option" v-on:change="SelectionImage($event)" class="SelectButton">
          <option value="Defaut" v-bind:selected="DefautSelect">Defaut</option>
          <option v-for="image in ListImage " v-bind:value="image.id">{{ image.name }}</option>
        </select>
      <div v-if="GalerieButton.valueOf() == false">
        <img v-if="DefautSelect == false" v-bind:src="ImageSRC"  class="ImageStyle" style="width: 100%; height: auto;">
      </div>
      <div v-if="GalerieButton.valueOf() == true" class="GalerieDiv">
        <img v-for="image in ListImage" v-bind:src="ImageURL + '/' + image.id"  class="ImageStyle" style="width: 100%; height: auto;">
      </div>

    </div>
    <div class="ImageDiv"></div>
    <p>
      Edit
      <code>components/HelloWorld.vue</code> to test HMR
    </p>
  </div>

  <p>
    Check out
    <a href="https://vuejs.org/guide/quick-start.html#local" target="_blank"
      >create-vue</a
    >, the official Vue + Vite starter
  </p>
  <p>
    Install
    <a href="https://github.com/johnsoncodehk/volar" target="_blank">Volar</a>
    in your IDE for a better DX
  </p>
  
  <p class="read-the-docs">Click on the Vite and Vue logos to learn more</p>
</template>

<style scoped>

.ImageStyle {
  width: 100px;
  height: 100px;
}
.read-the-docs {
  color: #888;
}
</style>
