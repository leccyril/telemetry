<template>
  <q-layout view="lHh Lpr lFf">
    <Menu />
     <q-page-container>
      <router-view/>
    </q-page-container>
  </q-layout>
</template>

<script>
import { ref } from 'vue'
import Menu from '@/views/Menu.vue'
export default {
  name: 'LayoutDefault',

  components: {
    Menu
  },

  setup() {
    return {
      leftDrawerOpen: ref(false),
    }
  },
}
</script>

<style scoped>
.color {
  background-color:#46CEAD;
}
</style>
