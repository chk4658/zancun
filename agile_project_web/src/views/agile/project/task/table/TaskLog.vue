<template>
  <div>
    <el-input
      size="small"
      placeholder="搜索"
      style="width: 300px;margin-left: 65%"
      @input="getTaskLogs('SEARCH')"
      v-model="tableQuery.searchName">
      <i slot="prefix" class="el-input__icon el-icon-search"></i>
    </el-input>
    <el-table
      :data="tableData"
      class="maxh-table"
      style="width: 100%">
      <el-table-column width="120"
                       prop="createAt"
                       label="操作时间"
      >
      </el-table-column>
      <el-table-column
        prop="content"
        label="内容">
      </el-table-column>
      <el-table-column label="人员" width="70">
        <template slot-scope="scope">
          <span>{{scope.row.createSysUser===null?scope.row.createSysUser:scope.row.createSysUser.realName}}</span>
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


  import {_listTaskLogs} from '@/api/taskLog';


  async function getTaskLogs(a) {
    if (a === 'SEARCH') {
      this.tableQuery.page = 1;
    }
    this.tableQuery.taskId = this.taskId;
    const result = await _listTaskLogs(this.tableQuery);
    if (result.code === 1200) {
      console.log(result.data.taskLogs)
      this.tableData = result.data.taskLogs;
      this.tableQuery.total = result.data.totalAmount;
    }
  }

  function handlePageChange(val) {
    this.tableQuery.page = val;
    this.getTaskLogs();
  }

  export default {
    name: 'TaskLog',
    props: {
      taskId: String,
      activeName: String,
      refresh: Boolean
    },
    data() {
      return {
        tableData: [],
        tableQuery: {
          page: 1,
          size: 15,
          total: 0,
          taskId: "",
          searchName: ''
        },
      }
    },
    created: function () {
      this.getTaskLogs();

    },
    methods: {
      handlePageChange,
      getTaskLogs,
    },
    watch: {
      taskId(val) {
        this.getTaskLogs();
      },
      activeName(val) {
        if (val === 'taskBlog') {
          this.getTaskLogs();
        }
      },
      refresh(val) {
        if (this.activeName === 'taskBlog') {
          this.getTaskLogs();
        }
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
