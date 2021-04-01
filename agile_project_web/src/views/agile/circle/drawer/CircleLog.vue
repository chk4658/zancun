<template>
  <div :style="{maxHeight: visibleHeight + 'px'}">

    <el-input
      size="small"
      placeholder="搜索"
      @input="search"
      style="width: 300px;margin-left: 65%"
      v-model="tableQuery.searchName">
      <i slot="prefix" class="el-input__icon el-icon-search"></i>
    </el-input>
    <el-table
      :data="tableData"
      class="maxh-table"
      style="width: 100%">
      <el-table-column width="130"
        prop="createAt"
        label="操作时间"
        >
      </el-table-column>
      <el-table-column
        prop="content"
        label="内容">
      </el-table-column>
      <el-table-column label="人员"  width="70">
        <template slot-scope="scope">
          <div>{{scope.row.createSysUser===null?'':scope.row.createSysUser.realName}}</div>
        </template>
      </el-table-column>
    </el-table>
      <el-pagination
      @current-change="handlePageChange"
      :current-page="this.tableQuery.page"
      :page-size="this.tableQuery.size"
      background
      style="text-align: left;
      padding-left: 21px;margin-top: 5px"
      layout="total, prev, pager, next"
      :total="this.tableQuery.total">
    </el-pagination>
  </div>
</template>

<script>


import { _listCircleLogs } from '@/api/circleLog';


function search() {
  this.tableQuery.page = 1;
  this.getCircleLogs();
}


async function getCircleLogs() {

  this.tableQuery.circleId = this.circleId;
  const result = await _listCircleLogs(this.tableQuery);
  console.log(result)
  if (result.code === 1200) {
    this.tableData = result.data.circleLogs;
    this.tableQuery.total = result.data.totalAmount;
  }
}

function handlePageChange(val) {
  this.tableQuery.page = val;
  this.getCircleLogs()
}

export default {
  name: 'CircleLog',
  props: {
    circleId: String
  },
  data () {
    return {
      tableData: [],
      tableQuery: {
        searchName: '',
        page: 1,
        size: 15,
        total: 0,
        circleId: "",
      },
    }
  },
  computed: {
    visibleHeight: function () {
      const browserHeight = window.innerHeight;
      const headerHeight = 160;
      return (browserHeight*0.85);
    },
  },
  created: function () {
    this.getCircleLogs();
  },
  beforeMount: function () {},
  mounted: function () {},
  beforeDestroy: function () {},
  destroyed: function () {},
  methods: {
    search,
    handlePageChange,
    getCircleLogs,
  },
  watch: {
    circleId(val) {
      this.getCircleLogs();
    }
  },
  directives: {}
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss">
  .maxh-table {

    .cell {
      overflow: visible;
      height: 100%;
    }

  }
</style>
