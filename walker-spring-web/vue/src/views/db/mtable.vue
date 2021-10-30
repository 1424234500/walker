<template>
  <div class="app-container" >

    <!--搜索 table-->
    <div class="div-box-down"
         v-if="showChoseDb"
         v-loading="loadingTables"
    >
      <form class="form-inline" >
        <div class="form-group">
          <label>database</label>
          <el-select v-model="database"
                     clearable
                     filterable
                     allow-create
                     default-first-option
                     placeholder="请选择"
                     no-match-text="新建">
            <el-option
              v-for="item in queryDatabase"
              :key="item"
              :label="item"
              :value="item"
            >
            </el-option>
          </el-select>
          <el-button-group>
            <el-button  class="btn btn-primary" @click="getTables()" >查询</el-button>
          </el-button-group>
        </div>
        <div class="form-group">
          <label>table</label>
          <el-select v-model="table"
                     clearable
                     filterable
                     allow-create
                     default-first-option
                     placeholder="请选择"
                     no-match-text="新建">
            <el-option
              v-for="item in queryTable"
              :key="item"
              :label="item"
              :value="item"
            >
            </el-option>
          </el-select>
          <el-button-group>
            <el-button  class="btn btn-primary" @click="getColumns()" >查询</el-button>
          </el-button-group>
        </div>
      </form>
    </div>

    <!--搜索栏目-->
    <div class="div-box-down"
         v-show="!loadingTables || !showChoseDb"
         v-loading="loadingCols"
    >

      <form class="form-inline" >
        <label>{{table }}</label> &nbsp;&nbsp;
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
        @current-change="handlerCurrentChangeTable"
        @selection-change="handlerSelectionChange"
        @sort-change="handlerSortChange"
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
          v-for="(value, key) in colMap"
          :key="key"
          :property="key"
          :label="(value=='' ? key : value)"
          sortable
          show-overflow-tooltip
          min-width="100px"
        >
          <template slot-scope="scope">
            {{scope.row[scope.column.property]  }}  <!-- 渲染对应表格里面的内容 -->
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          show-overflow-tooltip
          fixed="right"
          min-width="81px"
        >
          <template slot-scope="scope">
            <el-button-group>
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

<!--      提示sql-->
      <el-input
        v-show="showChoseDb"
        type="textarea"
        :rows="4"
        placeholder="执行sql"
        v-model="info">
      </el-input>


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
              <el-input v-model="rowUpdate[key]" type="textarea" />
            </el-form-item>

            <el-form-item>
              <el-button type="primary" @click="handlerSave()">确定</el-button>
              <el-button type="danger" @click="handlerCancel()">取消</el-button>
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
        rowSearch: {},   //搜索 列明:搜索值
        rowSearchDefault: {},//默认搜索 当传参过来指定条件嵌入
        rowUpdate: {},   //更新界面复制 列名:新值
        rowUpdateFrom: {},//更新界面源对象 列名:旧值
        rowSelect: [],   //选中行
        nowRow: null,     //当前行
        info: "",       //执行sql 提示
        queryDatabase: [],  //db列表
        database: "", //选中db

        queryTable: [], //table列表
        table: "",      //选中table


        page: {
          nowpage: 1,
          num: 0,
          order: "",
          pagenum: 0,
          shownum: 8,
        },
        loadingTables: true,
        loadingList: true,
        loadingCols: true,
        loadingSave: true,
        loadingUpdate: false,
        showChoseDb: false,
      }
    },
    props:['props'],//组件传参

    created() {
      if(this.$route.query.table){ //传递 指定table
        var params = this.$route.query
        this.showChoseDb = false
        this.database = params.database
        this.database = this.database ? this.database : ''
        this.table = params.table
        delete params.table
        delete params.database
        this.rowSearchDefault = this.assign({}, params)
        this.getColumns()
      }else if(this.props) {
        var params = this.props
        this.showChoseDb = false
        this.database = params.database
        this.database = this.database ? this.database : ''
        this.table = params.table
        this.rowSearchDefault = this.assign({}, params.params)
        this.getColumns()
      }else{
        this.showChoseDb = true
        this.getDatabases()
      }
    },

    methods: {
      getDatabases() {
        this.loadingTables = true
        var params = this.assign({}, {})
        this.get('/common/getDatabasesOrUsers.do', params).then((res) => {
          this.queryDatabase = res.data
          this.database = res.data != null && res.data.length > 0 ? res.data[0] : 'walker'
          this.database = 'walker'
          this.loadingTables = false
          this.getTables()
        }).catch(() => {
          this.loadingTables = false
        })

      },
      //查询展示的行列信息 备注
      getTables() {
        this.loadingTables = true
        var params = this.assign({"_TABLE_NAME_": this.table, "_DATABASE_": this.database}, {})
        this.get('/common/getTables.do', params).then((res) => {
          this.queryTable = res.data
          this.table = res.data != null && res.data.length > 0 ? res.data[0] : ''
          this.list = []
          this.colMap = {}
          this.clearRowSearch()
          this. getColumns()
          this.loadingTables = false
        }).catch(() => {
          this.loadingTables = false
        })

      },
      //查询展示的行列信息 备注
      getColumns() {
        this.loadingCols = true
        var params = this.assign({"_TABLE_NAME_": this.table, "_DATABASE_": this.database}, {})
        this.get('/common/getColsMap.do', {tableName: this.table}).then((res) => {
          this.colMap = res.data.colMap
          this.page.order = res.data.colMap['S_MTIME'] ? 'S_MTIME DESC' : ''

          this.colKey = res.data.colKey
          this.clearRowSearch()
          this. getListPage()
          this.loadingCols = false
        }).catch(() => {
          this.loadingCols = false
        })

      },
      //清空搜索条件
      clearRowSearch(){
        this.rowSearch = this.assign({}, this.rowSearchDefault)
        this.page.nowpage = 1
        this.page.order = ''
      },
      //分页查询
      getListPage() {
        this.loadingList = true
        // name/nowPage/showNum
        var obj = this.assign({nowPage: this.page.nowpage, showNum: this.page.shownum, order: this.page.order}, this.rowSearch)
        var params = this.assign({"_TABLE_NAME_": this.table, "_DATABASE_": this.database}, obj)
        this.get('/common/findPage.do', params).then((res) => {
          this.list = res.data.data
          this.page = res.data.page
          this.info = res.info
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
        this.handlerChange(this.list[this.list.length - 1])
      },
      //修改单行 展示弹框
      handlerChange(val) {
        this.loadingUpdate = ! this.loadingUpdate
        this.loadingSave = false
        console.info("handlerChange " + JSON.stringify(val))
        this.rowUpdateFrom = val
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
      handlerDelete(val) {
        console.info("handlerDelete " + " " + JSON.stringify(val))
        this.loadingList = true
        var params = this.assign({"_TABLE_NAME_": this.table, "_DATABASE_": this.database}, val)
        this.get('/common/delet.do', params).then((res) => {
          for(let j = 0; j < this.list.length; j++) {
            if(this.list[j] == val){
              this.list.splice(j, 1);
            }
          }
          this.info = res.info
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
          for(let i = 0; i < this.rowSelect.length; i++){
            this.handlerDelete(this.rowSelect[i])
          }
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
      //当前高亮行
      handlerCurrentChangeTable(row){
        console.info("handlerCurrentChangeTable", row)
        this.nowRow = row
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

