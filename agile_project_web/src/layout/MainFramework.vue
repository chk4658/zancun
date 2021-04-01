<template>
  <div style="height: 100%;overflow-y: hidden">
    <el-container style="height: 100%;overflow-y: hidden">
      <el-aside :width="collapsed?'140px':'359px'" class="aside">
        <side-bar></side-bar>
        <item-menu-bar :is-collapse="collapsed"></item-menu-bar>

        <span class="iconfont_icon_left" @click="putAwayLetGo">
          <i v-if="isLeft" class="el-icon-arrow-left"></i>
          <i v-if="!isLeft" class="el-icon-arrow-right"></i>
        </span>
      </el-aside>

      <el-container style="overflow-y: hidden">
        <el-main style="padding: 0;overflow-y: hidden">
          <router-view/>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import SideBar from './SideBar.vue';
import ItemMenuBar from './ItemMenuBar.vue';

export default {
  components: {
    SideBar,
    ItemMenuBar,
  },
  data() {
    return {
      collapsed: false,
      isLeft: true,
    };
  },
  computed: {
    title() {
      return this.$route.meta.title;
    },
  },
  methods: {
    putAwayLetGo() {
      this.collapsed = !this.collapsed;
      this.isLeft = !this.isLeft;
    },
  },
};
</script>
<style scoped>
  .aside {
    position: relative;
    transition: width 0.3s, transform 2s;
    overflow: visible;
  }

  .iconfont_icon_left {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    text-align: center;
    line-height: 30px;
    font-size: 20px;
    cursor: pointer;
    background-color: #fff;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    position: absolute;
    right: -15px;
    top: 19px;
    border: 1px #e1e1e1 solid;
    z-index: 999;
  }

</style>
