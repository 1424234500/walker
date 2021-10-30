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

      loadingList: true,
      loadingCols: true,
      dir: '/',
      dataGet : null, //当前节点
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
      this.get('/file/getColsMap.do', {}).then((res) => {
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
      if(data == null || data.EXT === 'dir'){
        // resolve([{name: 'region'}])
        this.getListPage(data, resolve)
      }else{
        resolve([])
      }
    },
    handleNodeClick(row) {
      console.log("handleNodeClick", row);
      if(row.EXT!='dir'){
        this.downPath(row.PATH, {}, row.NAME)
      }
    },

    //分页查询
    getListPage(data, resolve) {
      this.loadingList = true
      // name/nowPage/showNum
      var params = this.assign({dir : data == null ? '/' : data.PATH }, this.rowSearch)
      this.get('/file/dir.do', params).then((res) => {
        for(var i = 0; i < res.data.list.length; i++){
          var obj = res.data.list[i]
          obj.id = obj.PATH
          obj.label = obj.NAME
          obj.isLeaf = (obj.EXT == 'dir' ? false: true)
        }
        if(data == null){
          this.data = res.data.list
        }

        if(resolve != null){
          resolve(res.data.list)
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

