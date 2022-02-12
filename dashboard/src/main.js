import { createApp } from 'vue'
import App from './App.vue'
//used to designed application
import { Quasar, Notify } from 'quasar'
//used to route between page
import router from './router'
import quasarUserOptions from './quasar-user-options'

createApp(App)
.use(router)
.use(Quasar, {plugins: {Notify},},quasarUserOptions)
.mount('#app')
