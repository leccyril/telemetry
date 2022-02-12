<template>

<h1 align="center"> Telemetry for client {{this.id}}</h1>


  <div class="q-pa-md">
    <q-table
      class="my-sticky-header-column-table"
      :rows="datas"
      row-key="uuid"
      visible-columns="uuid,cpuUsage,memoryUsage,lastUpdate"
      @row-click="onRowClick"
    />
  </div>
  <transition name="fade">

  <div class="q-pa-md row items-start q-gutter-md" v-if="this.telemetry">
      <q-card flat bordered class="my-card col-8 offset-2">
        <q-card-section>
          <div class="text-h6">{{this.telemetry.uuid}}</div>
        </q-card-section>
        <q-separator inset />

        <q-card-section>
          <p v-for="item in this.telemetry.processes" :key="item">
          {{ item }}
        </p>
        </q-card-section>
      </q-card>
  </div>
</transition>

</template>

<script>
import api from '@/services/RestApiService'

export default {
  
  props :['id'],
  data() {
    return {
      datas: [],
      interval: null,
      telemetry: null,
    }
  },

  methods: {
     onRowClick(evt, row){
      console.log(row)
      this.telemetry=row
      this.isOpen = !this.isOpen
      console.log(this.isOpen)
    },
    getClientDatas(id) {
      api.get('/clients/'+id)
        .then((response) => {
          this.datas.push(response.data)
        })
        .catch((error) => {
          console.log(error)
      
        })
    },
  },
  beforeUnmount: () => {
    //clearInterval(this.interval)
  },
  created() {
    //this.$store.commit('setError', '')
    //this.$store.commit('setMessage', '')
    this.getClientDatas(this.id)
    this.interval = setInterval(() => {
      this.getClientDatas(this.id)
    }, 20000)
  },
}
</script>

<style lang="sass">
  td:first-child
    /* bg color is important for td; just specify one */
    background-color: #c1f4cd !important

  tr th
    position: sticky
    /* higher than z-index for td below */
    z-index: 2
    /* bg color is important; just specify one */
    background: #fff

  /* this will be the loading indicator */
  thead tr:last-child th
    /* height of all previous header rows */
    top: 48px
    /* highest z-index */
    z-index: 3
  thead tr:first-child th
    top: 0
    z-index: 1
  tr:first-child th:first-child
    /* highest z-index */
    z-index: 3

  td:first-child
    z-index: 1

  td:first-child, th:first-child
    position: sticky
    left: 0
</style>
