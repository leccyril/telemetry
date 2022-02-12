import { createRouter, createWebHistory } from 'vue-router'
import ClientsConnected from '../views/ClientsConnected.vue'
import ClientData from '../views/ClientData.vue'
import ClientsDatas from '../views/ClientsDatas.vue'

const routes = [
  {
    path: '/',
    name: 'ClientsConnected',
    component: ClientsConnected,
  },
  {
    path: '/telemetry/:id',
    name: 'ClientData',
    props: true,
    component: ClientData,
  },
  {
    path: '/telemetries',
    name: 'ClientsDatas',
    props: true,
    component: ClientsDatas,
  },
 
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
})

export default router
