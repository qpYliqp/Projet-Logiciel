<script setup lang="ts">



import { ref, VueElement } from 'vue'

import { defineProps } from 'vue'
import axios from 'axios'
import { parserOptions } from '@vue/compiler-dom';


//ref permet de garder les données en mémoire asychrones
const ListImage = ref([{id : -1, name : "defaut"}]);
const Option = ref("Defaut");
const ImageSRC = ref("");
const Galerie = ref(false);
const DefautSelect = ref(true);

//export default axios




let ImageURL = 'http://localhost:4000/images';

 function InitImages()
 {
  axios.get(ImageURL)
  .then(response =>
   {
      ListImage.value = response.data;
      console.log(ListImage);
  })
  .catch(error => 
  {
    console.log(error);
  })
}

InitImages();

function SelectionImage(event : any)
{
  if(event.target.value == "Defaut")
  {
    DefautSelect.value = true;
  }
  else
  {
    DefautSelect.value = false;
  }
  SelectImage(event.target.value);
}

function SelectImage(index : any)
{
  ImageSRC.value = ImageURL +"/" + index;
 
  
}


function Upload(event :any)
{
  var file = event.target.files[0];
  event.target.name = "file";
  let formData = new FormData();
  formData.append("file", file);

  axios.post( ImageURL,
  formData,
  {
    headers: {
        'Content-Type': 'multipart/form-data'
    }
  }
).then(function(){
  InitImages();
  console.log('SUCCESS!!');
})
.catch(function(){
  console.log('FAILURE!!');
  var t =  document.createElement("p");
  t.style.color = "red";
  t.innerHTML = "Error, Only JPEG are allowed";
  document.getElementsByClassName("ErrorDiv")[0].appendChild(t);
});
}



defineProps<{ msg: string }>()


function GalerieDisplay()
{
  if(Galerie.value == true)
  {
    Galerie.value = false;
  }
  else
  {
    Galerie.value = true;
    Option.value = "Defaut";
    DefautSelect.value = true;
  }
}




const count = ref(0)


//v-on:change="Upload($event)"

</script>


<template>

  <h1>{{ msg }}</h1>



  <div class="card">
    <h1 class="stp">{{ "BANKIMAGE.COM" }}</h1>
    <button type="button" @click="count++">count is {{ count }}</button>
    <div class="ErrorDiv"></div>
    <div class="SelectionImageDiv">
      <button v-on:click="GalerieDisplay()">Galerie</button>
        <input type="file" class="FileInput"  @change="Upload($event)" placeholder="Post Image On Server">
        <select v-model="Option" v-on:change="SelectionImage($event)">
          <option value="Defaut" v-bind:selected="DefautSelect">Defaut</option>
          <option v-for="image in ListImage " v-bind:value="image.id">{{ image.name }}</option>
        </select>
      <div v-if="Galerie == false">
        <img v-if="DefautSelect == false" v-bind:src="ImageSRC" alt="Image" class="ImageStyle">
      </div>
      <div v-if="Galerie == true" class="GalerieDiv">
        <img v-for="image in ListImage" v-bind:src="ImageURL + '/' + image.id" alt="Image" class="ImageStyle">
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
