<template>
  <div>
    <el-submenu
      v-if="menu.children&& menu.children.length>0"
      :key="menu.id"
      :index="menu.path||'/constructing-page'">
      <template slot="title">
        <i :class="menu.icon"/>
        <span>{{$t(menu.code)||menu.name}}</span>
      </template>
      <menu-item :menu="child" :key="child.id" v-for="child in menu.children"/>
    </el-submenu>
    <el-menu-item :key="menu.id" :index="menu.path||'/constructing-page'" v-else>
      <i :class="menu.icon"/>
      <span slot="title">{{$t(menu.code)||menu.name}}</span>
    </el-menu-item>
  </div>
</template>

<script>
export default {
  name: 'MenuItem',
  props: {
    menu: Object,
  },
};
</script>

<style scoped>

</style>
