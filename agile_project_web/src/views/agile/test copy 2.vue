<template>

  <div id="app">
    <div class="aaaa">
      <el-table
        :data="tableData"
        style="width: 100%">
    </el-table>

    </div>
  </div>
</template>



<script>

import mammoth from 'mammoth'
import XLSX from 'xlsx'


  export default {
    name: "DEMO",
    data () {
      return {
       vHtml: '',
       tableData: [],
      }
    },
    created() {
      const that = this
      var blob = null;
      var xhr = new XMLHttpRequest();
      xhr.open("GET", "/api/data/20210302/uid00857/E2UB后地板三大文件20181109.xlsx",true);
      xhr.responseType = "arraybuffer";//force the HTTP response, response-type header to be blob
      xhr.onload = function()
      {
          var data = new Uint8Array(xhr.response);
          var workbook = XLSX.read(data, {type: 'array'})
          console.log(workbook)
          var sheetNames = workbook.SheetNames // 工作表名称集合
          var worksheet = workbook.Sheets[sheetNames[0]];
          this.tableData = XLSX.utils.sheet_to_json(worksheet)
      }
      xhr.send();
    },
    computed: {

    },
    methods: {
      
    }
  }
</script>

<style lang="scss" scoped>
  .infinite-list-wrapper {
    width: 100%;
    min-height: 40px;
    max-height: 100px;
  }
  .aaaa{

    overflow: scroll;

  }
</style>
