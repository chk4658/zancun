<template>
  <div style="background-color: #fff;position: relative;
    overflow-y: auto;overflow-x: hidden">

    <el-row :gutter="20" style="margin-top: 50px">
      <el-col :span="18">
        <span style="font-size: 20px;font-weight: bolder;margin-left: 15px;">角色管理</span>
      </el-col>
      <el-col :span="6">
        <i style="font-size: 25px;float: right;
        margin-right: 15px;color: #01408B;cursor: pointer" class="el-icon-circle-plus-outline"
           v-has="'ROLE_ADD'"
           @click="addRoleIcon"></i>
      </el-col>
    </el-row>

    <el-row type="flex" justify="space-around" style="margin: 30px 15px">
      <el-input
        placeholder="请输入名称"
        v-model="searchValue"
        size="medium"
        @input="searchBar">
        <i slot="prefix" class="el-input__icon el-icon-search"></i>
      </el-input>
    </el-row>

    <div class="block" style="margin-top: 30px;">
      <div
        v-for="item in listRoleData"
        :key="item.id"
        :label="item.name"
        @mouseenter="item.isHover=true"
        @mouseleave="item.isHover=false"
        @click="exposeRoleId(item)"
        :class="item.isCurrent ? 'current role_list' : 'role_list'">
        <div style="float: left;position: relative;">
          <span
            v-if="!item.isEditing"
            style="-webkit-user-select: none;
                  -moz-user-select: none;
                  -ms-user-select: none;
                  user-select: none;
                  cursor: pointer;">{{item.name}}</span>
          <el-input type="text"
                    size="mini"
                    maxlength="13"
                    style="vertical-align:top"
                    v-if="item.isEditing"
                    v-focus="item.isEditing"
                    @blur="updateRole(item)"
                    v-model="item.name"
                    @keyup.enter.native="$event.target.blur">

          </el-input>
        </div>
        <div style="float:left;margin-left: 5px" v-has="'ROLE_EDIT'">
          <i class="el-icon-edit"
             v-if="!item.isEditing && item.isHover"
             @click.stop="editRole(item)"
             style="
              -webkit-user-select: none;
              -moz-user-select: none;
              -ms-user-select: none;
              user-select: none;
              cursor: pointer;
              color: #01408B;"></i>
        </div>
        <div style="float: right">
          <i class="el-icon-delete"
             v-has="'ROLE_DELETE'"
             @click.stop="deleteRole(item)"
             style="
              -webkit-user-select: none;
              -moz-user-select: none;
              -ms-user-select: none;
              user-select: none;
              cursor: pointer;
              color: #01408B;"></i>
        </div>
      </div>
    </div>

  </div>
</template>
<script>
  import {
    _listRole,
    _saveOrUpdateRole,
    _deleteRole,
    _searchByRoleName,
  } from '@/api/sysRoleApi';


  async function listRole() {
    const result = await _listRole();
    const resultDeal = result.data.sysRoleList;
    resultDeal.forEach((i) => {
      i.isEditing = false;
      i.isHover = false;
      i.isCurrent = false;
    });
    this.listRoleData = resultDeal;
  }


  function trim(s) {
    return s.replace(/(^\s*)|(\s*$)/g, "");
  }

  async function updateRole(item) {
    item.name = trim(item.name);
    if (item.name === '') {
      if (item.type === 'new') {
        this.listRoleData.splice(this.listRoleData.length - 1, 1);
      } else {

        item.name = this.oldName;
        item.isEditing = false;

      }
    } else {
      item.isEditing = false;
      if (item.type === 'new') {
        const result = await _saveOrUpdateRole({
          name: item.name,
        });
        if (result.code === 1200) {
          this.searchValue = '';
          this.listRole();
          this.$message({
            message: '新增角色成功',
            type: 'success',
            center: true,
          });
        }
      } else {
        if (this.oldName !== item.name) {
          const result = await _saveOrUpdateRole({
            id: item.id,
            name: item.name,
          });
          if (result.code === 1200) {
            this.searchValue = '';
            this.listRole();
            this.$message({
              message: '角色更新成功',
              type: 'success',
              center: true,
            });
            this.editValue = '';
          } else {
            this.editValue = '';
          }
        }
      }
    }
  }

  function editRole(item) {
    this.oldName = '';
    this.oldName = item.name;
    item.isEditing = true;
  }

  async function deleteRole(item) {
    this.$confirm('是否删除?', '角色删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        const result = await _deleteRole(item.id);
        if (result.code === 1200) {
          this.searchValue = '';
          this.listRole();
          this.$message({
            message: '角色删除成功',
            type: 'success',
            center: true,
          });
        }
      });
  }

  function addRoleIcon() {
    const newRole = {
      isEditing: true,
      name: '',
      type: 'new',
    };
    this.listRoleData.push(newRole);
  }

  async function searchBar() {
    const result = await _searchByRoleName({
      roleName: this.searchValue,
    });
    const resultDeal = result.data.search;
    resultDeal.forEach((i) => {
      i.isEditing = false;
      i.isHover = false;
      i.isCurrent = false;
    });
    this.listRoleData = resultDeal;
  }

  export default {
    data() {
      return {
        listRoleData: [],
        editValue: '',
        searchValue: '',
        oldName: '',
      };
    },
    methods: {
      exposeRoleId(item) {
        if ('id' in item) {
          this.listRoleData.forEach((i) => {
            i.isCurrent = false;
          });
          item.isCurrent = true;
          this.$emit('forRoleId', item.id);
        }
      },
      updateRole,
      listRole,
      deleteRole,
      addRoleIcon,
      searchBar,
      editRole,
      trim,
    },
    created() {
      this.listRole()
        .then(() => {
          this.exposeRoleId(this.listRoleData[0]);
        });
    },
  };
</script>
<style scoped>
  .role_list {
    height: 27px;
    line-height: 27px;
    margin: 0 15px;
    margin-bottom: 1px;
    font-size: 14px;
    border-radius: 3px;
    cursor: pointer;
    padding: 5px 8px;
  }

  .role_list:hover {
    background-color: #EDEDED;
  }

  .el-input--mini .el-input__inner {
    height: 23px;
  }

  .current {
    background-color: #E5EBF3;
  }
</style>
