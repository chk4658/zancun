<template>
  <div>

    <el-table
      class="maxh-table"
      :data="tableData"
      style="width: 100%">
      <el-table-column
        prop="name"
        label="归档任务名称">
      </el-table-column>
      <el-table-column
        align="center"
        prop="estEndTime"
        label="截止时间">
      </el-table-column>

      <el-table-column
        align="center"
        prop="actEndTime"
        label="实际结束时间">
      </el-table-column>
    </el-table>
    <el-pagination
      @current-change="archHandlePageChange"
      :current-page="this.tableQuery.page"
      :page-size="this.tableQuery.size"
      style="text-align: left;
    padding-left: 21px;margin-top: 5px"
      background
      layout="total, prev, pager, next"
      :total="this.tableQuery.total">
    </el-pagination>


  </div>
</template>
<script>
  import {_findStMarkTaskByProjectId} from '@/api/ArchivingTaskApi';

  function archHandlePageChange(val) {

    this.tableQuery.page = val;
    this.findStMarkTaskByProjectId();
  }


  async function findStMarkTaskByProjectId() {


    this.tableQuery.projectId = this.projectId;
    const result = await _findStMarkTaskByProjectId(this.tableQuery)

    this.tableData = result.data.stMarkTaskList;
    this.tableQuery.total = result.data.totalAmount;
  }


  export default {
    name: 'ArchivingTask',
    props: {
      projectId: {},
      activeName: String,
    },
    components: {},
    data() {
      return {
        tableData: [],
        tableQuery: {
          page: 1,
          size: 15,
          total: 0,
          projectId: '',
        },
      };
    },
    methods: {
      archHandlePageChange,
      findStMarkTaskByProjectId
    },
    created() {

      if (this.activeName === 'archivingTask') {
        this.findStMarkTaskByProjectId();
      }
    },
    watch: {
      projectId() {
      },
      activeName(val) {
        if (val === 'archivingTask') {
          this.findStMarkTaskByProjectId()
        }
      }
    },
  };
</script>
<style>
  .el-table__column-filter-trigger {
    line-height: 23px;
  }
</style>
<style lang="scss">
  .maxh-table {

    .cell {
      overflow: visible;
      height: 100%;
    }

  }
</style>
