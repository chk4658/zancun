<template>
  <div>
    <div class="role-transfer">
      <div style="margin:10px 5px 15px 30px;">
        <div style="margin:10px 0 5px 0;">
          <el-input v-model="name" style="width:200px;"></el-input>
          <el-button type="text" @click="add" style="margin-left: 10px;">添加</el-button>
        </div>
        <el-transfer
          filterable
          v-model="value"
          :data="data"
          class="hide-checkbox"
          @change="change"
          @left-check-change="leftCheckChange"
          @right-check-change="rightCheckChange">
        </el-transfer>
      </div>
      <div style="margin:10px 0 15px 230px;">
        <el-button type="primary" @click="select">选择</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </div>
  </div>
</template>

<script>

  import {_queryRoleTemplates} from '@/api/sysRoleTemplateApi';

  async function getRoles() {
    this.data = [];
    const s = new Set();
    this.rolesProp.forEach(role => {
      if (role !== '') {
        s.add(role);
      }
    });
    const result = await _queryRoleTemplates({});
    if (result.code === 1200) {
      const rolesTem = result.data.sysRoleTemplateList;


      if (this.rolePlus !== undefined && this.rolePlus.length !== 0) {
        var roles = rolesTem.concat(this.rolePlus)
      } else {
        var roles = rolesTem;
      }


      roles.forEach(role => {
        s.add(role.name || role.roleName);
      });
      for (var x of s) { // 遍历Set
        this.data.push({
          key: x,
          label: x,
          disabled: this.isCircler !== undefined && this.isCircler ? x === '圈长' : false
        });
      }
      this.$nextTick(() => {
        this.setVale();
      });
    }
  }

  function setVale() {
    this.value = [];
    this.rolesProp.forEach(role => {
      if (role !== '') {
        this.value.push(role);
      }

    });
  }


  function change(v) {
    if (this.isSingle) {

      this.value = [v[v.length - 1]];

    }
  }

  function leftCheckChange(v) {

    if (this.isSingle) {
      this.value = [v[v.length - 1]];

    } else {
      this.value.push(...v);
    }
  }


  function rightCheckChange(vs) {
    if (this.isSingle) {
      this.value = [];
    } else {
      vs.forEach(v => {
        this.value.splice(this.value.indexOf(v), 1);
      });
    }
  }


  function add() {
    if (this.name.replace(/(^\s*)|(\s*$)/g, "") !== "") {
      const key = this.name;
      const result = this.data.find(d => {
        return d.key === key;
      });
      if (result === undefined) {
        this.data.push({
          label: this.name,
          key: this.name,
          id: '',
        });
        if (this.isSingle) {
          this.value = [this.name];
        } else {
          this.value.push(this.name);
        }
      } else {
        this.$message({
          type: 'warning',
          message: '角色已有，请重输!',
          center: 'true'
        });
      }
    } else {

    }

  }

  function select() {
    this.$emit('getRoles', this.value);
    this.$emit('getCancel');
    this.$emit('refreshRole');

  }

  function cancel() {
    this.$emit('getCancel');
  }


  export default {
    name: 'RoleTransfer',
    props: {
      rolesProp: Array,
      isSingle: Boolean,
      rolePlus: Array,
      isCircler: Boolean
    },
    data() {
      return {
        name: '',
        data: [],
        value: [],
      };
    },
    computed: {},
    created: function () {
      this.getRoles();
    },
    methods: {
      getRoles,
      add,
      select,
      cancel,
      setVale,
      change,
      leftCheckChange,
      rightCheckChange
    },
    watch: {
      rolesProp(vals) {
        this.setVale();
      },
      rolePlus(val) {
        if (this.rolePlus !== undefined && this.rolePlus.length !== 0) {
          this.getRoles()
        }
      }
    },
    directives: {}
  };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
  .role-transfer {
    border: 1px #e1e1e1 solid;
    // text-align: center;
    // vertical-align: middle;
  }
</style>

<style lang="scss">
  .role-transfer{
    .hide-checkbox > {

      .el-transfer-panel:nth-child(n+2) {
        margin-left: 90px;
      }
      .el-transfer-panel {
        .el-transfer-panel__header {

          display: none;
          .el-checkbox {
            .el-checkbox__input {
              display: none;
            }
          }
        }

        .el-transfer-panel__body {
          .el-checkbox-group, .el-transfer-panel__list, .is-filterable {
            .el-checkbox, .el-transfer-panel__item {
              .el-checkbox__input {
                display: none;
              }
            }
          }

        }
      }
      .el-transfer__buttons{
        display: none;
      }

    }
  }

</style>
