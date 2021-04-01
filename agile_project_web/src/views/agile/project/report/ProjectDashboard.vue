<template>
  <div class="container">
    <div class="information">
      <main-top-part>
        <template v-slot:title>
          <div style="display: flex">
            <i :class="menu.icon"
               class="menu-icon"
               style="color: #01408B;
                    font-size: 32px;">
            </i>
            {{ menu.name }}
          </div>

        </template>
        <template v-slot:search>

          <div v-if="activeName === 'open'">
            <el-button type="primary"
                       style="border-radius: 20px !important;margin-right: 10px"
                       size="small"
                       @click='generate'
            >生成报表</el-button>
            <el-input
              size="small"
              placeholder="搜索"
              v-model="searchName"
              style="width: 328px;">
              <i slot="prefix" class="el-input__icon el-icon-search"></i>
            </el-input>
          </div>
        </template>

        <template v-slot:button>
          <div style="margin: 15px 0px 0px 0;">
            <el-button round :style="'background-color:' + ( activeName === 'report'  ? '#DCDFE6;' : ';') + 'width:80px'"
                       @click="activeName = 'report'">报表
            </el-button>
            <el-button round :style="'background-color:' + ( activeName === 'open'  ? '#DCDFE6;' : ';') + 'width:80px'"
                       @click="activeName = 'open'">开阀
            </el-button>
          </div>
        </template>

      </main-top-part>

      <div style="overflow: hidden;height:100%">
        <ProjectReport v-if="activeName === 'report'"></ProjectReport>
        <ProjectOpenList  v-if="activeName === 'open'"  ref="openList" :searchName="searchName" @load="getOpenLoad"
        ></ProjectOpenList>
      </div>
    </div>
  </div>

</template>

<script>

  import MainTopPart from '@/components/MainTopPart';

  import ProjectReport from './ProjectReport';

  import ProjectOpenList from './ProjectOpenList';

  function generate() {
    this.$refs.openList.generate()
  }

  // function getOpenLoad(date) {
  //   debugger;
  // }

  export default {
    components: {
      MainTopPart,
      ProjectReport,
      ProjectOpenList
    },
    data() {
      return {
        menu: [],
        activeName: 'report',
        searchName: '',
        openLoad: false,
      }
    },
    computed: {},
    created: function () {
      const menu = JSON.parse(localStorage.getItem('MENUS'))
        .filter(menu => menu.code === 'PROJECT_DASHBOARD');
      console.log(JSON.parse(localStorage.getItem('MENUS')))
      this.menu = menu[0];
    },
    beforeMount: function () {
    },
    mounted: function () {
    },
    beforeDestroy: function () {
    },
    destroyed: function () {
    },
    methods: {
      generate,
      getOpenLoad(data) {
        this.openLoad = data;
      }
    },
    watch: {},
    directives: {}
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>

  .container {
    width: 100%;
    height: 100%;
    display: flex;
    /*overflow-x: hidden;*/
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
</style>
