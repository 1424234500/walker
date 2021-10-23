<template>
  <div class="app-container"
       v-loading="loadingCols"
  >
    <el-input v-model="filterText" placeholder="Filter keyword" style="margin-bottom:30px;" />

    <el-tree
      ref="tree"
      :data="data"
      lazy
      :load="loadNode"
      v-loading="loadingList"
      :props="defaultProps"
      @node-click="handleNodeClick"
      :filter-node-method="filterNode"
      class="filter-tree"
    />
<!--    show-checkbox-->
<!--    @check-change="handleCheckChange"-->
<!--    default-expand-all-->


  </div>
</template>

<script>
export default {
  created() {
    this.getColumns()
  },
  data() {
    return {
      filterText: '',
      data: [{
        id: 1,
        label: 'Level one 1',
        children: [{
          id: 4,
          label: 'Level two 1-1',
          children: [{
            id: 9,
            label: 'Level three 1-1-1'
          }, {
            id: 10,
            label: 'Level three 1-1-2'
          }]
        }]
      }, {
        id: 2,
        label: 'Level one 2',
        children: [{
          id: 5,
          label: 'Level two 2-1'
        }, {
          id: 6,
          label: 'Level two 2-2'
        }]
      }, {
        id: 3,
        label: 'Level one 3',
        children: [{
          id: 7,
          label: 'Level two 3-1'
        }, {
          id: 8,
          label: 'Level two 3-2'
        }]
      }],
      defaultProps: {
        children: 'children',
        label: 'label',
        isLeaf: 'isLeaf'
      },



      list: [],
      colMap: {},      //列名:别名
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
        shownum: 50,
      },
      loadingList: true,
      loadingCols: true,
      loadingSave: true,
      loadingUpdate: false,


      loadingRole: false,
      loadingUpdateRole: false,
      areaShowRole: {}, //当前角色用户
      colMapRole: {},
      colKeyRole: {},
      listRoleDept: [],
      rowSelectRole: [],
    }
  },
  watch: {
    filterText(val) {
      this.$refs.tree.filter(val)
    }
  },

  methods: {
    filterNode(value, data) {
      if (!value) return true
      return data.label.indexOf(value) !== -1
    },
//查询展示的行列信息 备注
    getColumns() {
      this.loadingCols = true
      this.get('/common/getColsMap.do', {}).then((res) => {
        this.colMap = res.data.colMap
         this.page.order = res.data.colMap['S_MTIME'] ? 'S_MTIME DESC' : ''
        this.colKey = res.data.colKey
        this.loadingCols = false
        this.getListPage()
      }).catch(() => {
        this.loadingCols = false
      })

    },
    loadNode(node, resolve) {

      console.log("loadNode", node);
      var data = node.data
      if(1==1 || data == null || data.EXT == 'dir' || !data.ID ){
        // resolve([{name: 'region'}])
        this.getListPage(data, resolve)
      }else{
        resolve([])
      }
    },
    handleNodeClick(row) {
      console.log("handleNodeClick", row);
    },

    //分页查询
    getListPage(data, resolve) {
      this.loadingList = true
      // name/nowPage/showNum
      var params = this.assign({nowPage: this.page.nowpage, showNum: this.page.shownum, order: this.page.order}, this.rowSearch)
      if(data == null || !data['ID']){
        params['P_ID_NULL'] = 'true' //root根规则
      }else {
        params['P_ID'] = data['ID']
      }
      this.get('/area/findPage.do', params).then((res) => {
        var list = res.data.data
        this.page = res.data.page

        for(var i = 0; i < list.length; i++){
          var obj = list[i]
          obj.id = obj.ID
          obj.label = obj.NAME + ' \t ' + obj.PATH_NAME + ' \t ' + obj.PATH
          obj.isLeaf = (obj.NAME.indexOf('居委') >= 0 ? true: false)
        }
        if(data == null){
          this.data = list
        }

        if(resolve != null){
          resolve(list)
        }
        this.loadingList = false
      }).catch(() => {
        this.loadingList = false
      })
    },
    handleCheckChange(data, checked, indeterminate) {
      console.log(data, checked, indeterminate);
    },


  }
}
</script>

