<template>
  <div class="body">
    <div class="header">
      <el-row style="margin-bottom: 10px">
        <el-col>
          <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item v-for="item in project.parentsCircles" :key="item.id"
                                :to="{ path: 'circle', query: { id: item.id } }">
              {{ item.name }}
            </el-breadcrumb-item>
            <el-breadcrumb-item v-if="project.parentsCircles.length === 0"
                                :to="{ path: 'circle', query: { id: project.circle.id } }">
              {{ project.circle.name }}
            </el-breadcrumb-item>
            <el-breadcrumb-item>{{ project.projectName }}</el-breadcrumb-item>
          </el-breadcrumb>
        </el-col>
      </el-row>
      <el-row style="margin-bottom: 10px">
        <el-col :span="10">
          <div class="grid-content bg-purple" style="
              font-size: 29px;
              font-weight: normal;
              float: left;
              height: 40px;
              line-height: 40px;
            " v-if="!projectNameVisible">
            <span @click="changeProjectName" class="text-style">{{
              project.projectName
              }}</span>
            <i class="el-icon-star-off" v-if="flag" @click="addOrCancelCollection"
               style="cursor: pointer; color: #ccc"></i>
            <i class="el-icon-star-on" v-if="!flag" @click="addOrCancelCollection"
               style="cursor: pointer; color: #1a73e8"></i>
          </div>
          <el-input v-if="projectNameVisible" v-model="project.projectName" maxlength="50" class="pro-name"
                    show-word-limit v-focus="projectNameVisible" @blur="updateProject(1)" size="medium"
                    style="width: 100%; float: left"></el-input>
        </el-col>
        <el-col :span="14">
          <div class="grid-content bg-purple" style="height: 40px; float: right; margin-left: 10px">
            <el-popover placement="left-start" width="180" :visible-arrow="false" v-model="visible">
              <el-row>
                <el-col>
                  <el-menu :default-active="defaultActive" class="el-menu-vertical-demo">
                    <!-- <el-menu-item index="ADD"
                                  @click="goProject('ADD')"
                    >
                      <i class="el-icon-circle-plus-outline"></i>
                      <span slot="title">新增</span>
                    </el-menu-item> -->
                    <el-menu-item index="EDIT" v-if="authority" @click="goProject('EDIT')">
                      <i class="el-icon-edit-outline"></i>
                      <span slot="title">编辑</span>
                    </el-menu-item>
                    <el-menu-item index="DELETE" v-if="authority" @click="goProject('DELETE')">
                      <i class="el-icon-delete"></i>
                      <span slot="title">删除</span>
                    </el-menu-item>

                    <el-menu-item index="ONLINE" v-if="authority && project.hasOnLine !== 1" @click="onLine(1)">
                      <i class="el-icon-remove-outline"></i>
                      <span slot="title">上线</span>
                    </el-menu-item>
                    <el-menu-item index="NOT_ONLINE" v-if="authority && project.hasOnLine === 1" @click="onLine(0)">
                      <i class="el-icon-remove-outline"></i>
                      <span slot="title">下线</span>
                    </el-menu-item>

                    <el-menu-item index="FORBIDDEN" v-if="authority" @click="onLine(2)">
                      <i class="el-icon-circle-close"></i>
                      <span slot="title">禁用项目</span>
                    </el-menu-item>

                    <el-menu-item index="SUSPEND" v-if="authority" @click="onLine(3)">
                      <i class="el-icon-video-pause"></i>
                      <span slot="title">暂停项目</span>
                    </el-menu-item>

                    <el-menu-item index="FORBIDDEN_TESK" v-if="authority" @click="openDrawer('notEnable')">
                      <i class="el-icon-turn-off"></i>
                      <span slot="title">未启用任务</span>
                    </el-menu-item>

                    <el-menu-item index="SUSPEND_TESK" v-if="authority" @click="openDrawer('forbidden')">
                      <i class="el-icon-video-pause"></i>
                      <span slot="title">禁用任务</span>
                    </el-menu-item>

                    <el-menu-item index="BLOG" @click="openDrawer('blog')">
                      <i class="el-icon-notebook-1"></i>
                      <span slot="title">日志</span>
                    </el-menu-item>

                    <el-menu-item index="CHAT" @click="openDrawer('chat')">
                      <i class="el-icon-microphone"></i>
                      <span slot="title">交流</span>
                    </el-menu-item>
                    <el-menu-item index="PROJECT_ST_MARK"
                                  v-if="authority && project.status === '1'"
                                  @click="projectStMark()">
                      <i class="el-icon-folder"></i>
                      <span slot="title">归档项目</span>
                    </el-menu-item>

                    <el-popover placement="left" title="请选择导出字段" width="500" trigger="click">
                      <span slot="reference">
                        <el-menu-item index="DOWNLOAD" @click="changeSelect">
                          <i class="el-icon-download"></i>导出
                        </el-menu-item>
                      </span>
                      <template>
                        <el-checkbox :indeterminate="isIndeterminate" v-model="checkAll" @change="handleCheckAllChange">
                          全选
                        </el-checkbox>
                        <div style="margin: 15px 0"></div>
                        <el-checkbox-group v-model="checkedCities" @change="handleCheckedCitiesChange">
                          <el-checkbox v-for="(val, key) in this.headMap" :label="key" :key="key" style="width: 70px">
                            {{ val }}
                          </el-checkbox>
                        </el-checkbox-group>
                      </template>
                      <el-col :span="2">
                        <el-button type="text" icon="el-icon-download" style="margin-top: 10px" @click="tableToExcel">
                          导出报表
                        </el-button>
                      </el-col>
                    </el-popover>
                  </el-menu>
                </el-col>
              </el-row>

              <el-button type="text" style="font-size: 23px" slot="reference"><i class="el-icon-more"></i></el-button>
            </el-popover>
          </div>

          <div class="grid-content bg-purple-light" style="height: 40px; float: right; margin-left: 15px"
               @click="openDrawer('role')">
            <el-button size="medium" style="border-radius: 5px; font-size: 17px">
              <i class="fa fa-users"></i>/{{ project.total.role }}
            </el-button>
          </div>

          <div class="grid-content bg-purple-light" style="height: 40px; float: right; margin-left: 15px"
               @click="openDrawer('delivery')">
            <el-button size="medium" style="border-radius: 5px; font-size: 17px">
              <i class="el-icon-document-copy"></i>/{{ project.total.delivery }}
            </el-button>
          </div>

          <div class="grid-content bg-purple-light" style="height: 40px; float: right; margin-left: 15px"
               @click="openDrawer('projectIssue')">
            <el-button size="medium" style="border-radius: 5px; font-size: 17px">
              <i class="el-icon-s-order"></i>/{{project.total.issue}}
              <!--              项目问题数量-->
            </el-button>
          </div>

          <div class="user-info" style="
              height: 40px;
              line-height: 40px;
              float: right;
              margin-left: 15px;
            ">
            <i class="fa fa-user-circle" aria-hidden="true" style="color: #344e75"></i>
            <span style="margin-left: 5px">{{
              project.projectUser.realName
              }}</span>
          </div>

          <div class="grid-content bg-purple-light" style="height: 38px; float: right; margin-left: 15px">
            <div :style="{ backgroundColor: referStatus.color }" style="
                width: 20px;
                height: 20px;
                color: #fff;
                text-align: center;
                border-radius: 10px;
                margin-top: 10px;
              ">
              <!--              {{referStatus.name}}<span v-if="referStatus.name===''">暂空</span>-->
            </div>
          </div>

          <div class="grid-content bg-purple-light" style="height: 38px; float: right; margin-left: 15px">
            <div style="
                width: 138px;
                height: 40px;
                text-align: center;
                line-height: 40px;
                border: 1px #e1e1e1 solid;
                border-radius: 100px;
              ">
              {{ project.estEndTime }}
            </div>
          </div>
        </el-col>
      </el-row>

      <el-row style="margin-top: 8px">
        <el-col :span="18">
          <p v-if="
              !descriptionVisible &&
              project.description !== null &&
              project.description !== ''
            " ref="pStyle" :style="{ maxHeight: showDetailBtn.height }" @click="changeProjectDescription" style="
              word-break: break-all;
              font-family: 'Microsoft YaHei';
              font-size: 14px;
              line-height: 1.5;
              white-space: pre-wrap;
              word-wrap: break-word;
              overflow: hidden;
            " class="text-style">
            {{ project.description }}
          </p>

          <p v-if="
              !descriptionVisible &&
              (project.description === null || project.description === '')
            " style="
              color: #cccccc;
              font-family: 'Microsoft YaHei';
              font-size: 14px;
              line-height: 1.5;
            " class="text-style" @click="changeProjectDescription">
            添加描述
          </p>

          <el-button @click="showMore" type="text" v-if="showDetailBtn.desc && !descriptionVisible">
            {{ showDetailBtn.text }}
            <i class="el-icon-arrow-down" v-if="showDetailBtn.direction === 'down'"></i>
            <i class="el-icon-arrow-up" v-if="showDetailBtn.direction === 'up'"></i>
          </el-button>
          <el-input type="textarea" autosize class="cancel-padding" v-if="descriptionVisible"
                    v-focus="descriptionVisible" v-model="project.description" style="
              width: 100%;
              margin-block-start: 1em;
              margin-block-end: 1em;
              font-family: 'Microsoft YaHei';
              font-size: 14px;
            " @blur="updateProject(2)"></el-input>
        </el-col>
      </el-row>

      <el-row style="margin-top: 15px">
        <el-col :span="24">
          <div class="grid-content bg-purple" style="height: 36px; line-height: 36px; float: left">
            <dict-popover dict-code="TASK_VIEW" v-model="currentId" position="bottom-start" width="150" :hasArrow="true"
                          @update="changeStyle"></dict-popover>

            <el-button type="text" @click="bulletinTypeChoose = true" size="medium" class="is_hover"
                       v-if="currentId === '2'" style="margin-left: 20px; width: 60px; text-align: center">设置
            </el-button>
          </div>

          <div class="grid-content bg-purple" style="height: 36px; float: right; margin-left: 10px">
            <el-autocomplete class="inline-input" v-model="state1" clearable size="medium"
                             style="float: left; width: 100px" :fetch-suggestions="querySearch" placeholder="人员"
                             @select="handleSelect"></el-autocomplete>

            <el-input size="medium" placeholder="请输入内容" v-model="searchValue" style="width: 328px; float: left"><i
              slot="prefix" class="el-input__icon el-icon-search"></i>
            </el-input>
          </div>

          <div style="
              display: flex;
              justify-content: flex-end;
              float: right;
              margin-left: 10px;
            ">
            <el-button type="primary" class="round" size="medium" :disabled="!authority || currentId !== '1'"
                       @click="addMilestone">
              新增里程碑
            </el-button>
            <el-popover placement="bottom-start" width="150" :visible-arrow="false" :disabled="!authority"
                        v-model="addVisible">
              <div class="show_style_label" @click="addATask" style="padding-left: 10px" v-if="mSize !== 0">
                新增任务
              </div>
              <div class="show_style_label" @click="importDialogVisible = true" style="padding-left: 10px">
                模板导入
              </div>
              <div class="show_style_label" @click="downloadExcel" style="padding-left: 10px">
                EXCEL模板下载
              </div>
              <div class="show_style_label" style="padding-left: 10px">
                <el-upload action="/api/upload" :headers="{
                    token: token,
                  }" :show-file-list="false" accept=".xls,.xlsx" :on-success="uploadSuccess">
                  <div @click="addVisible = false">EXCEL导入</div>
                </el-upload>
              </div>
              <i class="el-icon-arrow-down add-icon" :class="{ banned: !authority }" style="color: #fff"
                 slot="reference"></i>
            </el-popover>
          </div>

          <div style="height: 36px; float: right; margin-left: 10px" v-if="authority">
            <el-button style="
                border-radius: 18px;
                width: 130px;
                background-color: #ef5350;
                border-color: #ef5350;
                color: white;
              " size="medium" @click="openIssue">
              提出问题
            </el-button>
          </div>
        </el-col>
      </el-row>
      <!--抽屉-->
      <el-drawer title="" :visible.sync="projectDrawerVisible" :with-header="false" @close="activeName = ''" size="50%">
        <project-drawer :project="project" :active="activeName" :authority="authority" @fun="getDrawer"
                        :refresh="projectDrawerVisible"></project-drawer>
      </el-drawer>

      <!--抽屉-->
      <el-drawer title="" :visible.sync="bulletinTypeChoose" :with-header="false" size="280px">
        <div style="margin-left: 30px; margin-right: 30px; margin-top: 50px">
          <h1 style="font-size: 20px">看板显示形式</h1>

          <div style="margin-top: 50px">
            <div class="show_style_label" :style="{
                'background-color': bulletinType === '1' ? '#e0e0e0' : '',
              }" @click="changeBulletinType('1')">
              <div style="
                  margin-left: 5px;
                  margin-right: 5px;
                  width: 15px;
                  display: inline-block;
                ">
                <i class="el-icon-check" v-if="bulletinType === '1'"></i>
              </div>
              状态
            </div>
            <div class="show_style_label" :style="{
                'background-color': bulletinType === '2' ? '#e0e0e0' : '',
              }" style="margin-top: 1px" @click="changeBulletinType('2')">
              <div style="
                  margin-left: 5px;
                  margin-right: 5px;
                  width: 15px;
                  display: inline-block;
                ">
                <i class="el-icon-check" v-if="bulletinType === '2'"></i>
              </div>
              优先级
            </div>
          </div>

          <div style="margin-top: 50px">
            <el-checkbox v-model="isShowMilestone" border size="medium" style="padding: 9px 20px">按里程碑划分</el-checkbox>
          </div>
        </div>
      </el-drawer>

      <el-dialog :title="projectAdd.id === '' ? '新增项目' : '编辑项目'" @close="closeProject"
                 :visible.sync="projectAdd.dialogVisible" append-to-body>
        <project-add @saveProject="saveProject" :projectId="projectAdd.id" :parentCircle="project.circle"
                     :projectType="projectAdd.type"></project-add>
      </el-dialog>

      <!-- 导入项目 -->
      <el-dialog title="模板导入" @close="importDialogVisible = false" :visible.sync="importDialogVisible" append-to-body>
        <ProjectTelImport @getNodes="getNodes"></ProjectTelImport>
      </el-dialog>

      <el-dialog @close="closeIssue" :visible.sync="projectIssueDialogVisible" width="860px" center append-to-body>
        <div slot="title" class="header-title">
          <span>创建项目问题清单</span>
        </div>
        <project-issue-edit :projectIssueFlag="projectIssueFlag" @end="closeIssue" :projectIssueId="projectIssueId"
                            :projectId="id">
        </project-issue-edit>
      </el-dialog>
    </div>
    <div style="
        width: 100%;
        box-sizing: content-box;
        margin-right: 20px;
        display: flex;
        overflow: hidden;
      ">
      <div v-if="currentId === '1'" style="height: 100%; width: 20px; background-color: #fff; z-index: 999"></div>
      <table-style ref="tableStyle" v-if="currentId === '1'" :searchValue="searchValue" :person="state1"
                   :authority="authority" :reBeginAuthority="reBeginAuthority" :isAddTask="isAddTask"
                   :rolePlus="rolePlus"
                   :project="project"></table-style>
      <bulletin-board v-if="currentId === '2' && !isShowMilestone" :isAddTask="isAddTask" :mSize="mSize"
                      :person="state1" :bulletinType="bulletinType" :authority="authority" :project="project"
                      :reBeginAuthority="reBeginAuthority" :searchValue="searchValue"
                      :rolePlus="rolePlus"></bulletin-board>
      <bulletin-board-milestone v-if="currentId === '2' && isShowMilestone" :isAddTask="isAddTask" :mSize="mSize"
                                :person="state1" :bulletinType="bulletinType" :authority="authority"
                                :reBeginAuthority="reBeginAuthority"
                                :searchValue="searchValue" :rolePlus="rolePlus"></bulletin-board-milestone>
      <date v-if="currentId === '3'" :searchValue="searchValue" :authority="authority" :person="state1"
            :reBeginAuthority="reBeginAuthority" :rolePlus="rolePlus" :project="project"></date>

      <gant v-if="currentId === '4'" :authority="authority" :searchValue="searchValue" :person="state1"
            :reBeginAuthority="reBeginAuthority" :project="project" :rolePlus="rolePlus"></gant>
    </div>
  </div>
</template>
<script>
  import TableStyle from "./task/table/TableStyle.vue";
  import BulletinBoard from "./task/kanban/BulletinBoard.vue";
  import Gant from "./task/gantt/Gantt.vue";
  import ProjectDrawer from "./ProjectDrawer";
  import DictPopover from "@/components/DictPopover";
  import BulletinBoardMilestone from "./task/kanban/BulletinBoardMilestone";
  import {
    _getProjectById,
    _updateProject,
    _deleteProject,
    _queryAllProjectList,
    _updateProjectOnLine,
    _importProject,
    _getDeliveryCount,
    _getProjectRole,
    _letProjectToStMark
  } from "@/api/projectApi";
  import {_findAllProjectIssue} from '@/api/projectIssue';
  import {_addOrCancelCollection} from "@/api/projectCollectionApi";
  import {_queryCircle} from "@/api/circleApi";
  import ProjectAdd from "../project/ProjectAdd";
  import {_listProjectRoles} from "@/api/projectRole";
  import {_listTaskDeliveries} from "@/api/taskDeliveryApi.js";
  import moment from "moment";
  import {_analysisExcel} from "@/api/excelDataApi";

  import {_listInRedis} from "../../../api/projectApi";
  import {_getToPut} from "../../../utils/taskUtils";

  import ProjectTelImport from "./ProjectTelImport";
  import Date from "./task/date/Date";

  import {downloadFile} from "@/api/utils";
  import ProjectIssueEdit from "./projectIssue/ProjectIssueEdit";

  async function getProjectById(a) {
    const resultPro = await _getProjectById(a);
    if (resultPro.code === 1200) {
      this.project.projectId = a;
      this.project.projectUser =
        resultPro.data.project.projectUser === null
          ? {
            id: "",
            realName: "",
          }
          : resultPro.data.project.projectUser;
      this.project.projectName = resultPro.data.project.name;
      this.project.description = resultPro.data.project.description;
      this.project.status = resultPro.data.project.status;
      this.project.estEndTime = moment(
        resultPro.data.project.estEndTime
      ).format("YYYY-MM-DD");
      // this.project.total.role = resultPro.data.roleCount;
      // this.rolePlus = resultPro.data.rolePlus;
      // this.persons = this.loadAll(resultPro.data.rolePlus);
      // this.project.total.delivery = resultPro.data.deliveryCount;
      this.project.hasOnLine = resultPro.data.project.hasOnLine;

      // 父级圈子面包屑

      this.project.circleId = resultPro.data.project.circleId;
      this.project.type = resultPro.data.project.type;
      this.project.parentsCircles = resultPro.data.parentsCircles;
      this.project.circle = resultPro.data.circle;
      this.project.hasOnLine = resultPro.data.project.hasOnLine;
      this.project.stMark = resultPro.data.project.stMark;

      const projectStatus =
        this.project.status === "4" ? "1" : this.project.status;

      const task = this.dateStatusList2.filter(
        (dateStatus) => dateStatus.code === projectStatus
      )[0];

      this.referStatus =
        task === undefined ? {name: "", color: "#ccc"} : task;

      // 圈长，项目负责人，创建人权限
      const authorityUser = JSON.parse(
        JSON.stringify([
          this.project.circle.ownerUid,
          this.project.projectUser.id,
          resultPro.data.project.createUserId,
        ])
      );
      this.userId = localStorage.getItem("id");

      this.authority = (this.project.stMark === null || this.project.stMark !== 1) &&
        (authorityUser.indexOf(this.userId) !== -1 || localStorage.getItem("isAdmin") === "true");

      // 只有圈长和项目经理(项目负责人)有重新开始任务的权限

      const reBeginArr = [
        this.project.circle.ownerUid,
        this.project.projectUser.id,
      ];
      this.reBeginAuthority =
        reBeginArr.indexOf(this.userId) !== -1 ||
        localStorage.getItem("isAdmin") === "true"
          ? true
          : false;
    }
  }


  // 自动刷新交付物数量
  async function refreshDelivery() {
    const result = await _getDeliveryCount(this.id);

    this.project.total.delivery =
      result.code === 1200 ? result.data.deliveryCount : 0;
  }

  // 自动刷新角色数量
  async function refreshRole() {
    const result = await _getProjectRole(this.id);
    if (result.code === 1200) {
      this.project.total.role = result.data.roleCount;
      this.rolePlus = result.data.rolePlus;
      this.persons = this.loadAll(result.data.rolePlus);
    }
  }

  async function refreshProjectIssue() {
    const result = await _findAllProjectIssue({projectId: this.id});
    this.project.total.issue = result.code === 1200 ? result.data.totalAmount : 0
  }

  async function refreshEndTime() {
    const resultPro = await _getProjectById(this.id);
    this.project.estEndTime = moment(resultPro.data.project.estEndTime).format(
      "YYYY-MM-DD"
    );

    this.project.status = resultPro.data.project.status;

    const projectStatus = this.project.status === "4" ? "1" : this.project.status;

    const task = this.dateStatusList2.filter(
      (dateStatus) => dateStatus.code === projectStatus
    )[0];

    this.referStatus = task === undefined ? {name: "", color: "#ccc"} : task;
  }

  /**
   * 新增按钮  相关
   */

  function addMilestone() {
    if (this.authority) {
      this.$refs.tableStyle.addProjectMilestone();
    }
  }

  function addATask() {
    this.addVisible = false;
    this.isAddTask = !this.isAddTask;
  }

  /**
   * 添加/取消收藏
   */
  async function addOrCancelCollection() {
    await _addOrCancelCollection({
      projectId: this.id,
      flag: this.flag,
    });
    if (this.flag === true) {
      this.flag = false;
      this.collections.push(this.id);
      this.$store.commit("changeCollection", true);
    } else {
      this.flag = true;
      this.collections.splice(this.collections.indexOf(this.id), 1);
      this.$store.commit("changeCollection", true);
    }
    localStorage.setItem("collections", this.collections);
  }

  async function updateProject(judge) {
    if (
      (judge === 1 && this.oldNameOrDes === this.project.projectName) ||
      (judge === 2 && this.oldNameOrDes === this.project.description)
    ) {
    } else {
      const result = await _updateProject({
        id: this.project.projectId === "" ? undefined : this.project.projectId,
        name: judge === 1 ? this.project.projectName : undefined,
        description: judge === 2 ? this.project.description : undefined,
      });
      if (result.code === 1200) {
        this.$root.$emit("getList", "");
      } else if (result.code === 4219) {
        this.project.projectName = this.oldNameOrDes;
      }
    }
    if (judge === 1) {
      this.projectNameVisible = false;
    } else {
      this.descriptionVisible = false;
    }
  }

  // more按钮
  async function goProject(code) {
    this.visible = false;
    this.isEdit = false;
    if (code === "ADD") {
      this.addProject("", "1");
    } else if (code === "EDIT") {
      this.isEdit = true;
      this.addProject(this.id, this.project.type);
    } else if (code === "DELETE") {
      this.$confirm("确认删除此项目?", "删除项目", {
        cancelButtonText: "取消",
        confirmButtonText: "确定",
        type: "warning",
      }).then(async () => {
        await _deleteProject(this.id);

        const result = await _queryAllProjectList({searchName: ""});
        let projectList = {};
        if (result !== null && result.code === 1200) {
          this.$store.commit("changeProjectEditOrDel", true);

          projectList = result.data.allVisibleProject;
        }

        this.$router.push({
          path: "project",
          query: {
            id: projectList[0].id,
            type: "1",
          },
        });
      });
    }
  }

  function addProject(projectId, projectType) {
    this.projectAdd.id = projectId;
    this.projectAdd.type = projectType;
    this.projectAdd.dialogVisible = true;
  }

  function closeProject() {
    this.projectAdd.id = "-1";
    this.projectAdd.type = "";
    this.projectAdd.dialogVisible = false;

    if (this.isEdit) {
      this.flag = this.collections.indexOf(this.id) === -1;
      this.getProjectById(this.id);
    }
  }

  function saveProject() {
    this.projectAdd.id = "";
    this.projectAdd.type = "";
    this.projectAdd.dialogVisible = false;
    this.$root.$emit("getList", "");
  }

  function openDrawer(a) {
    this.projectDrawerVisible = true;
    this.activeName = a;
  }

  async function getDrawer(data) {
    this.getProjectById(this.id);
    this.refreshDelivery();
    this.refreshProjectIssue();
    this.refreshRole()
    await this.$refs.tableStyle.listInRedis(data, this.searchValue);
    await this.$refs.tableStyle.treeLoad();
  }

  function showMore() {
    if (this.showDetailBtn.direction === "down") {
      this.showDetailBtn.direction = "up";
      this.showDetailBtn.text = "收起";
      this.showDetailBtn.height = this.$refs.pStyle.scrollHeight + "px";
    } else {
      this.showDetailBtn.direction = "down";
      this.showDetailBtn.text = "查看更多";
      this.showDetailBtn.height = "71px";
    }
  }

  function showDetailBtnFun() {
    this.$nextTick(() => {
      setTimeout(() => {
        if (this.$refs.pStyle.scrollHeight > 80) {
          this.showDetailBtn.desc = true;
        } else {
          this.showDetailBtn.desc = false;
        }
      }, 500);
    });
  }

  function isHidden(size) {
    this.mSize = size;
  }

  function changeBulletinType(value) {
    this.bulletinType = value;
  }

  function onLine(hasOnLine) {
    this.$confirm("这个操作修改项目线上状态，您想继续吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    })
      .then(async () => {
        const data = {
          id: this.id,
          hasOnLine: hasOnLine,
        };
        const result = await _updateProjectOnLine(data);
        if (result.code === 1200) {
          this.$message({
            type: "success",
            message: "修改成功！",
          });
          this.getProjectById(this.id);
          this.$root.$emit("getList", "");
        }
      })
      .catch(() => {
        this.$message({
          type: "info",
          message: "执行失败！",
        });
      });
  }

  var cityOptions = [
    "项目",
    "里程碑",
    "父任务",
    "任务名称",
    "状态",
    "优先级",
    "责任角色",
    "确认角色",
    "截止时间",
    "完成时间",
    "交付要求",
    "任务类型",
    "开阀条件",
    "开阀描述",
    "交付物",
  ];
  var filterVal = [
    "project",
    "milepost",
    "isChild",
    "name",
    "status",
    "priority",
    "reviewUserName",
    "confirmUserName",
    "estEndTime",
    "actEndTime",
    "isRequirement",
    "type",
    "openConditions",
    "openDescription",
    "taskDeliveryList",
  ];

  async function handleCheckAllChange(val) {
    if (this.cols.length > 0) {
      this.cols.forEach((x) => {
        if (filterVal.indexOf(x.projectAttrId) === -1) {
          cityOptions.push(x.label);
          filterVal.push(x.projectAttrId);
        }
      });
    }
    this.checkedCities = val ? filterVal : [];
    this.isIndeterminate = false;
    this.heads = filterVal;
  }

  function handleCheckedCitiesChange(value) {
    let checkedCount = value.length;
    this.checkAll = checkedCount === cityOptions.length;
    this.isIndeterminate = checkedCount > 0 && checkedCount < cityOptions.length;
    this.heads = value;
  }

  async function changeSelect() {
    //初始化
    // cityOptions = cityOptions.splice(0,14);
    // filterVal = filterVal.splice(0,14);
    cityOptions = [
      "项目",
      "里程碑",
      "父任务",
      "任务名称",
      "状态",
      "优先级",
      "责任角色",
      "确认角色",
      "截止时间",
      "完成时间",
      "交付要求",
      "任务类型",
      "开阀条件",
      "开阀描述",
      "交付物",
    ];
    filterVal = [
      "project",
      "milepost",
      "isChild",
      "name",
      "status",
      "priority",
      "reviewUserName",
      "confirmUserName",
      "estEndTime",
      "actEndTime",
      "isRequirement",
      "type",
      "openConditions",
      "openDescription",
      "taskDeliveryList",
    ];
    this.headMap = {};
    //固定表头存入headMap
    for (var i in filterVal) {
      this.headMap[filterVal[i]] = cityOptions[i];
    }
    const result = await _getToPut(this.id, this.searchValue, this, this.types);
    this.cols = result.cols;
    if (this.cols.length > 0) {
      this.cols.forEach((x) => {
        if (filterVal.indexOf(x.projectAttrId) === -1) {
          cityOptions.push(x.label);
          filterVal.push(x.projectAttrId);
          //存入headMap
          this.headMap[x.projectAttrId] = x.label;
        }
      });
    }

    this.checkedCities = filterVal;
    this.isIndeterminate = false;
    this.checkAll = true;

    this.heads = filterVal;
  }

  async function tableToExcel() {
    //获取项目名称
    const resultPro = await _getProjectById(this.id);
    const projectName = resultPro.data.project.name;
    const projectDscription = resultPro.data.project.description;

    const r = await _listInRedis({
      projectId: this.id,
      searchName: this.searchValue,
    });
    //获取新增标签

    const result = await _getToPut(this.id, this.searchValue, this, this.types);

    const cols = result.cols;
    const milestoneArray = result.milestoneArray;
    const childTaskList = result.childTaskList.sort((a, b) => {
      return a.sort < b.sort ? -1 : 1;
    });

    const childTaskList1 = [];
    //子任务中新增里程碑和父任务名称
    childTaskList.forEach((x) => {
      const projectMilestone = r.data.projectMilestoneList.filter(
        (y) => x.milestoneId === y.id
      )[0];
      const parentTask = r.data.parentTaskList.filter(
        (y) => x.parentId === y.id
      )[0];
      if (projectMilestone && parentTask) {
        x["milepost"] = projectMilestone.name;
        x["isChild"] = parentTask.name;
        childTaskList1.push(x);
      }
    });
    // if(cols.length > 0){
    //   cols.forEach(x=>{
    //     cityOptions.push(x.label)
    //     filterVal.push(x.projectAttrId)
    //   })
    // }

    require.ensure([], () => {
      const {exportJsonToExcel} = require("../../../vendor/Export2Excel");
      //对应的标签
      const filter = this.heads;
      //表头字段
      const tHeader = [];
      filter.forEach((x) => {
        tHeader.push(this.headMap[x]);
      });
      //根据不同sheet存入数据
      if (milestoneArray.length > 0) {
        var list = [];
        //项目名称
        var data1 = {};
        //项目描述
        var data2 = {};
        filter.forEach((x) => {
          if (x === "project") {
            data1[x] = projectName;
            data2[x] = projectDscription;
          } else {
            data1[x] = "";
            data2[x] = "";
          }
        });
        list.push(data1);
        list.push(data2);
        milestoneArray.forEach((x) => {
          //里程碑内容
          const mName = x.projectMilestone.name;
          x.taskList.forEach((y) => {
            y["milepost"] = mName;
          });
          //需要下载的标签内容
          x.taskList.forEach((y) => {
            list.push(y);
            //如果当前父任务有子任务存在
            const child = childTaskList1.filter(
              (child) => child.parentId === y.id
            );
            if (child.length !== 0) {
              list = list.concat(child);
            }
          });
        });
        const data = this.formatJson1(filter, list);
        exportJsonToExcel(tHeader, data, projectName);
      } else {
        exportJsonToExcel(tHeader, "", projectName);
      }
      // cityOptions = cityOptions.slice(0,14);
      // filterVal = filterVal.slice(0,14);
    });
  }

  function formatJson1(filterVal, jsonData) {
    jsonData.forEach((x) => {
      //转换状态
      const status = this.dateStatusList.filter(
        (dateStatus) => dateStatus.code === x.status
      )[0];
      if (status) x.status = status.name;
      //转换优先级
      const priority = this.priority.filter(
        (priority) => priority.code === x.priority
      )[0];
      if (priority) x.priority = priority.name;
      //转换交付要求
      if (x.isRequirement === 1 && x) {
        x.isRequirement = "是";
      } else {
        x.isRequirement = "否";
      }
      //转换任务类型
      if (x.type && x.type !== "" && x.type !== null && x.type.length !== 0) {
        var str = "";
        x.type.forEach((x) => {
          let type = this.types.filter((type) => type.code === x)[0];
          if (type) str = str + type.name + ",";
        });
        x.type = str.substring(0, str.length - 1);
      }
      //转换负责人
      if (x.reviewUser !== "" && x.reviewUser !== null && x.reviewUser) {
        if (x.reviewUser) x["reviewUserName"] = x.reviewUser.realName;
      }
      //转换审核人
      if (x.confirmUser !== "" && x.confirmUser !== null && x.confirmUser) {
        if (x.confirmUser) x["confirmUserName"] = x.confirmUser.realName;
      }

      //添加新增字段内容
      if (x.taskDataList) {
        const taskDataList = x.taskDataList.map((t) => {
          //判断新增字段是否是status类型
          if (t.attrType === "STATUS" && t.value !== "") {
            const dateStatus = this.dateStatusList.filter(
              (dateStatus) => dateStatus.code === t.value
            )[0];
            if (dateStatus)
              return {
                id: t.projectAttrId,
                value: dateStatus.name,
              };
          } else {
            return {
              id: t.projectAttrId,
              value: t.value,
            };
          }
        });
        taskDataList.forEach((t) => {
          x[t.id] = t.value;
        });
      }

      //添加交付物内容
      if (x.taskDeliveryList && x.taskDeliveryList.length !== 0) {
        var str = "";
        x.taskDeliveryList.forEach((t) => {
          str = str + t.deliveryName + ",";
        });
        x["taskDeliveryList"] = str.substring(0, str.length - 1);
      }
    });

    //前两行项目信息中交付要求置空
    const jsonDatum = jsonData[0];
    jsonDatum["isRequirement"] = "";
    const jsonDatum1 = jsonData[1];
    jsonDatum1["isRequirement"] = "";

    return jsonData.map((v) => filterVal.map((j) => v[j]));
  }

  /**
   * 上传文件
   */
  function uploadSuccess(res, file) {
    const path = res.data.path;
    this.analysisExcel(path);
  }

  async function analysisExcel(path) {
    const result = await _analysisExcel({
      path: path,
      projectId: this.id,
    });
    if (result && result.code === 1200) {
      this.getProjectById(this.id);
      this.$refs.tableStyle.listInRedis(this.id, this.searchValue);
    }
  }

  async function getNodes(data) {
    const resultRole = await _importProject(this.id, data);
    this.importDialogVisible = false;
    this.$refs.tableStyle.listInRedis(this.id, this.searchValue);
  }

  function downloadExcel() {
    downloadFile("data/project/excelTemplate.xlsx").then((response1) => {
      const fileName = "excelTemplate.xlsx";
      const fileData = response1;
      if (fileData !== null) {
        const blob = new Blob([fileData]);
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement("a");
        link.style.display = "none";
        link.href = url;
        link.setAttribute("download", fileName);
        document.body.appendChild(link);
        link.click();
      }
    });
  }

  function changeProjectName() {
    if (this.authority) {
      this.oldNameOrDes = this.project.projectName;
      this.projectNameVisible = true;
    }
  }

  function changeProjectDescription() {
    if (this.authority) {
      this.oldNameOrDes = this.project.description;
      this.descriptionVisible = true;
    }
  }

  function projectStMark() {
    this.$confirm('请确认是否设置为归档项目？', '项目归档', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      const result = await _letProjectToStMark(this.project.projectId);
      if (result.code === 1200) {
        this.$root.$emit("getList", "");
      }
    }).catch(() => {
      this.$message({
        type: 'info',
        message: '执行失败！',
      });
    });
  }

  export default {
    components: {
      ProjectIssueEdit,
      TableStyle,
      BulletinBoard,
      ProjectDrawer,
      DictPopover,
      ProjectAdd,
      BulletinBoardMilestone,
      Gant,
      Date,
      ProjectTelImport,
    },
    data() {
      return {
        //定义表头相关map
        headMap: {},
        token: localStorage.getItem("token"),
        id: "",
        project: {
          projectName: "",
          estEndTime: "",
          status: "",
          projectId: "",
          total: {
            role: "",
            delivery: "",
            issue: '',
          },
          hasOnLine: 0,
          circleId: "",
          description: "",
          parentsCircles: [],
          circle: {},
          type: "",
          projectUser: {},
        },
        projectNameVisible: false,
        descriptionVisible: false,
        // 当前呈现形式的id
        currentId: "1",
        aa: false,
        // 抽屉相关
        projectDrawerVisible: false,

        bulletinTypeChoose: false,
        // 新增里程碑/renwu
        isAddTask: false,
        addVisible: false,
        // 收藏按钮
        flag: true,

        collections: [],

        defaultActive: "ADD",
        visible: false,
        projectAdd: {
          id: "",
          type: "",
          dialogVisible: false,
        },
        // 传到projectAdd
        projectInformation: {},
        activeName: "",

        isEdit: false,

        searchValue: "",

        dateStatusList: [],
        dateStatusList2: [],
        referStatus: {},

        showDetailBtn: {
          desc: false,
          direction: "down",
          text: "查看更多",
          height: "71px",
        },

        // 项目操作权限数组
        userId: "",
        authority: false,
        reBeginAuthority: false,

        //for当前角色加上模板角色
        rolePlus: [],

        mSize: 0,

        // 看板显示
        // 是否有里程碑分层
        isShowMilestone: false,

        // 1表示以状态显示，2表示以优先级显示
        bulletinType: "1",

        importDialogVisible: false,

        isIndeterminate: true,
        checkAll: false,
        checkedCities: [],
        cities: cityOptions,
        filterVals: filterVal,
        types: [],
        priority: [],
        heads: [],
        cols: [],

        persons: [],
        state1: "",

        // 如果项目名称或描述和原来一样就别更新了
        oldNameOrDes: "",

        // 项目的问题提出dialog相关
        projectIssueDialogVisible: false,
        projectIssueFlag: false,
        projectIssueId: "",
      };
    },
    methods: {
      changeStyle(val) {
        this.currentId = val.code;
      },
      getProjectById,
      addMilestone,
      addOrCancelCollection,
      updateProject,
      addATask,
      goProject,
      closeProject,
      saveProject,
      addProject,
      openDrawer,
      getDrawer,
      showMore,
      refreshDelivery,
      refreshRole,
      refreshProjectIssue,
      showDetailBtnFun,
      refreshEndTime,
      isHidden,
      onLine,
      changeBulletinType,
      uploadSuccess,
      analysisExcel,

      changeProjectName,
      changeProjectDescription,

      getNodes,

      handleCheckAllChange,
      handleCheckedCitiesChange,
      changeSelect,

      tableToExcel,
      formatJson1,
      querySearch(queryString, cb) {
        const persons = this.persons;

        const results = queryString
          ? persons.filter(this.createFilter(queryString))
          : persons;
        // 调用 callback 返回建议列表的数据
        cb(results);
      },
      createFilter(queryString) {
        return (person) => {
          return (
            person.value.toLowerCase().indexOf(queryString.toLowerCase()) === 0
          );
        };
      },
      loadAll(list) {
        const la = [];

        const filter = [];

        list.forEach((rp) => {
          if (rp.projectRoleUsers.length !== 0) {
            rp.projectRoleUsers.forEach((pr) => {
              if (filter.indexOf(pr.sysUserId) === -1) {
                filter.push(pr.sysUserId);
                la.push({
                  value: pr.sysUsers[0].realName,
                  key: pr.sysUserId,
                });
              }
            });
          }
        });
        return la;
      },
      handleSelect(item) {
        console.log(item);
      },
      downloadExcel,
      openIssue() {
        this.projectIssueDialogVisible = true;
        this.$nextTick(() => {
          this.projectIssueFlag = true;
          this.projectIssueId = "";
        });
      },
      closeIssue(isRefresh) {
        if (isRefresh === "refresh") {
          this.refreshProjectIssue()
        }
        this.projectIssueDialogVisible = false;
        this.projectIssueFlag = false;
        this.projectIssueId = "";
      },
      projectStMark,
    },
    created() {
      const dictData = this.$store.getters.getDictionaryByKey("TASK_STATUS");
      this.dateStatusList = dictData.sysDictDataList;

      const dictData2 = this.$store.getters.getDictionaryByKey(
        "TASK_DATE_STATUS"
      );
      this.dateStatusList2 = dictData2.sysDictDataList;

      const aa = localStorage.getItem("collections");
      this.collections = aa.split(",");
      this.id = this.$route.query.id === undefined ? "" : this.$route.query.id;
      if (this.id !== "") {
        this.flag = this.collections.indexOf(this.id) === -1;
        this.getProjectById(this.id);
        this.refreshDelivery();
        this.refreshRole();
        this.refreshProjectIssue();

        const dictDataType = this.$store.getters.getDictionaryByKey("TASK_TYPE");
        this.types = dictDataType.sysDictDataList;

        const dictDataPriority = this.$store.getters.getDictionaryByKey(
          "PRIORITY"
        );
        this.priority = dictDataPriority.sysDictDataList;
      }
    },
    watch: {
      $route() {
        this.searchValue = "";
        this.state1 = "";
        const aa = localStorage.getItem("collections");
        this.collections = aa.split(",");
        this.id = this.$route.query.id === undefined ? "" : this.$route.query.id;
        if (this.id !== "") {
          this.flag = this.collections.indexOf(this.id) === -1;
          this.getProjectById(this.id);
          this.refreshDelivery();
          this.refreshRole();
          this.refreshProjectIssue();
          this.showDetailBtnFun();
        }
      },
    },
    mounted() {
      this.showDetailBtnFun();
    },
  };
</script>
<style scoped>
  .body {
    height: 100%;
    width: 100%;
    border-left: 1px #e1e1e1 solid;
    float: left;
    display: flex;
    box-sizing: border-box;
    flex-direction: column;
    background-color: #fff;
    overflow-x: hidden;
    overflow-y: hidden;
  }

  .show_style_label {
    cursor: pointer;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    height: 31px;
    font-size: 15px;
    line-height: 31px;
  }

  .show_style_label:hover {
    background-color: #dbe4ee;
    color: #01408b;
  }

  .round {
    border-bottom-left-radius: 20px !important;
    border-top-left-radius: 20px !important;
  }

  .add-icon {
    cursor: pointer;
    line-height: 36px;
    height: 36px;
    background-color: #344e75;
    border-bottom-right-radius: 20px;
    border-top-right-radius: 20px;
    width: 35px;
    border-left: 1px solid #fff;
    text-align: center;
  }

  .is_hover {
    border: 1px transparent solid;
  }

  .is_hover:hover {
    border: 1px #ccc dashed;
  }

  .banned {
    background-color: #9aa7ba;
  }

  .user-info {
    text-align: center;
    height: 38px;
    line-height: 38px;
    border: 1px #e1e1e1 solid;
    border-radius: 100px;
    padding: 0 20px;
  }

  .text-style {
    border: 1px dashed transparent;
  }

  .text-style:hover {
    border: 1px dashed #ccc;
  }
</style>
<style lang="scss">
  .el-autocomplete-suggestion__wrap,
  .el-scrollbar__wrap {
    margin-bottom: -5px !important;
  }

  .cancel-padding > {
    .el-textarea__inner {
      padding: 0;
    }
  }

  .pro-name > {
    .el-input__inner {
      padding: 0;
      font-family: Helvetica Neue, Helvetica, PingFang SC, Hiragino Sans GB,
      Microsoft YaHei, SimSun, sans-serif;
      -webkit-box-direction: normal;
      font-size: 29px;
      font-weight: normal;
      height: 40px;
      line-height: 40px;
    }
  }
</style>
