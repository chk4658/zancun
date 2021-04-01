<template>
  <div style="margin-left: 30px;margin-right: 30px;">
    <div ref="header">
      <h1 style="font-size: 20px">{{task.name}}</h1>
      <span style="font-size: 16px">{{task.description}}</span>
    </div>
    <el-tabs v-model="activeName" class="m-el-tabs">

      <el-tab-pane label="任务日志" name="taskBlog">

        <task-log :task-id="task.id" :activeName="activeName" :refresh="refresh"
                  :style="{maxHeight: visibleHeight + 'px'}" style="overflow-y: auto;"></task-log>

      </el-tab-pane>
      <el-tab-pane label="交流" name="taskChat">
        <chat :foreignId="task.id" :type="'TASK'" :has="hasProject" :key="new Date().getTime()"></chat>
      </el-tab-pane>


    </el-tabs>
  </div>
</template>

<script>
  import TaskLog from "./TaskLog";

  import Chat from '../../../chat/Chat';

  import { _hasByIdAndToken } from '@/api/projectApi';


  async function  hasByIdAndToken() {
    const result = await _hasByIdAndToken(this.task.projectId);
    this.hasProject = result.data.hasCircle;
  }


  export default {
    name: 'TaskDrawer',
    props: {
      task: {},
      active: String,
      refresh: Boolean
    },
    components: {
      TaskLog,
      Chat
    },
    data() {
      return {
        activeName: '',
        visibleHeight: 100,
        flag: false,
        hasProject: false,
      };
    },
    created() {

      this.activeName = this.active;
      this.hasByIdAndToken();
    },
    methods: {
      hasByIdAndToken
    },
    watch: {
      // active(val) {
      //   this.activeName = this.active;
      //
      // },
      refresh(val) {
        this.flag = false
        this.visibleHeight = window.innerHeight - this.$refs.header.offsetHeight - 130
        this.flag = true;
        if (val) {
          this.activeName = this.active;
        }
        hasByIdAndToken
      },
      task() {
        this.hasByIdAndToken();
      }
    },
    mounted() {
      this.visibleHeight = window.innerHeight - this.$refs.header.offsetHeight - 130
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
