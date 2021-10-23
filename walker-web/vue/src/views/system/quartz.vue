<template>
  <div class="app-container" >

    <!--搜索-->
    <div class="div-box-down"
         v-loading="loadingCols"
    >
      <form class="form-inline" >
        <div class="form-group" v-for="(value, key) in colMapShow">
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

    <div
      v-show="!loadingCols"
    >
      <el-table
        v-loading="loadingList"
        :data="list"
        :row-class-name="tableRowClassName"
        ref="multipleTable"
        @selection-change="handlerSelectionChange"
        highlight-current-row
        @current-change="handlerCurrentChangeTable"
        @sort-change="handlerSortChange"
        element-loading-text="Loading"
        border
        fit
        stripe
        show-summary
        sum-text="S"
        max-height="86%"
      >
        <!--      多选框-->
        <el-table-column fixed="left" aligin="center" type="selection" min-width="12px"> </el-table-column>
        <!--      序号-->
        <el-table-column fixed="left" align="center" type="index" min-width="12px"></el-table-column>

        <!--      设置表头数据源，并循环渲染出来，property对应列内容的字段名，详情见下面的数据源格式 -->
        <el-table-column
          v-for="(value, key) in colMapShow"
          :key="key"
          :property="key"
          :label="(value=='' ? key : value)"
          sortable
          show-overflow-tooltip
          min-width="100px"
        >
          <template slot-scope="scope">
            {{scope.row[scope.column.property]}}  <!-- 渲染对应表格里面的内容 -->
          </template>
        </el-table-column>


        <el-table-column
          label="操作"
          show-overflow-tooltip
          fixed="right"
          min-width="221px"
        >
          <template slot-scope="scope">
            <el-button-group>
              <el-button size="mini" type="primary" icon="el-icon-edit" circle @click.stop="handlerChange(scope.row)"></el-button>
              <el-button size="mini" type="success" icon="el-icon-menu" circle @click.stop="handlerShowTrigger(scope.row)"></el-button>
              <el-button size="mini" type="warning" icon="el-icon-search" circle @click.stop="handlerShowHis(scope.row)"></el-button>
              <el-button size="mini" type="danger" icon="el-icon-delete" circle @click.stop="handlerDelete(scope.row)"></el-button>
              <el-button size="mini" type="primary" @click.stop="handlerRun(scope.row)">立即执行</el-button>
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
              v-for="(value, key) in colMap"
              :key="key"
              :property="key"
              :label="(value=='' ? key : value)"
            >
              <el-input v-model="rowUpdate[key]" type="text"  />
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

      <el-dialog
        title="触发器列表"
        :visible.sync="loadingUpdateTrigger"
        width="86%"
      >
        <template>
          <el-button-group>
            <el-button  class="btn btn-success" @click="handlerAddColumnTrigger()" >添加</el-button>
            <el-button  class="btn btn-danger" @click="handlerSaveAllTriggers()" >保存</el-button>
          </el-button-group>
          <el-table
            v-loading="loadingTrigger"
            :data="listTrigger"
            :row-class-name="tableRowClassName"
            @selection-change="handlerSelectionChangeTrigger"
            @current-change="handlerCurrentChangeTableTrigger"
            @open="handlerOnShowTrigger"
            ref="multipleTableTrigger"
            element-loading-text="Loading"
            border
            fit
            stripe
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
              v-for="(value, key) in colMapTrigger"
              :key="key"
              :property="key"
              :label="(value=='' ? key : value)"
              sortable
              show-overflow-tooltip
              min-width="100px"
            >
              <template slot-scope="scope">
                <input
                  type="text"
                  class="form-control"
                  style="width: 18em; margin-right: 1em;"
                  :placeholder="scope.column.property"
                  v-model="scope.row[scope.column.property]"
                />
              </template>
            </el-table-column>
          </el-table>

        </template>

      </el-dialog>
      <el-dialog
        title="调度日志"
        :visible.sync="showDialogLog"
        width="86%"
      >
        <template v-if="showDialogLog">
          <mtable :props='showDialogLogParams' ></mtable>
        </template>
      </el-dialog>
    </div>



  </div>
</template>
<style scoped>
  /deep/  .el-table td{
    padding: 4px 0px;
  }
</style>



<script>
// import { getList } from '@/api/table'

export default {
  filters: {
    statusFilter(status) {
      const statusMap = {
        published: 'success',
        draft: 'gray',
        deleted: 'danger'
      }
      return statusMap[status]
    }
  },
  data() {
    return {
      list: [],
      colMap: {},      //列名:别名
      colMapShow: {}, //显示键
      colKey: "",     //主键名
      rowSearch: {},   //搜索 列明:搜索值
      rowUpdate: {},   //更新界面复制 列名:新值
      rowUpdateFrom: {},//更新界面源对象 列名:旧值
      rowSelect: [],   //选中多行
      nowRow: null,     //当前行
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

      nowRowTrigger: null,
      loadingTrigger: false,
      loadingHis: false,
      loadingUpdateTrigger: false,
      showTrigger: {}, //当前角色用户
      colMapTrigger: {},
      colKeyTrigger: {},
      listTrigger: [],
      listTriggerOld: [],
      rowSelectTrigger: [],
      rowSelectHis: [],
      quartzTable: 'W_QRTZ_JOB_DETAILS',
      quartzTableTrigger: 'W_QRTZ_CRON_TRIGGERS',
      info: "",
      info2: "",
      showDialogLog: false,
      showDialogLogParams: null,
    }
  },
  created() {
    this.getColumns()
  },

  methods: {
    //查询展示的行列信息 备注
    getColumns() {
      this.loadingCols = true
      this.get('/common/getColsMap.do', {tableName: this.quartzTable}).then((res) => {
        this.colMap = res.data.colMap
         this.page.order = res.data.colMap['S_MTIME'] ? 'S_MTIME DESC' : ''
        delete this.colMap.JOB_DATA
        delete this.colMap.SCHED_NAME
        delete this.colMap.JOB_GROUP
        delete this.colMap.IS_DURABLE
        delete this.colMap.IS_NONCONCURRENT
        delete this.colMap.IS_UPDATE_DATA
        delete this.colMap.REQUESTS_RECOVERY

        //this.colMap['CRON_EXPRESSION'] = '触发器cron'

        this.colMapShow = {}
        this.colMapShow['JOB_NAME'] = this.colMap['JOB_NAME']
        this.colMapShow['JOB_CLASS_NAME'] = this.colMap['JOB_CLASS_NAME']
        this.colMapShow['DESCRIPTION'] = this.colMap['DESCRIPTION']
        // this.colMapShow['STATUS'] = '状态'


        this.colKey = 'JOB_NAME'  //res.data.colKey
        this.clearRowSearch()
        this.loadingCols = false
        this. getListPage()
      }).catch(() => {
        this.loadingCols = false
      })



    },
    //清空搜索条件
    clearRowSearch(){
      this.rowSearch = {} //clear map
      this.page.nowpage = 1
    },
    //分页查询
    getListPage() {
      this.loadingList = true
      var params = this.assign({nowPage: this.page.nowpage, showNum: this.page.shownum, order: this.page.order}, this.rowSearch)
      this.get('/quartz/findPage.do', params).then((res) => {
        this.list = res.data.data
        this.page = res.data.page
        this.info = res.info
        this.loadingList = false
      }).catch(() => {
        this.loadingList = false
      })

    },
    //当前高亮行
    handlerCurrentChangeTable(row){
      console.info("handlerCurrentChangeTable", row)
      this.nowRow = row
    },
    //当前高亮行
    handlerCurrentChangeTableTrigger(row){
      console.info("handlerCurrentChangeTableTrigger", row)
      this.nowRowTrigger = row
    },
    //添加行
    handlerAddColumn(){
      var newObj = this.assign(this.nowRow?this.nowRow:{}, this.rowSearch)
      newObj[this.colKey] = ''
      //newObj["JOB_NAME"] = this.nowRow == null ? "" : this.nowRow["JOB_NAME"]
      //newObj["DESCRIPTION"] = this.nowRow == null ? "" : this.nowRow["DESCRIPTION"]
      //newObj['CRON_EXPRESSION'] = '0 0 10 * * ?'
      this.list.push(newObj)
      this.handlerChange(newObj)
    },
    //添加行
    handlerAddColumnTrigger(){
      let newObj = this.assign({}, {})
      newObj["CRON_EXPRESSION"] = this.nowRowTrigger == null ? '0 0 0 * * ?' : this.nowRowTrigger["CRON_EXPRESSION"]
      newObj["DESCRIPTION"] = ' 每天0点0分0秒 触发'
      newObj["S_FLAG"] = '1'  //需要保存
      this.listTrigger.push(newObj)
      this.$refs.multipleTableTrigger.toggleRowSelection(newObj, true)
//      this.$nextTick(this.handlerOnShowTrigger())  //下次 DOM 更新循环结束之后执行延迟回调，在修改数据之后使用 $nextTick，则可以在回调中获取更新后的 DOM
    },
    //立即操作
    handlerRun(val) {
      console.info("handlerRun " + JSON.stringify(val))
      this.loadingList = true
      this.get('/quartz/run.do', val).then((res) => {
        this.info = res.info
        this.loadingList = false
      }).catch(() => {
        this.loadingList = false
      })

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

      this.rowUpdateFrom = Object.assign(this.rowUpdateFrom, this.rowUpdate) //update assign
      var params = this.rowUpdateFrom
      this.post('/quartz/save.do', params).then((res) => {
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
      const params = {JOB_NAME: val[this.colKey]}
      this.get('/quartz/delet.do', params).then((res) => {
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
        const params = {JOB_NAME: ids}
        this.get('/quartz/delet.do', params).then((res) => {
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

    //展示 并支持添加修改 关联角色属性 一个人有多种角色 部门角色 列表 提供添加和删除(非部门)
    handlerShowTrigger(val) {
      this.showTrigger = val
      this.loadingUpdateTrigger = ! this.loadingUpdateTrigger
      this.loadingTrigger = true
      this.listTrigger = []
      this.colMapTrigger = {}
      this.colMapTrigger['CRON_EXPRESSION'] = '表达式'
      this.colMapTrigger['DESCRIPTION'] = '描述'
      this.colKeyTrigger = 'CRON_EXPRESSION'

      var params = {nowPage: 1, showNum: 10}
      params[this.colKey] = val[this.colKey]
      this.get('/quartz/findPageTrigger.do', params).then((res) => {
        this.listTrigger = []
        for(var i = 0; i < res.data.data.length; i++){
          this.listTrigger.push(res.data.data[i])
        }
        // this.page = res.data.page
        this.info = res.info
        this.listTriggerOld = []
        for(var i = 0; i < this.listTrigger.length; i++){
          this.listTrigger[i].S_FLAG = '1'
          this.listTriggerOld.push(this.listTrigger[i])
        }


//        this.$nextTick(this.handlerOnShowTrigger())  //下次 DOM 更新循环结束之后执行延迟回调，在修改数据之后使用 $nextTick，则可以在回调中获取更新后的 DOM
//        为何第一次初始化弹窗后依然不能自动勾选 延时器方案？
        let self = this
        setTimeout(function(){
          self.handlerOnShowTrigger()
        },1) //   function 里面的this指向的是windows

        this.loadingTrigger = false
      }).catch(() => {
        this.loadingTrigger = false
      })

    },
    //默认选中
    handlerOnShowTrigger(){
      for(var i = 0; i < this.listTrigger.length; i++){
        if(this.listTrigger[i].S_FLAG == '1'){
          this.$refs.multipleTableTrigger.toggleRowSelection(this.listTrigger[i], true)
        }else{
          this.$refs.multipleTableTrigger.toggleRowSelection(this.listTrigger[i], false)
        }
      }

    },
    handlerShowHis(val){
      this.showDialogLogParams =  {
        database: '',
        table: 'W_LOG_MODEL',
        params: {
          'WAY' : val['JOB_NAME'],
          'URL' : val['JOB_CLASS_NAME']
        },
      }
      this.showDialogLog = ! this.showDialogLog

      // params['INFO'] = val[this.colKey]
      // this.$router.push({
      //   path:'/db/table',
      //   query: params,
      // })
    },
    //新建的  取消勾选的 差异化 触发器保存
    handlerSaveAllTriggers(){
      this.loadingTrigger = true;
      var listOn = []
      var listOff = []
      var map = {}
      var mapOld = {}
      for(var i = 0; i < this.rowSelectTrigger.length; i++){
        map[this.rowSelectTrigger[i][this.colKeyTrigger]] = '1' //现在选中的
      }
      for(var i = 0; i < this.listTriggerOld.length; i++){
        mapOld[this.listTriggerOld[i][this.colKeyTrigger]] = this.listTriggerOld[i]['S_FLAG'] //原来选中的
      }

      for (var key in mapOld) {
        if(mapOld[key] == '1' && (map[key] == '0' || map[key] == null)){
          listOff.push(key)
        }
      }
      for (var key in map) {
        if((mapOld[key]==null || mapOld[key]=='0') && map[key] == '1'){
          listOn.push(key)
        }
      }

      if(listOn.length > 0 || listOff.length > 0){
        //console.info(this.showTrigger)
        var params = this.assign({}, this.showTrigger)
        //
        params['ON'] =  listOn.join(",")
        params['OFF'] = listOff.join(",")

        this.get('/quartz/saveTriggers.do', params).then((res) => {
          this.loadingTrigger = false
          // this.loadingUpdateTrigger = false
        }).catch(() => {
          this.loadingTrigger = false
        })
      }else{
        this.$message({message:'没有改动', type:'waring'});
        this.loadingTrigger = false;

      }

    },
    //多选改变  状态机
    handlerSelectionChangeTrigger(val) {
      console.info("handlerSelectionChangeTrigger" + JSON.stringify(val))
      //debugger
      this.rowSelectTrigger = val
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

