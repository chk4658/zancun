<template>
  <div>
    <el-submenu v-for="item in circleManage" :key="item.id" index="/circle">
      <template slot="title">
        <i :class="item.icon"></i>
        <span>{{item.name}}</span>
      </template>
      <el-menu-item-group>
        <template slot="title">
          <el-button-group>
            <el-button v-for="child in item.children"
                       style="width: 90px"
                       size="medium"
                       :key="child.id"
                       :type="choose === child.code ? 'primary':'default'"
                       @click="changeBtn">{{child.name}}
            </el-button>
          </el-button-group>
          <i
            style="font-size: 25px;float: right;margin: 6px 15px 0 0;"
            class="el-icon-circle-plus-outline"
            @click="add"
          ></i>

        </template>

        <el-row type="flex" justify="space-around" style="margin: 0 15px 10px 40px">
          <el-input
            size="medium"
            placeholder="请输入名称"
            v-model="searchValue"
            @input="searchBar">
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
        </el-row>

        <div class="menu-style" :style="{maxHeight: visibleHeight}">
<!--          <div-->
<!--            v-infinite-scroll="loadMore" infinite-scroll-disabled="disabled" infinite-scroll-distance='10'>-->
<!--            <el-menu-item v-for="(circle,index) in circles"-->
<!--                          :index="'/circle?id='+circle.id" @click="goCircle(circle.id)" :key="index">-->
<!--              <i class="fa fa-circle-o-notch"></i>-->
<!--              {{ circle.name }}-->
<!--            </el-menu-item>-->
<!--          </div>-->





          <div v-infinite-scroll="loadMore" infinite-scroll-disabled="disabled" infinite-scroll-distance='22'>
            <el-menu-item v-for="(circle,index) in circles"
                          :index="'/circle?id='+circle.id" @click="goCircle(circle.id)" :key="index">
              <el-tooltip class="item" effect="dark" :content="circle.name" placement="top-end"
                          :disabled="isDisabledTooltip">
                <div style="width: 226px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;"
                     @mouseover="onOverflow('all'+circle.id)">
                      <span :ref="'all'+circle.id">
                        <i class="fa fa-list-alt"></i>{{ circle.name }}
                      </span>
                </div>
              </el-tooltip>
            </el-menu-item>
          </div>

          <p v-if="!disabled" style="text-align: center"><i class="el-icon-loading"></i>加载中...</p>




        </div>

      </el-menu-item-group>
    </el-submenu>

    <el-menu-item v-for="item in circleOthers"
                  :key="item.id" :index="item.path||'/constructing-page'">
      <i :class="item.icon"></i>
      <span slot="title">{{item.name}}</span>
    </el-menu-item>

    <!-- 弹出层 -->
    <el-dialog
      class="abow_dialog"
      title="新增圈子"
      @close="closeCircle"
      :visible.sync="dialogVisible"
      append-to-body>
      <circle-edit @saveCircle="save" :circleId="circleId" :parentCircle="null">
      </circle-edit>
    </el-dialog>


  </div>
</template>

<script>
  import {_treeGenerator} from '@/api/sysMenuApi';
  import {
    _queryParentList,
    _queryMyCircles,
  } from '@/api/circleApi';

  import CircleEdit from './CircleEdit.vue';


  function add() {
    this.circleId = '';
    this.dialogVisible = true;
  }

  function closeCircle() {
    this.circleId = '-1';
    this.dialogVisible = false;
  }

  function save(data) {
    this.circleId = '-1';
    this.getCircleList();
    this.dialogVisible = false;
    this.$router.push({path: 'circle', query: {id: data.id}});
  }

  function searchBar() {
    this.getCircleList();
  }


  function changeBtn() {
    if (this.choose === 'MY') {
      this.choose = 'ALL';
    } else {
      this.choose = 'MY';
    }
    this.getCircleList();
  }

  async function getCircleList() {
    let result = null;
    this.resetLoad();
    if (this.choose === 'ALL') {
      result = await _queryParentList({searchName: this.searchValue});
    } else if (this.choose === 'MY') {
      result = await _queryMyCircles({searchName: this.searchValue});
    }
    if (result !== null && result.code === 1200) {
      this.circleList = result.data.circleList;
      this.loadMore();
    }

  }

  function goCirclePush(id) {
    if (this.circleList !== null && this.circleList.length > 0) {
      this.$router.push({
        path: 'circle',
        query: {id: (id !== undefined && id !== null) ? id : this.circleList[0].id}
      });
    } else {
      this.$router.push({path: 'circle'});
    }
  }

  function handleOpen() {

  }

  function handleClose() {

  }

  function goCircle(id) {
    this.$router.push({path: 'circle', query: {id: id}});
  }


  /**
   * 无线滚动
   */
  function resetLoad() {
    this.circles = [];
    this.busy = false;
    this.currentPage = -1;
    this.pageSize = 15;
  }

  function loadMore() {
    setTimeout(() => {
      this.currentPage += 1;
      this.busy = true
      if (this.isLaod()) {
        this.setCurrentPage();
        this.busy = false;
      } else this.busy = true;
    }, 500);
  }


  function isLaod() {
    const length = this.circleList.length;
    const pageSize = this.pageSize;
    const currentPage = this.currentPage;
    const _totalPage = parseInt(length / pageSize);
    const totalPage = length % pageSize > 0 ? _totalPage + 1 : _totalPage;
    return currentPage <= totalPage - 1;
  }


  function setCurrentPage() {
    const datas = this.circleList;
    const currentPage = this.currentPage;
    const pageSize = this.pageSize;
    const _total = (currentPage + 1) * pageSize;
    const total = _total < datas.length ? _total : datas.length;
    this.setCircles(currentPage * pageSize,
      total,
      datas);
  }

  function setCircles(n, total, datas) {
    for (var i = n; i < total; i++) {
      this.circles.push(datas[i]);
    }
  }


  function onOverflow(str) {
    let parentWidth = this.$refs[str][0].parentNode.offsetWidth;
    let contentWidth = this.$refs[str][0].offsetWidth;
    // 判断是否禁用tooltip功能
    this.isDisabledTooltip = contentWidth <= parentWidth;
  }

  export default {
    name: 'CircleParentList',
    components: {CircleEdit},
    data() {
      return {
        dialogVisible: false,
        circleId: '',
        circleList: [],
        circleManage: [],
        circleOthers: [],
        choose: 'MY',
        searchValue: '',
        active: 'CIRCLE',

        //无限滚动
        circles: [],
        busy: false,
        currentPage: -1,
        pageSize: 15,

        isDisabledTooltip: false,
      };
    },
    methods: {
      getCircleList,
      handleClose,
      handleOpen,
      goCircle,
      searchBar,
      changeBtn,
      add,
      closeCircle,
      save,
      goCirclePush,

      // 无限加载
      loadMore,
      isLaod,
      setCurrentPage,
      setCircles,
      resetLoad,

      onOverflow
    },
    created() {
      this.circleId = this.$route.query.id;
      const result = JSON.parse(localStorage.getItem('MENUS'));
      const circleMenus = result
        .filter(menu => menu.groups === 'CIRCLE' && !menu.parentId)
        .map(x => _treeGenerator(x, result));
      const circleManage = circleMenus.filter(menu => menu.code === 'CIRCLE_MANAGE');
      const circleOthers = circleMenus.filter(menu => menu.code !== 'CIRCLE_MANAGE');
      this.circleOthers = circleOthers;
      this.circleManage = circleManage;
      this.getCircleList();

    },
    watch: {
      // $route(val) {
      //   this.circleId = val.query.id;
      //   const result = JSON.parse(localStorage.getItem('MENUS'));
      //   const circleMenus = result
      //     .filter(menu => menu.groups === 'CIRCLE' && !menu.parentId)
      //     .map(x => _treeGenerator(x, result));
      //   const circleManage = circleMenus.filter(menu => menu.code === 'CIRCLE_MANAGE');
      //   const isLengths = circleManage.filter(menu => menu.path === val.path);
      //   if (isLengths > 0) {

      //   }
      //   this.getCircleList();
      // },

    },
    computed: {
      visibleHeight: function () {
        const browserHeight = window.innerHeight;
        const menuNum = this.circleOthers.length + this.circleManage.length + 2;
        const menuHeight = menuNum * 60;
        const headerHeight = 80;
        return (browserHeight - menuHeight - headerHeight) + 'px';
      },
      noMore() {
        return this.circleList.length === this.circles.length
      },
      disabled() {
        return this.busy || this.noMore;
      }
    },
  };
</script>

<style scoped lang="scss">

  .el-menu-item, .el-submenu {
    border-bottom: 1px #e1e1e1 solid;
  }

  .el-submenu__title + .el-menu--inline {
    .el-menu-item {
      border-bottom: 0px #e1e1e1 solid;
    }
  }

  .menu-style {
    overflow-y: auto;
    overflow-x: hidden;
    /* max-height: 700px; */
  }

</style>
