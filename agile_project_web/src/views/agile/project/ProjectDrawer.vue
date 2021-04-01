<template>
  <div style="margin-left: 30px;margin-right: 30px;height: 100%">
    <div ref="header">
      <h1 style="font-size: 20px">{{project.projectName}}</h1>
      <span style="font-size: 16px">{{project.description}}</span>
    </div>

    <el-tabs v-model="activeName" class="m-el-tabs">
      <el-tab-pane label="交付物" name="delivery">
        <deliverables :project-id="project.projectId" :activeName="activeName" v-if="flag"
          :style="{maxHeight: ownHeight + 'px'}" style="overflow-y: auto"></deliverables>
      </el-tab-pane>
      <el-tab-pane label="角色管理" name="role">
        <role-manage :projectId="project.projectId" :activeName="activeName" :authority="authority" v-if="flag"
          :style="{maxHeight: ownHeight + 'px'}" style="overflow-y: auto" @save='getProjectMilestone'></role-manage>
      </el-tab-pane>

      <el-tab-pane label="项目问题" name="projectIssue">
        <project-issue-inner :projectId="project.projectId" :activeName="activeName" :authority="authority" v-if="flag"
          :style="{maxHeight: ownHeight + 'px'}" style="overflow-y: auto" @save='getProjectMilestone'>
        </project-issue-inner>
      </el-tab-pane>

      <el-tab-pane label="归档任务" name="archivingTask">
        <archiving-task :project-id="project.projectId" :activeName="activeName" v-if="flag"
          :style="{maxHeight: ownHeight + 'px'}" style="overflow-y: auto"></archiving-task>
      </el-tab-pane>
      <el-tab-pane label="日志" name="blog">
        <project-log :projectId="project.projectId" :activeName="activeName" v-if="flag"
          :style="{maxHeight: ownHeight + 'px'}" style="overflow-y: auto"></project-log>
      </el-tab-pane>
      <el-tab-pane label="交流" name="chat">
        <chat :foreignId="project.projectId" :type="'PROJECT'" :has="hasProject"></chat>
      </el-tab-pane>

      <el-tab-pane v-if="hasProject" label="未启用" name="notEnable">
        <project-status v-if="activeName === 'notEnable'" :projectId="project.projectId" :status="'NOT_ENABLE'"
          @save='getProjectMilestone'></project-status>
      </el-tab-pane>

      <el-tab-pane v-if="hasProject" label="禁用" name="forbidden">
        <project-status v-if="activeName === 'forbidden'" :projectId="project.projectId" :status="'FORBIDDEN'"
          @save='getProjectMilestone'></project-status>
      </el-tab-pane>

    </el-tabs>
  </div>
</template>

<script>
  import Deliverables from './Deliverables';
  import RoleManage from './RoleManage.vue';
  import ProjectStatus from './ProjectStatus.vue';
  import ProjectLog from "./ProjectLog";
  import Chat from '../chat/Chat';
  import ArchivingTask from "./ArchivingTask";
  import ProjectIssueInner from "./ProjectIssueInner";
  import { _hasByIdAndToken } from '@/api/projectApi';

  function getProjectMilestone() {
    this.$emit('fun', this.project.projectId);
  }


  async function hasByIdAndToken() {
    const result = await _hasByIdAndToken(this.project.projectId);
    this.hasProject = result.data.hasCircle;
  }

  export default {
    name: 'ProjectDrawer',
    props: {
      project: {},
      active: String,
      authority: Boolean,
      refresh: Boolean
    },
    components: {
      ProjectIssueInner,
      ArchivingTask,
      Deliverables,
      RoleManage,
      ProjectStatus,
      ProjectLog,
      Chat
    },
    data() {
      return {
        activeName: '',
        ownHeight: 100,
        flag: false,
        hasProject: false,
      };
    },
    created() {
      this.activeName = this.active;
      this.hasByIdAndToken();
    },
    methods: {
      getProjectMilestone,
      hasByIdAndToken,
    },
    watch: {
      active(val) {
        this.activeName = this.active;
        this.hasByIdAndToken();
      },
      refresh(val) {
        this.flag = false
        this.ownHeight = window.innerHeight - this.$refs.header.offsetHeight - 100
        this.flag = true
      },
    },
    // computed: {
    //   visibleHeight: function () {
    //     const browserHeight = window.innerHeight;
    //     const headerHeight = 210;
    //     return (browserHeight - headerHeight);
    //   },
    // },

    mounted() {
      this.ownHeight = window.innerHeight - this.$refs.header.offsetHeight - 100
      this.flag = true
    }
  };
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
<!--  .el-drawer{-->

<!--    overflow-y: scroll-->
<!--  }-->

<!--</style>-->