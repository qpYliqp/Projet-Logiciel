<script setup lang="ts">

import { ref } from 'vue'
import { defineProps } from 'vue'
import axios from 'axios'
import { json } from 'stream/consumers';

//declare a variable null in vue.js
var imgs = "";
 
 function test() { axios.get('http://localhost:4000/images')
  .then(response => {
    console.log(response.data)
    
    var imgs = JSON.stringify(response.data[0].name);
    for(let i= 0; i < response.data.length; i++){
      
    }
    document.getElementsByClassName("stp")[0].innerHTML = imgs;
  })
  .catch(error => {
   
  })
}



 







// const msg =


defineProps<{ msg: string }>()



  



const count = ref(0)
</script>


<template>
  <h1>{{ msg }}</h1>



  <div class="card">
    <h1 class="stp">{{ imgs }}</h1>
    <button type="button" @click="test()">count is {{ count }}</button>
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
  <select class="SelectionImages"></select>
  <p class="read-the-docs">Click on the Vite and Vue logos to learn more</p>
</template>

<style scoped>
.read-the-docs {
  color: #888;
}
</style>
