<template>
  <div style="width: 100%;
              height: 100%;
              display: flex;">
    <user-menu-bar @handleChange="handleChange" id="umb" :style="{width: umbWidth}"></user-menu-bar>
    <div style="width: 5px;height: 100%;background-color:#e1e1e1; cursor: pointer"
         @mousedown="drag"></div>
    <div class="information">
      <main-top-part>
        <template v-slot:title>
          <div style="display: flex">
            <i :class="userIcon" class="menu-icon"
               style="color: #01408B;
                    font-size: 32px;">
            </i>人员管理
          </div>
        </template>
        <template v-slot:search>
          <el-input
            size="small"
            placeholder="搜索"
            @input="search"
            style="width: 328px;"
            v-model="searchUserValue"><i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
        </template>
        <template v-slot:button>
          <el-button-group style="margin-top: 20px">
            <el-button :type="0==display?'primary':'default'"
                       style="border-top-left-radius: 4px;border-bottom-left-radius: 4px"
                       size="medium"
                       @click="showAvailableUser">成员
            </el-button>
            <el-button :type="1==display?'primary':'default'"
                       style="border-top-right-radius: 4px;border-bottom-right-radius: 4px"
                       size="medium"
                       @click="showForbiddenUser">已禁用
            </el-button>
             <el-button
                        size="medium"
                        :loading="refreshLoad"
                       @click="refresh">批量更新
            </el-button>
             <el-button
                        size="medium"
                        :disabled="refreshLoad"
                        @click="add">添加
            </el-button>
          </el-button-group>
        </template>
      </main-top-part>


      <el-row style="margin: 10px 20px;">
        <el-col :span="12">
          <el-button type="text" class="textButtonFontSize" size="middle" v-has="'USER_DELETE'" @click="deleteUser">
            <i class="el-icon-delete"></i>删除
          </el-button>
          <el-button type="text" class="textButtonFontSize" size="middle" v-has="'USER_FORBIDDEN'"
                     @click="forbiddenUser"
                     icon="el-icon-circle-close">{{enableForbidden}}
          </el-button>

        </el-col>
      </el-row>


      <el-row style="margin: 0 20px 80px;overflow-y: auto;overflow-x: hidden">
        <el-col :span="23">

          <transition name="fadeUser" mode="out-in">
            <el-table
              v-if="showAvailable"
              key="norm"
              ref="multipleTable"
              :data="tableData"
              tooltip-effect="dark"
              style="width: 100%;"
              @selection-change="handleSelectionChange">
              <el-table-column
                type="selection"
                width="55">
              </el-table-column>
              <el-table-column
                prop="realName"
                label="姓名">
              </el-table-column>
              <el-table-column
                prop="account"
                label="账号">
              </el-table-column>
              <el-table-column
                prop="mobile"
                label="联系方式"
                show-overflow-tooltip>
              </el-table-column>
              <el-table-column
                prop="email"
                label="邮箱"
                show-overflow-tooltip>
              </el-table-column>
              <el-table-column
                prop="departmentName"
                label="部门"
                show-overflow-tooltip>
              </el-table-column>
            </el-table>
            <el-table
              v-else
              key="forb"
              ref="multipleTable"
              :data="tableDataForbidden"
              tooltip-effect="dark"
              style="width: 100%"
              @selection-change="handleSelectionChange2">
              <el-table-column
                type="selection"
                width="55">
              </el-table-column>
              <el-table-column
                prop="realName"
                label="姓名">
              </el-table-column>
              <el-table-column
                prop="account"
                label="账号">
              </el-table-column>
              <el-table-column
                prop="mobile"
                label="联系方式"
                show-overflow-tooltip>
              </el-table-column>
              <el-table-column
                prop="email"
                label="邮箱"
                show-overflow-tooltip>
              </el-table-column>
              <el-table-column
                prop="departmentName"
                label="部门"
                show-overflow-tooltip>
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
      title="添加"
      :visible.sync="dialogVisible"
      width="20%">
      <el-input v-model="form.account" placeholder="请输入UID"></el-input>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </span>
    </el-dialog>


  </div>
</template>

<script>
  import {
    _findByCompanyAndDepartment,
    _batchDeleteUser,
    _batchEnabledUser,
    _batchAvailableUser,
    _pullSysUserData,
    _saveSysUser
  } from '@/api/sysUserApi';
  import UserMenuBar from './UserMenuBar.vue';
  import MainTopPart from '../../../components/MainTopPart.vue';

  async function findByCompanyAndDepartment(a, b, c, d) {
    const result = await _findByCompanyAndDepartment(
      {
        page: this.tableQuery.page,
        size: this.tableQuery.size,
        name: a,
        enabled: b,
        companyId: c,
        departmentId: d,
      },
    );
    if (b === 0) {
      this.tableData = result.data.sysUserRecords;
    } else {
      this.tableDataForbidden = result.data.sysUserRecords;
    }

    this.tableQuery.total = result.data.totalAmount;
  }

  async function search() {
    this.tableQuery.page = 1;
    await this.findByCompanyAndDepartment(this.searchUserValue, this.enableNum, null, null);
  }

  async function batchDeleteUser(a) {
    const result = await _batchDeleteUser({
      userIds: a,
    });
    if (result.code === 1200) {
      this.$message({
        message: '删除成功',
        type: 'success',
        center: true,
      });
    }
  }

  async function batchEnabledUser(a) {
    if (a !== '') {
      const result = await _batchEnabledUser({userIds: a});
      if (result.code === 1200) {
        this.$message({
          message: '已切换至禁用状态',
          type: 'success',
          center: true,
        });
      }
    }
  }

  async function batchAvailableUser(a) {
    if (a !== '') {
      const result = await _batchAvailableUser({userIds: a});
      if (result.code === 1200) {
        this.$message({
          message: '已解除禁用状态',
          type: 'success',
          center: true,
        });
      }
    }
  }

  async function handleChange(data) {
    this.departmentData = data;
    this.tableQuery.page = 1;
    if (data[1] === undefined) {
      await this.findByCompanyAndDepartment(this.searchUserValue, this.enableNum, data[0], null);
    } else {
      await this.findByCompanyAndDepartment(this.searchUserValue, this.enableNum, data[1], data[0]);
    }
  }

  function drag(event) {

    const e = event || window.event;
    const w = parseInt(document.getElementById('umb').style.width);

    const oriX = e.clientX;


    document.onmousemove = (e) => {

      const go = e.clientX - oriX;
      document.getElementById('umb').style.width = w + go + 'px';


    };

    document.onmouseup = (e) => {
      this.umbWidth = document.getElementById('umb').style.width;
      document.onmousemove = null;
      document.onmouseup = null;
    };


  }


  async function refresh() {
    this.refreshLoad = true;
    const result = await _pullSysUserData();
    if (result.data === 1200) {
      this.$message.info('刷新成功');
      const departmentData = this.departmentData; 
      const departmentData0 = departmentData.length >= 1 
                              ? (departmentData.length === 2 ? ( departmentData[1] === undefined ? departmentData[0] : departmentData[1])
                              : departmentData[0]) : null;
      this.findByCompanyAndDepartment(this.searchUserValue, this.enableNum, departmentData1, departmentData0);
    }
    this.refreshLoad = false;
  }


  function add() {
    this.dialogVisible = true;
    
  }

  function cancel() {
    this.form.account = "";
    this.dialogVisible = false;
  }

  function save() {
    const result = _saveSysUser({account: this.form.account});
    if (result.data.result) {
      this.$message.info("添加完成");
      this.findByCompanyAndDepartment(null, this.enableNum, null, null);
      this.dialogVisible = false;
    } else {
      this.$message.error("添加失败，请检查人员是否存在域中");
    }
  }

  export default {
    data() {
      return {
        tableData: [],
        tableDataForbidden: [],
        multipleSelection: [],
        multipleSelection2: [],
        showAvailable: true,
        enableNum: 0,
        tableQuery: {
          page: 1,
          size: 15,
          total: 0,
        },
        display: '0',
        enableForbidden: '禁用',
        searchUserValue: '',
        userIcon: '',
        umbWidth: '500px',
        departmentData: [],
        refreshLoad: false,
        dialogVisible: false,
        form: {
          id: '',
          account: '',
        }

      };
    },
    components: {
      UserMenuBar,
      MainTopPart,
    },
    methods: {
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      handleSelectionChange2(val) {
        this.multipleSelection2 = val;
      },

      showAvailableUser() {
        this.tableQuery.page = 1;
        this.showAvailable = true;
        this.enableNum = 0;
        this.display = 0;
        this.enableForbidden = '禁用';
        this.findByCompanyAndDepartment(null, this.enableNum, null, null);
      },
      showForbiddenUser() {
        this.tableQuery.page = 1;
        this.showAvailable = false;
        this.enableNum = 1;
        this.display = 1;
        this.enableForbidden = '解除禁用';
        this.findByCompanyAndDepartment(null, this.enableNum, null, null);
      },
      deleteUser() {
        if (this.enableNum === 0) {
          if (this.multipleSelection.length === 0) {
            this.$message({
              message: '未选择任何人员',
              type: 'warning',
              center: true,
            });
          } else {
            this.$confirm('是否删除?', '删除人员', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
            })
              .then(() => {
                const batchUser = [];
                this.multipleSelection.forEach((item) => {
                  batchUser.push(item.id);
                });
                const batchString = batchUser.join(',');
                this.batchDeleteUser(batchString)
                  .then(() => {
                    this.findByCompanyAndDepartment(null, this.enableNum, null, null);
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
            this.$confirm('是否删除?', '删除人员', {
              confirmButtonText: '确定',
              cancelButtonText: '取消',
              type: 'warning',
            })
              .then(() => {
                const batchUser = [];
                this.multipleSelection2.forEach((item) => {
                  batchUser.push(item.id);
                });
                const batchString = batchUser.join(',');
                this.batchDeleteUser(batchString)
                  .then(() => {
                    this.findByCompanyAndDepartment(null, this.enableNum, null, null);
                  });
              });
          }
        }
      },
      forbiddenUser() {
        const batchUser2 = [];
        if (this.enableNum === 0) {
          this.multipleSelection.forEach((item) => {
            batchUser2.push(item.id);
          });
          const batchString2 = batchUser2.join(',');
          this.batchEnabledUser(batchString2)
            .then(() => {
              this.findByCompanyAndDepartment(null, this.enableNum, null, null);
            });
        } else {
          this.multipleSelection2.forEach((item) => {
            batchUser2.push(item.id);
          });
          const batchString2 = batchUser2.join(',');
          this.batchAvailableUser(batchString2)
            .then(() => {
              this.findByCompanyAndDepartment(null, this.enableNum, null, null);
            });
        }
      },
      handlePageChange(val) {
        this.tableQuery.page = val;
        const departmentData = this.departmentData; 
        const departmentData0 = departmentData.length >= 1 
                              ? (departmentData.length === 2 ? ( departmentData[1] === undefined ? departmentData[0] : departmentData[1])
                              : departmentData[0]) : null;
        const departmentData1 = departmentData.length === 2 ? (departmentData[1] !== undefined ?  departmentData[0] : null) : null;
        this.findByCompanyAndDepartment(this.searchUserValue, this.enableNum, departmentData0, departmentData1);
      },
      handleSizeChange(val) {
        this.tableQuery.size = val;
        const departmentData = this.departmentData; 
        const departmentData0 = departmentData.length >= 1 
                      ? (departmentData.length === 2 ? ( departmentData[1] === undefined ? departmentData[0] : departmentData[1])
                      : departmentData[0]) : null;
        const departmentData1 = departmentData.length === 2 ? (departmentData[1] !== undefined ?  departmentData[0] : null) : null;
        this.findByCompanyAndDepartment(this.searchUserValue, this.enableNum, departmentData0, departmentData1);
      },
      search,
      findByCompanyAndDepartment,
      batchDeleteUser,
      batchEnabledUser,
      batchAvailableUser,
      handleChange,
      drag,
      refresh,
      add,
      cancel,
      save,
    },
    created() {
      this.findByCompanyAndDepartment(null, this.enableNum, null, null);
      const userMenus = JSON.parse(localStorage.getItem('MENUS'))
        .filter(menu => menu.code === 'USER');
      this.userIcon = userMenus.length === 0 ? 'el-icon-user' : userMenus[0].icon;
    },
  };
</script>

<style scoped>
  .information {
    width: 100%;
    flex: 1;
    background-color: #fff;
    float: left;
    display: flex;
    flex-direction: column;
    position: relative;
  }

  .page {
    position: absolute;
    bottom: 20px;
    left: 20px;
  }
</style>
