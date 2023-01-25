import { computed, ref, VueElement } from 'vue'
import { defineComponent } from "vue";
import Vue from 'vue'
import axios from 'axios'
/*
J'ai essayé de faire la défintions des variables avec "export defaults"
mais je n'arrivais pas à accéder aux fonctions et computed: 
(du moins, les valeurs renvoyées n'étaient pas les bonnes)
C'est pour ça qu'en attendant je définis tout de manière impérative et gloables les variables

*/
export let ImageURL = 'http://localhost:4000/images';
export const ListImage = ref([{id : -1, name : "defaut"}]);
export const Option = ref("Defaut");
export const ImageSRC = ref("");
export const DefautSelect = ref(true);
export const ErrorMsg = ref({text : "" , bool : false})
export const GalerieButton = ref(false);

export function GalerieStyle() {
    if(GalerieButton.value == true) {
        return "Galerie-isActive"
    } else {
        return "Galerie-isInactive"
    }
}

  export function InitImages()
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
export function SelectImage(index : any)
{
  ImageSRC.value = ImageURL +"/" + index;
}

 export function SelectionImage(event : any)
 {
   ErrorMsg.value = {text : "" , bool : false}
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

 export function Upload(event :any)
 {
   ErrorMsg.value = {text : "" , bool : false}
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
 })
 .catch(function(){
   console.log('FAILURE!!');
   ErrorMsg.value = {text : "Only JPEG are allowed" , bool : true}
 });
 }
 
 export function GalerieDisplay()
 {
   ErrorMsg.value = {text : "" , bool : false}
   if(GalerieButton.value == true)
   {
     GalerieButton.value = false;
   }
   else
   {
     GalerieButton.value = true;
     Option.value = "Defaut";
     DefautSelect.value = true;
   }
 }
 
