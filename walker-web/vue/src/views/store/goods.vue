<template>
<div class="app-container" >

  <!--搜索-->
  <div class="div-box-down" v-loading="loadingCols" >
    <form class="form-inline" >
      <div class="form-group" v-for="(value, key) in colMap">
        <label>{{value=='' ? key : value}}</label>
        <input
          type="text"
          class="form-control"
          style="width: 10em; margin-right: 1em;"
          v-on:keyup.13="getListPage()"
          :placeholder="key"
          v-model="rowSearch[key]"
        />
      </div>
      <el-button-group>
        <el-button  class="btn btn-primary" @click="getListPage()" >查询</el-button>
        <el-button  class="btn btn-success" @click="handlerAddColumn()" >添加</el-button>
        <el-button  class="btn btn-danger" @click="clearRowSearch();getListPage();" >清除</el-button>
      </el-button-group>
    </form>
  </div>

  <!--  定制模拟展示 带附件图片 文件
    img class="image-icon" :src="item.IMGS | filterImg(0) "
    img class="image-large" :src="item.IMGS | filterImg(1) "

   -->
  <div v-show="!loadingCols" class="div-all">
    <div class="div-all">
        <div class="div-item" v-for="(item, i) in list"   @click.stop="handlerChange(item) " >
            <img class="image-large " :alt="item.IMGS0" :key="item.IMGS0" v-lazy="item.IMGS0" >

            <div class="div-item-sub">
                <div class="sub-left">
                  <div>
                    <img class="image-icon margin2px " :alt="item.IMGS1" :key="item.IMGS1" v-lazy="item.IMGS1 " >
                  </div>
                </div>
                <div class="sub-middle">
                    <div>{{item.PRICE | money }} </div>
                </div>
                <div class="sub-right">
                    <div>{{item.NAME | substr(10) }}</div>
                </div>
            </div>
        </div>
    </div>
  </div>


<el-dialog
  title="修改"
  :visible.sync="loadingUpdate"
  v-if="loadingUpdate"
  width="86%"
  :before-close="handlerCancel"
>
  <template>
    <el-form
      v-loading="loadingSave"
      ref="form"
      :model="rowUpdate"
      label-width="100px"
    >
      <el-form-item
        v-for="(value, key) in colMap"
        :key="key"
        :property="key"
        :label="(value=='' ? key : value)"
      >
        <el-input v-model="rowUpdate[key]" type="textarea" />
      </el-form-item>

      <el-form-item
      label="图片管理"
      >
        <ablum :props='ablum' @transfer="onGetImgs"></ablum>
      </el-form-item>

      <el-form-item>
        <el-button type="primary" @click="handlerSave()">确定</el-button>
        <el-button type="danger" @click="handlerCancel()">取消</el-button>
      </el-form-item>
    </el-form>
  </template>
</el-dialog>

</div>
</template>


<script>
  // import { getList } from '@/api/table'
import Vue from 'vue'

  export default {
//    filters: {
//      statusFilter(status) {
//        const statusMap = {
//          published: 'success',
//          draft: 'gray',
//          deleted: 'danger'
//        }
//        return statusMap[status]
//      }
//    },
    data() {
      return {
        table: 'S_GOODS',
        database: '',
        list: [],
        colMap: {},      //列名:别名
        colKey: "",     //主键名
        rowSearch: {},   //搜索 列明:搜索值
        rowUpdate: {},   //更新界面复制 列名:新值
        rowUpdateFrom: {},//更新界面源对象 列名:旧值
        rowSelect: [],   //选中行
        nowRow: null,     //当前行
        page: {
          nowpage: 1,
          num: 0,
          order: "",
          pagenum: 0,
          shownum: 8,
        },
        loadingList: true,
        visibleList: false,
        loadingCols: true,
        loadingSave: true,
        loadingUpdate: false,
        ablum : null,
      }
    },
    created() {
      this.getColumns()
    },

    methods: {
      //查询展示的行列信息 备注
      getColumns() {
        this.loadingCols = true
        this.get('/common/getColsMap.do', {tableName: this.table}).then((res) => {
          this.colMap = res.data.colMap
          this.page.order = res.data.colMap['S_MTIME'] ? 'S_MTIME DESC' : ''
          this.colKey = res.data.colKey
          this.clearRowSearch()
          this.loadingCols = false
          this. getListPage()
        }).catch(() => {
          this.loadingCols = false
        })

      },
      onGetImgs(imgs){
        this.rowUpdate.IMGS = imgs
        console.log("i get the ablum res " + imgs)
      },
      //清空搜索条件
      clearRowSearch(){
        this.rowSearch = {} //clear map
        this.page.nowpage = 1
      },
      //分页查询
      getListPage() {
        this.loadingList = true
        var obj = this.assign({nowPage: this.page.nowpage, showNum: this.page.shownum, order: this.page.order}, this.rowSearch)
        var params = this.assign({"_TABLE_NAME_": this.table, "_DATABASE_": this.database}, obj)
        var _self = this
        this.get('/common/findPage.do', params).then((res) => {
          _self.list = res.data.data
//          debugger
          for(let j = 0; j < _self.list.length; j++) {
              var imgs = _self.list[j]['IMGS']
              _self.list[j]['IMGS0'] = Vue.filter('filterImg')(imgs, 0, '400x225')
              _self.list[j]['IMGS1'] = Vue.filter('filterImg')(imgs, 1, '100x100')
          }
          _self.page = res.data.page
          _self.info = res.info
          _self.loadingList = false
        }).catch(() => {
          _self.loadingList = false
        })

      },
      //添加行
      handlerAddColumn(){
        var newObj = this.assign(this.nowRow?this.nowRow:{}, this.rowSearch)
        newObj[this.colKey] = ''
        newObj["S_FLAG"] = '1'
        newObj["IMGS"] = ''
        this.list.push(newObj)
        this.handlerChange(this.list[this.list.length - 1])
      },
      //修改单行 展示弹框
      handlerChange(val) {
        this.loadingUpdate = ! this.loadingUpdate
        this.loadingSave = true
        console.info("handlerChange " + JSON.stringify(val))
//        传参给弹框组件
        this.ablum = {
          imgs: val.IMGS
        }

        this.rowUpdateFrom = val;
        this.rowUpdate = JSON.parse(JSON.stringify(val))
        this.loadingSave = false
      },
      //取消修改  不做操作
      handlerCancel(){
        console.info("handlerCancel "+ JSON.stringify(this.rowUpdate))
        this.loadingUpdate = ! this.loadingUpdate
      },
      //保存修改  保存至表格 和 数据库 ? 还是批量改完后一次存储 先临时选中修改过的
      handlerSave(){
        console.info("handlerSave "+ JSON.stringify(this.rowUpdate))
        this.loadingSave = true

        this.rowUpdateFrom = Object.assign(this.rowUpdateFrom, this.rowUpdate) //update assign
        var params = this.assign({"_TABLE_NAME_": this.table, "_DATABASE_": this.database}, this.rowUpdateFrom)
        this.post('/common/save.do', params).then((res) => {
          this.loadingSave = false
          this.loadingUpdate = ! this.loadingUpdate
          this.info = res.info
        }).catch(() => {
          this.loadingSave = false
        })
      },
      //删除单行
      handlerDelete(val, index) {
        console.info("handlerDelete " + " " + JSON.stringify(val))
        this.loadingList = true
        const params = {ids: val[this.colKey]}
        this.get('/teacher/delet.do', params).then((res) => {
          for(let j = 0; j < this.list.length; j++) {
            if(this.list[j] == val){
              this.list.splice(j, 1);
            }
          }
          this.loadingList = false
        }).catch(() => {
          this.loadingList = false
        })
      },
      //删除选中多行
      handlerDeleteAll(){
        // const val = this.$refs.multipleTable.data
        console.info("handlerDeleteAll " + " " + JSON.stringify(this.rowSelect))
        if(this.rowSelect.length > 0){
          this.loadingList = true
          let ids = ""
          for(let i = 0; i < this.rowSelect.length; i++){
            ids += this.rowSelect[i][this.colKey] + ","
          }
          ids = ids.substring(0, ids.length - 1)
          const params = {'ids': ids}
          this.get('/teacher/delet.do', params).then((res) => {
            this.loadingList = false
            for(let i = 0; i < this.rowSelect.length; i++){
              for(let j = 0; j < this.list.length; j++) {
                if(this.list[j] == this.rowSelect[i]){
                  this.list.splice(j, 1);
                }
              }
            }
            this.rowSelect = []
          }).catch(() => {
            this.loadingList = false
          })
        }
      },
      //多选改变
      handlerSelectionChange(val) {
        console.info("handlerSelectionChange" + JSON.stringify(val))
        console.info(val)
        this.rowSelect = val
        // this.multipleSelection = val;
        // this.$refs.multipleTable.toggleAllSelection()
        // this.$refs.multipleTable.toggleRowSelection(VAL);
      },
      //排序事件
      handlerSortChange(val) {
        console.info("handlerSortChange " + JSON.stringify(val))
        // column: {…}
        // order: "ascending"
        // prop: "time"
        this.page.order = val.prop + (val.order.startsWith("desc") ? " desc" : "")
      },
      //翻页
      handlerCurrentChange(val) {
        console.info("handlerCurrentChange")
        console.info(val)
        this.page.nowpage = val
        this.getListPage()
      },
      // 修改每页数量
      handlerSizeChange(shownum) {
        this.page.shownum = shownum
        this. getListPage()
      },
      //行高亮各种颜色
      tableRowClassName({row, rowIndex}) {
        if (rowIndex === 1) {
          return 'warning-row';
        } else if (rowIndex === 3) {
          return 'success-row';
        }
        return '';
      },
    }
  }
</script>

