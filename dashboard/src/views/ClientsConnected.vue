<template>

<h1 align="center"> Client(s) connected</h1>
 <div class="row items-start" > 
   <div class="col-6" v-for="client in clients" :key="client">
    <q-card class="my-card" >
      <q-card-section align="center"> 
       {{ client}}
       <router-link style="text-decoration: none; color: inherit;" :to="{ name: 'ClientData', params: { id: client}}" >
          <q-btn color="primary" label="Check telemetry" href="'/telemetry/'+{{client}}"/>
        </router-link>
        <q-btn color="deep-orange" label="Stop process" @click="closeClient(client)"/>
        
      </q-card-section>
    </q-card>
   </div>
 </div>
<div class="offset-5" id="no-client" v-if="clients.length===0">
        <h2 align="center" class="orange-text">SORRY NO CLIENT(S) CONNECTED </h2>
</div>
</template>

<script>
import api from '@/services/RestApiService'
import { useQuasar } from 'quasar'


export default {
  components: {
  },

  setup () {
    const $q = useQuasar()

    return {
      triggerPositive () {
        console.log('positive')
        $q.notify({
          position: 'top',
          timeout: 2500,
          type: 'positive',
          message: 'Client disconnected successfully'
        })
      },

      triggerNegative (print) {
        $q.notify({
          position: 'top',
          type: 'negative',
          message: print
        })
      },
}
   },
  
  data() {
    return {
      clients: [],
      interval: null,
    }
  },
  methods: {
    closeClient(client){
      api.post('/clients/close/'+client)
        .then(() => {
          this.triggerPositive()
        })
        .catch((error) => {
          console.log(error)
          this.triggerNegative('Error while closing client')
      
        })
    },
    getClientsConnected() {
      api.get('/clients/connected')
        .then((response) => {
          this.clients = response.data
        })
        .catch((error) => {
          console.log(error)
          this.triggerNegative('Server Connection Error, please contact administrator')
      
        })
    },
  },
  created() {
    this.getClientsConnected()
    this.interval = setInterval(() => {
      this.getClientsConnected()
    }, 5000)
  },
}
</script>

<style scoped>
h2.orange-text {
    color: orange!important;
  }
body{
  font-size:2em;
}
.my-card{
  font-size:2em;
  margin:20px;
}
</style>