<template>
  <div style="margin-left: 30px;margin-right: 30px;">
    <!-- <h1>{{user.realName}}</h1> -->
    <h1>{{circle.name}}
      <div style="font-size: 10px;padding-top:5px">{{circle.description}}</div>
    </h1>
    <el-tabs v-model="activeName" >
      <el-tab-pane label="交流" name="chat" >
        <chat :foreignId="circle.id" :type="'CIRCLE'" :key="new Date().getTime()" :has="hasCircle"></chat>
      </el-tab-pane>
      <el-tab-pane label="角色名称" name="role">
        <circle-role :circleId='circle.id' :key="new Date().getTime()"></circle-role>
      </el-tab-pane>
      <el-tab-pane label="日志" name="log">
        <circle-log :circleId='circle.id' :key="new Date().getTime()" ></circle-log>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>

import CircleRole from './CircleRole';

import CircleLog from './CircleLog';

import Chat from '../../chat/Chat';

import { _hasByIdAndToken } from '@/api/circleApi';



async function  hasByIdAndToken() {
   const result = await  _hasByIdAndToken(this.circle.id);
   this.hasCircle = result.data.hasCircle;
}

export default {
  name: 'CircleDrawer',
  props: {
    user: Object,
    circle: Object,
    activeName: String,
  },
  components: { CircleRole,CircleLog,Chat },
  data () {
    return {

      hasCircle: false,

    }
  },
  computed: {},
  created: function () {
    this.hasByIdAndToken();
  },
  beforeMount: function () {},
  mounted: function () {},
  beforeDestroy: function () {},
  destroyed: function () {},
  methods: {
    hasByIdAndToken
  },
  watch: {
    circle(val) {
      this.hasByIdAndToken();
    }
  },
  directives: {}
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
