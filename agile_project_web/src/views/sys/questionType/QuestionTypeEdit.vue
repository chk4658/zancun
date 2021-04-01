<template>
  <el-form ref="form" :model="form.data" :rules="form.rules">
    <el-form-item label="问题类别名称" prop="name">
      <el-input v-model="form.data.name"/>
    </el-form-item>
    <el-form-item label="级联" prop="parentId">
      <el-cascader
        :props="{ checkStrictly: true }"
        v-model="form.data.parentId"
        :options="questionType.tree"/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" v-if="!questionTypeId" @click="onAdd">创建</el-button>
      <el-button type="primary" v-if="questionTypeId" @click="onUpdate">更新</el-button>
      <el-button @click="onCancel">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script>
  import {
    _saveOrUpdate,
    _getListById,
    _getParentList,
  } from '@/api/sysQuestionType.js';


  async function getQuestionTypes() {
    const result = await _getParentList();
    if (result.code === 1200) {
      let tem = result.data.questionTypeList
      if (this.questionTypeId) {
        tem = result.data.questionTypeList.filter(qt => qt.id !== this.questionTypeId)
      }
      this.questionType.tree = [{
        value: null,
        label: '根节点',
        children: tem.map(qt => {
          return {
            children: null,
            value: qt.id,
            label: qt.name
          }
        }),
      }]
    }

  }

  async function getQuestionType() {
    const result = await _getListById(this.questionTypeId);
    if (result.code === 1200) {
      this.form.data.name = result.data.selectOne.name;
      this.form.data.parentId = result.data.selectOne.parentId === null ? [null] : result.data.selectOne.parentId;
    }
  }

  async function onAdd() {
    this.form.disabled = true;
    this.$refs.form.validate(async (valid) => {
      if (valid) {
        const obj = {
          name: this.form.data.name,
          parentId: (typeof this.form.data.parentId) === 'string'
            ? this.form.data.parentId : this.form.data.parentId[this.form.data.parentId.length - 1],
        }
        const result = await _saveOrUpdate(obj);
        if (result.code === 1200) {
          if (this.onSubmitHandler) {
            this.$message({
              message: '添加成功',
              type: 'success',
              center: true,
            });
            this.onSubmitHandler();
          }
        }
      } else {
        this.form.disabled = false;
      }
    });
  }

  async function onUpdate() {
    this.form.disabled = true;
    this.$refs.form.validate(async (valid) => {
      if (valid) {
        const obj = {
          id: this.questionTypeId,
          name: this.form.data.name,
          parentId: (typeof this.form.data.parentId) === 'string'
            ? this.form.data.parentId : this.form.data.parentId[this.form.data.parentId.length - 1],
        }
        const result = await _saveOrUpdate(obj);
        if (result.code === 1200) {
          if (this.onSubmitHandler) {
            this.$message({
              message: '更新成功',
              type: 'success',
              center: true,
            });
            if (this.onSubmitHandler) {
              this.onSubmitHandler();
            }
          }
        }
      } else {
        this.form.disabled = false;
      }
    });
  }

  function onCancel() {
    if (this.onCancelHandler) {
      this.onCancelHandler();
    }
  }

  export default {
    components: {},
    name: 'QuestionTypeEdit',
    props: {
      questionTypeId: String,
      onSubmitHandler: Function,
      onCancelHandler: Function,
    },
    watch: {
      questionTypeId(val) {
        this.form.data = {
          name: '',
          parentId: [null],
        };
        this.getQuestionTypes();
        if (val) {
          this.getQuestionType();
        }
      },
    },
    methods: {
      onAdd,
      onUpdate,
      onCancel,
      getQuestionType,
      getQuestionTypes,
    },
    data() {
      return {
        questionType: {
          data: [],
          tree: [],
        },
        form: {
          data: {
            name: '',
            parentId: [null],
          },
          rules: {
            name: [{
              required: true,
              message: '请输入菜单名称',
              trigger: 'blur',
            }],
            parentId: [{
              required: false,
              message: 'please choose parentId',
              trigger: 'blur',
            }],
          },
          disabled: false,
        },
      };
    },
    created() {
      this.getQuestionTypes();
      if (this.questionTypeId) {
        this.getQuestionType();
      }
    },
  };
</script>
