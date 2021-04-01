<template>
  <div class="body-container" style="" v-el-table-infinite-scroll="loadMore">
    <el-table :data="data"
              :height="height + 'px'"

              ref="multipleTable"
              class="infinite-list-wrapper"
              @select="handleSelection"
              v-loading="loading">
      <el-table-column
        type="selection"
        width="55">
      </el-table-column>
      <el-table-column label="项目名称"  prop='name' 
         >
        <template slot-scope="scope">
          <el-button type="text" @click="go(scope.row.id)" size="mini">{{scope.row.name}}</el-button>
        </template>
      </el-table-column>

      <el-table-column
        label="项目负责人"
        width="150px" >
        <template slot-scope="scope">
          {{ scope.row.projectUser !== null ? scope.row.projectUser.realName : '' }}
        </template>
      </el-table-column>
      <el-table-column
        prop='circleBelongs'
        label="圈子"
        >
      </el-table-column>
    </el-table>
    <el-dialog
      v-dialogDrag
      width="70%"
      title="提示"
      @close="closeReport"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :visible.sync="reportDialogVisible"
      append-to-body>
      <ProjectReport :projectIds='projectIds'>
      </ProjectReport>
    </el-dialog>
  </div>
</template>

<script>
  import ProjectReport from './ProjectOpen';

  import {
    _queryPlatformOpenList
  } from '@/api/platformOpen';


  async function getList() {
    this.tableQuery.searchName = this.searchName;
    this.resetLoad();
    this.loading = true;
    const result = await _queryPlatformOpenList(this.tableQuery);
    if (result !== null && result.code === 1200) {
      this.tableData = result.data.projects;
      this.loadMore();
      this.loading = false;
    }
  }


  /**
   * 无线滚动
   */
  function resetLoad() {
    this.data = [];
    this.tableData = [];
    this.busy = false;
    this.currentPage = -1;
    this.pageSize = 15;
  }


  function isLaod() {
    const length = this.tableData.length;
    const pageSize = this.pageSize;
    const currentPage = this.currentPage;
    const _totalPage = parseInt(length / pageSize);
    const totalPage = length % pageSize > 0 ? _totalPage + 1 : _totalPage;
    return currentPage <= totalPage - 1;
  }


  function setCurrentPage() {
    const datas = this.tableData;
    const currentPage = this.currentPage;
    const pageSize = this.pageSize;
    const _total = (currentPage + 1) * pageSize;
    const total = _total < datas.length ? _total : datas.length;
    this.setData(currentPage * pageSize,
      total,
      datas);
  }

  function setData(n, total, datas) {
    for (var i = n; i < total; i++) {
      this.data.push(datas[i]);
    }

  }

  function loadMore() {
    this.$nextTick(() => {
      setTimeout(() => {
        this.currentPage += 1;
        this.busy = true;
        if (this.isLaod()) {
          this.setCurrentPage();
          this.busy = false;
        } else {
          this.busy = true;
        }
      }, 500);
      this.toggleRow();
    })
  }


  function toggleRow() {
    this.$nextTick(() => {
      this.data.forEach(d => {
        const _d = this.multipleSelection.filter(s => s.id === d.id);
        if (_d.length > 0) {
          this.$refs.multipleTable.toggleRowSelection(d, true);
        }
      })
    })
  }

  /**
   * 生成报表
   */
  function generate() {
    const projects = this.multipleSelection;
    if (projects.length === 0) {
      this.$message({
        type: 'info',
        message: '请选择项目!',
      });
    } else if (projects.length > 11) {
      this.$message({
        type: 'info',
        message: '选择的项目不能超过10个!',
      });
    } else {
      this.projectIds = [];
      this.projectIds = projects.map(p => p.id);
      this.reportDialogVisible = true;
    }
  }

  function closeReport() {
    this.reportDialogVisible = false;
  }

  function go(id) {
    this.$router.push({
      path: 'project',
      query: {id: id},
    });
  }

  function handleSelection(val) {
    this.multipleSelection = val;
  }

  export default {
    name: 'ProjectReportList',
    components: {
      ProjectReport,
    },
    props: {
      searchName: String
    },
    data() {
      return {
        tableData: [],
        menu: [],
        tableQuery: {
          searchName: '',
        },
        projectIds: [],
        reportDialogVisible: false,
        data: [],
        busy: false,
        currentPage: -1,
        pageSize: 20,
        multipleSelection: [],
        loading: false,
      };
    },

    methods: {
      getList,
      generate,
      closeReport,
      isLaod,
      setCurrentPage,
      setData,
      loadMore,
      resetLoad,
      go,
      handleSelection,
      toggleRow
    },
    created() {
      const menu = JSON.parse(localStorage.getItem('MENUS'))
        .filter(menu => menu.code === 'PROJECT_REPORT');
      this.menu = menu[0];
      this.getList();
    },
    mounted: function () {
      this.$nextTick(v => {
        this.height = window.innerHeight * 0.83
      })
    },
    watch: {
      searchName(val) {
        this.getList();
      },
      loading(val) {
        this.$emit("load", val);
      }
    },
    computed: {
      noMore() {
        return this.tableData.length === this.data.length
      },
      disabled() {
        return this.busy || this.noMore
      },

    },
  };
</script>


<style scoped>
  >>> .el-table th {
    background-color: #eee;
  }


  .page {
    position: absolute;
    bottom: 20px;
    left: 20px;
  }

  .infinite-list-wrapper {
    width: 99%;
  }

  .body-container {
    height: 100%;

  }


</style>

<style lang="scss" scoped>
  .open_dialog {
    display: flex;
    justify-content: center;
    align-items: Center;
    overflow: hidden;

    .el-dialog {
      margin: 0 auto !important;
      height: 80%;
      overflow: hidden;

      .el-dialog__body {
        position: absolute;
        // left: 0;
        top: 80px;
        bottom: 0;
        // right: 0;
        // padding: 0;
        z-index: 1;
        overflow: scroll;
      }
    }
  }
</style>
