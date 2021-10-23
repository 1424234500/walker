import Vue from 'vue'
import Vuex from 'vuex'
import getters from './getters'
import app from './modules/app'
import settings from './modules/settings'
import user from './modules/user'

Vue.use(Vuex)
const state = {

}


const store = new Vuex.Store({//放模块，state/mutations/actions,等
  modules: {
    app,
    settings,
    user
  },
  getters,
  state,
  mutations:{
    increment (value) {

    }
  }

})

export default store

// this.$store.commit('user/setDbAuto', this);
// var obj = this.$store.getters.getDbAuto
