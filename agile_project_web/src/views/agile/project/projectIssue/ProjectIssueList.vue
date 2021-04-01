<template>
  <div style="width: 100%; height: 100%; display: flex; overflow-x: hidden">
    <div class="information">
      <main-top-part>
        <template v-slot:title>
          <div style="display: flex">
            <i :class="menu.icon" class="menu-icon" style="color: #01408b; font-size: 32px">
            </i>
            {{ menu.name }}
          </div>
        </template>
        <template v-slot:search>
          <div class="btn-search">
            <el-select v-model="tableQuery.projectId" filterable placeholder="请选择项目" size="small" clearable
                       :filter-method="filterProject"
                       @change="search" style="width: 328px; margin-left: 10px">
              <el-option v-for="item in projectOptions" :key="item.value" :label="item.label" :value="item.value">
              </el-option>
            </el-select>
          </div>
        </template>
        <template v-slot:button>
          <div style="margin-top: 20px;">
            <el-button style="
                border-radius: 18px;
                width: 130px;
                background-color: #ef5350;
                border-color: #ef5350;
                color: white;
              " size="small" @click="openIssue('')">新增项目问题
            </el-button>
          </div>
        </template>
      </main-top-part>

      <el-row style="margin: 30px 20px 80px; overflow-y: auto">
        <el-col :span="24">
          <el-table :data="tableData" class="maxh-table">
            <el-table-column prop="project.name" label="项目名称"></el-table-column>
            <el-table-column prop="projectMilestone.name" label="里程碑名称"></el-table-column>
            <el-table-column prop="createUserDepartment.fullName" label="所在部门"></el-table-column>
            <el-table-column prop="description" label="问题描述"></el-table-column>
            <el-table-column prop="involved" label="涉及区域"></el-table-column>
            <el-table-column prop="source" label="来源"></el-table-column>
            <el-table-column prop="createUser.realName" label="创建人"></el-table-column>
            <el-table-column prop="createAt" label="创建时间"></el-table-column>
            <el-table-column label="操作" width="100">
              <template slot-scope="scope">
                <el-button type="text" @click="openIssue(scope.row.id)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-col>
      </el-row>

      <el-row class="page">
        <el-col :span="24">
          <el-pagination @size-change="handleSizeChange" @current-change="handlePageChange" background
                         :current-page="this.tableQuery.page" :page-sizes="[15, 30, 50, 100]"
                         :page-size="this.tableQuery.size"
                         style="text-align: left" layout="total, sizes, prev, pager, next, jumper"
                         :total="this.tableQuery.total">
          </el-pagination>
        </el-col>
      </el-row>
      <el-dialog @close="closeIssue" :visible.sync="projectIssueDialogVisible" width="860px" center append-to-body>
        <div slot="title" class="header-title">
          <span>{{projectIssueTitle}}</span>
        </div>
        <project-issue-edit :projectIssueFlag="projectIssueFlag" @end="closeIssue" :projectIssueId="projectIssueId">
        </project-issue-edit>
      </el-dialog>
    </div>
  </div>
</template>

<script>
  import MainTopPart from "@/components/MainTopPart";
  import {_queryOwnerProjectList} from "@/api/projectApi";
  import {_findAllProjectIssue} from "@/api/projectIssue";
  import ProjectIssueEdit from "./ProjectIssueEdit";

  function search() {
    console.log(this.tableQuery.projectId);
    this.tableQuery.page = 1;
    this.findAllProjectIssue();
  }

  async function findAllProjectIssue() {
    const result = await _findAllProjectIssue(this.tableQuery);
    this.tableData = result.code === 1200 ? result.data.projectIssueList : [];
    this.tableQuery.total = result.code === 1200 ? result.data.totalAmount : 0
    console.log(result);
  }

  async function getMyProjects() {
    const result = await _queryOwnerProjectList({searchName: ""});
    this.projectOptions =
      result.code === 1200
        ? result.data.curUserProject.map((cp) => {
          return {
            value: cp.id,
            label: cp.name,
          };
        })
        : [];
  }

  function handlePageChange(val) {
    this.tableQuery.page = val;
    this.findAllProjectIssue();
  }

  function handleSizeChange(val) {
    this.tableQuery.size = val;
    this.findAllProjectIssue();
  }

  async function filterProject(searchName) {
    const result = await _queryOwnerProjectList({searchName})
    this.projectOptions = result.code === 1200 ? result.data.curUserProject.map(cp => {
      return {
        value: cp.id,
        label: cp.name
      }
    }) : []
  }

  export default {
    components: {
      ProjectIssueEdit,
      MainTopPart,
    },
    name: "projectIssueList",
    data() {
      return {
        menu: {},
        tableQuery: {
          projectId: "",
          page: 1,
          size: 15,
          total: 0,
        },
        projectOptions: [],
        tableData: [],

        // 项目的问题提出dialog相关
        projectIssueDialogVisible: false,
        projectIssueFlag: false,
        projectIssueId: "",
        projectIssueTitle: ""
      };
    },
    computed: {},
    created: function () {
      const menu = JSON.parse(localStorage.getItem("MENUS")).filter(
        (menu) => menu.code === "PROJECT_ISSUE_LIST"
      );
      console.log(menu);
      console.log(JSON.parse(localStorage.getItem("MENUS")));
      this.menu = menu[0];
      this.findAllProjectIssue();
    },
    mounted() {
      this.getMyProjects();
    },
    methods: {
      search,
      getMyProjects,
      findAllProjectIssue,
      handlePageChange,
      handleSizeChange,
      filterProject,
      openIssue(id) {
        this.projectIssueTitle = id === '' ? '创建项目问题清单' : '查看项目问题清单'
        this.projectIssueDialogVisible = true;
        this.$nextTick(() => {
          this.projectIssueFlag = true;
          this.projectIssueId = id;
        });
      },
      closeIssue(isRefresh) {
        if (isRefresh === "refresh") {
        }
        this.findAllProjectIssue();
        this.projectIssueDialogVisible = false;
        this.projectIssueFlag = false;
        this.projectIssueId = "";
      },
    },
    watch: {},
    directives: {},
  };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  >>> .el-table th {
    background-color: #eee;
  }

  .information {
    width: 100%;
    flex: 1;
    background-color: #fff;
    float: left;
    display: flex;
    flex-direction: column;
    position: relative;
    border-left: 1px solid #e1e1e1;
  }

  .page {
    position: absolute;
    bottom: 20px;
    left: 20px;
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
