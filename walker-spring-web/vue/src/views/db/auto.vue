<template>
  <div class="app-container" >

    <div class="div-box-down"
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

      <br>

      <div v-loading="loadingSql" >
        <el-tabs
          v-model="listTabValue" type="card" editable @edit="handleTabsEdit">
          <el-tab-pane
            :key="item.name"
            v-for="(item, index) in listTab"
            :label="item.title"
            :name="item.name"
          >
            <el-input
              type="textarea"
              :rows="4"
              placeholder="输入需要执行的sql"
              v-model="item.content">
            </el-input>
          </el-tab-pane>
        </el-tabs>
        <el-button-group>
          <el-button  class="btn btn-warning" @click="getListPage()" >执行sql</el-button>
          <el-button  class="btn btn-danger" @click="initTab()" >清空所有sql</el-button>
        </el-button-group>
      </div>

    </div>

    <div v-show="obj[listTabValue].list!=null">
      <el-table
        v-loading="loadingList"
        :data="obj[listTabValue].list"
        ref="multipleTable"
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
        <!--      序号-->
        <el-table-column fixed="left" align="center" type="index" min-width="12px"></el-table-column>

        <!--      设置表头数据源，并循环渲染出来，property对应列内容的字段名，详情见下面的数据源格式 -->
        <el-table-column
          v-for="(value, key) in obj[listTabValue].colMap"
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


      <!--    分页-->
      <el-pagination
        @current-change="handlerCurrentChange"
        @size-change="handlerSizeChange"
        :current-page="obj[listTabValue].page.nowpage"
        :page-size="obj[listTabValue].page.shownum"
        :page-sizes="[2, 4, 8, 16, 32, 64, 128]"
        :pager-count="9"
        layout="total, sizes, prev, pager, next, jumper"
        :total="obj[listTabValue].page.num"
        background
        style="float:right;margin:10px 20px 0 0;"
      >
      </el-pagination>
  </div>


    <el-input v-show="obj[listTabValue].info "
      type="textarea"
      :rows="10"
      placeholder="执行sql结果"
      v-model="obj[listTabValue].info">
    </el-input>
  </div>
</template>

<style scoped>
  /deep/  .el-table td{
    padding: 4px 0px;
  }
</style>



<script>
  // import { getList } from '@/api/table'
  import { Message } from 'element-ui';

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
        obj: [],  //多卡片页
        // obj: this.dbAuto,  //多卡片页

        list: [],
        colMap: {},      //列名:别名
        colKey: "",     //主键名
        rowSearch: {},   //搜索 列明:搜索值
        rowUpdate: {},   //更新界面复制 列名:新值
        rowUpdateFrom: {},//更新界面源对象 列名:旧值
        rowSelect: [],   //选中行
        info: "",       //执行sql 提示

        queryTable: [], //table列表
        table: "",      //选中table


        page: {
          nowpage: 1,
          num: 0,
          order: "",
          pagenum: 0,
          shownum: 8,
        },
        loadingList: false,
        loadingTables: false,
        loadingSql: false,

        tabPre: 'T',
        defaultSql: 'select t.* from walker.W_USER t where 1=1 ',
        listTab: [],
        tabIndex: 0,
        listTabValue: '0',    //当前处理卡片页面



        queryDatabase: [],  //db列表
        database: "" //选中db
      }
    },
    created() {
      this.initTab()
      this.getDatabases()
    },

    methods: {
      initTab(){
        if(!this.obj || this.obj.length == 0) {
          this.obj = []
          this.listTab = []
          for (var i = 0; i < 3; i++) {
            this.addTab()
          }
          this.listTabValue = this.listTab[0].name
        }
      },
      addTab( ){
        var tab =  this.handleTabsEdit('', 'add')
        // initTabVal(tab)
        return tab
      },
      initTabVal(tab){
        this.obj[tab.name] = {}
        this.obj[tab.name].page = {
          nowpage: 1,
          num: 0,
          order: "",
          pagenum: 0,
          shownum: 8,
        }
        this.obj[tab.name].list = null
        this.obj[tab.name].colKey = ''
        this.obj[tab.name].colMap = null
      },
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
          this.table = res.data != null && res.data.length > 0 ? res.data[0] : 'W_USER'
          this.loadingTables = false
        }).catch(() => {
          this.loadingTables = false
        })

      },
      //查询展示的行列信息 备注
      getColumns() {
        var tab = this.addTab()
        tab.content = 'select t.* from ' + this.database + '.' + this.table + ' t where 1=1 '
      },
      //执行手写sql
      getListPage(){
        this.loadingSql = true
        var tab = null
        for(var i = 0; i < this.listTab.length; i++){
          if(this.listTab[i].name == this.listTabValue){
            tab = this.listTab[i]
          }
        }
        if(tab == null){
          Message.error('tab 页异常?');
          return
        }
        // this.addTab(tab)
        this.obj[this.listTabValue].list = null
        this.obj[this.listTabValue].info = ''
        this.obj[this.listTabValue].colKey = ''
        this.obj[this.listTabValue].colMap = null


        // var tab = this.listTab[parseInt(this.listTabValue)]
        var sql = tab.content
        var obj = this.assign({nowPage: this.obj[this.listTabValue].page.nowpage, showNum: this.obj[this.listTabValue].page.shownum, order: this.obj[this.listTabValue].page.order}, {})
        var params = this.assign({"_SQL_": sql}, obj)
        this.get('/common/exeSql.do', params).then((res) => {
          if(res.data.data != null){
            // this.list = res.data.data
            this.obj[this.listTabValue].list = res.data.data

            this.obj[this.listTabValue].page = res.data.page
            this.obj[this.listTabValue].colMap = res.data.colMap
            this.obj[this.listTabValue].colKey = res.data.colKey
          }else{
            this.obj[this.listTabValue].info = res.data.info
          }
          // tab.content = res.info
          tab.title = tab.title.split(' ')[0] + ' ' + res.costTime
          this.loadingSql = false
        }).catch(() => {
          this.loadingSql = false
        })
      },

      //排序事件
      handlerSortChange(val) {
        console.info("handlerSortChange " + JSON.stringify(val))
        // column: {…}
        // order: "ascending"
        // prop: "time"
        this.obj[this.listTabValue].page.order = val.prop + (val.order.startsWith("desc") ? " desc" : "")
      },
      //翻页
      handlerCurrentChange(val) {
        console.info("handlerCurrentChange")
        console.info(val)
        this.obj[this.listTabValue].page.nowpage = val
        this.getListPage()
      },
      // 修改每页数量
      handlerSizeChange(shownum) {
        this.obj[this.listTabValue].page.shownum = shownum
        this.getListPage()
      },
    //  tab页控制
      handleTabsEdit(targetName, action) {
        if (action === 'add') {
          const tab = {
            title: this.tabPre + this.tabIndex,
            name: '' + this.tabIndex,
            content: this.defaultSql,
          }
          this.listTab.push(tab);
          this.listTabValue = tab.name;
          this.tabIndex++
          this.initTabVal(tab)
          return tab
        }
        if (action === 'remove') {
          let tabs = this.listTab;
          this.list[this.listTabValue] = null
          let activeName = this.listTabValue;
          if (activeName === targetName) {
            tabs.forEach((tab, index) => {
              if (tab.name === targetName) {
                let nextTab = tabs[index + 1] || tabs[index - 1];
                if (nextTab) {
                  activeName = nextTab.name;
                }
              }
            });
          }

          this.listTabValue = activeName;
          this.listTab = tabs.filter(tab => tab.name !== targetName);
        }
      }
      
    }
  }
</script>

