<script setup lang="ts">

import { ref } from 'vue'
import { defineProps } from 'vue'
import axios from 'axios'

//declare a variable null in vue.js

let ImageURL = 'http://localhost:4000/images';
let selection = document.createElement("select");
let galerie = document.createElement("button");
let defaut = new Option("Defaut", "defaut");
    defaut.setAttribute("selected", "selected");
    selection.add(defaut);
 
 function SelectCreate() { axios.get(ImageURL)
  .then(response => {
    
    galerie.setAttribute("type", "button");
    galerie.innerHTML = "Galerie";
    galerie.value = "0";
    
    

    
    document.getElementsByClassName("SelectionImageDiv")[0].appendChild(selection);
    document.getElementsByClassName("SelectionImageDiv")[0].appendChild(galerie);
    
  
    for(let i= 0; i <= response.data.length; i++)
    {
      let opt = new Option(response.data[i].name, response.data[i].id);
      selection.add(opt);
    }
  })
  .catch(error => {
   
  })
}

selection.onchange = function changer(event){
  SelectionImage(event);
};

function ClickedButtonStyle(button : any){
  if(button.value == "1")
  {
    button.style.color = "green";
  }
  else if(button.value == "0")
  {
    button.style.color = "black";
  }

}

galerie.onclick = function clicked()
{
  GalerieClicked();

}
  function GalerieClicked()
  {

  if(galerie.value == "0")
  {
    galerie.value = "1";
    if(defaut.selected == false)
    {
      defaut.selected = true;
    }
    ClickedButtonStyle(galerie);
    DisplayGalerie();
  }
  else if(galerie.value == "1")
  {
    galerie.value = "0";
    ClickedButtonStyle(galerie);
    RemoveGalerie();
  }
  
};


function SelectionImage(event : any)
{
  if(galerie.value == "1")
  {
      GalerieClicked(); 
  }

  if(event.target.value != "defaut")
  {
    let box = document.getElementsByClassName("ImageDiv")[0];
    if(box.getElementsByClassName("ImageStyle").length == 0)
    {
      box.appendChild(SelectImage(event.target.value));
    }
    else
    {
     box.getElementsByClassName("ImageStyle")[0].setAttribute("src", ImageURL +"/" + event.target.value);
    }
  }
  else
  {
    let box = document.getElementsByClassName("ImageDiv")[0];
    if(box.getElementsByClassName("ImageStyle").length > 0)
    {
      box.getElementsByClassName("ImageStyle")[0].remove();
    }
  
  }
}

function SelectImage(index : any)
{
  
  let img = document.createElement("img");
  img.setAttribute("src", ImageURL +"/" + index);
  img.className = "ImageStyle";
  return img;
  
}

function DisplayGalerie()
{
     let box = document.getElementsByClassName("ImageDiv")[0];
    if(box.getElementsByClassName("ImageStyle").length > 0)
    {
      box.getElementsByClassName("ImageStyle")[0].remove();
    }

    axios.get(ImageURL).then(response => {
      for(let i = 0; i < response.data.length; i++)
      {
        console.log(response.data[i].id);
        box.appendChild(SelectImage(response.data[i].id));
      }
    })
}

function RemoveGalerie()
{
  
  let box = document.getElementsByClassName("ImageDiv")[0];
  if(box.getElementsByClassName("ImageStyle").length > 0)
  {
    console.log(box.getElementsByClassName("ImageStyle").length);
    for(let i = 0; i <= box.getElementsByClassName("ImageStyle").length; i++)
    {
      console.log("id : "+box.getElementsByClassName("ImageStyle")[0]);
      box.getElementsByClassName("ImageStyle")[0].remove();
    }
  }
}




defineProps<{ msg: string }>()
SelectCreate();


const count = ref(0)
</script>


<template>
  <h1>{{ msg }}</h1>



  <div class="card">
    <h1 class="stp">{{ "BANKIMAGE.COM" }}</h1>
    <button type="button" @click="count++">count is {{ count }}</button>
    <div class="SelectionImageDiv"></div>
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
