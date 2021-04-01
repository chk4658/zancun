<template>
  <div :style="{maxHeight: visibleHeight + 'px'}" style="overflow-y: auto">
    <el-table
      :data="tableData"
      class="maxh-table"
      style="width: 100%">
      <el-table-column
        prop="roleName"
        label="角色名称"
        width="100"
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
        width="70"
        label="人员">
        <template slot-scope="scope">
          <el-tag
            v-for="u in scope.row.circleRoleUsers" :key="u.sysUserId" size="mini">
            <span>{{u.sysUser.realName}}</span>
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column width="90" align="right">
        <template slot="header" >
          <i class="el-icon-circle-plus"
             style="font-size: 20px;position: relative;top: 2px;cursor: pointer;"
             @click="add" v-hasCirce="'{\'circleId\':\'' + circleId +'\', \'key\': \'hasOperate\', \'value\':true}'"></i>
        </template>
        <template slot-scope="scope">
          <i class="el-icon-edit" style="margin-right: 5px;" v-if="scope.row.roleName !== '圈长'"
             @click="edit(scope.row.id)"
             v-hasCirce="'{\'circleId\':\'' + circleId +'\', \'key\': \'hasOperate\', \'value\':true}'"></i>
          <i class="el-icon-delete" style="margin-right: 5px;" v-if="scope.row.roleName !== '圈长'"
             @click="del(scope.row.id)"
             v-hasCirce="'{\'circleId\':\'' + circleId +'\', \'key\': \'hasOperate\', \'value\':true}'"></i>
          <i class="el-icon-user" v-if="scope.row.roleName !== '圈长'"
             @click="bindUser(scope.row)"
             v-hasCirce="'{\'circleId\':\'' + circleId +'\', \'key\': \'hasOperate\', \'value\':true}'"></i>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @current-change="roleHandlePageChange"
      :current-page="this.tableQuery.page"
      :page-size="this.tableQuery.size"
      background
      style="text-align: left;
      padding-left: 21px;margin-top: 5px"
      layout="total, prev, pager, next"
      :total="this.tableQuery.total">
    </el-pagination>


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

    <el-dialog
      :title="form.id === '' ?'添加': '编辑'"
      @close="closeCircleRole"
      :visible.sync="form.dialogVisible"
      width="450px"
      append-to-body>
      <div style="width: 400px">
        <el-form :model="form" :rules="rules" ref="ruleForm" label-width="80px">
          <el-form-item label="名称" prop="roleName">
            <el-input v-model="form.roleName"></el-input>
          </el-form-item>
          <el-form-item label="角色描述">
            <el-input type="textarea" :rows="5" v-model="form.roleDescription"></el-input>
          </el-form-item>
          <el-form-item label="职责使命">
            <el-input type="textarea" :rows="5" v-model="form.duty"></el-input>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="save()">提交</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-dialog>
  </div>
</template>
<script>


  import {
    _listCircleRoles,
    _deleteCircleRole,
    _queryCircleRole,
    _updateCircleRole
  } from '@/api/circleRole';

  import {_updateCircleRoleUser} from '@/api/circleRoleUser';

  import {_putIndexedCircleOperation} from '@/api/circleApi';

  import UserTree from '@/components/UserTree';


  async function getCircleRoles() {
    this.tableQuery.circleId = this.circleId;
    const result = await _listCircleRoles(this.tableQuery);
    if (result.code === 1200) {
      this.tableData = result.data.circleRoleList;
      this.tableQuery.total = result.data.totalAmount;
    }
  }


  async function updateCircleRoleUser() {
    const result = await _updateCircleRoleUser({
      circleRoleId: this.bindUsers.circleRoleId,
      userIds: this.bindUsers.userIds,
    });
    return result.code === 1200;
  }


  function roleHandlePageChange(val) {
    this.tableQuery.page = val;
    this.getCircleRoles();
  }

  async function edit(id) {
    const result = await _queryCircleRole(id);
    if (result.code === 1200) {
      const circleRole = result.data.circleRole;
      this.form.id = circleRole.id;
      this.form.roleName = circleRole.roleName;
      this.form.roleDescription = circleRole.roleDescription;
      this.form.duty = circleRole.duty;
      this.form.dialogVisible = true;
    } else {
      this.$message({
        type: 'error',
        message: '执行失败！',
      });
    }
  }

  function del(id) {
    this.$confirm('这个操作将永久解绑并删除角色信息，您想继续吗？', '解绑并删除角色', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      const result = await _deleteCircleRole(id);
      this.getCircleRoles();
      if (result.code === 1200) {
        this.$message({
          type: 'success',
          message: '解绑并删除成功!',
        });
        await _putIndexedCircleOperation(this);
        this.getCircleRoles();
      }
    }).catch(() => {
      this.$message({
        type: 'info',
        message: '执行失败！',
      });
    });
  }


  function bindUser(circleRole) {
    this.bindUsers.circleRoleId = circleRole.id;
    this.bindUsers.users = circleRole.circleRoleUsers.map(ru => ru.sysUser);
    this.bindUsers.userIds = this.bindUsers.users.map(u => u.id).join(",");
    this.bindUsers.dialogVisible = true;
  }

  async function getUsers(data) {
    this.bindUsers.users = data;
    this.bindUsers.userIds = this.bindUsers.users.map(u => u.id).join(",");
    const result = await this.updateCircleRoleUser();
    if (result) {
      this.$message({
        type: 'success',
        message: '绑定成功!'
      });
      await _putIndexedCircleOperation(this);
      this.getCircleRoles();
      this.bindUsers.dialogVisible = false;
      this.$router.push({path: 'circle', query: {id: this.circleId}});
    }
  }

  function closeBindUsers(data) {
    this.bindUsers.users = [];
    this.bindUsers.userIds = '';
    this.bindUsers.dialogVisible = false;
  }

  function add() {
    this.resertForm();
    this.form.dialogVisible = true;
  }

  async function save() {
    this.$refs.ruleForm.validate(async (valid) => {
      if (valid) {

        this.form.circleId = this.circleId;
        const result = await _updateCircleRole(this.form);
        if (result.code === 1200) {
          this.$message({
            type: 'success',
            message: '提交成功!',
          });
          await _putIndexedCircleOperation(this);
          this.getCircleRoles();
          this.form.dialogVisible = false;
        }

      } else {
        this.$message.error('有空值');
        return false;
      }
    })

  }

  function resertForm() {
    this.form.id = '';
    this.form.roleName = '';
    this.form.roleDescription = '';
    this.form.duty = '';
  }

  function closeCircleRole() {
    this.resertForm();
    this.form.dialogVisible = false;
  }


  export default {
    name: 'CircleRole',
    props: {
      circleId: String
    },
    components: {UserTree},
    data() {
      return {
        tableQuery: {
          page: 1,
          size: 15,
          total: 0,
          circleId: "",
        },
        tableData: [],
        bindUsers: {
          dialogVisible: false,
          users: [],
          userIds: '',
          circleRoleId: '',
        },
        form: {
          dialogVisible: false,
          id: '',
          roleName: '',
          roleDescription: '',
          duty: '',
          circleId: '',
        },
        rules: {
          roleName: [
            {required: true, message: '请输入名称', trigger: 'blur'},
          ],
        }
      }
    },
    computed: {
      visibleHeight: function () {
        const browserHeight = window.innerHeight;
        // const headerHeight = 160;
        return (browserHeight * 0.75);
      },
    },
    created: function () {
      this.getCircleRoles();
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
      getCircleRoles,
      roleHandlePageChange,
      add,
      edit,
      del,
      bindUser,
      getUsers,
      closeBindUsers,
      updateCircleRoleUser,
      resertForm,
      closeCircleRole,
      save
    },
    watch: {
      circleId() {
        this.getCircleRoles();
      }
    },
    directives: {},
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss">
  .maxh-table {

    .cell {
      overflow: visible;
      height: 100%;
    }

  }
</style>
