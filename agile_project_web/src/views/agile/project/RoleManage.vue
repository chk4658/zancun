<template>
  <div>
    <el-table
      class="maxh-table"
      :data="tableData"
      @filter-change="handleFilterChange"
      style="width: 100%">
      <el-table-column
        prop="roleName"
        width="100"
        column-key="roleName"
        label="角色名称"
        :filters="filterRoleName"
      >
      </el-table-column>
      <el-table-column
        prop="roleDescription"
        label="角色描述">
      </el-table-column>
      <el-table-column
        prop="duty"
        label="职责使命">
      </el-table-column>
      <el-table-column
        :filters="filterUserName"
        column-key="projectRoleUsers"
        prop="projectRoleUsers"
        width="80"
        label="人员">
        <template slot-scope="scope">
          <el-tag
            v-for="u in scope.row.projectRoleUsers" :key="u.sysUserId" size="mini">
            <span>{{u.sysUsers[0].realName}}</span>
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column width="90" align="right">
        <template slot="header">
          <i class="el-icon-circle-plus"
             style="font-size: 20px;position: relative;top: 2px;cursor: pointer;"
             v-if="authority"
             @click="add"></i>
        </template>
        <template slot-scope="scope">
          <i class="el-icon-edit" style="margin-right: 5px;"
             v-if="authority"
             @click="edit(scope.row.id,scope.row.roleName)"></i>
          <i class="el-icon-delete" style="margin-right: 5px;"
             v-if="authority&&scope.row.roleName !== '圈长'&&scope.row.roleName !== '项目经理'"
             @click="del(scope.row.id)"></i>
          <i class="el-icon-user"
             v-if="authority&&scope.row.roleName !== '圈长'&&scope.row.roleName !== '项目经理'"
             @click="bindUser(scope.row)"></i>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @current-change="roleHandlePageChange"
      :current-page="this.tableQuery.page"
      :page-size="this.tableQuery.size"
      style="text-align: left;
    padding-left: 21px;margin-top: 5px"
      background
      layout="total, prev, pager, next"
      :total="this.tableQuery.total">
    </el-pagination>


    <el-dialog
      class="abow_dialog" style="height: 75%;"
      :title="form.id === '' ?'添加': '编辑'"
      @close="closeProjectRole"
      :visible.sync="form.dialogVisible"
      width="550px"
      append-to-body>
      <div style="width: 500px">
        <el-form :model="form" :rules="rules" ref="ruleForm" label-width="100px">
          <el-form-item label="名称" prop="roleName">
            <el-input v-model="form.roleName" style='width:90%' :readonly="!toPower"></el-input>
          </el-form-item>
          <el-form-item label="角色描述">
            <el-input type="textarea" :rows="5" v-model="form.roleDescription" style='width:90%'
                      :readonly="!toPower"></el-input>
          </el-form-item>
          <el-form-item label="职责使命">
            <el-input type="textarea" :rows="5" v-model="form.duty" style='width:90%' :readonly="!toPower"></el-input>
          </el-form-item>

          <el-form-item label="任务当量">
            <el-input-number v-model="form.taskEquivalent" style='width:90%' :controls="false"
                             :max="2"></el-input-number>
          </el-form-item>

          <el-form-item label="项目当量">
            <el-date-picker
              style='width:90%'
              v-model="roleEquivalent"
              type="monthrange"
              range-separator="至"
              start-placeholder="开始月份"
              end-placeholder="结束月份"
              @change="changeRoleEquivalent">
            </el-date-picker>
          </el-form-item>

          <el-form-item>
            <el-tag style="margin:5px;height:33px;width:45%" v-for="(re,index) in roleEquivalents" :key="index">
              {{re.time}}:
              <el-input-number v-model="re.equivalent" style='width:60%' :controls="false" :max="2"></el-input-number>
              <el-button type="text" icon="el-icon-close" @click="roleEquivalents.splice(index,1)"></el-button>
            </el-tag>

          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="save()">提交</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>

    <el-dialog
      title="选择用户"
      @close="closeBindUsers"
      :visible.sync="bindUsers.dialogVisible"
      width="1200px"
      append-to-body>
      <user-tree
        :usersProp='bindUsers.users'
        :isSingle='true'
        @getUsers="getUsers"
        @getCancel="closeBindUsers">
      </user-tree>
    </el-dialog>
  </div>
</template>
<script>


  import {
    _listProjectRoles,
    _updateProjectRole,
    _queryProjectRole,
    _deleteProjectRole,
  } from '@/api/projectRole';

  import moment from "moment";


  import {
    _saveOrUpdateRoleUser,
  } from '@/api/projectRoleUserApi';


  import UserTree from '@/components/UserTree';


  async function getProjectRoles() {
    this.tableQuery.projectId = this.projectId;
    const result = await _listProjectRoles({
      ...this.tableQuery,
      roleNames: this.roleNames.join(','),
      userNames: this.userNames.join(',')
    });
    if (result.code === 1200) {
      console.log(result)
      this.tableData = result.data.projectRoleList;

      this.tableQuery.total = result.data.totalAmount;


      const ruser = []
      const rrole = []
      const Arr = []
      result.data.projectRoleListAll.forEach(td => {
        td.projectRoleUsers.forEach(pr => {
          Arr.push({
            name: pr.sysUsers[0].realName,
            projectRoleId: pr.projectRoleId,
          })
          ruser.push(pr.sysUsers[0].realName)
        })
        rrole.push(td.roleName)
      })

      this.filterRoleName = Array.from(new Set(rrole)).map(x => {
        return {
          text: x,
          value: x,
        }
      })
      this.filterUserName = Array.from(new Set(ruser)).map(x => {
        const ids = []
        Arr.filter(a => a.name === x).forEach(e => ids.push(e.projectRoleId))
        return {
          text: x,
          value: ids.join(','),
        }
      })

      this.$emit('save')
    }

  }


  function roleHandlePageChange(val) {
    this.tableQuery.page = val;
    this.getProjectRoles();
  }

  function resertForm() {
    this.form.id = '';
    this.form.roleName = '';
    this.form.roleDescription = '';
    this.form.duty = '';
    this.form.taskEquivalent = undefined;
    this.form.roleEquivalent = '';
    this.roleEquivalents = [];
    this.roleEquivalent = '';
  }


  function closeProjectRole() {
    this.resertForm();
    this.form.dialogVisible = false;
  }

  function add() {
    this.resertForm();
    this.form.dialogVisible = true;
  }

  async function save() {
    this.form.projectId = this.projectId;
    this.form.roleEquivalent = this.roleEquivalents === '' ? '' : JSON.stringify(this.roleEquivalents);
    const result = await _updateProjectRole(this.form);
    if (result.code === 1200) {
      this.$message({
        type: 'success',
        message: '提交成功!',
      });
      this.getProjectRoles();
      this.form.dialogVisible = false;
    }
  }

  async function edit(id, roleName) {
    this.toPower = (roleName !== '圈长' && roleName !== '项目经理');
    const result = await _queryProjectRole(id);

    if (result.code === 1200) {
      const projectRole = result.data.projectRole;
      this.form.id = projectRole.id;
      this.form.roleName = projectRole.roleName;
      this.form.roleDescription = projectRole.roleDescription;
      this.form.duty = projectRole.duty;
      this.form.taskEquivalent = projectRole.taskEquivalent === undefined ? "" : projectRole.taskEquivalent;
      this.form.roleEquivalent = projectRole.roleEquivalent;
      this.roleEquivalents = projectRole.roleEquivalent !== null ? JSON.parse(projectRole.roleEquivalent) : "";

      this.form.dialogVisible = true;
    } else {
      this.$message({
        type: 'error',
        message: '执行失败！',
      });
    }
  }

  async function del(id) {
    this.$confirm('这个操作将永久解绑并删除角色信息，您想继续吗？', '解绑并删除角色', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        const result = await _deleteProjectRole(id);
        this.getProjectRoles();
        if (result.code === 1200) {
          this.$message({
            type: 'success',
            message: '解绑并删除成功!',
          });
          this.getProjectRoles();
        }
      })
      .catch(() => {
        this.$message({
          type: 'info',
          message: '执行失败！',
        });
      });
  }

  async function bindUser(projectRole) {
    this.bindUsers.projectRoleId = projectRole.id;
    this.bindUsers.users = projectRole.projectRoleUsers.map(ru => ru.sysUsers[0]);
    this.bindUsers.userIds = this.bindUsers.users.map(u => u.id).join(',');
    this.bindUsers.dialogVisible = true;
  }

  async function updateCircleRoleUser() {
    const result = await _saveOrUpdateRoleUser({
      projectRoleId: this.bindUsers.projectRoleId,
      userId: this.bindUsers.userIds,
    });
    return result.code === 1200;
  }

  async function getUsers(data) {
    this.bindUsers.users = data;
    this.bindUsers.userIds = this.bindUsers.users.map(u => u.id)
      .join(',');
    const result = await this.updateCircleRoleUser();
    if (result) {
      this.$message({
        type: 'success',
        message: '绑定成功!',
      });
      this.getProjectRoles();
      this.bindUsers.dialogVisible = false;
    }
  }

  function closeBindUsers(data) {
    this.bindUsers.users = [];
    this.bindUsers.userIds = '';
    this.bindUsers.dialogVisible = false;
  }

  function changeRoleEquivalent(val) {
    this.roleEquivalents = [];
    if (val !== null) {
      console.log(val)
      console.log(val[0].getTime())
      console.log(val[1].getTime())
      let s = moment(val[0]).format('YYYY-MM').split("-");
      let e = moment(val[1]).format('YYYY-MM').split("-");
      const min = new Date();
      const max = new Date();
      min.setFullYear(s[0], s[1]);
      max.setFullYear(e[0], e[1]);
      let curr = min;
      while (curr <= max) {
        let month = curr.getMonth();
        let str = curr.getFullYear() + "-" + (month < 10 ? '0' + month : month);
        let s1 = curr.getFullYear() + "-00";
        if (str === s1) {
          str = (curr.getFullYear() - 1) + "-12";
        }
        this.roleEquivalents.push({
          time: str,
          equivalent: undefined,
        });
        curr.setMonth(month + 1);


      }
    }
  }

  function handleFilterChange(filters) {
    if ('roleName' in filters) {
      this.roleNames = filters.roleName;
    }
    if ('projectRoleUsers' in filters) {

      this.userNames = filters.projectRoleUsers;
    }

    this.tableQuery.page = 1;
    this.getProjectRoles();
  }


  export default {
    name: 'RoleManage',
    props: {
      projectId: {},
      activeName: String,
      authority: Boolean,
    },
    components: {UserTree},
    data() {
      return {
        toPower: true,
        roleEquivalent: '',
        roleEquivalents: [],
        tableData: [],
        tableQuery: {
          page: 1,
          size: 15,
          total: 0,
          projectId: '',
        },
        roleNames: [],
        userNames: [],
        bindUsers: {
          dialogVisible: false,
          users: [],
          userIds: '',
          projectRoleId: '',
        },
        form: {
          dialogVisible: false,
          id: '',
          roleName: '',
          roleDescription: '',
          duty: '',
          projectId: '',
          roleEquivalent: '',
          taskEquivalent: undefined,
        },
        equivalent: {
          dialogVisible: false,
        },
        rules: {
          roleName: [
            {
              required: true,
              message: '请输入名称',
              trigger: 'blur',
            },
          ],
        },

        filterRoleName: [],
        filterUserName: [],
      };
    },
    methods: {
      getProjectRoles,
      roleHandlePageChange,
      resertForm,
      closeProjectRole,
      add,
      save,
      edit,
      del,
      bindUser,
      getUsers,
      closeBindUsers,
      updateCircleRoleUser,
      changeRoleEquivalent,
      handleFilterChange
    },
    created() {
      if (this.activeName === 'role') {
        this.getProjectRoles();
      }
    },
    watch: {
      projectId() {
        this.roleNames = [];
        this.userNames = [];
        this.getProjectRoles();
      },
      activeName(val) {
        if (val === 'role') {
          this.tableQuery = {
            page: 1,
            size: 15,
            total: 0,
            projectId: '',
          };
          this.getProjectRoles();
        }
      }
    },
  };
</script>
<style>
  .el-table__column-filter-trigger {
    line-height: 23px;
  }
</style>
<style lang="scss">
  .maxh-table {

    .cell {
      overflow: visible;
      height: 100%;
    }

  }
</style>
