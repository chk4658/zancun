<template>
  <div>
    <div>
      <el-form :model="form" :rules="rules" ref="ruleForm" label-width="150px" style="width:600px">
        <el-form-item label="上传图片">
          <!--  图片上传组件-->
          <UploadImage
            ref="uploadRefImage"
            :imageName="form.imgUrl"
            @uploadFilesSucc="uploadFilesSucc">
          </UploadImage>
        </el-form-item>
        <el-form-item label="圈主">
          <el-input v-model="form.ownerName" style="width:200px" disabled></el-input>
          <el-button type="text" icon="el-icon-s-custom" style="margin-left: 10px;"
                     @click="addOwner">指定圈长
          </el-button>
          <!-- <el-button type="text" icon="el-icon-close" style="margin-left: 10px;"
          @click="clearOwner">清除圈长</el-button> -->
        </el-form-item>
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" maxlength="50" show-word-limit></el-input>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input type="textarea" :rows="5" v-model="form.description"></el-input>
        </el-form-item>
        <el-form-item label="隶属于" prop="parentId">
          <el-input v-model="form.parentName" style="width:300px" disabled></el-input>
          <el-button type="text" icon="el-icon-plus" style="margin-left: 10px;"
                     @click="addCircle"></el-button>
          <el-button type="text" icon="el-icon-close" style="margin-left: 10px;"
                     @click="clearCircle"></el-button>
        </el-form-item>

        <el-form-item label="角色" prop="circleRoleIds">
          <template v-for="(item,index) in roles">
              <span :key="item.id"
                    :style="index > 0 ? 'margin-left: 5px;' : ''"
              >{{item}}
              <el-button v-if="item !== '圈长'" type="text" icon="el-icon-close"
                         @click="clearRole(item)"></el-button></span>
          </template>
          <el-button icon="el-icon-plus" :style="roles.length > 0 ? 'margin-left: 10px;' : ''"
                     @click="addRole"></el-button>
        </el-form-item>

        <el-form-item label="允许操作圈子">
          <el-radio-group v-model="form.isOperate">
            <el-radio label="0">否</el-radio>
            <el-radio label="1">所有人</el-radio>
            <el-radio label="2">授权成员</el-radio>
            <el-input v-if="form.isOperate === '2'" v-model="form.operateNames" style="width:150px" disabled></el-input>
            <el-button type="text" icon="el-icon-plus" v-if="form.isOperate === '2'"
                       style="margin-left: 10px;"
                       @click="addOperate"></el-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="允许添加圈子">
          <el-radio-group v-model="form.isAddCircle">
            <el-radio label="0">否</el-radio>
            <el-radio label="1">所有人</el-radio>
            <el-radio label="2">授权成员</el-radio>
            <el-input v-if="form.isAddCircle === '2'" v-model="form.addCircleNames" style="width:150px"
                      disabled></el-input>
            <el-button type="text" icon="el-icon-plus" v-if="form.isAddCircle === '2'"
                       style="margin-left: 10px;"
                       @click="addIsCircle"></el-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="允许添加项目">
          <el-radio-group v-model="form.isAddProject">
            <el-radio label="0">否</el-radio>
            <el-radio label="1">所有人</el-radio>
            <el-radio label="2">授权成员</el-radio>
            <el-input v-if="form.isAddProject === '2'" v-model="form.addProjectNames" style="width:150px"
                      disabled></el-input>
            <el-button type="text" icon="el-icon-plus" v-if="form.isAddProject === '2'"
                       style="margin-left: 10px;"
                       @click="addProject"></el-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save()"
                     v-hasCirce="'{\'circleId\':\'' + circleId +'\', \'key\': \'hasOperate\', \'value\':true}'">提交
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 选择圈长 -->
    <el-dialog
      title="选择圈长"
      @close="closeOwner"
      :visible.sync="ownerDialogVisible"
      width="1200px"
      append-to-body>
      <user-tree
        :isSingle='true'
        :usersProp='owners'
        @getUsers="getOwners"
        @getCancel="closeOwner">
      </user-tree>
    </el-dialog>


    <!-- 选择圈子 -->
    <el-dialog
      title="选择圈子"
      :visible.sync="circleDialogVisible"
      @close="closeCircle"
      width="550px"
      append-to-body>
      <circle-tree :parentCircle="circle" :circleId='circleId' @submitCircle="submitCircle"
                   @cancelCircle="cancelCircle"></circle-tree>
    </el-dialog>

    <!-- 选择角色 -->
    <el-dialog
      title="选择角色"
      @close="closeRole"
      :visible.sync="roleDialogVisible"
      width="600px"
      append-to-body>
      <role-transfer
        :rolesProp="roles"
        :isSingle='false'
        :isCircler="true"
        @getRoles="getRoles"
        @getCancel="closeRole">
      </role-transfer>
    </el-dialog>


    <!-- 是否允许操作圈子 -->
    <el-dialog
      title="选择用户"
      @close="closeOperate"
      :visible.sync="operateDialogVisible"
      width="1200px"
      append-to-body>
      <user-tree
        :usersProp='operates'
        @getUsers="getOperates"
        @getCancel="closeOperate">
      </user-tree>
    </el-dialog>


    <!-- 允许添加圈子 -->
    <el-dialog
      title="选择用户"
      @close="closeIsCircle"
      :visible.sync="addCircleDialogVisible"
      width="1200px"
      append-to-body>
      <user-tree
        :usersProp='isAddCircles'
        @getUsers="getAddCircles"
        @getCancel="closeIsCircle">
      </user-tree>
    </el-dialog>

    <!-- 允许添加项目 -->
    <el-dialog
      title="选择用户"
      @close="addProjectDialogVisible = false"
      :visible.sync="addProjectDialogVisible"
      width="1200px"
      append-to-body>
      <user-tree
        :usersProp='isAddProjects'
        @getUsers='getAddProjects'
        @getCancel='addProjectDialogVisible = false'>
      </user-tree>
    </el-dialog>
  </div>
</template>

<script>

  import UserTree from '@/components/UserTree';
  import RoleTransfer from '@/components/RoleTransfer';
  import CircleTree from '@/components/CircleTree';
  import UploadImage from '@/components/UploadImage';

  import {_updateCircle, _queryCircle, _putIndexedCircleOperation} from '@/api/circleApi';
  import {_queryUsers, _querySysUser, _querySysUsersByCircle, _querySysUsersByIds} from '@/api/sysUserApi';
  import {_queryRoleTemplatesByCircle, _queryRoleTemplatesByRoleNames} from '@/api/sysRoleTemplateApi';

  /*删除数组中的某一个对象
  _arr:数组
  _obj:需删除的对象
  */
  function removeArray(_arr, _obj) {
    var length = _arr.length;
    for (var i = 0; i < length; i++) {
      if (_arr[i] === _obj) {
        if (i === 0) {
          _arr.shift();
          return _arr;
        } else if (i === length - 1) {
          _arr.pop();
          return _arr;
        } else {
          _arr.splice(i, 1);
          return _arr;
        }
      }
    }
  }


  async function getUser(id) {
    const result = await _querySysUser(id);
    return result.data.sysUser;
  }

  async function getUsersByIds(ids) {
    const result = await _querySysUsersByIds({ids: ids});
    return result.data.sysUserList;
  }

  async function queryCircle(id) {
    const result = await _queryCircle(id);
    return result.data.circle;
  }

  /**
   * 根据CircleId ,type  获取用户信息
   */
  async function getUsersByCircle(id, type) {
    const result = await _querySysUsersByCircle({
      circleId: id,
      type: type,
    });
    return result.data.sysUsers;
  }

  /**
   * 根据CircleId ,type  获取用户信息
   */
  async function getRolesByCircle(id) {
    const result = await _queryRoleTemplatesByCircle({circleId: id});
    return result.data.circleRoleList;
  }

  /**
   * 根据CircleId ,type  获取用户信息
   */
  async function getRolesByRoleNames(roleNames) {
    const result = await _queryRoleTemplatesByRoleNames({roleNames: roleNames});
    return result.data.sysRoleTemplateList;
  }

  /**
   * 编辑
   */
  async function getCircle(id) {
    if (id === '' || id === '-1') {
      const id = localStorage.getItem('id');
      const userName = localStorage.getItem('userName');
      this.form.ownerName = '';
      this.form.ownerUid = '';
      this.form.id = '';
      this.form.name = '';
      this.form.description = '';
      this.owners = [
        {
          id: id,
          realName: userName,
        }
      ];
      this.form.ownerUid = id;
      this.form.ownerName = userName;
      this.circle = this.parentCircle === null ? {
        id: '',
        name: '',
      } : this.parentCircle;
      this.form.parentId = this.circle.id;
      this.form.parentName = this.circle.name;
      this.roles = ['圈长'];
      this.form.roleNames = '圈长';

      this.operates = [];
      this.form.operateIds = '';
      this.form.operateNames = '';
      this.form.isOperate = '0';

      this.isAddCircles = [];
      this.form.addCircleIds = '';
      this.form.addCircleNames = '';
      this.form.isAddCircle = '0';

      this.isAddProjects = [];
      this.form.addProjectIds = '';
      this.form.addProjectNames = '';
      this.form.isAddProject = '0';
      this.form.imgUrl = '';
      this.tip = '创建'
    } else {
      this.tip = '更改'
      const result = await _queryCircle(id);
      if (result.code === 1200) {
        const circle = result.data.circle;
        this.form.ownerName = circle.ownerName;
        this.form.ownerUid = circle.ownerUid;
        this.form.id = circle.id;
        this.form.name = circle.name;
        this.form.description = circle.description;
        this.form.ownerUid = circle.ownerUid;
        this.owners = [];
        const owner = await this.getUser(circle.ownerUid);
        this.owners.push(owner);
        this.form.ownerName = owner.realName;
        if (circle.parentId !== '0') {
          const parentCircle = await this.queryCircle(circle.parentId);
          this.circle = parentCircle === null ? (
            this.parentCircle === null ? {
              id: '',
              name: '',
            } : this.parentCircle
          ) : parentCircle;
          this.form.parentId = this.circle.id;
          this.form.parentName = this.circle.name;
        }
        this.roles = [];
        const roles = await this.getRolesByCircle(circle.id);
        roles.forEach((r) => {
          this.roles.push(r.roleName);
        });
        this.form.roleNames = this.roles.join(',');

        this.form.isOperate = `${circle.isOperate}`;
        if (this.form.isOperate === '2') {
          const operates = await this.getUsersByCircle(circle.id, 'OPERATE');
          this.operates = operates;
          if (operates !== null) {
            this.getOperates(operates);
          }
        }

        this.form.isAddCircle = `${circle.isAddCircle}`;
        if (this.form.isAddCircle === '2') {
          const isAddCircles = await this.getUsersByCircle(circle.id, 'ADD_CIRCLE');
          this.isAddCircles = isAddCircles;
          if (isAddCircles !== null) {
            this.getAddCircles(isAddCircles);
          }
        }

        this.form.isAddProject = `${circle.isAddProject}`;
        if (this.form.isAddProject === '2') {
          const isAddProjects = await this.getUsersByCircle(circle.id, 'ADD_PROJECT');
          this.isAddProjects = isAddProjects;
          if (isAddProjects !== null) {
            this.getAddProjects(isAddProjects);
          }
        }
        this.form.imgUrl = circle.imgUrl;
      }
    }
  }


  /**
   * 选择圈长 ====================》
   */

  async function addOwner() {
    this.owners = [];
    if (this.form.ownerUid !== '') {
      const owner = await this.getUser(this.form.ownerUid);
      this.owners.push(owner);
    }
    this.ownerDialogVisible = true;
  }

  function getOwners(data) {
    if (data !== null && data.length > 0) {
      this.form.ownerName = data[0].realName;
      this.form.ownerUid = data[0].id;
    }
  }

  function clearOwner() {
    this.form.ownerName = '';
    this.form.ownerUid = '0';
  }

  function closeOwner() {
    this.owners = [];
    this.ownerDialogVisible = false;
  }

  /**
   * 隶属于
   */
  function addCircle() {
    this.timer = setTimeout(this.circleDialogVisible = true, 1000);
  }


  function closeCircle() {
    this.circleDialogVisible = false;
  }


  function submitCircle(val) {
    this.parentCircle = val;
    this.form.parentId = val.id;
    this.form.parentName = val.name;
    this.circleDialogVisible = false;
  }

  function cancelCircle() {
    this.circleDialogVisible = false;
  }

  function clearCircle() {
    this.form.parentId = '0';
    this.form.parentName = '';
  }

  /**
   * 角色
   */
  async function addRole() {
    this.roles = [];
    if (this.form.roleNames !== '') {
      this.roles = this.form.roleNames.split(',');
    }
    this.roleDialogVisible = true;
  }

  function getRoles(data) {
    this.roles = data;
    this.form.roleNames = this.roles.join(',');
  }

  function clearRole(val) {
    const vals = this.removeArray(this.roles, val);
    this.roles = vals;
    this.form.roleNames = this.roles.join(',');
    this.roleDialogVisible = false;
  }

  function closeRole() {
    this.roleDialogVisible = false;
  }


  // 允许操作圈子

  async function addOperate() {
    if (this.form.operateIds !== '') {
      const operates = await this.getUsersByIds(this.form.operateIds);
      this.operates = operates;
    }
    this.operateDialogVisible = true;
  }

  function getOperates(data) {
    this.form.operateIds = '';
    this.form.operateNames = '';
    const ids = [];
    const names = [];
    data.forEach(d => {
      ids.push(d.id);
      names.push(d.realName);
    });
    this.form.operateIds = ids.join(',');
    this.form.operateNames = names.join(',');
  }

  function closeOperate() {
    this.operates = [];
    this.operateDialogVisible = false;
  }


  // 允许添加圈子
  async function addIsCircle() {
    if (this.form.addCircleIds !== '') {
      const isAddCircles = await this.getUsersByIds(this.form.addCircleIds);
      this.isAddCircles = isAddCircles;
    }
    this.addCircleDialogVisible = true;
  }

  function getAddCircles(data) {
    this.form.addCircleIds = '';
    this.form.addCircleNames = '';
    const ids = [];
    const names = [];
    data.forEach((d) => {
      ids.push(d.id);
      names.push(d.realName);
    });
    this.form.addCircleIds = ids.join(',');
    this.form.addCircleNames = names.join(',');
  }

  function closeIsCircle() {
    this.isAddCircles = [];
    this.addCircleDialogVisible = false;
  }

  // 允许添加项目
  async function addProject() {
    if (this.form.addProjectIds !== '') {
      const isAddProjects = await this.getUsersByIds(this.form.addProjectIds);
      this.isAddProjects = isAddProjects;
    }
    this.addProjectDialogVisible = true;
  }


  function getAddProjects(data) {
    this.form.addProjectIds = '';
    this.form.addProjectNames = '';
    const ids = [];
    const names = [];
    data.forEach(d => {
      ids.push(d.id);
      names.push(d.realName);
    });
    this.form.addProjectIds = ids.join(',');
    this.form.addProjectNames = names.join(',');
  }

  function closeProject() {
    this.isAddProjects = [];
    this.addProjectDialogVisible = false;
  }

  // 保存
  function save() {
    const ownerUid = this.form.ownerUid;
    if (ownerUid === '') {

    }
    this.$refs.ruleForm.validate((valid) => {
      if (valid) {
        this.$confirm('这个操作将' + this.tip + '圈子信息，您想继续吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          const result = await _updateCircle(this.form);
          if (result.code === 1200) {
            this.$message({
              type: 'success',
              message: '提交成功!'
            });
            await _putIndexedCircleOperation(this);
            this.$emit('saveCircle', result.data.circle);
          }
        }).catch((e) => {
          this.$message({
            type: 'info',
            message: '执行失败！',
          });
        });
      } else {
        this.$message.error('有空值');
        return false;
      }
    });

  }

  /**
   * 上传文件
   */
  function uploadFilesSucc(resFile) {
    this.form.imgUrl = resFile.path;
  }

  export default {
    name: 'CircleEdit',
    props: {
      circleId: String,
      parentCircle: Object,
    },
    components: {UserTree, RoleTransfer, CircleTree, UploadImage},
    data() {
      return {
        timer: '',
        // 圈长
        owners: [],
        ownerDialogVisible: false,
        // 隶属于
        circleDialogVisible: false,
        circle: {},
        // 角色
        roleDialogVisible: false,
        roles: ['圈长'],
        // 操作圈子
        operateDialogVisible: false,
        operates: [],
        //添加圈子
        addCircleDialogVisible: false,
        isAddCircles: [],
        //允许添加项
        addProjectDialogVisible: false,
        isAddProjects: [],
        form: {
          id: '',
          ownerName: '',
          ownerUid: '',
          name: '',
          description: '',
          parentId: '',
          parentName: '',
          roleNames: '圈长',
          isOperate: '0',
          operateIds: '',
          operateNames: '',
          isAddCircle: '0',
          addCircleIds: '',
          addCircleNames: '',
          isAddProject: '0',
          addProjectIds: '',
          addProjectNames: '',
          imgUrl: '',
        },
        userTree: {
          highlightCurrentRow: true
        },
        rules: {
          name: [
            {required: true, message: '请输入名称', trigger: 'blur'},
          ],
          description: [
            {required: true, message: '请输入描述', trigger: 'blur'},
          ],

        },

        tip: ''
      };
    },
    computed: {},
    created: async function () {
      this.getCircle(this.circleId);
    },
    beforeDestroy() {
      clearTimeout(this.timer);
    },
    methods: {
      cancelCircle,
      removeArray,
      getUser,
      getUsersByIds,
      queryCircle,
      getUsersByCircle,
      getRolesByCircle,
      addOwner,
      getCircle,
      getOwners,
      clearOwner,
      closeOwner,
      addCircle,
      clearCircle,
      submitCircle,
      addRole,
      getRoles,
      getRolesByRoleNames,
      clearRole,
      closeRole,
      addOperate,
      getOperates,
      closeOperate,
      addIsCircle,
      getAddCircles,
      closeIsCircle,
      addProject,
      getAddProjects,
      closeProject,
      save,
      closeCircle,
      uploadFilesSucc,
    },
    watch: {
      circleId(val) {
        this.getCircle(val);
      },
      parentCircle(val) {
        if (this.form.id === '') {
          this.circle = this.parentCircle === null ? {
            id: '',
            name: '',
          } : this.parentCircle;
          this.form.parentId = this.circle.id;
          this.form.parentName = this.circle.name;
        }
      }
    },
  };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
