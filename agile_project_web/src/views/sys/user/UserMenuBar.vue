<template>
  <div class="personnel_menubar">


    <el-row :gutter="20" style="margin-top: 50px;margin-left: 0px;margin-right: 0px">
      <el-col :span="18">
        <span class="department_title">部门管理</span>
      </el-col>
      <el-col :span="6">
        <i style="font-size: 25px;float: right;cursor: pointer;
        margin-right: 15px;" class="el-icon-circle-plus-outline"
           v-has="'DEPARTMENT_ADD'"
           @click="addCompanyInput"></i>
      </el-col>
    </el-row>


    <el-row type="flex" justify="space-around" style="margin: 30px 15px">
      <el-input
        placeholder="请输入名称"
        v-model="searchValue"
        @input="searchBar">
        <i slot="prefix" class="el-input__icon el-icon-search"></i>
      </el-input>
    </el-row>

    <el-row type="flex" justify="space-between" style="margin: 15px ">
      <el-tree
        style="width: 100%"
        :data="menuListData"
        class="department_tree"
        node-key="id"
        @node-click="handleNodeClick"
        :accordion="true"
        :expand-on-click-node="false"
        :highlight-current="true">


        <div class="custom-tree-node" slot-scope="{ node, data }">
          <span class="ellipsis" v-if="!data.isEditing">{{ data.fullName }}</span>
          <el-input v-if="data.isEditing"
                    size="mini"
                    maxlength="28"
                    v-focus="data.isEditing"
                    v-model="data.fullName"
                    @blur="add(data, data.fullName,node)"
                    @keyup.enter.native="$event.target.blur"/>

          <div>
            <el-popover
              placement="bottom-start"
              width="100"
              v-hasDepartmentPopover="'DEPARTMENT_EDIT+DEPARTMENT_DELETE'"
              trigger="click"
              v-model="btnContainerVisible[data.id]">


              <el-row>
                <el-col :span="24" class="btn btn-hover" v-has="'DEPARTMENT_EDIT'"
                        @click.stop.native="() => edit(data)">
                  <i class="el-icon-edit" style="margin-right: 10px"></i>
                  <span>编辑</span>
                </el-col>
                <el-col :span="24" class="btn btn-hover" v-has="'DEPARTMENT_DELETE'"
                        @click.stop.native="() => remove(node,data)">
                  <i class="el-icon-delete" style="margin-right: 10px"></i>
                  <span>删除</span>
                </el-col>
              </el-row>

              <el-button slot="reference"
                         style="font-size: 20px;"
                         class="el-icon-more"
                         type="text"
                         size="mini"
                         @click.stop="() => changeMoreVisible(data.id)"></el-button>
            </el-popover>

            <el-button
              class="el-icon-circle-plus-outline"
              style="font-size: 20px;margin-left: 5px"
              type="text"
              size="mini"
              v-has="'DEPARTMENT_ADD'"
              v-if="node.parent.parent===null"
              @click.stop="append(data,node)">
            </el-button>
          </div>
        </div>
      </el-tree>
    </el-row>
  </div>
</template>
<script>
  import {
    _listCompanyAndDepartment,
    _saveOrUpdateCompany,
    _findByName,
    _deleteCompanyById,
  } from '@/api/sysCompanyApi';
  import {
    _updateDepartment,
    _deleteDepartment,
  } from '@/api/sysDepartmentApi';
  import {
    _findByCompanyAndDepartment,
  } from '@/api/sysUserApi';

  import utils from '@/api/utils';


  function trim(s) {
    return s.replace(/(^\s*)|(\s*$)/g, "");
  }


  function changeMoreVisible(id) {
    Object.keys(this.btnContainerVisible)
      .forEach((key) => {
        if (key !== id && this.btnContainerVisible[key] === true) {
          this.$set(this.btnContainerVisible, key, false);
        }
      });

    this.$set(this.btnContainerVisible, id, this.btnContainerVisible[id]);
  }

  async function listCompanyAndDepartment() {
    const result = await _listCompanyAndDepartment({});
    const menuListDataDeal = JSON.parse(JSON.stringify(result.data.results)
      .replace(/departments/g, 'children'));
    menuListDataDeal.forEach((i) => {
      this.$set(i, 'isEditing', false);
      this.$set(i, 'type', 'company');
      i.children.forEach((j) => {
        this.$set(j, 'isEditing', false);
        this.$set(j, 'type', 'department');
      });
    });
    this.menuListData = menuListDataDeal;
  }


  function addCompanyInput() {
    const company = {
      isEditing: true,
      fullName: '',
      type: 'company',
    };
    this.menuListData.push(company);
  }

  async function add(data, fullName, node) {
    fullName = trim(fullName);
    let result;
    let message;
    if (fullName === '') {

      if ('id' in data) {
        this.$set(data, 'isEditing', false);
        data.fullName = this.oldName;

      } else {
        if (data.type === 'company') {

          this.menuListData.splice(this.menuListData.length - 1, 1);
        } else {

          const len = node.parent.data.children.length - 1;
          node.parent.data.children.splice(len, 1);
        }
      }
    } else {
      if (this.oldName === data.fullName) {
        this.$set(data, 'isEditing', false);
      } else {
        if (utils.isNotNull(data.id)) {
          const {id} = data;
          if (data.type === 'company') {
            result = await _saveOrUpdateCompany({
              id,
              fullName,
            });
            message = '公司更新成功';
          } else {
            result = await _updateDepartment({
              id: data.id,
              companyId: data.companyId,
              fullName,
            });
            message = '部门更新成功';
          }
        } else if (data.type === 'company') {
          result = await _saveOrUpdateCompany({
            fullName,
          });
          message = '公司新增成功';
        } else {
          result = await _updateDepartment({
            companyId: data.companyId,
            fullName,
          });
          message = '部门新增成功';
        }
        if (result.code === 1200) {
          this.listCompanyAndDepartment();
          this.$message({
            message,
            type: 'success',
            center: true,
          });
        }
      }
    }
  }

  async function searchBar() {
    const result = await _findByName({
      fullName: this.searchValue,
    });
    const menuListDataDeal = JSON.parse((JSON.stringify(result.data.nameList)).replace(/departments/g, 'children'));
    menuListDataDeal.forEach((i) => {
      i.children.forEach((j) => {
        j.isEditing = false;
      });
    });
    this.menuListData = menuListDataDeal;
  }

  async function handleNodeClick(data, node) {
    this.$set(node, 'expanded', !node.expanded);
    this.$emit('handleChange', [data.id, data.companyId]);
  }

  async function edit(data) {
    this.oldName = '';
    this.oldName = data.fullName;
    this.$set(data, 'isEditing', true);
    this.$set(this.btnContainerVisible, data.id, !this.btnContainerVisible[data.id]);
  }

  async function append(data, node) {
    this.$set(node, 'expanded', true);
    if (data.type === 'company') {
      const newChild = {
        fullName: '',
        isEditing: true,
        companyId: data.id,
        type: 'department',
      };
      if (!data.children) {
        this.$set(data, 'children', []);
      }
      data.children.push(newChild);
    }

    if (data.type === 'department') {
      const newChild = {
        fullName: '',
        isEditing: true,
        companyId: data.companyId,
        parentId: data.id,
        type: 'department',
      };
      if (!data.children) {
        this.$set(data, 'children', []);
      }
      data.children.push(newChild);
    }
  }

  async function remove(node, data) {
    const {parent} = node;
    const children = parent.data.children || parent.data;
    const index = children.findIndex(d => d.id === data.id);
    children.splice(index, 1);
    if (node.parent.parent === null) {
      await _deleteCompanyById(data.id);
    } else {
      await _deleteDepartment(data.id);
    }

    this.$set(this.btnContainerVisible, data.id, !this.btnContainerVisible[data.id]);
  }

  export default {
    data() {
      return {
        isAddCompany: false,
        addCompanyValue: '',
        searchValue: '',
        menuListData: [],
        editValue: '',
        btnContainerVisible: {},
        tableQuery: {
          page: 1,
          size: 20,
          total: 0,
        },
        oldName: '',
      };
    },
    methods: {
      listCompanyAndDepartment,
      add,
      searchBar,
      handleNodeClick,
      edit,
      append,
      remove,
      changeMoreVisible,
      addCompanyInput,
      trim,
    },
    created() {
      this.listCompanyAndDepartment();
    },
  };
</script>
<style scoped>
  .personnel_menubar {
    /*width: 500px;*/
    /*min-width: 500px;*/
    height: 100%;
    background-color: #fff;
    float: left;
    /*border-right: 1px #e1e1e1 solid;*/
    overflow-y: auto;
    overflow-x: hidden;
  }

  .personnel_menubar .department_title {
    font-size: 20px;
    font-weight: bolder;
    margin-left: 15px;
  }

  .custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
  }

  .ellipsis {
    width: 400px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
  }


  .btn-hover:hover {
    background-color: #344E75;
    color: #fff;
  }

  .btn {
    height: 30px;
    line-height: 30px;
    border-bottom: 1px solid #e1e1e1;
    padding: 0 8px;
    border-radius: 3px;
  }

  .btn:last-child {
    border-bottom: none;
  }

</style>
<style lang="scss">
  .department_tree > {
    .el-tree-node {
      .el-tree-node__content {
        height: 30px;
      }
    }
  }
</style>
