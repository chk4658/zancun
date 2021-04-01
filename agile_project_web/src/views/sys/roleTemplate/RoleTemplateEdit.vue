<template>
  <el-form
    ref="form"
    :model="form.data"
    label-width="100px"
    :rules="form.rules">
    <el-form-item label="名称" prop="name">
      <el-input v-model="form.data.name"></el-input>
    </el-form-item>
    <el-form-item label="角色描述">
      <el-input v-model="form.data.description"></el-input>
    </el-form-item>
    <el-form-item label="职责与使命">
      <el-input type="textarea" v-model="form.data.duty"></el-input>
    </el-form-item>
    <el-form-item label="可用/禁用" prop="enabled">
      <el-radio v-model="form.data.enabled" label="0">可用</el-radio>
      <el-radio v-model="form.data.enabled" label="1">禁用</el-radio>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" v-if="!roleTemplateId" @click="onAdd">创建</el-button>
      <el-button type="primary" v-if="roleTemplateId" @click="onUpdate">更新</el-button>
      <el-button @click="onCancel">取消</el-button>
    </el-form-item>
  </el-form>
</template>
<script>
  import {_saveOrUpdateRoleTemplate, _getRoleTemplateById} from '@/api/sysRoleTemplateApi.js';

  async function getRoleTemplateById(a) {
    const result = await _getRoleTemplateById(a);
    if (result.code === 1200) {
      this.form.data.name = result.data.SysRoleTemplate.name;
      this.form.data.description = result.data.SysRoleTemplate.description;
      this.form.data.duty = result.data.SysRoleTemplate.duty;
      this.form.data.enabled = result.data.SysRoleTemplate.enabled.toString();
    }
  }

  async function onUpdate() {
    this.form.disabled = true;
    this.$refs.form.validate(async (valid) => {
      if (valid) {
        const result = await _saveOrUpdateRoleTemplate({
          name: this.form.data.name,
          description: this.form.data.description,
          duty: this.form.data.duty,
          enabled: this.form.data.enabled,
          id: this.roleTemplateId,
        });
        if (result.code === 1200) {
          if (this.onSubmitHandler) {
            this.$message({
              message: '角色模板更新成功',
              type: 'success',
              center: true,
            });
            this.onSubmitHandler();
          }
        }
      }
    });
  }

  async function onAdd() {
    this.form.disabled = true;
    this.$refs.form.validate(async (valid) => {
      if (valid) {
        const result = await _saveOrUpdateRoleTemplate({
          name: this.form.data.name,
          description: this.form.data.description,
          duty: this.form.data.duty,
          enabled: this.form.data.enabled,
        });
        if (result.code === 1200) {
          if (this.onSubmitHandler) {
            this.$message({
              message: '角色模板创建成功',
              type: 'success',
              center: true,
            });
            this.onSubmitHandler();
          }
        }
      }
    });
  }

  function onCancel() {
    if (this.onCancelHandler) {
      this.onCancelHandler();
    }
  }

  export default {
    name: 'RoleTemplateEdit',
    props: {
      roleTemplateId: String,
      onSubmitHandler: Function,
      onCancelHandler: Function,
    },
    data() {
      return {
        form: {
          data: {
            name: '',
            description: '',
            duty: '',
            enabled: '',
          },
          disabled: false,
          rules: {
            name: [{
              required: true,
              message: '请输入角色模板名称',
              trigger: 'blur',
            }],
            enabled: [{
              required: true,
              message: '请选择是否可用',
              trigger: 'blur',
            }],
          },
        },
      };
    },
    methods: {
      onUpdate,
      getRoleTemplateById,
      onAdd,
      onCancel,
    },
    created() {
      if (this.roleTemplateId) {
        this.getRoleTemplateById(this.roleTemplateId);
      }
    },
  };
</script>
