<template>

  <div class="side_navigation_bar">
    <div class="inside">
      <img src="./../assets/logo1.png" alt="">

      <message-center></message-center>

      <div class="gap_flex"></div>
      <i class="el-icon-date icon"
         style="margin-bottom: 30px;cursor: pointer" @click="goPath('/calender-task')"></i>
      <i class="el-icon-search icon" style="margin-bottom: 30px"></i>

      <personal-center></personal-center>

    </div>

  </div>

</template>

<script>

import PersonalCenter from './PersonalCenter.vue';
import MessageCenter from '@/layout/MessageCenter.vue';


function goPath(path) {
  this.$router.push(path);
}


export default {
  components: {
    PersonalCenter,
    MessageCenter,
  },
  data() {
    return {};
  },
  methods: {
    goPath,
  },
  created() {
  },
};
</script>
<style lang="scss" scoped>
  .icon {
    font-size: 30px;
    color: #D5D7D8;
    width: 100%;
    text-align: center;
  }

  .side_navigation_bar {
    width: 100px;
    height: 100%;
    position: fixed;
    left: 0;
    top: 0;
    z-index: 0;
    background-color: #292f4c;
    display: flex;
    flex-direction: column;

    .inside {
      width: 76px;
      height: 100%;
      display: flex;
      flex-direction: column;
      justify-content: center;
      align-items: center;
    }

  }

  img {
    margin-top: 18px;
  }

  .gap_flex {
    flex: 1;
  }
</style>
