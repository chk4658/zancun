<template>
  <div style="width: 100%;
              height: 100%;
              display: flex;overflow-x: hidden">
    <div class="information">
      <main-top-part>
        <template v-slot:title>
          <div style="display: flex">
            <i :class="userIcon"
               class="menu-icon"
               style="color: #01408B;
                    font-size: 32px;">
            </i>
            角色模板管理
          </div>

        </template>
        <template v-slot:search>
          <el-input
            size="small"
            placeholder="请输入角色名称搜索"
            @input="search"
            style="width: 328px;"
            v-model="searchRoleTemplateValue">
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
        </template>
        <template v-slot:button>
          <el-button-group style="margin-top: 20px">
            <el-button :type="0==display?'primary':'default'"
                       style="border-top-left-radius: 4px;border-bottom-left-radius: 4px"
                       size="medium"
                       @click="showAvailableRoleTemplate">成员
            </el-button>
            <el-button :type="1==display?'primary':'default'"
                       style="border-top-right-radius: 4px;border-bottom-right-radius: 4px"
                       size="medium"
                       @click="showForbiddenRoleTemplate">已禁用
            </el-button>
          </el-button-group>
        </template>
      </main-top-part>

      <el-row style="margin: 10px 0 0 20px">
        <el-col :span="12">
          <el-button type="text" size="middle" v-has="'ROLE_TEMPLATE_DELETE'" @click="batchDelete">
            <i class="el-icon-delete"></i>删除
          </el-button>
          <el-button type="text" size="middle" v-has="'ROLE_TEMPLATE_FORBIDDEN'" @click="batchEnabled"
                     icon="el-icon-circle-close">{{enableForbidden}}
          </el-button>
        </el-col>
      </el-row>


      <el-row style="margin: 10px 20px 80px;overflow-y: auto;overflow-x: hidden">
        <el-col :span="24">
          <transition name="fadeRoleTemplate" mode="out-in">
            <el-table
              :data="tableData"
              v-if="showAvailable"
              key="norm"
              @selection-change="handleSelectionChange"
              ref="multipleTable"
              style="width: 100%;">
              <el-table-column
                type="selection"
                :selectable="selectable"
                width="55">
              </el-table-column>
              <el-table-column
                prop="name"
                label="名称">
              </el-table-column>
              <el-table-column
                prop="description"
                label="角色描述">
              </el-table-column>
              <el-table-column
                prop="duty"
                label="职责与使命">
              </el-table-column>
              <el-table-column
                align="right">
                <template slot="header">
                  <i class="el-icon-circle-plus"
                     style="font-size: 20px;position: relative;top: 2px;cursor: pointer;"
                     v-has="'ROLE_TEMPLATE_ADD'"
                     @click="addRoleTemplate"></i>
                </template>
                <template slot-scope="scope">
                  <i class="el-icon-edit" @click="handleEdit(scope.row.id)"
                     v-has="'ROLE_TEMPLATE_EDIT'"
                     v-if="scope.row.name!=='圈长'&&scope.row.name!=='项目经理'"
                     style="font-size: 20px; margin-right: 20px; cursor: pointer;"></i>
                  <i class="el-icon-delete" @click="handleDelete(scope.$index, scope.row)"
                     v-has="'ROLE_TEMPLATE_DELETE'"
                     v-if="scope.row.name!=='圈长'&&scope.row.name!=='项目经理'"
                     style="font-size: 20px; cursor: pointer"></i>
                </template>
              </el-table-column>
            </el-table>
            <el-table
              :data="tableDataForbidden"
              v-else
              key="forb"
              @selection-change="handleSelectionChange2"
              ref="multipleTable"
              style="width: 100%;">
              <el-table-column
                type="selection"
                width="55">
              </el-table-column>
              <el-table-column
                prop="name"
                label="名称">
              </el-table-column>
              <el-table-column
                prop="description"
                label="角色描述">
              </el-table-column>
              <el-table-column
                prop="duty"
                label="职责与使命">
              </el-table-column>
              <el-table-column
                align="right">
                <template slot="header">
                  <i class="el-icon-circle-plus"
                     style="font-size: 20px;position: relative;top: 2px;cursor: pointer;"
                     v-has="'ROLE_TEMPLATE_ADD'"
                     @click="addRoleTemplate"></i>
                </template>
                <template slot-scope="scope">
                  <i class="el-icon-edit" @click="handleEdit(scope.row.id)"
                     style="font-size: 20px; margin-right: 20px; cursor: pointer;"></i>
                  <i class="el-icon-delete" @click="handleDelete(scope.$index, scope.row)"
                     style="font-size: 20px; cursor: pointer"></i>
                </template>
              </el-table-column>
            </el-table>
          </transition>
        </el-col>
      </el-row>


      <el-row class="page">
        <el-col :span="24">
          <el-pagination
            @size-change="handleSizeChange"
            @current-change="handlePageChange"
            :current-page="this.tableQuery.page"
            background
            :page-sizes="[15, 30, 50, 100]"
            :page-size="this.tableQuery.size"
            layout="total, sizes, prev, pager, next, jumper"
            :total="this.tableQuery.total">
          </el-pagination>
        </el-col>
      </el-row>
    </div>

    <el-dialog
      @close="closeForm"
      :visible.sync="form.visible"
      :modal-append-to-body=false
      :title="form.title">
      <role-template-edit
        v-if="form.visible"
        :role-template-id="form.data.id"
        :on-cancel-handler="closeForm"
        :on-submit-handler="closeForm">
      </role-template-edit>
    </el-dialog>
  </div>
</template>

<script>
  import {
    _listRoleTemplate,
    _searchRoleTemplateByName,
    _deleteRoleTemplate,
    _batchDeleteRoleTemplate,
    _batchEnabledRoleTemplate,
    _batchAvailableRoleTemplate,
  } from '@/api/sysRoleTemplateApi';
  import RoleTemplateEdit from './RoleTemplateEdit.vue';
  import MainTopPart from '@/components/MainTopPart.vue';

  async function listRoleTemplate() {
    const result = await _listRoleTemplate({
      page: this.tableQuery.page,
      size: this.tableQuery.size,
      enabled: this.enableNum,
    });
    if (this.enableNum === 0) {
      this.tableData = result.data.SysRoleTemplateList;
    } else {
      this.tableDataForbidden = result.data.SysRoleTemplateList;
    }
    this.tableQuery.total = result.data.totalAmount;
  }

  async function search() {
    this.tableQuery.page = 1;
    const result = await _searchRoleTemplateByName(
      {
        page: this.tableQuery.page,
        size: this.tableQuery.size,
        roleName: this.searchRoleTemplateValue,
        enabled: this.enableNum,
      },
    );

    if (this.searchRoleTemplateValue === '') {
      await this.listRoleTemplate();
    }
    if (this.enableNum === 0) {
      this.tableData = result.data.search;
      this.tableQuery.total = result.data.totalAmount;
    } else {
      this.tableDataForbidden = result.data.search;
      this.tableQuery.total = result.data.totalAmount;
    }
  }

  async function batchDeleteRoleTemplate(a) {
    const result = await _batchDeleteRoleTemplate({
      tempRoleIds: a,
    });
    const promise = new Promise((resolve, reject) => {
      if (result.code === 1200) {
        this.$message({
          message: '删除成功',
          type: 'success',
          center: true,
        });
        resolve();
      } else {
        reject();
      }
      return promise;
    });
  }

  async function batchEnabledRoleTemplate(a) {
    const result = await _batchEnabledRoleTemplate({
      roleIds: a,
    });
    const promise = new Promise((resolve, reject) => {
      if (result.code === 1200 && (this.multipleSelection !== undefined
        && this.multipleSelection.length > 0)) {
        this.$message({
          message: '已切换至禁用状态',
          type: 'success',
          center: true,
        });
        resolve();
      } else {
        reject();
      }
      return promise;
    });
  }

  function closeForm() {
    this.form.data.id = null;
    this.form.visible = false;
    this.listRoleTemplate();
  }

  async function batchAvailableRoleTemplate(a) {
    const result = await _batchAvailableRoleTemplate({roleIds: a});
    const promise = new Promise((resolve, reject) => {
      if (result.code === 1200 && (this.multipleSelection2 !== undefined
        && this.multipleSelection2.length > 0)) {
        this.$message({
          message: '已解除禁用状态',
          type: 'success',
          center: true,
        });
        resolve();
      } else {
        reject();
      }
      return promise;
    });
  }

  async function handleDelete(index, row) {
    this.$confirm('是否删除?', '角色模板删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        const result = await _deleteRoleTemplate(row.id);
        if (result.code === 1200) {
          this.$message({
            message: '删除成功',
            type: 'success',
            center: true,
          });
        }
        if (this.enableNum === 0) {
          this.tableData.splice(index, 1);
        } else {
          this.tableDataForbidden.splice(index, 1);
        }
        this.tableQuery.total = this.tableQuery.total - 1;
      });
  }

  export default {
    data() {
      return {
        tableData: [],
        tableDataForbidden: [],
        showAvailable: true,
        enableNum: 0,
        form: {
          data: {
            id: null,
          },
          visible: false,
          title: '',
        },
        display: '0',
        addFormShow: false,
        editFormShow: false,
        currentList: '',
        multipleSelection: [],
        multipleSelection2: [],
        tableQuery: {
          page: 1,
          size: 15,
          total: 0,
        },
        enableForbidden: '禁用',
        searchRoleTemplateValue: '',
        userIcon: '',
      };
    },
    components: {
      RoleTemplateEdit,
      MainTopPart,
    },
    methods: {
      handleEdit(roleTemplateId) {
        this.form.data.id = roleTemplateId;
        this.form.visible = true;
        this.form.title = '更新角色模板';
      },
      addRoleTemplate() {
        this.form.data.id = null;
        this.form.visible = true;
        this.form.title = '创建角色模板';
      },
      showAvailableRoleTemplate() {
        this.tableQuery.page = 1;
        this.showAvailable = true;
        this.enableNum = 0;
        this.display = 0;
        this.enableForbidden = '禁用';
        this.listRoleTemplate();
      },
      showForbiddenRoleTemplate() {
        this.tableQuery.page = 1;
        this.showAvailable = false;
        this.enableNum = 1;
        this.display = 1;
        this.enableForbidden = '解除禁用';
        this.listRoleTemplate();
      },
      batchDelete() {
        if (this.enableNum === 0) {
          if (this.multipleSelection.length === 0) {
            this.$message({
              message: '未选择任何人员',
              type: 'warning',
              center: true,
            });
          } else {
            this.$confirm('是否删除?', '角色模板删除', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
            })
              .then(() => {
                const batchRoleTemplate = [];
                this.multipleSelection.forEach((item) => {
                  batchRoleTemplate.push(item.id);
                });
                const batchString = batchRoleTemplate.join(',');
                this.batchDeleteRoleTemplate(batchString)
                  .then(() => {
                    this.listRoleTemplate();
                  });
              });
          }
        } else {
          if (this.multipleSelection2.length === 0) {
            this.$message({
              message: '未选择任何人员',
              type: 'warning',
              center: true,
            });
          } else {
            this.$confirm('是否删除?', '角色模板删除', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
            })
              .then(() => {
                const batchRoleTemplate = [];
                this.multipleSelection2.forEach((item) => {
                  batchRoleTemplate.push(item.id);
                });
                const batchString = batchRoleTemplate.join(',');
                this.batchDeleteRoleTemplate(batchString)
                  .then(() => {
                    this.listRoleTemplate();
                  });
              });
          }
        }
      },
      batchEnabled() {
        const batchRoleTemplate2 = [];
        if (this.enableNum === 0) {
          if (this.multipleSelection.length !== 0) {
            this.multipleSelection.forEach((item) => {
              batchRoleTemplate2.push(item.id);
            });
            const batchString2 = batchRoleTemplate2.join(',');
            this.batchEnabledRoleTemplate(batchString2)
              .then(() => {
                this.listRoleTemplate();
              });
          }
        } else {
          if (this.multipleSelection2.length !== 0) {
            this.multipleSelection2.forEach((item) => {
              batchRoleTemplate2.push(item.id);
            });
            const batchString2 = batchRoleTemplate2.join(',');
            this.batchAvailableRoleTemplate(batchString2)
              .then(() => {
                this.listRoleTemplate();
              });
          }
        }
      },
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      handleSelectionChange2(val) {
        this.multipleSelection2 = val;
      },
      handlePageChange(val) {
        this.tableQuery.page = val;
        this.listRoleTemplate();
      },
      handleSizeChange(val) {
        this.tableQuery.size = val;
        this.listRoleTemplate();
      },
      listRoleTemplate,
      search,
      handleDelete,
      batchDeleteRoleTemplate,
      batchEnabledRoleTemplate,
      batchAvailableRoleTemplate,
      closeForm,
      selectable(row, index) {
        if (row.name === '圈长' || row.name === '项目经理') {
          return false;
        } else {
          return true;
        }

      }
    },
    created() {
      this.listRoleTemplate();
      const userMenus = JSON.parse(localStorage.getItem('MENUS'))
        .filter(menu => menu.code === 'ROLE_TEMPLATE');
      this.userIcon = userMenus.length === 0 ? 'fa fa-users' : userMenus[0].icon;
    },
  };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->

<style scoped>
  .fadeRoleTemplate-enter-active, .fadeRoleTemplate-leave-active {
    transition: all .3s ease;
  }

  .fadeRoleTemplate-enter, .fadeRoleTemplate-leave-to
    /* .fadeRoleTemplate-leave-active below version 2.1.8 */
  {
    opacity: 0;
    transform: translateX(10px)
  }

  .roleTemplate_menubar .department_title span {
    float: right;
  }

  .middle_part_roleTemplate {
    width: 100%;
    box-sizing: border-box;
    flex: 1;
    min-width: 764px;
  }

  .el-table > th {
    background-color: #eee;
  }

  div .cell {
    height: 23px;
  }

  .el-select-dropdown__wrap {
    margin-bottom: -3px !important;
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


  .page {
    position: absolute;
    bottom: 20px;
    left: 20px;
  }
</style>
