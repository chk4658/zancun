<template>
  <el-form
    ref="form"
    :model="form.data"
    label-width="100px"
    :rules="form.rules">
    <el-form-item label="枚举类名称" prop="name">
      <el-input v-model="form.data.name" style="width: 400px"></el-input>
    </el-form-item>
    <el-form-item label="编码" prop="code">
      <el-input v-model="form.data.code" style="width: 400px"></el-input>
    </el-form-item>
    <el-form-item label="状态值" v-if="enumerationId">
      <div v-for="(item,index) in form.data.dictData" :key="item.id">
        <div style="width: 700px">
          状态名称
          <el-input v-model="item.name"
                    style="width: 140px;margin-left: 8px;margin-right: 18px"></el-input>
          颜色层级
          <el-input v-model="item.color"
                    style="width: 90px;margin-left: 8px;margin-right: 18px"></el-input>
          状态编码
          <el-input v-model="item.code"
                    style="width: 100px;margin-left: 8px;margin-right: 12px"></el-input>
          <i @click="deleteDictData(item.id,index)" class="el-icon-delete"
             style="-webkit-user-select: none;
                -moz-user-select: none;
                -ms-user-select: none;
                user-select: none;
                color: #01408B;
                vertical-align: middle;
                font-size: 22px;
                cursor: pointer;"></i>
        </div>
      </div>
      <i @click="addDictData" class="el-icon-circle-plus-outline"
         style="-webkit-user-select: none;
                -moz-user-select: none;
                -ms-user-select: none;
                user-select: none;
                color: #01408B;
                font-size: 22px;
                vertical-align: middle;
                cursor: pointer;"></i>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" v-if="!enumerationId" @click="onAdd">创建</el-button>
      <el-button type="primary" v-if="enumerationId" @click="onUpdate">更新</el-button>
      <el-button @click="onCancel">取消</el-button>
    </el-form-item>
  </el-form>
</template>
<script>
  import {
    _getEnumerationById,
    _saveEnumeration,
    _saveDictData,
    _deleteDictData,
  } from '@/api/sysDictApi';

  async function onAdd() {
    this.form.disabled = true;
    this.$refs.form.validate(async (valid) => {
      if (valid) {
        const result = await _saveEnumeration({
          name: this.form.data.name,
          code: this.form.data.code,
        });
        if (result.code === 1200) {
          if (this.onSubmitHandler) {
            this.$message({
              message: '枚举类创建成功',
              type: 'success',
              center: true,
            });
            this.onSubmitHandler();
          }
        }
      }
    });
  }

  async function onUpdate() {
    this.form.disabled = true;
    this.$refs.form.validate(async (valid) => {
      if (valid) {


        const len = this.form.data.dictData.filter(dict => dict.name === '' && dict.color === '' && dict.code === '').length;
        if (len > 0) {
          this.$confirm('是否更新' + len + '条空数据?', '提示', {
            confirmButtonText: '确认',
            cancelButtonText: '再看看',
            type: 'warning',
          }).then(async () => {
            if (this.form.data.dictData.length !== 0) {
              this.form.data.dictData.forEach((item) => {
                _saveDictData({
                  name: item.name,
                  id: item.id === undefined ? null : item.id,
                  code: item.code,
                  color: item.color,
                  sysDictId: item.sysDictId,
                });
              });
            }
            const result = await _saveEnumeration({
              id: this.enumerationId,
              name: this.form.data.name,
              code: this.form.data.code,
            });
            if (result.code === 1200) {
              if (this.onSubmitHandler) {
                this.$message({
                  message: '枚举类更新成功',
                  type: 'success',
                  center: true,
                });
                this.onSubmitHandler();
              }
            }
          })
        }else {
          if (this.form.data.dictData.length !== 0) {
            this.form.data.dictData.forEach((item) => {
              _saveDictData({
                name: item.name,
                id: item.id === undefined ? null : item.id,
                code: item.code,
                color: item.color,
                sysDictId: item.sysDictId,
              });
            });
          }
          const result = await _saveEnumeration({
            id: this.enumerationId,
            name: this.form.data.name,
            code: this.form.data.code,
          });
          if (result.code === 1200) {
            if (this.onSubmitHandler) {
              this.$message({
                message: '枚举类更新成功',
                type: 'success',
                center: true,
              });
              this.onSubmitHandler();
            }
          }
        }


      }
    });
  }

  async function getEnumerationById(a) {
    const result = await _getEnumerationById(a);
    if (result.code === 1200) {
      this.form.data.name = result.data.SysDict.name;
      this.form.data.code = result.data.SysDict.code;
      this.form.data.dictData = result.data.SysDict.sysDictDataList;
    }
  }

  function onCancel() {
    if (this.onCancelHandler) {
      this.onCancelHandler();
    }
  }

  async function deleteDictData(a, b) {
    if (a !== undefined) {
      const result = await _deleteDictData(a);
      if (result.code === 1200) {
        this.$message({
          message: '状态值删除成功',
          type: 'success',
          center: true,
        });
      }
      this.form.data.dictData.splice(b, 1);
    } else {
      this.$message({
        message: '取消新增',
        type: 'success',
        center: true,
      });
      this.form.data.dictData.splice(b, 1);
    }
  }

  async function addDictData() {
    const newData = {
      name: '',
      code: '',
      color: '',
      sysDictId: this.enumerationId,
    };
    this.form.data.dictData.push(newData);
  }

  export default {
    name: 'EnumerationEdit',
    props: {
      enumerationId: String,
      onSubmitHandler: Function,
      onCancelHandler: Function,
    },
    data() {
      return {
        form: {
          data: {
            name: '',
            code: '',
            dictData: [],
          },
          disabled: false,
          rules: {
            name: [{
              required: true,
              message: '请输入枚举类名称',
              trigger: 'blur',
            }],
            code: [{
              required: true,
              message: '请输入编码',
              trigger: 'blur',
            }],
          },
        },
      };
    },
    methods: {
      onAdd,
      onCancel,
      getEnumerationById,
      onUpdate,
      addDictData,
      deleteDictData,
    },
    created() {
      if (this.enumerationId) {
        this.getEnumerationById(this.enumerationId);
      }
    },
  };
</script>
