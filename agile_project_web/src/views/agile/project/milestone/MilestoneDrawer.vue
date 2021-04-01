<template>
  <div style="margin-left: 30px;margin-right: 30px;">
    <h1 ref="header" style="font-size: 20px">{{milestone.name}}</h1>

    <el-input
      size="small"
      placeholder="搜索"
      style="width: 300px;margin-left: 65%"
      @input="getMilestonesLogs('SEARCH')"
      v-model="tableQuery.searchName">
      <i slot="prefix" class="el-input__icon el-icon-search"></i>
    </el-input>

    <div :style="{maxHeight: ownHeight + 'px'}" style="overflow-y: auto">

      <el-table
        :data="tableData"
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
            <span>{{scope.row.createSysUser.realName}}</span>
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

  </div>
</template>

<script>


  import {_listMilestonesLogs} from '@/api/taskLog';


  async function getMilestonesLogs(a) {
    if (a === 'SEARCH') {
      this.tableQuery.page = 1;
    }
    this.tableQuery.projectMilestoneId = this.milestone.id;
    const result = await _listMilestonesLogs(this.tableQuery);
    if (result.code === 1200) {
      this.tableData = result.data.projectMilestoneLogs;
      this.tableQuery.total = result.data.totalAmount;
    }
  }

  function handlePageChange(val) {
    this.tableQuery.page = val;
    this.getMilestonesLogs();
  }

  export default {
    name: 'MilestoneDrawer',
    props: {
      milestone: Object,
      refresh: Boolean
    },
    data() {
      return {
        tableData: [],
        tableQuery: {
          page: 1,
          size: 15,
          total: 0,
          projectMilestoneId: "",
          searchName: ''
        },
        ownHeight: 100,
        flag: false
      }
    },
    created: function () {
      this.getMilestonesLogs();
    },
    methods: {
      handlePageChange,
      getMilestonesLogs,
    },
    watch: {
      refresh(val) {
        if (val) {
          this.getMilestonesLogs();
        }

        this.flag = false
        this.ownHeight = window.innerHeight - this.$refs.header.offsetHeight - 100
        this.flag = true

      }
    },
    directives: {},
    mounted() {
      this.ownHeight = window.innerHeight - this.$refs.header.offsetHeight - 100
      this.flag = true
    }
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
  .el-tabs__item:focus.is-active.is-focus:not(:active) {
    box-shadow: none;
    -webkit-box-shadow: none;
  }

  .el-drawer:focus {
    outline: none;
  }
</style>
<!--<style lang="scss">-->
<!--  .el-drawer {-->

<!--    overflow-y: scroll-->
<!--  }-->

<!--</style>-->
