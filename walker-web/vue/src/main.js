import Vue from 'vue'

// import 'bootstrap/dist/css/bootstrap.min.css'
// import 'bootstrap/dist/js/bootstrap.min'


import 'normalize.css/normalize.css' // A modern alternative to CSS resets

import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import locale from 'element-ui/lib/locale/lang/en' // lang i18n

import '@/styles/index.scss' // global css


import App from './App'
import store from './store'
import router from './router'


// 全局过滤器问题 register global utility filters
//  参数1：过滤器名称 参数2：过滤器的逻辑
//2、如果想在methods中使用filters的方法,相应的就有两种方法
//使用全局的filter： Vue.filters['filterName'] (val)
//使用局部的filter: this.$options.filters['filterName'] (val)
import * as filters from './filters' // global filters
console.log(filters)
Object.keys(filters).forEach(key => {
  Vue.filter(key, filters[key])
})

// 注册为全局组件 供直接使用
import VueLazyload from 'vue-lazyload'
//Vue.use(VueLazyload)
//or with options
//lazy对于filter找不到问题 必须要在此配置所有src的过滤拦截器加工
//使用vue-cli脚手架快速生成的框架中，
//src**同级目录**中有static文件夹
//src**子文件夹**中有assets文件夹
//在使用vue-lazyload，设置error或loading属性的图片路径时，
//图片在assets文件夹，就需要使用require（）进行引入。
//Vue.use(lazyLoad, {
//  error: require('./assets/img/default-photo.png'),
//  loading: require('./assets/img/default-photo.png')
//})
//图片在static文件夹，就可以直接写路径了
//Vue.use(lazyLoad, {
//  error: require('../static/img/default-photo.png'),
//  loading: require('../static/img/default-photo.png')
//
//})
//vue-lazyload是在main.js文件中引入，不会被webpack进行编译，src中的文件会被webpack编译，包括assets，assets文件夹中的图片地址，会在编译过程中改变。因此vue-lazyload无法正确获得图片地址，就不能显示图片了。
var lazyconf = {
  preLoad: 1.3, // 预加载高度比例
  error: require('./assets/emoji.png'), // '/static/img/404.a57b6f31.png',
  loading: require('./assets/emojiblue.png'),
  attempt: 3,
  observer: true,
  lazyComponent: true,
  listenEvents: [ 'scroll' ],  // the default is ['scroll', 'wheel', 'mousewheel', 'resize', 'animationend', 'transitionend']
  observerOptions: {
    rootMargin: '0px',
    threshold: 0.1
  },
  filter: {
    srcFilter (el) {
      el.src = filters['filterImg'](el.src, 0)
    },
  },
}
//Object.keys(filters).forEach(key => {
//  lazyconf.filter[key] = filters[key]
//})

Vue.use(VueLazyload, lazyconf)





import mtable from './views/db/mtable'
Vue.component('mtable', mtable)

import varea from './views/user/varea'
Vue.component('varea', varea)
import areatree from './views/user/areatree'
Vue.component('areatree', areatree)

import dept from './views/user/dept'
Vue.component('dept', dept)
import depttree from './views/user/depttree'
Vue.component('depttree', depttree)
import role from './views/user/role'
Vue.component('role', role)
import roleuser from './views/user/roleuser'
Vue.component('roleuser', roleuser)
import user from './views/user/user'
Vue.component('user', user)

import controller from './views/echarts/controller'
Vue.component('controller', controller)
import socket from './views/echarts/socket'
Vue.component('socket', socket)

import ablum from './views/template/ablum'
Vue.component('ablum', ablum)

import fileupload from './views/template/fileupload'
Vue.component('fileupload', fileupload)


import '@/icons' // icon
import '@/permission' // permission control


//将方法挂载到Vue原型上 vue上下文 全局 this.get
import { get, post, put, delet, down, downPath, downId, previewImg } from '@/utils/http'
Vue.prototype.get = get
Vue.prototype.post = post
Vue.prototype.delet = delet
Vue.prototype.put = put
Vue.prototype.down = down
Vue.prototype.downPath = downPath
Vue.prototype.downId = downId
Vue.prototype.previewImg = previewImg


//批量挂载 工具类方法
//import { assign, clone} from '@/utils/index'
//Vue.prototype.assign = assign
//Vue.prototype.clone = clone
import * as utils from '@/utils/index'
Object.keys(utils).forEach(key => {
  Vue.prototype[key] = utils[key];
})



/**
 * If you don't want to use mock-server
 * you want to use MockJs for mock api
 * you can execute: mockXHR()
 *
 * Currently MockJs will be used in the production environment,
 * please remove it before going online! ! !
 */
// import { mockXHR } from '../mock'
// if (process.env.NODE_ENV === 'production') {
//   mockXHR()
// }

// set ElementUI lang to EN
Vue.use(ElementUI, { locale })






Vue.config.productionTip = false


new Vue({
  el: '#app',
  router,
  store,
  // components : components,
  render: h => h(App)
})

//debugger
