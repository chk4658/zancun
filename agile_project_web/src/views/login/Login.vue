<template>
  <div style="
  position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;" v-on:keyup.enter=login>
    <canvas class="canvas" ref="canvas"/>
    <el-row :gutter="10" style="margin-top: 25vh">
      <el-col :md="{span:6,offset:9}" :sm="{span:12,offset:6}" :xs="{span:24,offset:0}">
        <el-card>
          <div style="padding-top: 10px;padding-bottom: 10px;font-weight: 100;color:#5d7191">
            SSDT-敏捷管理系统
          </div>
          <el-form
            ref="form"
            :model="form.data" :rules="form.rule" v-loading="loading" v-on:keyup.enter=login
          >
            <el-form-item label="账号" prop="account">
              <el-input
                v-model="form.data.account"
                :disabled="form.disabled"
              />
            </el-form-item>
            <el-form-item label="密码" prop="password">
              <el-input
                v-on:keyup.enter=login
                v-model="form.data.password"
                :disabled="form.disabled"
                show-password/>
            </el-form-item>
            <el-form-item>
              <el-button
                type="primary"
                @click="login" v-on:keyup.enter=login :disabled="form.disabled">
                登录
                <i class="el-icon-arrow-right el-icon--right" v-if="!form.disabled"/>
                <i class="el-icon-loading el-icon--right" v-if="form.disabled"/>
              </el-button>
              <language-switch style="float: right"/>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
  import {
    _login,
    _loginByUserId,
    _autoLogin,
  } from '@/api/loginApi';

  import LanguageSwitch from '@/components/LanguageSwitch.vue';

  import {_putIndexedCircleOperation} from '@/api/circleApi';


  async function login() {
    this.$refs.form.validate(async (valid) => {
      if (valid) {
        this.form.disabled = true;
        const result = await _login({
          account: this.form.data.account,
          password: this.form.data.password,
        });
        if (result && result.code === 1200) {
          this.$store.commit('setLoginInfo', {
            userName: result.data.user.realName,
            id: result.data.user.id,
            token: result.data.token,
          });
          localStorage.setItem('user', JSON.stringify(result.data.user));
          localStorage.setItem('roles', JSON.stringify(result.data.roles));
          const menus = result.data.menus.filter(menu => menu.type === 'MENU')
            .sort((a, b) => a.sort.localeCompare(b.sort));
          localStorage.setItem('MENUS', JSON.stringify(menus));
          const BUTTON = result.data.menus.filter(menu => menu.type === 'BUTTON')
            .map(x => x.path);
          localStorage.setItem('BUTTONS', JSON.stringify(BUTTON));
          localStorage.setItem('isAdmin', result.data.isAdmin);
          console.log(localStorage.getItem('token'));
          const circleOperation = result.data.circleOperation;
          await _putIndexedCircleOperation(this);
          await this.$router.push('/');
          return;
        }
        this.form.disabled = false;
      }
    });
  }


  async function autoLogin() {
    console.log(localStorage);
    const result = await _autoLogin({authUser: this.authUser});
    if (result && result.code === 1200) {
      this.$store.commit('setLoginInfo', {
        userName: result.data.user.realName,
        id: result.data.user.id,
        token: result.data.token,
      });
      localStorage.setItem('user', JSON.stringify(result.data.user));
      localStorage.setItem('roles', JSON.stringify(result.data.roles));
      const menus = result.data.menus.filter(menu => menu.type === 'MENU')
        .sort((a, b) => a.sort.localeCompare(b.sort));
      localStorage.setItem('MENUS', JSON.stringify(menus));
      const BUTTON = result.data.menus.filter(menu => menu.type === 'BUTTON')
        .map(x => x.path);
      localStorage.setItem('BUTTONS', JSON.stringify(BUTTON));
      localStorage.setItem('isAdmin', result.data.isAdmin);

      console.log(localStorage.getItem('token'));
      const circleOperation = result.data.circleOperation;
      await _putIndexedCircleOperation(this);
      await this.$router.push('/');
      return;
    } else {
      this.loading = false;
    }
  }


  export default {
    name: 'Login',
    components: {LanguageSwitch},
    async created() {
      const autoLogin = this.$route.params.autoLogin;
      this.loading = autoLogin === undefined ? true : autoLogin;
      if (this.loading) {
        this.autoLogin();
      }
    },
    methods: {
      login,
      autoLogin,
    },
    watch: {},
    mounted: async function () {
    },
    data() {
      return {
        loading: true,
        authUser: localStorage.getItem("authUser"),
        form: {
          disabled: false,
          data: {
            account: null,
            password: null,
          },
          rule: {
            account: [{
              required: true,
              message: '请输入账号',
              trigger: 'blur',
            }],
            password: [{
              required: true,
              message: '请输入密码',
              trigger: 'blur',
            }],
          },
        },
      };
    },
  };
</script>

<style scoped>
  .canvas {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
  }
</style>
