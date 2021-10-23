<template>
<!--
  文件上传

-->
<div  v-loading="data.loading">
      <el-upload
        class="upload-demo"
        multiple
        drag
        action="/file/upload.do"
        :headers="data.uploadHeaders"
        :before-upload="handlerUploadBeforeUpload"
        :on-preview="handlerUploadOnPreview"
        :on-remove="handlerUploadOnRemove"
        :on-change="handlerUploadOnChange"
        :before-remove="handlerUploadBeforeRemove"
        :limit="998"
        :on-exceed="handlerUploadOnExceed"
        :on-success="handlerUploadOnSuccess"
        :on-error="handlerUploadOnError"
        :data="{dir:data.dir}"
        :file-list="data.fileList">
        <el-button size="small" type="primary">点击/拖动上传</el-button>
        <div slot="tip" class="el-upload__tip">最多上传998个文件 每个不超过100MB</div>
      </el-upload>




</div>






</template>


<script>
  // import { getList } from '@/api/table'
  import { getToken,getUser } from '@/utils/cookie' // get token from cookie

  export default {
    data() {
      return {
        data: {
          loading: true
          , imgs: ""
          , list: []  //图片展示列表
          , vifDel: true
          , vifUpload: true
          , dir: ""     //上传目录
          , fileList: [] //上传列表
          , uploadHeaders: {"TOKEN" : getToken()}
        }

      }
    },
    props:['props'],//组件传参

    created() {
      var params = {}
      if(this.$route.query.table){ //传递 指定table
        params = this.$route.query
      }else if(this.props) {
        params = this.props
      }else{

      }
      this.data = this.assign(this.data, params)
//      debugger
      this.init()
    },

    methods: {
//    ---------------------
      init() {
//      debugger

        this.data.loading = true
        console.log("fileupload init " + this.data)
        var items = this.data.imgs.split(",")

        for(var i = 0; i < items.length; i++){
          var obj = items[i]
          if(obj.length > 0){
            this.data.list.push(obj)
          }
        }

        this.data.loading = false
      },
//      回调函数
       onChangeReturn(){
          var res = this.data.list.join(",")
          console.log("res : " + res)
          this.$emit('transfer',res)
       },
      //删除单行
      handlerDelete(ids) {
        this.data.loading = true
        const params = {'ids': ids}
        var _this = this
        this.get('/file/deleteByIds.do', params).then((res) => {
//        debugger
          for(let j = 0; j < _this.data.list.length; j++) {
            if(_this.data.list[j] == ids){
              _this.data.list.splice(j, 1);
            }
          }
          for(let j = 0; j < _this.data.fileList.length; j++) {
            if(_this.data.fileList[j] == ids){
              _this.data.fileList.splice(j, 1);
            }
          }
//          this.$elupload.upload.clearFiles(); //如何同步修改upload的list
          _this.data.loading = false
          this.onChangeReturn()

        }).catch(() => {
          _this.data.loading = false
        })
      },

//-----------------文件规则
      handlerUploadBeforeRemove(file, fileList) {
        console.log("handlerBeforeRemove ", file, fileList )
//        return this.$confirm(`确定移除 ${ file.name }？`);
      },
      handlerUploadOnRemove(file, fileList) {
        console.log("handlerOnRemove", file, fileList);
        var res = file.response
        var f = res.data
        var id = f.ID
        var name = f.NAME
//        var type = this.getFileType(name)
        this.handlerDelete(id)
      },


      handlerUploadOnPreview(file) {
        console.log("handlerOnPreview", file);
        var res = file.response
        var f = res.data
        var id = f.ID
        var name = f.NAME
//        var type = this.getFileType(name)
        this.downPath(id, {}, name)
      },
      handlerUploadOnExceed(files, fileList) {
        this.$message.warning(`当前限制选择10个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
      },

//      上传成功后预览
      handlerUploadOnSuccess(res, file, fileList) {
        console.log("handlerOnSuccess ", res, file, fileList )
        this.$message.success(`上传成功 ${file.name} `);

        var f = res.data
        var id = f.ID
        var name = f.NAME
        var type = this.getFileType(name)
        if(type == "image"){
          //this.$root.$filters.filterImg(id)
        }else{
        }
        this.data.list.push(id)
        this.onChangeReturn()
      },
      handlerUploadOnError(res, file, fileList) {
        console.log("handlerOnError ", res, file, fileList )
//        this.imageUrl = URL.createObjectURL(file.raw);
        this.$message.error(`上传失败 ${file.name} `);``
      },

//      上传前md5校验
      handlerUploadBeforeUpload(file) {
        console.log("handlerBeforeUpload ", file )

//        debugger
        // const isJPG = file.type === 'image/jpeg';
        // const isLt2M = file.size / 1024 / 1024 < 2;
        //
        // if (!isJPG) {
        //   this.$message.error('上传头像图片只能是 JPG 格式!');
        // }
        // if (!isLt2M) {
        //   this.$message.error('上传头像图片大小不能超过 2MB!');
        // }
        // return isJPG && isLt2M;
        return true
      },
      handlerUploadOnChange(file, fileList) {
        console.log("handlerOnChange ", file, fileList)
        this.data.fileList = fileList
        // this.fileList3 = fileList.slice(-3);
      },

//      -------------
    }
  }
</script>


