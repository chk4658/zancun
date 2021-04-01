<template>
  <div>
    <el-submenu v-for="(item,index) in projectManage" :key="item.id" :index="'/project'+index">
      <template slot="title">
        <i :class="item.icon"></i>
        <span>{{ item.name }}</span>
      </template>
      <el-menu-item-group>
        <template slot="title" v-if="item.code === 'PROJECT_MANAGE' && item.children && item.children.length > 0">
          <el-button-group>
            <el-button v-for="child in item.children"
                       style="width: 65px"
                       size="mini"
                       :key="child.id"
                       :type="choose === child.code ? 'primary':'default'"
                       @click="changeBtn(child.code)">{{ child.name }}
            </el-button>
          </el-button-group>

          <el-popover
            placement="right-start"
            width="180"
            :visible-arrow="false"
          >
            <el-row>
              <el-col>
                <el-menu
                  @select="handleMenuSelect"
                  default-active="ADD"
                  class="el-menu-vertical-demo">
                  <el-menu-item index="ADD" @click="add('1')">
                    <span slot="title">空白页</span>
                  </el-menu-item>
                  <el-menu-item index="IMPORT_TRL_ADD" @click="importAdd">
                    <span slot="title">模板导入</span>
                  </el-menu-item>
                  <el-menu-item index="IMPORT_ADD" @click="importAdd">
                    <span slot="title">项目导入</span>
                  </el-menu-item>
                  <el-menu-item index="EXCEL_DOWN" @click="downloadExcel">
                    <span slot="title">EXCEL模板下载</span>
                  </el-menu-item>
                  <el-menu-item index="EXCEL_ADD" @click="add('1')">
                    <span slot="title">EXCEL导入</span>
                  </el-menu-item>
                </el-menu>
              </el-col>
            </el-row>
            <i class="el-icon-circle-plus-outline"
               style="font-size: 25px;float: right;margin: 2px 13px 0 0;cursor: pointer"
               slot="reference"></i>
          </el-popover>

        </template>
        <div v-show="!(item.code==='PROJECT_MANAGE'&&choose==='NOT_ONLINE')">
          <div v-if="item.children && item.children.length > 0">
            <el-row type="flex" justify="space-around" style="margin: 0 15px 10px 40px">
              <el-input
                size="medium"
                placeholder="请输入名称"
                v-model="searchValue"
                @input="searchBar">
                <i slot="prefix" class="el-input__icon el-icon-search"></i>
              </el-input>
            </el-row>

            <div class="temporary-style"
                 v-if="choose==='MY'"
                 @click="goTemporaryTask">

                <span>
                  <i class="fa fa-list-alt" style="color: #909399;margin-right: 1px"></i>
                  临时任务
                </span>
            </div>

            <div class="menu-style" :style="{maxHeight: visibleHeight}">
              <div v-infinite-scroll="loadMore" infinite-scroll-disabled="disabled" infinite-scroll-distance='22'>
                <el-menu-item v-for="(project,index) in projects"
                              :index="'/project?id='+project.id" @click="goProject(project.id)" :key="index">
                  <el-tooltip class="item" effect="dark" :content="project.name" placement="top-end"
                              :disabled="isDisabledTooltip">
                    <div style="width: 226px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;"
                         @mouseover="onOverflow('all'+project.id)">
                      <span :ref="'all'+project.id">
                        <i class="fa fa-list-alt"></i>{{ project.name }}
                      </span>
                    </div>
                  </el-tooltip>
                </el-menu-item>
              </div>

              <p v-if="!disabled" style="text-align: center"><i class="el-icon-loading"></i>加载中...</p>
            </div>
          </div>
          <div v-else>
            <el-row type="flex" justify="space-around" style="margin: 0px 15px 10px 40px">
              <el-input
                size="medium"
                placeholder="请输入名称"
                v-model="searchValueFavorite"
                @input="getFavoriteList">
                <i slot="prefix" class="el-input__icon el-icon-search"></i>
              </el-input>
            </el-row>

            <div class="menu-style" :style="{maxHeight: visibleHeight}">
              <el-menu-item v-for="(project,index) in projectListFavorite"
                            :index="'/project?id='+project.id" @click="goProject(project.id)" :key="index">
                <el-tooltip class="item" effect="dark" :content="project.name" placement="top-end"
                            :disabled="isDisabledTooltip">
                  <div style="width: 226px;overflow: hidden;white-space: nowrap;text-overflow: ellipsis;"
                       @mouseover="onOverflow('fav'+project.id)">
                  <span :ref="'fav'+project.id">
                    <i class="fa fa-list-alt"></i>{{ project.name }}
                  </span>
                  </div>
                </el-tooltip>
              </el-menu-item>
            </div>

          </div>
        </div>


      </el-menu-item-group>
    </el-submenu>

    <el-menu-item v-for="item in projectOthers"
                  :key="item.id" :index="item.path||'/constructing-page'">
      <i :class="item.icon"></i>
      <span slot="title">{{ item.name }}</span>
    </el-menu-item>

    <!--    新增项目弹出框-->
    <el-dialog
      title="新增项目"
      @close="closeProject"
      :visible.sync="dialogVisible"
      append-to-body>
      <project-add
        @saveProject="save"
        @getProject='getProject'
        :projectId="propProjectId"
        :parentCircle="null"
        :addType="activeIndex === 'EXCEL_ADD'"
        :projectType='projectType'></project-add>
    </el-dialog>

    <!-- 导入项目 -->
    <el-dialog :width="activeIndex === 'IMPORT_TRL_ADD' ? '80%': '70%'"
               :title="activeIndex === 'IMPORT_TRL_ADD' ? '模板导入': '项目导入'"
               @close="closeProjectImport"
               :visible.sync="importDialogVisible"
               append-to-body>
      <ProjectTelImport v-if="activeIndex === 'IMPORT_TRL_ADD'" @getNodes="getNodes"
                        :key="propProjectId"></ProjectTelImport>
      <ProjectImport v-if="activeIndex === 'IMPORT_ADD'" @getNodes="getProjectNodes"
                     :key="propProjectId"></ProjectImport>
    </el-dialog>
  </div>
</template>

<script>
  import {_treeGenerator} from '@/api/sysMenuApi';

  import ProjectAdd from './ProjectAdd';

  import ProjectTelImport from './ProjectTelImport';

  import ProjectImport from './ProjectImport';


  import {_importProject, _importByExistProject} from '@/api/projectApi';

  import {
    _queryAllProjectList,
    _queryOwnerProjectList,
    _queryNotOnlineProjectList,
  } from '@/api/projectApi';

  import {_userFavorites} from '@/api/projectCollectionApi';


  import {downloadFile} from '@/api/utils';


  function changeBtn(choose) {
    console.log(choose)
    this.choose = choose;
    this.getList();
  }

  function goPath(path) {
    this.$router.push(path);
  }


  async function getList() {
    this.getFavoriteList();
    this.resetLoad();
    let result = null;
    if (this.choose === 'ALL') {
      this.projectList = [];
      result = await _queryAllProjectList({searchName: this.searchValue});

      if (result !== null && result.code === 1200) {
        this.projectList = result.data.allVisibleProject;
        this.loadMore();
      }
    } else if (this.choose === 'MY') {
      this.projectList = [];
      result = await _queryOwnerProjectList({searchName: this.searchValue});
      if (result !== null && result.code === 1200) {
        this.projectList = result.data.curUserProject;
        this.loadMore();
      }
    } else if (this.choose === 'NOT_ONLINE') {
      this.$router.push({path: '/project-online'});
    }


    //  else if (this.choose === 'NOT_ONLINE') {
    //   // result = await _queryNotOnlineProjectList({searchName: this.searchValue});
    //   // if (result !== null && result.code === 1200) {
    //   //   this.projectList = result.data.notOnlineProjectList;
    //   //   this.loadMore();
    //   // }
    //   this.$router.push({ path: '/project-online'  });
    // }

  }


  function goPush(id) {
    if ((this.projectList !== null && this.projectList.length > 0) || (id !== undefined && id !== null)) {
      this.$router.push({
        path: 'project',
        query: {
          id: (id !== undefined && id !== null) ? id : this.projectList[0].id,
        },
      });
    } else {
      this.$router.push({
        path: 'project',
      });
    }
  }


  async function searchBar() {
    this.resetLoad();
    let result = null;
    if (this.choose === 'ALL') {
      result = await _queryAllProjectList({searchName: this.searchValue});
      if (result !== null && result.code === 1200) {
        this.projectList = result.data.allVisibleProject;
        this.loadMore();
      }
    } else if (this.choose === 'MY') {
      result = await _queryOwnerProjectList({searchName: this.searchValue});
      if (result !== null && result.code === 1200) {
        this.projectList = result.data.curUserProject;
        this.loadMore();
      }
    }
  }

  async function getFavoriteList() {
    const result = await _userFavorites({searchName: this.searchValueFavorite});
    if (result.code === 1200) {
      this.projectListFavorite = result.data.favoriteProject;
      this.collectionsCurrent = [];
      result.data.favoriteProject.forEach((item) => {
        this.collectionsCurrent.push(item.id);
      });
      localStorage.setItem('collections', this.collectionsCurrent);
    }
  }

  function add(projectType) {
    this.propProjectId = '';
    this.projectType = projectType;
    this.dialogVisible = true;
  }


  function closeProject() {
    this.propProjectId = '-1';
    this.projectType = '';
    this.dialogVisible = false;
  }

  function handleOpen() {

  }

  function handleClose() {

  }

  async function goProject(id) {
    this.$router.push({
      path: 'project',
      query: {
        id: id,
      },
    });
  }


  function save() {
    this.propProjectId = '-1';
    this.getList();
    this.dialogVisible = false;
  }

  function onOverflow(str) {
    let parentWidth = this.$refs[str][0].parentNode.offsetWidth;
    let contentWidth = this.$refs[str][0].offsetWidth;
    // 判断是否禁用tooltip功能
    this.isDisabledTooltip = contentWidth <= parentWidth;
  }

  function importAdd() {
    this.propProjectId = '';
    this.importDialogVisible = true;
  }

  function closeProjectImport() {
    this.propProjectId = '-1';
    this.importDialogVisible = false;
  }

  async function getProject(data, projectType) {
    if (projectType === '3') {
      const result = await _importProject(data.id, this.importData);
    } else if (projectType === '4') {
      const result = await _importByExistProject(data.id, this.importData);
    }
    this.$router.push({
      path: 'project',
      query: {id: data.id},
    });
  }

  function getNodes(data) {
    if (data === null || data.length === 0) {
      this.$message({
        type: 'error',
        message: '未选择需要导入的模板',
      });
    } else {
      this.importData = data;
      this.closeProjectImport();
      this.add('3');
    }
  }

  async function getProjectNodes(data) {
    if (data === null || data.length === 0) {
      this.$message({
        type: 'error',
        message: '未选择需要导入的项目',
      });
    } else {
      this.importData = data;
      this.closeProjectImport();
      this.add('4');
    }
  }


  /**
   * 无限滚动
   */
  function resetLoad() {
    this.$nextTick(function () {
      this.projects = [];
      this.busy = false;
      this.currentPage = -1;
      this.pageSize = 15;
    });
  }

  function loadMore() {
    setTimeout(() => {
      this.currentPage += 1;
      this.busy = true
      console.log('asdasdasda' + this.isLaod())
      if (this.isLaod()) {
        this.setCurrentPage();
        this.busy = false;
      } else this.busy = true;
    }, 300);
  }


  function isLaod() {
    const length = this.projectList.length;
    const pageSize = this.pageSize;
    const currentPage = this.currentPage;
    const _totalPage = parseInt(length / pageSize);
    const totalPage = length % pageSize > 0 ? _totalPage + 1 : _totalPage;
    return currentPage <= totalPage - 1;
  }


  function setCurrentPage() {
    const datas = this.projectList;
    const currentPage = this.currentPage;
    const pageSize = this.pageSize;
    const _total = (currentPage + 1) * pageSize;
    const total = _total < datas.length ? _total : datas.length;
    this.setProjects(currentPage * pageSize,
      total,
      datas);
  }

  function setProjects(n, total, datas) {
    for (var i = n; i < total; i++) {
      this.projects.push(datas[i]);
    }
  }


  function handleMenuSelect(key, keyPath) {
    this.activeIndex = key;
  }

  function goTemporaryTask() {

    this.$router.push({
      path: '/temporaryTask',
    });
  }


  function downloadExcel() {
    downloadFile('data/project/excelTemplate.xlsx').then((response1) => {
      const fileName = 'excelTemplate.xlsx';
      const fileData = response1;
      if (fileData !== null) {
        const blob = new Blob([fileData]);
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.style.display = 'none';
        link.href = url;
        link.setAttribute('download', fileName);
        document.body.appendChild(link);
        link.click();
      }
    });
  }


  export default {
    name: 'ProjectParentList',
    props: {
      id: String,
    },
    components: {
      ProjectAdd,
      ProjectTelImport,
      ProjectImport,
    },
    data() {
      return {
        propProjectId: '',
        dialogVisible: false,
        projectId: '',
        projectList: '',
        projectListFavorite: '',
        projectManage: [],
        projectOthers: [],
        choose: 'MY',
        searchValue: '',
        searchValueFavorite: '',
        collectionsCurrent: [],
        active: 'PROJECT',
        isDisabledTooltip: false,


        projectType: '',


        //项目导入
        importDialogVisible: false,
        importData: [],

        //无限滚动
        projects: [],
        busy: false,
        currentPage: -1,
        pageSize: 15,

        innerHeight: window.innerHeight,

        activeIndex: '',


      };
    },
    methods: {
      getList,
      handleClose,
      handleOpen,
      goProject,
      changeBtn,
      goPath,
      add,
      closeProject,
      save,
      searchBar,
      getFavoriteList,
      onOverflow,
      goPush,
      getProject,
      // 项目导入
      importAdd,
      closeProjectImport,
      getNodes,

      // 无限加载
      loadMore,
      isLaod,
      setCurrentPage,
      setProjects,
      resetLoad,


      handleMenuSelect,
      getProjectNodes,
      goTemporaryTask,
      downloadExcel
    },
    created() {


      const result = JSON.parse(localStorage.getItem('MENUS'));
      const projectMenus = result
        .filter(menu => menu.groups === 'PROJECT' && !menu.parentId)
        .map(x => _treeGenerator(x, result));
      const projectManage = projectMenus.filter(menu => menu.code === 'PROJECT_FAVORITE' || menu.code === 'PROJECT_MANAGE');
      const projectOthers = projectMenus.filter(menu => menu.code !== 'PROJECT_MANAGE' && menu.code !== 'PROJECT_FAVORITE');

      this.projectOthers = projectOthers;
      this.projectManage = projectManage;


      this.getList();

      // const path = this.$route.path;
      // if (path === '/project') {
      //   this.getList();
      //   this.getFavoriteList();
      // }
    },
    mounted() {
      window.onresize = () => {
        this.innerHeight = window.innerHeight;
      };
      this.$root.$on('getList', res => {
        this.getList();
      });
    },
    watch: {
      getLocal(val) {
        if (val) {
          this.getFavoriteList();
          this.$store.commit('changeCollection', false);
        }
      },
      getEditOrDel(val) {
        if (val) {
          this.getList();
          this.$store.commit('changeProjectEditOrDel', false);
        }
      },
      innerHeight(newValue, oldValue) {
        if (newValue) {
          this.innerHeight = newValue
        }
      },
    },
    computed: {
      visibleHeight() {
        const browserHeight = this.innerHeight;
        const menuNum = this.projectOthers.length + this.projectManage.length + 2;
        const menuHeight = menuNum * 60;
        const headerHeight = 100;
        return (browserHeight - menuHeight - headerHeight) + 'px';
      },
      getLocal() {
        return this.$store.state.collection;
      },
      noMore() {
        return this.projectList.length === this.projects.length
      },
      disabled() {
        return this.busy || this.noMore
      },

    },
  };
</script>

<style lang="scss" scoped>

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
  }

  .temporary-style {
    width: 283px;
    height: 50px;
    padding-left: 40px;
    box-sizing: border-box;
    line-height: 50px;
    cursor: pointer;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    font-size: 14px;
    color: #303133;
    vertical-align: middle;
    /*border: 1px dashed #ccc;*/

    &:hover {
      color: white;
      background-color: #ccc;
    }
  }

</style>
