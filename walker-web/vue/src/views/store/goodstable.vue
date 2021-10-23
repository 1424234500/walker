<template>
  <div class="app-container" >
    <mtable :props='showDialogLogParams' ></mtable>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        showDialogLogParams: {
          database: '',
          table: 'S_GOODS',
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

