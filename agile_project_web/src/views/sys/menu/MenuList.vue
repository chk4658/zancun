<template>
  <div class="body" style="overflow-y: auto">
    <main-top-part>
      <template v-slot:title>
        <div style="display: flex">
          <i :class="userIcon" class="menu-icon" style="color: #01408B;font-size: 32px;">
          </i>菜单管理
        </div>
      </template>
      <template v-slot:search>
        <el-input
          size="small"
          placeholder="搜索"
          @input="search"
          style="width: 328px;"
          v-model="searchMenuValue">
          <i slot="prefix" class="el-input__icon el-icon-search"></i>
        </el-input>
      </template>
    </main-top-part>

    <el-row style="margin: 30px 20px 20px;">
      <el-col :span="24">
        <el-table
          class="own-table"
          :data="tableData"
          style="width: 100%;"
          row-key="id"
          :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
          <el-table-column
            prop="name"
            label="菜单名称">
          </el-table-column>
          <el-table-column
            label="分类">
            <template slot-scope="scope">
              <div slot="reference" class="name-wrapper">
                <el-tag size="min"
                        style="height: 23px;line-height: 23px">
                  {{ scope.row.type }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="icon"
            label="图标">
          </el-table-column>
          <el-table-column
            label="类别">
            <template slot-scope="scope">
              <div slot="reference" class="name-wrapper">
                <el-tag size="min"
                        style="height: 23px;line-height: 23px">
                  {{ scope.row.groups }}
                </el-tag>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            prop="code"
            label="编码">
          </el-table-column>
          <el-table-column
            prop="path"
            label="前端路径">
          </el-table-column>
          <el-table-column
            align="right">
            <template slot="header">
              <i class="el-icon-circle-plus"
                 style="font-size: 20px;position: relative;top: 2px;cursor: pointer;"
                 v-has="'MENU_ADD'"
                 @click="addMenu"></i>
            </template>
            <template slot-scope="scope">
              <div class="table-operation-wrap">
                <el-button type="text"
                           v-has="'MENU_EDIT'"
                           @click="editMenu(scope.row.id)">编辑
                </el-button>
                <el-button type="text"
                           v-has="'MENU_DELETE'"
                           @click="deleteMenu(scope.row.id)">删除
                </el-button>
                <el-button type="text"
                           v-has="'MENU_UP'"
                           v-if="scope.row.parentId!==null"
                           @click="upMenu(scope.row.id)">上移
                </el-button>
                <el-button type="text"
                           v-has="'MENU_DOWN'"
                           v-if="scope.row.parentId!==null"
                           @click="downMenu(scope.row.id)">下移
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-col>

    </el-row>


    <el-dialog
      @close="closeForm"
      :visible.sync="form.visible"
      :modal-append-to-body=false
      :title="form.title">
      <menu-edit
        v-if="form.visible"
        :menu-id="form.data.id"
        :on-cancel-handler="closeForm"
        :on-submit-handler="closeForm">
      </menu-edit>
    </el-dialog>
  </div>
</template>

<script>
  import {
    _listMenu,
    _deleteMenu,
    _upMenu,
    _downMenu,
    _searchMenu,
  } from '@/api/sysMenuApi.js';
  import MenuEdit from './MenuEdit.vue';
  import MainTopPart from '@/components/MainTopPart.vue';

  async function listMenu() {
    const result = await _listMenu({
      page: this.tableQuery.page,
      size: this.tableQuery.size,
    });
    this.tableData = result.data.sysMenuList;
  }

  function editMenu(menuId) {
    this.form.data.id = menuId;
    this.form.visible = true;
    this.form.title = '更新菜单';
  }

  function closeForm() {
    this.form.data.id = null;
    this.form.visible = false;
    this.listMenu();
  }

  function addMenu() {
    this.form.data.id = null;
    this.form.title = '新增菜单';
    this.form.visible = true;
  }

  async function deleteMenu(menuId) {
    this.$confirm('是否删除?', '菜单删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        const result = await _deleteMenu(menuId);
        if (result.code === 1200) {
          this.$message({
            type: 'success',
            message: '删除成功!',
            center: true,
          });
          this.listMenu();
        }
      });
  }

  async function upMenu(menuId) {
    const result = await _upMenu(menuId);
    if (result.code === 1200) {
      this.$message({
        type: 'success',
        message: '上移成功!',
        center: true,
      });
      this.listMenu();
    }
  }

  async function downMenu(menuId) {
    const result = await _downMenu(menuId);
    if (result.code === 1200) {
      this.$message({
        type: 'success',
        message: '下移成功!',
        center: true,
      });
      this.listMenu();
    }
  }

  async function searchMenu(a) {
    const result = await _searchMenu({
      value: a,
    });
    if (a === '') {
      this.listMenu();
    } else {
      this.tableData = result.data.search;
    }
  }

  export default {
    data() {
      return {
        tableData: [],
        form: {
          data: {
            id: null,
          },
          visible: false,
          title: '',
        },
        tableQuery: {
          page: 1,
          size: 20,
          total: 0,
        },
        searchMenuValue: '',
        userIcon: '',
      };
    },
    components: {
      MenuEdit,
      MainTopPart,
    },
    methods: {
      search() {
        this.searchMenu(this.searchMenuValue);
      },
      listMenu,
      editMenu,
      closeForm,
      addMenu,
      deleteMenu,
      upMenu,
      downMenu,
      searchMenu,
    },
    created() {
      this.listMenu();
      const userMenus = JSON.parse(localStorage.getItem('MENUS'))
        .filter(menu => menu.code === 'MENU');
      this.userIcon = userMenus.length === 0 ? 'fa fa-bars' : userMenus[0].icon;
    },
  };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->

<style>
  .menu_menubar .department_title span {
    float: right;
  }

  div .cell {
    height: 23px;
  }

  .el-select-dropdown__wrap {
    margin-bottom: -3px !important;
  }
</style>
<style lang="scss">
  .own-table > {

    .el-table__header-wrapper {

      .el-table__header {

        .has-gutter {
          th {
            background-color: #eee;
          }
        }
      }
    }
  }
</style>
