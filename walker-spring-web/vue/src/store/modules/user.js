// import { login, logout, getInfo } from '@/api/shiro'
import { getToken,setToken,getUser,setUser } from '@/utils/cookie' // get token from cookie
import { resetRouter } from '@/router'
import { get, post } from '@/utils/http'

//属性
const state = {   // //要设置的全局访问的state对象
  count: 1, //要设置的初始属性值

};
//get
const mutations = {

};
//other异步 方法?
const actions = {

}

export default {
  namespaced: true,
  state,
  mutations,
  actions,
}

