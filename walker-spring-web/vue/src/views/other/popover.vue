<template>
  <div class="app-container">
    使用模板
    <template>
      提示框
      <el-popover
        placement="top-start"
        title="标题"
        width="200"
        trigger="hover"
        content="这是一段内容,这是一段内容,这是一段内容,这是一段内容。">
        <el-button slot="reference">hover 激活</el-button>
      </el-popover>

      <el-popover
        placement="bottom"
        title="标题"
        width="200"
        trigger="click"
        content="这是一段内容,这是一段内容,这是一段内容,这是一段内容。">
        <el-button slot="reference">click 激活</el-button>
      </el-popover>

      <el-popover
        ref="popover"
        placement="right"
        title="标题"
        width="200"
        trigger="focus"
        content="这是一段内容,这是一段内容,这是一段内容,这是一段内容。">
      </el-popover>
      <el-button v-popover:popover>focus 激活</el-button>

      <el-popover
        placement="bottom"
        title="标题"
        width="200"
        trigger="manual"
        content="这是一段内容,这是一段内容,这是一段内容,这是一段内容。"
        v-model="visible">
        <el-button slot="reference" @click="visible = !visible">手动激活</el-button>
      </el-popover>
    </template>


  </div>
</template>

<script>
export default {

  filters: {
    statusFilter(status) {
      const statusMap = {
        success: 'success',
        pending: 'danger'
      }
      return statusMap[status]
    },

  },
  data() {
    return {
      src: 's1,s2,s3',
      visible: true,
      name: 'name',
      value: 'value',
      region: 'region',
      date1: 'date1',
      date2: 'date2',
      delivery: false,
      type: ['type1', 'type2', 'type3'],
      resource: '',
      desc: ''
    }
  },
  methods: {
    onSubmit() {
      this.$message('submit!')
    },
    onCancel() {
      this.$message({
        message: 'cancel!',
        type: 'warning'
      })
    }
  }
}
</script>

<style scoped>
.line{
  text-align: center;
}
</style>

