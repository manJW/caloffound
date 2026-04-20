import Vue from 'vue'
import Vuex from 'vuex'
import foundation from './modules/foundation'

Vue.use(Vuex)

export default new Vuex.Store({
  modules: {
    foundation
  }
})
