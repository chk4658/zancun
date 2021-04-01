<template>
  <div>
    <el-form :model="form" :rules="rules" ref="ruleForm" label-width="150px" style="width:90%">
      <el-form-item label="月份" prop="name">
        <el-date-picker
          v-model="form.months"
          value-format="yyyy-MM"
          type="month"
          placeholder="选择月"
          size="medium"
          style="width:100%"
          :disabled="form.id !== ''">
        </el-date-picker>
      </el-form-item>
      <el-form-item v-show="form.id === ''">
        <div style="margin: 10px 20px 10px;">
          <el-button round :style="'background-color:' + ( activeName === 'filling'  ? '#DCDFE6;' : ';') + 'width:80px'"
          @click="active('filling')">填写</el-button>
          <el-button round :style="'background-color:' + ( activeName === 'import'  ? '#DCDFE6;' : ';') + 'width:80px'"
          @click="active('import')">导入</el-button>
        </div>
      </el-form-item>
      <div v-show="activeName === 'filling'">
        <el-form-item label="人员">
          <el-input v-model="form.userName" @focus="selectUsers" readonly
                    v-if="equivalentId === ''||equivalentId==='-1'"></el-input>
          <el-input v-model="form.userName" disabled v-if="!(equivalentId === ''||equivalentId==='-1')"></el-input>
        </el-form-item>
        <el-form-item label="其他当量">
          <el-input-number v-model="form.other" :controls="false"></el-input-number>
        </el-form-item>
        <el-form-item label="交付质量">
          <el-input-number v-model="form.deliveryQuality" :min="0" :controls="false"></el-input-number>
        </el-form-item>
        <el-form-item label="知识">
          <el-input-number v-model="form.knowledge" :min="0" :controls="false"></el-input-number>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="save">保存</el-button>
        </el-form-item>
      </div>
      <div v-show="activeName === 'import'">
        <el-form-item >
          <el-upload action="/api/upload"
            :headers="{
              token: token
            }"
            accept=".xls,.xlsx"
            :on-success="uploadSuccess"
            :file-list="fileList">
            <el-button icon="el-icon-upload">上传EXCEL</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitImport">提交</el-button>
          <el-button type="primary" @click="download">模板下载</el-button>
        </el-form-item>
      </div>

    </el-form>

    <el-dialog
      title="选择用户"
      @close="closeUsers"
      :visible.sync="userDialogVisible"
      width="1200px"
      append-to-body>
      <user-tree
        :isSingle='true'
        :usersProp='users'
        @getUsers="getUsers"
        @getCancel="closeUsers">
      </user-tree>
    </el-dialog>

  </div>
</template>

<script>


  import UserTree from '@/components/UserTree';

  import { _updateEquivalen, _getEquivalen,_excelImportEquivalen } from '@/api/equivalenApi';


  import { downloadFile } from '@/api/utils';

  function selectUsers() {
    this.userDialogVisible = true;
  }

  function getUsers(data) {
    this.users = data;
    if (data !== null && data.length > 0) {
      this.form.sysUserId = data[0].id;
      this.form.userName = data[0].realName;
    }
    this.closeUsers();
  }

  function closeUsers() {
    this.userDialogVisible = false;
  }

  async function getEquivalen() {
    if (this.equivalentId === '' || this.equivalentId === '-1') {
      this.users = [];
      this.form.id = '';
      this.form.project = '';
      this.form.task = undefined;
      this.form.other = undefined;
      this.form.deliveryQuality = undefined;
      this.form.knowledge = undefined;
      this.form.work = '';
      this.form.sysUserId = '';
      this.form.userName = '';
      this.form.months = '';
      this.form.path = ''
      this.fileList = [];
      this.users = {};
    } else {
      this.activeName = "filling";
      const result = await _getEquivalen(this.equivalentId);
      const equivalent = result.data.equivalent;
      this.users = [equivalent.sysUser];
      this.form.id = equivalent.id;
      this.form.project = equivalent.project;
      this.form.task = equivalent.task;
      this.form.other = equivalent.other === null ? undefined : equivalent.other;
      this.form.deliveryQuality = equivalent.deliveryQuality === null ? undefined : equivalent.deliveryQuality;
      this.form.knowledge = equivalent.knowledge === null ? undefined : equivalent.knowledge;
      this.form.work = equivalent.work;
      this.form.sysUserId = equivalent.sysUser.id;
      this.form.userName = equivalent.sysUser.realName;
      this.form.months = equivalent.months;
    }
  }


  function save() {
    this.$refs.ruleForm.validate((valid) => {
      if (valid && this.form.sysUserId !== '' && this.form.months !== '') {
        this.$confirm('这个操作将提交当量信息，您想继续吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          const result = await _updateEquivalen(this.form);
          if (result.code === 1200) {
            this.$message({
              type: 'success',
              message: '提交成功!'
            });
            this.$emit('save');
          }
        }).catch((e) => {
          this.$message({
            type: 'info',
            message: '执行失败！',
          });
        });
      } else {
        this.$message.error('检查人员或者月份是否是空');
        return false;
      }
    });

  }

  function active(name) {
    this.activeName = name;
    this.form.task = undefined;
    this.form.other = undefined;
    this.form.deliveryQuality = undefined;
    this.form.knowledge = undefined;
    this.form.sysUserId = '';
    this.form.userName = '';
    this.users = [];
    this.form.path = '';
  }


  /**
   * 上传文件
   */
  function uploadSuccess(res, file) {
    const path = res.data.path;
    this.form.path = path;
    this.fileList = [{name: file.name, url: path}]
  }


  function download() {
    downloadFile("data/equivalent/当量管理模板.xlsx").then((response1) => {});
  }

  async function submitImport() {
    if(this.form.months === '' || this.form.path === '') {
      this.$message.error('请选择月份且上传文件');
    } else {
      this.$confirm('这个操作将提交当量信息，您想继续吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        const result = await _excelImportEquivalen(this.form);
        if (result.code === 1200) {
          this.$message({
            type: 'success',
            message: '提交成功!'
          });
          this.$emit('save');
        }
      }).catch((e) => {
        this.$message({
          type: 'info',
          message: '执行失败！',
        });
      });
    }


  }

  export default {
    props: {
      equivalentId: String,
    },
    components: {UserTree},
    data() {
      return {
        token: localStorage.getItem('token'),
        activeName: "filling",
        form: {
          id: '',
          project: '',
          task: undefined,
          other: undefined,
          deliveryQuality: undefined,
          knowledge: undefined,
          work: '',
          sysUserId: '',
          userName: '',
          months: '',
          path: '',
        },
        fileList: [],
        userDialogVisible: false,
        users: [],
        rules: {},
      }
    },
    computed: {},
    created: function () {
      this.getEquivalen();
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
      selectUsers,
      getUsers,
      closeUsers,
      save,
      getEquivalen,
      active,
      uploadSuccess,
      download,
      submitImport,
    },
    watch: {
      equivalentId(val) {
        this.getEquivalen();
      }
    },
    directives: {}
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
</style>
