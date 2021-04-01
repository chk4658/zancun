<template>
  <div style="height: 100%; border-left: 1px #e1e1e1 solid;display: flex">
    <role-menu-bar @forRoleId="acceptRoleId" id="rmb" :style="{width: rmbWidth}"></role-menu-bar>
    <div style="width: 5px;height: 100%;background-color:#e1e1e1; cursor: pointer"
         @mousedown="drag"></div>
    <div class="information">
      <main-top-part>
        <template v-slot:title>
          <div style="display: flex">
            <i :class="userIcon"
               class="menu-icon"
               style="color: #01408B;
                    font-size: 32px;">
            </i>
            权限管理
          </div>
        </template>
        <template v-slot:search>
          <el-input
            size="small"
            placeholder="请输入姓名搜索"
            @input="findUserByRoleId"
            style="width: 328px;"
            v-if="1==display"
            v-model="searchUserValue"><i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
        </template>
        <template v-slot:button>
          <el-button-group style="margin-top: 20px">
            <el-button :type="0==display?'primary':'default'"
                       style="border-top-left-radius: 4px;border-bottom-left-radius: 4px"
                       size="medium"
                       @click="showMenu">角色菜单
            </el-button>
            <el-button :type="1==display?'primary':'default'"
                       style="border-top-right-radius: 4px;border-bottom-right-radius: 4px"
                       size="medium"
                       @click="showTable">角色成员
            </el-button>
          </el-button-group>
        </template>
      </main-top-part>

      <el-row style="margin: 10px 0 0 20px" v-if="!showMenuAvail">
        <el-col :span="12">
          <el-button
            type="text"
            @click="bindRoleUser"
            v-has="'ROLE_USER_ADD'"
            size="middle">
            <i class="el-icon-edit-outline"></i>
            绑定
          </el-button>

          <el-button
            type="text"
            @click="unbind"
            v-has="'ROLE_USER_ADD'"
            size="middle">
            <i class="el-icon-link"></i>
            解绑
          </el-button>
        </el-col>
      </el-row>


      <el-row style="margin: 10px 20px 80px;overflow-y: auto;overflow-x: hidden">
        <el-col :span="24">
          <transition name="fadeRole" mode="out-in">
            <el-tree
              :data="menuData"
              v-if="showMenuAvail"
              class="own-tree"
              check-strictly
              show-checkbox
              default-expand-all
              node-key="id"
              ref="tree"
              highlight-current
              :props="defaultProps">
            </el-tree>
            <el-table
              :data="tableData"
              class="own-table"
              @selection-change="handleSelectionChange"
              v-else
              style="width: 100%">
              <el-table-column
                type="selection"
                width="55">
              </el-table-column>
              <el-table-column
                prop="realName"
                label="姓名">
              </el-table-column>
              <el-table-column
                prop="departmentName"
                label="部门">
              </el-table-column>
              <el-table-column
                prop="mobile"
                label="联系方式">
              </el-table-column>
              <el-table-column
                prop="sex"
                label="性别">
              </el-table-column>
            </el-table>
          </transition>

        </el-col>
      </el-row>

      <el-row class="page">
        <el-col :span="24">
          <div v-has="'ROLE_MENU_ADD'">
            <el-button
              type="primary"
              size="small"
              v-if="showMenuAvail"
              @click="getCheckedKeys"
              style="border-radius: 5px;width: 120px">
              保存
            </el-button>
          </div>

          <el-pagination
            v-if="!showMenuAvail"
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
            background
            :current-page="this.tableQuery.page"
            :page-sizes="[15, 30, 50, 100]"
            :page-size="this.tableQuery.size"
            style="text-align: left;"
            layout="total, sizes, prev, pager, next, jumper"
            :total="this.tableQuery.total">
          </el-pagination>
        </el-col>
      </el-row>


      <!--      选择人员与角色绑定-->
      <el-dialog
        title="选择人员"
        @close="closeOwner"
        :visible.sync="ownerDialogVisible"
        width="1200px"
        append-to-body>
        <user-tree
          :isSingle='false'
          :usersProp='owners'
          @getUsers="getOwners"
          @getCancel="closeOwner">
        </user-tree>
      </el-dialog>
    </div>
  </div>
</template>
<script>
  import {_findUserByRoleId, _findMenuByRoleId, _bindMenu, _bindUser, _deleteByRoleIdAndUserId} from '@/api/sysRoleApi';
  import {_listMenu} from '@/api/sysMenuApi';
  import RoleMenuBar from './RoleMenuBar.vue';
  import MainTopPart from '@/components/MainTopPart.vue';
  import UserTree from '@/components/UserTree.vue';

  async function listMenu() {
    const result = await _listMenu();
    this.menuData = result.data.sysMenuList;
  }

  async function findUserByRoleId() {
    const result = await _findUserByRoleId(
      {
        page: this.tableQuery.page,
        size: this.tableQuery.size,
        roleId: this.publicRoleId,
        searchName: this.searchUserValue.replace(/(^\s*)|(\s*$)/g, ''),
      },
    );
    this.tableData = result.data.sysUserList;
    this.tableQuery.total = result.data.totalAmount;

  }

  async function findMenuByRoleId() {
    const result = await _findMenuByRoleId(this.publicRoleId);
    const idArray = [];
    result.data.sysMenus.forEach((item) => {
      idArray.push(item.id);
    });
    if (this.showMenuAvail) {
      setTimeout(() => {
        this.$refs.tree.setCheckedKeys(idArray);
      }, 300);
    }
  }

  async function getCheckedKeys() {
    const a = this.$refs.tree.getCheckedKeys()
      .join(',');
    const result = await _bindMenu(this.publicRoleId, {
      menuIds: a,
    });
    if (result.code === 1200) {
      this.$message({
        message: '更新成功',
        type: 'success',
        center: true,
      });
    }
  }

  function closeOwner() {
    this.owners = [];
    this.ownerDialogVisible = false;
  }

  async function getOwners(data) {

    const aa = [];
    data.forEach((item) => {
      aa.push(item.id);
    });
    const userIdsString = aa.join(',');
    const result = await _bindUser(this.publicRoleId, {
      userIds: userIdsString,
    });
    if (result.code === 1200) {
      this.$message({
        message: '更新成功',
        type: 'success',
        center: true,
      });
      await this.findUserByRoleId();
    }
  }

  async function bindRoleUser() {
    this.ownerDialogVisible = true;
  }

  function handleSelectionChange(val) {
    this.rest = [];
    if (val.length === 0) {
      this.rest = [];
    } else {
      val.forEach(ch => this.rest.push(ch.id));
    }
  }

  async function unbind() {

    if (this.rest.length === 0) {
      this.$message({
        type: 'warning',
        message: '未选择!',
        center: 'true'
      });
    } else {
      const userIdsString = this.rest.join(',');
      const result = await _deleteByRoleIdAndUserId({
        roleId: this.publicRoleId,
        userIds: userIdsString
      })
      if (result.code === 1200) {
        this.$message({
          message: '解绑成功',
          type: 'success',
          center: true,
        });
        this.tableQuery.page = 1;
        await this.findUserByRoleId();
      }
    }

  }

  function drag(event) {

    const e = event || window.event;
    const w = parseInt(document.getElementById('rmb').style.width);

    const oriX = e.clientX;


    document.onmousemove = (e) => {

      const go = e.clientX - oriX;
      document.getElementById('rmb').style.width = w + go + 'px';


    };

    document.onmouseup = (e) => {

      this.rmbWidth = document.getElementById('rmb').style.width;
      document.onmousemove = null;
      document.onmouseup = null;
    };


  }

  export default {
    data() {
      return {
        publicRoleId: '-1',
        tableData: [],
        tableQuery: {
          page: 1,
          size: 15,
          total: 0,
        },
        menuData: [],
        defaultProps: {
          children: 'children',
          label: 'name',
        },
        display: '0',
        showMenuAvail: true,
        userIcon: '',
        owners: [],
        ownerDialogVisible: false,
        rest: [],
        allData: [],
        rmbWidth: '315px',
        searchUserValue: ''
      };
    },
    components: {
      RoleMenuBar,
      MainTopPart,
      UserTree,
    },
    methods: {
      acceptRoleId(data) {

        this.tableQuery.page = 1;
        this.publicRoleId = data;
        this.findUserByRoleId();
        this.findMenuByRoleId();
      },
      append(data) {
        const newChild = {
          id: id++,
          label: 'testtest',
          children: [],
        };
        if (!data.children) {
          this.$set(data, 'children', []);
        }
        data.children.push(newChild);
      },
      remove(node, data) {
        const parent = node.parent;
        const children = parent.data.children || parent.data;
        const index = children.findIndex(d => d.id === data.id);
        children.splice(index, 1);
      },
      handlePageChange(val) {
        this.tableQuery.page = val;
        this.findUserByRoleId();
      },
      handleSizeChange(val) {
        this.tableQuery.size = val;
        this.findUserByRoleId();
      },
      showMenu() {
        this.display = 0;
        this.showMenuAvail = true;
        this.listMenu()
          .then(() => {
            this.findMenuByRoleId();
          });
      },
      showTable() {
        this.display = 1;
        this.showMenuAvail = false;
      },
      findUserByRoleId,
      findMenuByRoleId,
      getCheckedKeys,
      listMenu,
      closeOwner,
      getOwners,
      bindRoleUser,
      handleSelectionChange,
      unbind,
      drag,

    },
    created() {
      this.listMenu();
      const userMenus = JSON.parse(localStorage.getItem('MENUS'))
        .filter(menu => menu.code === 'ROLE');
      this.userIcon = userMenus.length === 0 ? 'fa fa-black-tie' : userMenus[0].icon;
    },
  };
</script>
<style>
  .fadeRole-enter-active, .fadeRole-leave-active {
    transition: all .3s ease;
  }

  .fadeRole-enter, .fadeRole-leave-to /* .fadeRole-leave-active below version 2.1.8 */
  {
    opacity: 0;
    transform: translateX(10px)
  }

  .page {
    position: absolute;
    bottom: 20px;
    left: 20px;
  }

  .information {
    width: 100%;
    flex: 1;
    background-color: #fff;
    float: left;
    display: flex;
    flex-direction: column;
    position: relative;
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

  .own-tree > {
    .el-tree-node {
      .el-tree-node__content {
        height: 40px;
      }
    }
  }
</style>
