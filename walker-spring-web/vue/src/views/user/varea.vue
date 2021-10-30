<template>
  <div class="app-container" >

    <!--搜索-->
    <div class="div-box-down"
         v-loading="loadingCols"
    >
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
          v-for="(value, key) in colMap"
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
          min-width="161px"
        >
          <template slot-scope="scope">
            <el-button-group>
              <el-button size="mini" type="primary" icon="el-icon-edit" circle @click.stop="handlerChange(scope.row)"></el-button>
              <el-button size="mini" type="success" @click.stop="handlerShowRole(scope.row)">R</el-button>
              <el-button size="mini" type="warning" @click.stop="handlerShowUser(scope.row)">U</el-button>
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
              v-for="(value, key) in colMap"
              :key="key"
              :property="key"
              :label="(value=='' ? key : value)"
            >
              <el-input v-model="rowUpdate[key]" type="text" />
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
        title="拥有的角色"
        :visible.sync="loadingUpdateRole"
        width="86%"
      >
        <template>
          部门角色:
          <el-table
            v-loading="loadingRole"
            :data="listRoleDept"
            :row-class-name="tableRowClassName"
            :default-sort = "{prop: 'S_FLAG', order: 'descending'}"
            @selection-change="handlerSelectionChangeRole"
            @open="handlerOnShowRole"
            ref="multipleTableRoleUser"
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
              v-for="(value, key) in colMapRole"
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
          </el-table>
          <el-button
            class="btn btn-danger"
            @click="handlerSaveAllRoles()"
            style="margin:4px 0px 0 2px;"
          >保存角色</el-button>

        </template>
      </el-dialog>

      <el-dialog
        title="地理用户"
        :visible.sync="showDialogUser"
        width="86%"
      >
        <template v-if="showDialogUser">
          <user :props="showDialogUserParams"></user>
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
      colKey: "",     //主键名
      rowSearchDefault: {},//默认搜索 当传参过来指定条件嵌入
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


      loadingRole: false,
      loadingUpdateRole: false,
      deptShowRole: {}, //当前角色用户
      colMapRole: {},
      colKeyRole: {},
      listRoleDept: [],
      rowSelectRole: [],

      showDialogUser: false,
      showDialogUserParams: null,
    }
  },
  props:['props'],//组件传参
  created() {


    if(this.props) {
      var params = this.props
      this.rowSearchDefault = this.assign({}, params.params)
    }
    this.getColumns()
  },
  methods: {
    //查询展示的行列信息 备注
    getColumns() {
      this.loadingCols = true
      this.get('/common/getColsMap.do', {tableName: 'W_AREA'}).then((res) => {
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
    //清空搜索条件
    clearRowSearch(){

      this.rowSearch = this.assign({}, this.rowSearchDefault)
      this.page.nowpage = 1
    },
     //分页查询
     getListPage() {
      this.loadingList = true
      // name/nowPage/showNum
      var params = this.assign({nowPage: this.page.nowpage, showNum: this.page.shownum, order: this.page.order}, this.rowSearch)
      this.get('/area/findPage.do', params).then((res) => {
        this.list = res.data.data
        this.page = res.data.page
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
    //添加行
    handlerAddColumn(){
      var newObj = this.assign(this.nowRow?this.nowRow:{}, this.rowSearch)
      newObj[this.colKey] = ''
      newObj["S_FLAG"] = '1'
      newObj["P_ID"] = this.nowRow == null ? "" : this.nowRow["ID"]
      this.list.push(newObj)
      this.handlerChange(newObj)
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
      this.post('/area/save.do', params).then((res) => {
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
      const params = {ids: val[this.colKey]}
      this.get('/area/delet.do', params).then((res) => {
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
        const params = {ids: ids}
        this.get('/area/delet.do', params).then((res) => {
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
    handlerShowUser(val){
      this.showDialogUserParams =  {
        params: {
          'AREA_ID' : val[this.colKey]
        },
      }
      this.showDialogUser = ! this.showDialogUser
    },
    //展示 并支持添加修改 关联角色属性 一个人有多种角色 部门角色 列表 提供添加和删除(非部门)
    handlerShowRole(val) {
      this.areaShowRole = val
      this.loadingUpdateRole = ! this.loadingUpdateRole
      this.loadingRole = true
      this.listRoleDept = []
      this.get('/common/getColsMap.do', {tableName: 'W_ROLE'}).then((res) => {
        this.colMapRole = res.data.colMap
        this.colKeyRole = res.data.colKey
        var params = {ID:val.ID, S_FLAG:''}
        this.get('/role/getDeptRoles.do', params).then((res) => {
          this.listRoleDept = res.data.listDept
          this.rowSelectRole = []
          this.loadingRole = false
          this.$nextTick(this.handlerOnShowRole)  //下次 DOM 更新循环结束之后执行延迟回调，在修改数据之后使用 $nextTick，则可以在回调中获取更新后的 DOM
        }).catch(() => {
          this.loadingRole = false
        })
      }).catch(() => {
        this.loadingRole = false
      }).then(res=>{
        this.handlerOnShowRole()
      })

    },
    //默认选中
    handlerOnShowRole(){
      for(var i = 0; i < this.listRoleDept.length; i++){
        if(this.listRoleDept[i].S_FLAG == '1'){
          // this.rowSelectRole.push(this.listRoleDept[i])
          this.$refs.multipleTableRoleUser.toggleRowSelection(this.listRoleDept[i], true)
        }
      }

    },
    //用户角色 批量保存 删除 比对默认值 找出差异化 提交
    handlerSaveAllRoles(){
      var listOn = []
      var listOff = []
      var map = {}
      for(var i = 0; i < this.rowSelectRole.length; i++){
        map[this.rowSelectRole[i][this.colKeyRole]] = '1'
      }
      for(var i = 0; i < this.listRoleDept.length; i++){
        var roleUser = this.listRoleDept[i]
        var id = roleUser[this.colKeyRole]
        var newFlag = map[id] == null ? '0' : '1'
        var oldFlag = roleUser['S_FLAG']
        if(oldFlag != newFlag){
          newFlag == '1' ? listOn.push(id) : listOff.push(id)
        }
      }
      if(listOn.length > 0 || listOff.length > 0){
        var params = {ID: this.areaShowRole[this.colKey], ON: listOn.join(","), OFF: listOff.join(",")}
        this.get('/role/saveRoles.do', params).then((res) => {
          this.loadingRole = false
          this.loadingUpdateRole = false
        }).catch(() => {
          this.loadingRole = false
        })
      }else{
        this.$message({message:'没有改动', type:'waring'});
      }

    },
    //多选改变role
    handlerSelectionChangeRole(val) {
      console.info("handlerSelectionChangeRole" + JSON.stringify(val))
      this.rowSelectRole = val
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

