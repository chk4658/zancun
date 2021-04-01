<template>
  <el-form ref="form" :model="form.data" :rules="form.rules" label-width="80px">
    <el-tabs v-model="tab">
      <el-tab-pane label="修改密码" name="userInfo" style="width: 20%">
        <el-form-item label="姓名" prop="userName">
          <el-input :disabled="true" v-model="form.data.userName"/>
        </el-form-item>
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input type="password" v-model="form.data.oldPassword"/>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="form.data.newPassword"/>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="form.data.confirmPassword"/>
        </el-form-item>
      </el-tab-pane>
    </el-tabs>
    <el-form-item>
      <el-button type="primary" @click="onUpdate">修改</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
import {
  _queryUser,
  _updateUserPassword,
} from '@/api/sysUserApi';

async function getUser() {
  const result = await _queryUser(this.id);
  if (result.code === 1200) {
    this.form.data.userName = result.data.user.userName;
  }
}

async function onUpdate() {
  this.$refs.form.validate(async (valid) => {
    if (valid) {
      const result = await _updateUserPassword({
        newPassword: this.form.data.newPassword,
        oldPassword: this.form.data.oldPassword,
      });
      if (result.code === 1200) {
        this.$message({
          message: '修改成功',
          type: 'success',
        });
      }
    }
  });

}

export default {
  name: 'UserEdit',
  watch: {},
  methods: {
    onUpdate,
    getUser,
  },
  data() {
    const password = (rule, callback) => {
      if (this.form.data.confirmPassword !== this.form.data.newPassword) {
        callback(new Error('两次密码输入不一致'));
      }
    };
    const confirmPasswordRule = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'));
      } else if (value !== this.form.data.newPassword) {
        callback(new Error('两次输入密码不一致!'));
      } else {
        callback();
      }
    };
    return {
      id: this.$store.state.id,
      tab: 'userInfo',
      role: {
        data: [],
      },
      form: {
        data: {
          userName: '',
          newPassword: '',
          confirmPassword: '',
          oldPassword: '',
        },
        rules: {
          userName: [{required: false, message: '请输入姓名', trigger: 'blur'}],
          oldPassword: [{required: true, message: '请输入旧密码', trigger: 'blur'}],
          newPassword: [
            {required: true, trigger: 'blur'}],
          confirmPassword: [
            {required: true, validator: confirmPasswordRule, trigger: 'blur'}],
        },
        disabled: false,
        title: '',
      },
    };
  },
  async created() {
    this.tab = 'userInfo';
    this.getUser();
  },
};
</script>
