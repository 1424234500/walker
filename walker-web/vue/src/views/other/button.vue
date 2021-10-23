<template>
  <div class="app-container" >
<a href='https://cloud.tencent.com/developer/section/1489867'> https://cloud.tencent.com/developer/section/1489867</a></a>
<br>
    <el-row>
      <el-button>默认按钮</el-button>
      <el-button type="primary">primary按钮</el-button>
      <el-button type="success">success按钮</el-button>
      <el-button type="info">info按钮</el-button>
      <el-button type="warning">warning按钮</el-button>
      <el-button type="danger">danger按钮</el-button>
    </el-row>


    <el-row>
      <el-button plain>朴素按钮</el-button>
      <el-button type="primary" plain>主要按钮</el-button>
      <el-button type="success" plain>成功按钮</el-button>
      <el-button type="info" plain>信息按钮</el-button>
      <el-button type="warning" plain>警告按钮</el-button>
      <el-button type="danger" plain>危险按钮</el-button>
    </el-row>

    <el-row>
      <el-button round>圆角按钮</el-button>
      <el-button type="primary" round>主要按钮</el-button>
      <el-button type="success" round>成功按钮</el-button>
      <el-button type="info" round>信息按钮</el-button>
      <el-button type="warning" round>警告按钮</el-button>
      <el-button type="danger" round>危险按钮</el-button>
    </el-row>

    <el-row>
      <el-button icon="el-icon-search" circle></el-button>
      <el-button type="primary" icon="el-icon-edit" circle></el-button>
      <el-button type="success" icon="el-icon-check" circle></el-button>
      <el-button type="info" icon="el-icon-message" circle></el-button>
      <el-button type="warning" icon="el-icon-star-off" circle></el-button>
      <el-button type="danger" icon="el-icon-delete" circle></el-button>
    </el-row>


    <el-row>
      <el-button disabled>默认按钮</el-button>
      <el-button type="primary" disabled>主要按钮</el-button>
      <el-button type="success" disabled>成功按钮</el-button>
      <el-button type="info" disabled>信息按钮</el-button>
      <el-button type="warning" disabled>警告按钮</el-button>
      <el-button type="danger" disabled>危险按钮</el-button>
    </el-row>

    <el-row>
      <el-button plain disabled>朴素按钮</el-button>
      <el-button type="primary" plain disabled>主要按钮</el-button>
      <el-button type="success" plain disabled>成功按钮</el-button>
      <el-button type="info" plain disabled>信息按钮</el-button>
      <el-button type="warning" plain disabled>警告按钮</el-button>
      <el-button type="danger" plain disabled>危险按钮</el-button>
    </el-row>


    <el-button type="primary" icon="el-icon-edit"></el-button>
    <el-button type="primary" icon="el-icon-share"></el-button>
    <el-button type="primary" icon="el-icon-delete"></el-button>
    <el-button type="primary" icon="el-icon-search">搜索</el-button>
    <el-button type="primary">上传<i class="el-icon-upload el-icon--right"></i></el-button>


    <el-button-group>
      <el-button type="primary" icon="el-icon-arrow-left">上一页</el-button>
      <el-button type="primary">下一页<i class="el-icon-arrow-right el-icon--right"></i></el-button>
    </el-button-group>
    <el-button-group>
      <el-button type="primary" icon="el-icon-edit"></el-button>
      <el-button type="primary" icon="el-icon-share"></el-button>
      <el-button type="primary" icon="el-icon-delete"></el-button>
    </el-button-group>


    <el-button type="primary" :loading="true">加载中</el-button>

    <el-row>
      <el-button>默认按钮</el-button>
      <el-button size="medium">中等按钮</el-button>
      <el-button size="small">小型按钮</el-button>
      <el-button size="mini">超小按钮</el-button>
    </el-row>
    <el-row>
      <el-button round>默认按钮</el-button>
      <el-button size="medium" round>中等按钮</el-button>
      <el-button size="small" round>小型按钮</el-button>
      <el-button size="mini" round>超小按钮</el-button>
    </el-row>




    <div>
      <span>图片相关 lazy插件</span>
      <el-row>
        <span>vlazy-url</span>
        <span  v-for="item in urls">
          <div>{{item}}</div>
          <div><img style="width:200px;" v-lazy="item" ></div>
        </span>
        <span>
          <div>src-404: @/assets/404_images/404.png, {{imgAssert}}, ../../assets/emoji.png </div>
          <div>
          <!-- 引入资源的方式static文件夹可以使用~/static/方式引入, assets文件夹可以使用 ~@/assets 方式引入 -->
            <img style="width:200px;" src="@/assets/404_images/404.png" >
            <img style="width:200px;" src="@/assets/emoji.png" >
            <img style="width:200px;" src="@/assets/emojiblue.png" >
            <img style="width:200px;" src="@/assets/icons.png" >
            <img style="width:200px;" :src="imgAssert" >
            <img style="width:200px;" :src="imgStatic" >
          </div>
        </span>
      </el-row>
<p> url过滤器 本地图片文件加载 img src </p>
      <el-row>
      <template >
        <label>filter:</label> {{ name | orderNoFilter }}
      </template>
      <template >
        <div>
          <label>{{imgs | filterImg(0) }}</label>
          <img class="image-icon" :src="imgs | filterImg(0)  " >
        </div>
        <div>
           <label>imgsfilter-v-lazy: {{imgs | filterImg(2) }}</label>
            img class="image-icon" v-lazy="imgs | filterImg(2)  "
        </div>
      </template>
      </el-row>

<p> url过滤器 本地图片文件 lazy 加载 img v-lazy </p>


    </div>
    <div>
      <p> 文件上传组件</p>
      <p>res: {{imgs}} </p>
    <ablum :props='ablum' @transfer="onGetImgs"></ablum>

    </div>




  </div>
</template>

<script>
  export default {
    data() {
      return {

       fits: ['fill', 'contain', 'cover', 'none', 'scale-down']
       ,url: 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg'
       ,src: 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'
       ,urls: [
         'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg'
       , 'https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48jpeg.jpeg'
       , "@/assets/404_images/404.png"
       , 'err']
       , imgs: 'ef3c802bcdcee2b0af1cb1b4c1078257'

        ,name: 'name',
        region: '',
        date1: '',
        date2: '',
        delivery: false,
        type: [],
        resource: '',
        desc: ''
        , ablum: {
          imgs: "https://img.alicdn.com/imgextra/https://img.alicdn.com/imgextra/i2/2355352178/O1CN01OyQ1EM1RxZD2wKyYC_!!2355352178.jpg_430x430q90.jpg,7f31c886301044ed2021f54043a6a0c7"
        },
        imgAssert:require('../../assets/emoji.png'),
        imgStatic:"",
//                  <!-- 引入资源的方式static文件夹可以使用~/static/方式引入, assets文件夹可以使用 ~@/assets 方式引入 -->

      }
    },
    methods: {
      onSubmit() {
        this.$message('submit!')
      },
      onCancel() {
        this.$message({
          message: 'cancel!',
          type: 'warning'
        })
      }
      , onGetImgs(imgs){
        this.imgs = imgs
        console.log("i get the ablum res " + this.imgs)
      }
    }
  }
</script>

<style scoped>
  .line{
    text-align: center;
  }
</style>

