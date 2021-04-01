<template>
  <div>

    <el-input
      size="small"
      placeholder="搜索"
      style="width: 300px;margin-left: 65%"
      @input="getProjectLogs('SEARCH')"
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


  import {_listProjectLogs} from '@/api/projectLog';


  async function getProjectLogs(a) {
    if (a === 'SEARCH') {
      this.tableQuery.page = 1;
    }
    this.tableQuery.projectId = this.projectId;
    const result = await _listProjectLogs(this.tableQuery);
    if (result.code === 1200) {
      this.tableData = result.data.projectLogs;
      this.tableQuery.total = result.data.totalAmount;
    }
  }

  function handlePageChange(val) {
    this.tableQuery.page = val;
    this.getProjectLogs();
  }

  export default {
    name: 'ProjectLog',
    props: {
      projectId: String,
      activeName: String,
    },
    data() {
      return {
        tableData: [],
        tableQuery: {
          page: 1,
          size: 15,
          total: 0,
          projectId: "",
          searchName: ''
        },
      }
    },
    created: function () {
      if (this.activeName === 'blog') {
        this.getProjectLogs();
      }
    },
    methods: {
      handlePageChange,
      getProjectLogs,
    },
    watch: {
      projectId(val) {
        this.getProjectLogs();
      },
      activeName(val) {
        if (val === 'blog') {
          this.getProjectLogs();
        }
      }
    },
    directives: {},
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
