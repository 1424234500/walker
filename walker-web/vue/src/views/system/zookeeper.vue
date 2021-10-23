


<template>
  <div class="app-container" >
<!--搜索-->
    <div class="div-box-down"
         v-loading="loadingCols"
    >
      <form class="form-inline" >
        <div class="form-group" v-for="(ivalue, ikey) in colMap">
          <label>{{ivalue=='' ? ikey : ivalue}}</label>
          <input
            type="text"
            class="form-control"
            style="width: 10em; margin-right: 1em;"
            v-on:keyup.13="getListPage()"
            :placeholder="ikey"
            v-model="rowSearch[ikey]"
          />
        </div>
        <el-button-group>
          <el-button  class="btn btn-primary" @click="getListPage()" >查询</el-button>
          <el-button  class="btn btn-danger" @click="clearRowSearch();getListPage();" >home</el-button>
          <el-button  class="btn btn-success" @click="handlerAddColumn()" >添加</el-button>
          <el-button  class="btn btn-default" @click="goParent()" >上级目录</el-button>
        </el-button-group>

      </form>
    </div>


    <div
      v-show="!loadingCols"
    >
      <el-table
        v-loading="loadingList"
        :data="list"
        :row-class-name="tableRowClassName"
        ref="multipleTable"
        @row-click="handlerRowClick"
        @selection-change="handlerSelectionChange"
        @sort-change="handlerSortChange"
        element-loading-text="Loading"
        border
        fit
        show-summary
        sum-text="S"
        highlight-current-row
        max-height="86%"
      >
        <!--      多选框-->
        <el-table-column fixed="left" aligin="center" type="selection" min-width="12px"> </el-table-column>
        <!--      序号-->
        <el-table-column fixed="left" align="center" type="index" min-width="12px"></el-table-column>

        <!--      设置表头数据源，并循环渲染出来，property对应列内容的字段名，详情见下面的数据源格式 -->
        <el-table-column
          v-for="(ivalue, ikey) in colMapShow"
          :key="ikey"
          :property="ikey"
          :label="(ivalue=='' ? ikey : ivalue)"
          sortable
          show-overflow-tooltip
          min-width="60px"
        >
          <template slot-scope="scope">
            {{scope.row[scope.column.property]}}  <!-- 渲染对应表格里面的内容 -->
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          show-overflow-tooltip
          fixed="right"
          min-width="91px"
        >
          <template slot-scope="scope">
            <el-button-group>
      <!--        <el-button v-if=" scope.row.EXT!='dir' " size="mini" type="success" icon="el-icon-download" circle @click.stop="download(scope.row)"></el-button> -->
              <el-button size="mini" type="primary" icon="el-icon-edit" circle @click.stop="handlerChange(scope.row)"></el-button>
              <el-button size="mini" type="danger" icon="el-icon-delete" circle @click.stop="handlerDelete(scope.row)"></el-button>
            </el-button-group>
          </template>
        </el-table-column>
      </el-table>

      <el-button
        v-if="rowSelect.length > 0"
        class="btn btn-danger"
        @click="handlerDeleteAll()"
        style="float:left;margin:4px 0px 0 2px;"
      >删除选中</el-button>
      <!--    分页-->
      <el-pagination
        @current-change="handlerCurrentChange"
        @size-change="handlerSizeChange"
        :current-page="page.nowpage"
        :page-size="page.shownum"
        :page-sizes="[2, 4, 8, 16, 32, 64, 128]"
        :pager-count="9"
        layout="total, sizes, prev, pager, next, jumper"
        :total="page.num"
        background
        style="float:right;margin:10px 20px 0 0;"
      >
      </el-pagination>


      <el-dialog
        title="修改"
        :visible.sync="loadingUpdate"
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
              v-for="(ivalue, ikey) in colMap"
              :key="ikey"
              :property="ikey"
              :label="(ivalue=='' ? ikey : ivalue)"
            >
              <el-input v-model="rowUpdate[ikey]" type="text" />
            </el-form-item>

            <el-form-item>
              <el-button-group>
                <el-button type="primary" @click="handlerSave()">确定</el-button>
                <el-button type="danger" @click="handlerCancel()">取消</el-button>
              </el-button-group>
            </el-form-item>
          </el-form>
        </template>
      </el-dialog>
    </div>





  </div>
</template>
<style scoped>
  /deep/  .el-table td{
    padding: 4px 0px;
  }
  /deep/ .row-table-dir{
    background: #5bc0de;
  }
  /deep/ .row-table-sh{
    color: #761c19;
  }
</style>



<script>
// import { getList } from '@/api/table'

export default {
  filters: {
    statusFilter(status) {
      const statusMap = {
        dir: 'success',
        sh: 'gray',
        ddeleted: 'danger'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      list: [],
      colMap: {},      //列名:别名
      colMapShow: {},      //列名:展示的列名
      colKey: "",     //主键名
      fileList: [],
      rowSearch: {},   //搜索 列明:搜索值
      rowUpdate: {},   //更新界面复制 列名: 新值
      rowUpdateFrom: {},//更新界面源对象 列名:旧值
      rowSelect: [],   //选中行
      page: {
        nowpage: 1,
        num: 0,
        order: "",
        pagenum: 0,
        shownum: 8,
      },
      loadingList: true,
      loadingCols: true,
      loadingSave: true,
      loadingUpdate: false,
    }
  },
  created() {
    this.getColumns()
  },

  methods: {
    //查询展示的行列信息 备注
    getColumns() {
      this.loadingCols = true
      this.get('/zookeeper/getColsMap.do', {}).then((res) => {
        this.colMap = res.data.colMap
        this.page.order = res.data.colMap['S_MTIME'] ? 'S_MTIME DESC' : ''
        this.colMapShow = this.assign({}, this.colMap)
        delete this.colMapShow["PATH"]
        delete this.colMapShow["EXT"]

        this.colKey = res.data.colKey
        this.rowSearch[this.colKey] = "/" //默认值
        this.clearRowSearch()
        this.loadingCols = false
        this.getListPage()
      }).catch(() => {
        this.loadingCols = false
      })

    },
    //清空搜索条件
    clearRowSearch(){
      this.rowSearch = {} //clear map
      this.page.nowpage = 1
      this.rowSearch[this.colKey] = ''
    },
     //分页查询
     getListPage() {
      this.loadingList = true
      // name/nowPage/showNum
      var params = this.assign({}, this.rowSearch)
      params[this.colKey] = this.rowSearch[this.colKey] ? this.rowSearch[this.colKey]  : ""
      this.get('/zookeeper/findPage.do', params).then((res) => {
        this.list = res.data.data
        this.page = res.data.page
        this.loadingList = false
      }).catch(() => {
        this.loadingList = false
      })
    },
    //添加行
    handlerAddColumn(){
      var newObj = this.assign(this.nowRow?this.nowRow:{}, this.rowSearch)
      newObj[this.colKey] = ''
      newObj["S_FLAG"] = '1'
      this.list.push(newObj)
      this.handlerChange(newObj)
    },
    //上一级目录
    goParent(){
      console.info("goParent ", this.rowSearch[this.colKey])
      if(this.rowSearch[this.colKey].endsWith('/')){
        this.rowSearch[this.colKey] = this.rowSearch[this.colKey].substring(0, this.rowSearch[this.colKey].length - 1)
      }
      var dd = this.rowSearch[this.colKey].split('/')
      if(dd.length > 1 ){
        var last = dd[dd.length - 1]
        if(last.length > 0)
          this.rowSearch[this.colKey] = this.rowSearch[this.colKey].substring(0, this.rowSearch[this.colKey].length - last.length - 1)
        if(this.rowSearch[this.colKey].length <= 0){
          this.rowSearch[this.colKey] = '/'
        }
      }else{
        this.rowSearch[this.colKey] = '/'
      }
      this.getListPage()

    },
    //点击进入子目录或者下载
    handlerRowClick(row, event,column){
      console.info("handlerRowClick ", row, event, column)
      this.loadingList = true   //不添加这玩意儿会导致 不能即时更新界面展示?
//      判定是否可点击
//debugger
      this.rowSearch[this.colKey] = row[this.colKey]
      if(row['CHILD_SIZE'] > 0){
        this.getListPage()
      }else{
        // this.download(row)
      }
      this.loadingList = false
    },
    //修改单行 展示弹框
    handlerChange(val) {
      this.loadingUpdate = ! this.loadingUpdate
      this.loadingSave = false
      console.info("handlerChange " + JSON.stringify(val))
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

      var params = Object.assign(this.rowUpdateFrom, this.rowUpdate) //update assign
      params["FROM_" + this.colKey] = this.rowUpdate[this.colKey]
      params["SEARCH_" + this.colKey] = this.rowSearch[this.colKey]

      this.post('/zookeeper/save.do', params).then((res) => {
        this.loadingSave = false
        this.loadingUpdate = ! this.loadingUpdate
      }).catch(() => {
        this.loadingSave = false
      })

    },
    //删除单行
    handlerDelete(val, index) {
      console.info("handlerDelete " + " " + JSON.stringify(val))
      this.loadingList = true

      const params = {"ids": val[this.colKey]}
      this.get('/zookeeper/delet.do', params).then((res) => {
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
        const params = {"ids": ids}
        this.get('/zookeeper/delet.do', params).then((res) => {
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
      return 'row-table-' + row['EXT'];
    },


  }
}
</script>

