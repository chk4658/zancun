<template>
  <div class="body" v-show="id !== ''">
    <div class="header">
      <el-row style="margin-bottom:10px">
        <el-col>
          <el-breadcrumb separator-class="el-icon-arrow-right">
            <el-breadcrumb-item
              v-for="item in parentsCircles"
              :key="item.id"
              :to="{ path: 'circle',query: {id: item.id}}">
              {{ item.name }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="8" style="display:flex">
          <el-image :src="'/api'+circle.imgUrl" style="width: 100px;height: 100px">
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>
          <div class="title" @click="clickCircleName" style="margin-top:30px;margin-left:5px">
            <div class="title" v-if="!isCircleName" style="width:150%">
              {{ circle.name }}
            </div>
            <el-input v-if="isCircleName" v-model="circle.name"
                      maxlength="50"
                      show-word-limit
                      v-focus="isCircleName"
                      size="medium"
                      style="width:250%"
                      @blur="updateCircleName"
                      @keyup.enter.native="$event.target.blur"></el-input>
          </div>
        </el-col>
        <el-col :span="16">
          <div class="header-btn-container">
            <div class="user-info">
              <i class="fa fa-user-circle" aria-hidden="true" style="color: #344E75"></i>
              <span style="margin-left: 5px">{{ user === null ? '' : user.realName }}</span>
            </div>

            <div class="hexagon">
              <i class="fa fa-circle-o-notch" aria-hidden="true"></i>
              <div style="margin: 0 5px">
                <span>{{ circleChildrenNum }}</span>
                <span style="margin: 0 5px">/</span>
                <span>{{ circleAllChildrenNum }}</span>
              </div>
            </div>

            <div class="hexagon">
              <i class="fa fa-list-alt" aria-hidden="true"></i>
              <div style="margin: 0 5px">
                <span>{{ circleProjectNum }}</span>
                <span style="margin: 0 5px">/</span>
                <span>{{ circleAllProjectNum }}</span>
              </div>
            </div>

            <div class="hexagon">
              <i class="fa fa-user" aria-hidden="true"></i>
              <div style="margin: 0 5px">
                <span>{{ circleCurUserNum }}</span>
                <span style="margin: 0 5px">/</span>
                <span>{{ circleAllUserNum }}</span>
              </div>
            </div>

            <div class="qa-use-mun">
              <div style="border-right: 1px solid #e1e1e1;padding-right: 5px;cursor:pointer;"
                   @click="clickCircleDrawer('chat')">
                <span>Q&A</span>
                <span style="margin: 0 5px">/</span>
                <span>{{ circleChatNum }}</span>
              </div>
              <div style="padding-left: 5px;cursor:pointer;" @click="clickCircleDrawer('role')">
                <i class="fa fa-users"></i>
                <span style="margin: 0 5px">/</span>
                <span>{{ circleRoleNum }}</span>
              </div>
            </div>

            <div class="more" v-hasCirce="'{\'circleId\':\'' + id +'\', \'key\': \'hasOperate\', \'value\':true}'">
              <el-popover
                placement="left-start"
                width="180"
                :visible-arrow="false"
                v-model="visible"
              >
                <el-row>
                  <el-col>
                    <el-menu
                      :default-active="defaultActive"
                      class="el-menu-vertical-demo">
                      <!-- <el-menu-item index="ADD"
                                    @click="goCircle('ADD')"
                      >
                        <i class="el-icon-circle-plus-outline"></i>
                        <span slot="title">新增</span>
                      </el-menu-item> -->
                      <el-menu-item index="EDIT"
                                    @click="goCircle('EDIT')"

                      >
                        <i class="el-icon-edit-outline"></i>
                        <span slot="title">编辑</span>
                      </el-menu-item>
                      <el-menu-item index="DELETE"
                                    @click="goCircle('DELETE')"
                      >
                        <i class="el-icon-delete"></i>
                        <span slot="title">删除</span>
                      </el-menu-item>

                      <el-menu-item index="BLOG"
                                    @click="clickCircleDrawer('log')"
                      >
                        <i class="el-icon-notebook-1"></i>
                        <span slot="title">日志</span>
                      </el-menu-item>
                    </el-menu>
                  </el-col>
                </el-row>

                <i class="el-icon-more" style="font-size: 22px" slot="reference"></i>
              </el-popover>

            </div>
          </div>

        </el-col>
      </el-row>


      <el-row style="margin: 20px 0;font-size: 18px">
        <el-col :span="18">
          <p v-if="isCircleDescription&&circle.description!==null&&circle.description!==''"
             ref="desc"
             :style="{maxHeight: showDetailBtn.height}"
             @click="clickCircleDescription"
             style="white-space:normal;" class="text-style">{{ circle.description }}</p>

          <p v-if="isCircleDescription&&(circle.description===null||circle.description==='')"
             style="color: #cccccc"
             class="text-style"
             @click="clickCircleDescription">添加描述</p>

          <el-button @click="showMore" type="text" v-if="showDetailBtn.desc && isCircleDescription">
            {{ showDetailBtn.text }}
            <i class="el-icon-arrow-down" v-if="showDetailBtn.direction==='down'"></i>
            <i class="el-icon-arrow-up" v-if="showDetailBtn.direction==='up'"></i>
          </el-button>
          <el-input type="textarea"
                    autosize
                    v-if="!isCircleDescription"
                    v-focus="!isCircleDescription"
                    v-model="circle.description" style="width:100%"
                    @blur='updateCircleDescription'
                    @keyup.enter.native='$event.target.blur'></el-input>
        </el-col>
      </el-row>


      <el-row>
        <el-col :offset="6" :span="18">
          <div style="display: flex;justify-content: flex-end">


            <el-button type="primary"
                       class="round"
                       size="medium"
                       @click="addCircle('',true)"
                       v-hasCirce="'{\'circleId\':\'' + id +'\', \'key\': \'hasAddCircle\', \'value\':true}'">
              新增圈子
            </el-button>

            <!-- <el-popover
              placement="bottom-start"
              width="180"
              :visible-arrow="false"
              v-model="addVisible"
              v-hasCirce="'{\'circleId\':\'' + id +'\', \'key\': \'hasAddCircle\', \'value\':true}'">
              <el-row>
                <el-col>
                  <el-menu
                    :default-active="defaultActive"
                    class="el-menu-vertical-demo">
                    <el-menu-item index="PROJECT_ADD"
                                  @click="addProject('')">
                      <span slot="title">空白项目</span>
                    </el-menu-item>
                     <el-menu-item index="PROJECT_IMPORT"
                                  @click="importProject()">
                      <span slot="title">导入项目</span>
                    </el-menu-item>
                  </el-menu>
                </el-col>
              </el-row>
              <i class="el-icon-arrow-down add-icon" style="color: #fff" slot="reference"></i>
            </el-popover> -->


            <el-input
              style="margin-left: 20px;width: 400px"
              size="medium"
              placeholder="请输入内容"
              v-model="circleName"
              @input="getChildrenCircles">
              <i slot="prefix" class="el-input__icon el-icon-search"></i>
            </el-input>
          </div>

        </el-col>
      </el-row>
    </div>
    <div class="body-container">

      <el-row :gutter="20" style="margin:  0 10px">
        <el-col v-for="(item,index) in childrenCircles" :key="index" :span="6" style="margin-top: 15px;">
          <el-card class="box-card" body-style='
                   padding: 15px;
                   background-color:#F5F5F5;display: flex;
                   height: 180px;
                   justify-content: space-between;
                   flex-direction: column;'>
            <div @click="childrenCircleClick(item.id)" class="box-card-header" style="height: 30%;overflow-y: auto;word-wrap:break-word;">
              <i class="fa fa-circle-o-notch icon"></i>
              <span>{{ item.name }}</span>
            </div>
           <div class="box-card-desc" style="overflow-y: auto;margin: 15px 0 0 0;height: 50%;word-wrap:break-word;">
               {{ item.description}}
            </div>
            <div class="card-container">
              <div class="user-info">
                <i class="fa fa-user-circle" aria-hidden="true" style="color: #344E75"></i>
                <span style="margin-left: 5px">{{ item.ownerUser === null ? '' : item.ownerUser.realName }}</span>
              </div>

              <div class="hexagon">
                <i class="fa fa-circle-o-notch" aria-hidden="true"></i>
                <span style="margin: 0 5px">/</span>
                <span>{{ item.allChildrenCircleNum }}</span>
              </div>

              <div class="hexagon">
                <i class="fa fa-list-alt" aria-hidden="true"></i>
                <span style="margin: 0 5px">/</span>
                <span>{{ item.allProjectNum }}</span>
              </div>

            </div>
            <div class="box-card-footer">
              {{ item.createAt }}
              <div>
                <el-button type="text" icon="el-icon-edit" size="middle"
                           @click="addCircle(item.id,true)"
                           v-hasCirce="'{\'circleId\':\'' + item.id +'\', \'key\': \'hasOperate\', \'value\':true}'"></el-button>
                <el-button type="text" icon="el-icon-close"
                           size="middle"
                           @click="deleteCircle(item.id,'child')"
                           v-hasCirce="'{\'circleId\':\'' + item.id +'\', \'key\': \'hasOperate\', \'value\':true}'">
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>


      <el-row :gutter="20" style="margin:  0 10px 20px">
        <el-col v-for="(item,index) in projects" :key="index" :span="6" style="margin-top: 15px;">
          <el-card class="box-card" body-style='
                padding: 15px;
                background-color:#F5F5F5;display: flex;
                height: 150px;
                justify-content: space-between;
                flex-direction: column;'>
            <div class="box-card-header" @click="projectClick(item.id)" style="height: 30%;overflow-y: auto;word-wrap:break-word;">
              <i class="fa fa-list-alt icon">
              </i>
              <span>{{ item.name }}</span>
            </div>
            <div class="box-card-desc" style="overflow-y: auto;margin: 15px 0 0 0;height: 50%;word-wrap:break-word;">
               {{ item.description}}
            </div>
            <div class="box-card-footer">
              {{ item.createAt }}
              <!-- <div>
                <el-button type="text" icon="el-icon-edit" :key="item.id"
                           @click="addProject(item.id)"
                           v-hasCirce="'{\'circleId\':\'' + id +'\', \'key\': \'hasOperate\', \'value\':true}'">
                </el-button>
                <el-button type="text" icon="el-icon-close"
                           @click="deleteProject(item.id)" :key="item.id"
                           v-hasCirce="'{\'circleId\':\'' + id +'\', \'key\': \'hasOperate\', \'value\':true}'">
                </el-button>
              </div> -->
            </div>
          </el-card>
        </el-col>
      </el-row>

    </div>


    <!-- 弹出层 -->
    <el-dialog
      class="abow_dialog"
      :title="circleId === '' ? '新增圈子' : '编辑圈子'"
      @close="closeCircle"
      :visible.sync="circleFrom.dialogVisible"
      append-to-body>
      <circle-edit @saveCircle="saveCircle"
                   :circleId="circleId"
                   :parentCircle="circleFrom.isChildCircle ? this.circle: null"
      >
      </circle-edit>
    </el-dialog>

    <!-- 抽屉 -->
    <el-drawer
      title=""
      :visible.sync="circleDrawer.drawer"
      :with-header="false"
      size='50%'
    >
      <circle-drawer :user='user' :circle='circle' :activeName='circleDrawer.activeName'>
      </circle-drawer>
    </el-drawer>

    <el-dialog
      :title="project.id === '' ? '新增项目' : '修改项目'"
      @close="closeProject"
      :visible.sync="project.dialogVisible"
      append-to-body>
      <project-add
        @saveProject="saveProject"
        :projectId="project.id"
        :parentCircle="this.circle"
        :projectType='project.projectType'
        @getProject='getProject'></project-add>
    </el-dialog>


    <!-- 导入项目 -->
    <el-dialog width="70%"
               title="导入项目"
               @close="closeProjectImport"
               :visible.sync="project.importDialogVisible"
               append-to-body>
      <ProjectTelImport @getNodes="getNodes" :key="project.id"></ProjectTelImport>
    </el-dialog>


  </div>
</template>

<script>

import {
  _queryCircle,
  _queryChildrenCircles,
  _deleteCircle,
  _updateCircle,
  _updateCircleNameAndDescription,
  _queryCircleProjectNum,
  _putIndexedCircleOperation,
} from '@/api/circleApi';

import {_querySysUser} from '@/api/sysUserApi';

import {_queryCircleUserNum} from '@/api/circleRoleUser';

import {_importProject} from '@/api/projectApi';

import CircleEdit from './CircleEdit.vue';

import CircleDrawer from './drawer/CircleDrawer.vue';

import ProjectAdd from '../project/ProjectAdd';

import ProjectTelImport from '../project/ProjectTelImport';


import {_deleteProject} from '@/api/projectApi';

async function getCircle() {
  const result = await _queryCircle(this.id);
  if (result.code === 1200) {
    this.circle = result.data.circle;
    this.parentsCircles = result.data.parentsCircles;
    this.getChildrenCircles();
    this.getUser();

    this.showDetailBtnFun();
  }
}

async function getCircleProjectNum() {
  const result = await _queryCircleProjectNum({circleId: this.id});
  if (result.code === 1200) {
    this.circleAllProjectNum = result.data.curCircleAllProjectNum;
    this.circleProjectNum = result.data.curCircleProjectNum;
    this.circleAllChildrenNum = result.data.curCircleAllChildrenNum;
    this.circleChildrenNum = result.data.curCircleChildrenNum;
    this.circleRoleNum = result.data.curCircleRoleNum;
    this.circleChatNum = result.data.curCircleChatNum;
  }
}

async function getCircleUserNum() {
  const result = await _queryCircleUserNum({circleId: this.id});
  if (result.code === 1200) {
    this.circleCurUserNum = result.data.curCircleUserNum;
    this.circleAllUserNum = result.data.allCircleUserNum;
  }
}

async function getChildrenCircles() {
  const result = await _queryChildrenCircles({parentId: this.circle.id, searchName: this.circleName});
  if (result.code === 1200) {
    this.childrenCircles = result.data.childrenCircles;
    this.projects = result.data.projectList;
  }
}


function childrenCircleClick(id) {
  this.$router.push({path: 'circle', query: {id: id}});
}

function projectClick(id) {
  this.$router.push({path: 'project', query: {id: id}});
}

async function getUser() {
  const result = await _querySysUser(this.circle.ownerUid);
  if (result.code === 1200) {
    this.user = result.data.sysUser;
  }
}

async function clickCircleName() {
  this.isCircleName = true;
}

async function updateCircleName() {
  this.isCircleName = false;
  const result = await _updateCircleNameAndDescription({
    id: this.circle.id,
    name: this.circle.name,
    description: this.circle.description,
  });
  this.$router.push({path: 'circle', query: {id: this.circle.id, type: '1'}});
}

function clickCircleDescription() {
  this.isCircleDescription = false;
}

async function updateCircleDescription() {
  const result = await _updateCircleNameAndDescription({
    id: this.circle.id,
    name: this.circle.name,
    description: this.circle.description,
  });
  this.isCircleDescription = true;
  this.showDetailBtnFun();
}

function addCircle(id, isChildCircle) {
  this.circleFrom.isChildCircle = isChildCircle;
  this.circleId = id;
  this.circleFrom.dialogVisible = true;
}

function saveCircle(data) {
  this.circleId = '-1';
  if (!this.circleFrom.isChildCircle) {
    this.$router.push({path: 'circle', query: {id: data.id, type: '1'}});
  } else {
    this.getChildrenCircles();
    this.$router.push({path: 'circle', query: {id: this.id, type: '1'}});
  }
  this.circleFrom.dialogVisible = false;

}

function closeCircle() {
  this.circleId = '-1';
  this.circleFrom.dialogVisible = false;
}

function deleteCircle(id, type,parentId) {
  this.$confirm('这个操作将永久删除圈子信息，您想继续吗？', '删除圈子', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const result = await _deleteCircle(id);
    if (result.code === 1200) {
      this.$message({
        type: 'success',
        message: '删除成功!',
      });
      await _putIndexedCircleOperation(this);
      if (type === 'parent') {
        if (parentId !== null && parentId !== '') {
          this.$router.push({path: 'circle', query: {id: parentId,type: '1'}});
        } else {
          this.$router.push({path: 'circle', query: {type: '1'}});
        }
      } else if (type === 'child') {
        this.getChildrenCircles();
        this.$router.push({path: 'circle', query: {id: this.id, type: '1'}});
      }

    }
  }).catch(() => {
    this.$message({
      type: 'info',
      message: '执行失败！',
    });
  });
}

/**
 * 打开抽屉
 */
function clickCircleDrawer(activeName) {
  this.circleDrawer.activeName = activeName;
  this.circleDrawer.drawer = true;
}


function goCircle(code) {
  if (code === 'ADD') {
    this.addCircle('', false);
  } else if (code === 'EDIT') {
    this.addCircle(this.circle.id, false);
  } else if (code === 'DELETE') {
    this.deleteCircle(this.circle.id, 'parent',this.circle.parentId);
  }
}


function addProject(id) {
  this.project.id = id;
  this.project.projectType = '1';
  this.project.dialogVisible = true;
}

function importProject() {
  this.project.id = '';
  this.project.projectType = '3';
  this.project.importDialogVisible = true;
}

function closeProjectImport() {
  this.project.id = '-1';
  this.project.importDialogVisible = false;
}

function closeProject() {
  this.project.id = '-1';
  this.project.dialogVisible = false;
}

function saveProject() {
  this.project.id = '';
  this.project.dialogVisible = false;
  this.getChildrenCircles();
}

async function getProject(data, projectType) {
  if (projectType === '3') {
    const result = await _importProject(data.id, this.project.importData);
  }
}

function getNodes(data) {
  if (data === null || data.length === 0) {
    this.$message({
      type: 'error',
      message: '未选择需要导入的模板',
    });
  } else {
    this.project.importData = data;
    this.closeProjectImport();
    this.project.id = '';
    this.project.dialogVisible = true;
  }
}

function deleteProject(id) {
  this.$confirm('这个操作将永久删除项目信息，您想继续吗？', '删除项目', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    const result = await _deleteProject(id);
    if (result.code === 1200) {
      this.$message({
        type: 'success',
        message: '删除成功!',
      });
      this.getChildrenCircles();
    }
  }).catch(() => {
    this.$message({
      type: 'info',
      message: '执行失败！',
    });
  });
}


function showDetailBtnFun() {

  this.$nextTick(() => {
    if (this.$refs.desc.scrollHeight > 80) {
      this.showDetailBtn.desc = true;
    } else {
      this.showDetailBtn.desc = false;
    }
  });
}

function showMore() {
  if (this.showDetailBtn.direction === 'down') {
    this.showDetailBtn.direction = 'up';
    this.showDetailBtn.text = '收起';
    this.showDetailBtn.height = this.$refs.desc.scrollHeight + 'px';

  } else {
    this.showDetailBtn.direction = 'down';
    this.showDetailBtn.text = '查看更多';
    this.showDetailBtn.height = '71px';
  }
}

export default {
  name: 'CircleList',
  components: {CircleEdit, CircleDrawer, ProjectAdd, ProjectTelImport},
  data() {
    return {
      id: '',
      circleCurUserNum: 0,
      circleAllUserNum: 0,
      circleAllProjectNum: 0,
      circleProjectNum: 0,
      circleAllChildrenNum: 0,
      circleChildrenNum: 0,
      circleRoleNum: 0,
      circleChatNum: 0,
      defaultActive: 'ADD',
      circle: {},
      parentsCircles: [],
      childrenCircles: [],
      projects: [],
      user: {},
      isCircleName: false,
      isCircleDescription: true,
      circleId: {},
      circleDrawer: {
        activeName: '',
        drawer: false,
      },
      circleName: '',
      circleFrom: {
        isChildCircle: false,
        dialogVisible: false,
      },
      visible: false,
      addVisible: false,

      project: {
        id: '',
        dialogVisible: false,
        importDialogVisible: false,
        importData: [],
        projectType: '',
      },
      showDetailBtn: {
        desc: false,
        direction: 'down',
        text: '查看更多',
        height: '71px',
      },

    };
  },
  computed: {},
  created() {
    this.id = this.$route.query.id === undefined ? '' : this.$route.query.id;
    if (this.id !== '') {
      this.getCircle();
      this.getCircleUserNum();
      this.getCircleProjectNum();
    }
  },
  mounted() {
    this.showDetailBtnFun();
  },
  methods: {
    getUser,
    clickCircleName,
    updateCircleName,
    clickCircleDescription,
    updateCircleDescription,
    addCircle,
    saveCircle,
    closeCircle,
    getCircle,
    getChildrenCircles,
    deleteCircle,
    clickCircleDrawer,
    goCircle,
    childrenCircleClick,
    projectClick,
    getCircleUserNum,
    getCircleProjectNum,
    addProject,
    importProject,
    closeProjectImport,
    closeProject,
    getNodes,
    getProject,
    saveProject,
    deleteProject,
    showDetailBtnFun,
    showMore,
  },
  watch: {
    $route() {
      this.circleName = '';
      this.id = this.$route.query.id === undefined ? '' : this.$route.query.id;
      if (this.id !== '') {
        this.getCircle();
        this.getCircleUserNum();
        this.getCircleProjectNum();
      }
    },
  },
  directives: {},
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
.text-style {
  padding: 5px 200px 5px 0;
  margin: 0;
  border: 1px dashed #FFFFFF;
  overflow: hidden;

  &:hover {
    border: 1px dashed #ccc;
  }
}

.body {
  display: flex;
  flex-direction: column;
}

.body-container {
  overflow: auto;
  overflow-x: hidden;
  height: 100%;
  flex: 1;
}


.card {
  margin: 10px 10px 0px 10px;

  .card-content {
    position: relative;
    display: flex;

    > * {
      margin-right: 30px;
    }
  }
}

.round {
  // border-bottom-left-radius: 20px !important;
  // border-top-left-radius: 20px !important;

  border-radius: 20px !important;
}

.header-btn-container {
  display: flex;
  width: 100%;
  justify-content: flex-end;
  align-items: center;

  .user-info {
    text-align: center;
    height: 40px;
    line-height: 40px;
    border: 1px #e1e1e1 solid;
    border-radius: 100px;
    padding: 0 20px;
  }

  .hexagon {
    background: url("../../../assets/icon/hexagon_2x.png") no-repeat;
    background-size: 100% 100%;
    padding: 0 20px;
    margin-left: 10px;
    height: 40px;
    display: flex;
    justify-content: center;
    align-items: center;

  }

  .qa-use-mun {
    height: 40px;
    border: 1px #e1e1e1 solid;
    border-radius: 5px;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 0 20px;
    margin: 0 10px;
  }

  .more {
    margin: 0 5px;
  }
}

.card-container {
  display: flex;
  margin: 10px 0 3px 0;
  width: 100%;

  > * {
    padding: 0 10px;
    margin: 0 15px 0 0;
    line-height: 30px;
    
  }

  .user-info {
    text-align: center;
    border: 1px #e1e1e1 solid;
    border-radius: 100px;
    font-size: 13px;
  }

  .hexagon {
    padding-top: 2px;
    text-align: center;
    background: url("../../../assets/icon/hexagon_2x.png") no-repeat;
    background-size: 100% 100%;
    justify-content: center;
    align-items: center;
  }
}

.el-menu-item {
  &:hover {
    background-color: #003f8a;
    color: #fff;
  }
}

.add-icon {
  cursor: pointer;
  line-height: 40px;
  height: 40px;
  background-color: #344E75;
  border-bottom-right-radius: 20px;
  border-top-right-radius: 20px;
  width: 35px;
  border-left: 1px solid #fff;
  text-align: center;
}

.box-card {

  &-header {
    border-bottom: 1px #e1e1e1 solid;
    padding: 0 0 8px 0;
    cursor: pointer;

    &:hover {

    }
  }

  .icon {
    margin-right: 10px;
    color: #344E75;
  }

  &-desc {
    margin: 15px 0;
    font-size: 10px;
  }

  &-footer {
    display: flex;
    justify-content: space-between;
    font-size: 13px;
    align-items: center;
  }

}

</style>
