<template>
  <div id="app">
    <!--    缓存操作记录页面-->
<!--    <keep-alive>-->
<!--      <router-view v-if="$route.meta.keepAlive"></router-view>-->
<!--    </keep-alive>-->
<!--    <router-view v-if="!$route.meta.keepAlive"></router-view>-->
<!--    include="home"-->



<!--    <keep-alive>-->
    <router-view></router-view>
<!--    </keep-alive>-->


  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>
