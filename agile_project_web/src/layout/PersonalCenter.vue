<template>

  <div>
    <el-popover
      placement="right"
      width="400"
      :visible-arrow="false"
      v-model="visible"
      :offset="10"
    >
      <div class="menu">
        <el-row>
          <el-col :span="24"
                  class="user-card">
            <i style="margin: 0 0 12px 20px;font-size: 30px;" class="fa fa-user-circle-o"></i>
            <span style="margin: 0 0 12px 20px;font-size: 20px">{{userName}}</span>
          </el-col>
        </el-row>

        <el-row>
          <el-col>
            <el-menu
              default-active="1"
              class="el-menu-vertical-demo">
              <el-menu-item v-for="(menu,index) in userMenus" :key="menu.id"
                            :index="index.toString()" @click="goPath(menu.path)">
                <i :class="menu.icon"></i>
                <span slot="title">{{menu.name}}</span>
              </el-menu-item>
              <el-menu-item index="logout" @click="logout">
                <i class="el-icon-switch-button"></i>
                <span slot="title">退出</span>
              </el-menu-item>
            </el-menu>
          </el-col>
        </el-row>


        <div style="border-top: 1px #e1e1e1 solid;margin-bottom: 20px">
          <div
            style="font-size: 16px; text-align: left; color: black;
              margin: 20px">
            工作状态
          </div>
          <dict-radio dict-code="WORK_STATUS" v-model="workStatus" style="padding-left: 20px"/>
        </div>
      </div>

      <el-button type="text" class="fa fa-user-circle-o icon" slot="reference"></el-button>
    </el-popover>
  </div>

</template>

<script>

import DictRadio from '../components/DictRadio.vue';
import {console} from "vuedraggable/src/util/helper";

function goPath(path) {
  this.$router.push(path);
  this.visible = false;
}

function logout() {
  this.$store.commit('clearLoginInfo');
  this.$router.push({ name: 'login', params:{autoLogin: false}});
}


export default {
  components: {
    DictRadio,
  },
  data() {
    return {
      userName: localStorage.getItem('userName'),
      visible: false,
      userMenus: [],
      workStatus: '2',
    };
  },
  methods: {
    goPath,
    logout,
  },
  created() {
    // console.log(JSON.parse(localStorage.getItem('BUTTONS')))
    const userMenus = JSON.parse(localStorage.getItem('MENUS'))
      .filter(menu => menu.groups === 'PERSONAL');
    console.log(userMenus)
    this.userMenus = userMenus;
  },
};
</script>
<style lang="scss" scoped>
  .icon {
    font-size: 30px;
    color: #D5D7D8;
    width: 100%;
    text-align: center;
    margin-bottom: 20px;
  }

  .el-menu-vertical-demo {
    li {
      height: 50px;
      line-height: 50px;
      border-radius: 4px;
    }
  }


  .user-card {
    border-bottom: 1px solid #D5D7D8;
    height: 50px;
    display: flex;
    align-items: center
  }


</style>
