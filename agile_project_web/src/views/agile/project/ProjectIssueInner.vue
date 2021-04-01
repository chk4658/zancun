<template>
  <div>
    <el-table class="maxh-table" :data="tableData" style="width: 100%">
      <el-table-column prop="project.name" label="项目名称"></el-table-column>
      <el-table-column prop="projectMilestone.name" label="里程碑名称"></el-table-column>
      <el-table-column prop="createUserDepartment.fullName" label="所在部门"></el-table-column>
      <el-table-column prop="description" label="问题描述"></el-table-column>
      <el-table-column prop="involved" label="涉及区域"></el-table-column>
      <el-table-column prop="source" label="来源"></el-table-column>
      <el-table-column prop="createUser.realName" label="创建人"></el-table-column>
      <el-table-column prop="createAt" label="创建时间"></el-table-column>

      <el-table-column width="90" align="right">
        <template slot="header">
          <i class="el-icon-circle-plus" style="font-size: 20px;position: relative;top: 2px;cursor: pointer;"
             @click="openIssue('')"></i>
        </template>
        <template slot-scope="scope">
          <i class="el-icon-edit" style="margin-right: 5px;" @click="openIssue(scope.row.id)"></i>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination @current-change="roleHandlePageChange" :current-page="this.tableQuery.page"
                   :page-size="this.tableQuery.size" style="text-align: left;
    padding-left: 21px;margin-top: 5px" background layout="total, prev, pager, next" :total="this.tableQuery.total">
    </el-pagination>

    <el-dialog @close="closeIssue" :visible.sync="projectIssueDialogVisible" width="860px" center append-to-body>
      <div slot="title" class="header-title">
        <span>{{projectIssueTitle}}</span>
      </div>
      <project-issue-edit :projectIssueFlag="projectIssueFlag" @end="closeIssue" :projectIssueId="projectIssueId" :projectId="projectId">
      </project-issue-edit>
    </el-dialog>

  </div>
</template>
<script>

  import {_findAllProjectIssue} from '@/api/projectIssue';
  import ProjectIssueEdit from "./projectIssue/ProjectIssueEdit";


  async function getProjectIssue() {
    this.tableQuery.projectId = this.projectId
    const result = await _findAllProjectIssue(this.tableQuery);
    this.tableData = result.code === 1200 ? result.data.projectIssueList : [];
    this.tableQuery.total = result.code === 1200 ? result.data.totalAmount : 0
  }


  function roleHandlePageChange(val) {
    this.tableQuery.page = val;
    this.getProjectIssue()

  }


  export default {
    name: 'ProjectIssueInner',
    props: {
      projectId: String,
      activeName: String,
      authority: Boolean,
    },
    components: {ProjectIssueEdit},
    data() {
      return {
        tableData: [],
        tableQuery: {
          page: 1,
          size: 15,
          total: 0,
          projectId: '',
        },


        // 项目的问题提出dialog相关
        projectIssueDialogVisible: false,
        projectIssueFlag: false,
        projectIssueId: '',
        projectIssueTitle: ""
      };
    },
    methods: {
      getProjectIssue,
      roleHandlePageChange,
      openIssue(id) {
        this.projectIssueTitle = id === '' ? '创建项目问题清单' : '查看项目问题清单'
        this.projectIssueDialogVisible = true;
        this.$nextTick(() => {
          this.projectIssueFlag = true
          this.projectIssueId = id
        })
      },
      closeIssue(isRefresh) {
        if (isRefresh === 'refresh') {
          this.$emit('save')
        }
        this.getProjectIssue()
        this.projectIssueDialogVisible = false;
        this.projectIssueFlag = false;
        this.projectIssueId = ''

      }
    },
    created() {
      if (this.activeName === 'projectIssue') {
        this.getProjectIssue();
      }
    },
    watch: {
      projectId() {
        this.getProjectIssue();
      },
      activeName(val) {
        if (val === 'projectIssue') {
          this.tableQuery = {
            page: 1,
            size: 15,
            total: 0,
            projectId: '',
          };
          this.getProjectIssue();
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
