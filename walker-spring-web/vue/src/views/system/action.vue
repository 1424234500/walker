<template>
  <div class="app-container" >
    <mtable :props='showDialogLogParams' ></mtable>
  </div>
</template>
<style scoped>
  /deep/  .el-table td{
    padding: 4px 0px;
  }
</style>



<script>

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
      showDialogLogParams: {
        database: '',
        table: 'W_ACTION',
        params: {
          //'ID' : val[this.colKey]
        },
      },


    }
  },
  created() {

  },
  methods: {

  }
}
</script>

