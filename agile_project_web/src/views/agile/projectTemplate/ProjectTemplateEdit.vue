<template>
  <div>
    <el-form :model="form" :rules="rules" ref="ruleForm" label-width="150px" style="width:600px">
      <el-form-item label="上传图片" prop="imgUrl">
        <!--  图片上传组件-->
        <UploadImage
          ref="uploadRefImage"
          :imageName="form.imgUrl"
          @uploadFilesSucc="uploadFilesSucc">
        </UploadImage>
      </el-form-item>
      <el-form-item label="名称" prop="name">
        <el-input maxlength="50" show-word-limit v-model="form.name"></el-input>
      </el-form-item>
      <el-form-item label="项目模板类型" prop="projectTemplateTypeId">
        <el-select v-model="form.projectTemplateTypeId" placeholder="请选择">
          <el-option
            v-for="item in projectTemplateTypes"
            :key="item.id"
            :label="item.name"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="启用/禁用">
        <el-switch
          v-model="form.enabledStatus">
        </el-switch>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="save()">提交</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>

  import {_queryProjectTemplateTypes} from '@/api/projectTempleTypeApi';


  import {_queryProjectTemplate, _updateProjectTemplate} from '@/api/projectTempleApi';

  import UploadImage from '@/components/UploadImage';

  /**
   * 上传文件
   */
  function uploadFilesSucc(resFile) {
    this.form.imgUrl = resFile.path;
  }


  async function getProjectTemplateTypes() {
    const result = await _queryProjectTemplateTypes({});
    if (result.code === 1200) {
      this.projectTemplateTypes = result.data.projectTemplateTypes;
    }
  }


  async function getProjectTemplate(id) {
    if (id === '' || id === '-1') {
      this.$set(this.form, "id", '');
      this.$set(this.form, "name", '');
      this.$set(this.form, "imgUrl", '');
      this.$set(this.form, "projectTemplateTypeId", this.projectTemplateTypeId);
      this.$set(this.form, "enabledStatus", this.enabledStatus);
      this.$set(this.form, "enabled", 0);
    } else {
      const result = await _queryProjectTemplate(id);
      if (result.code === 1200) {
        const projectTemplates = result.data.projectTemplate;
        this.$set(this.form, "id", projectTemplates.id);
        this.$set(this.form, "name", projectTemplates.name);
        this.$set(this.form, "imgUrl", projectTemplates.imgUrl);
        this.$set(this.form, "projectTemplateTypeId", projectTemplates.projectTemplateTypeId);
        this.$set(this.form, "enabledStatus", projectTemplates.enabled === 0 ? true : false);
        this.$set(this.form, "enabled", projectTemplates.enabled);
      }
    }
  }


  async function save() {
    this.$refs.ruleForm.validate((valid) => {
      if (valid) {
        this.$confirm('这个操作将创建项目模板信息，您想继续吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(async () => {
          this.form.enabled = this.form.enabledStatus ? 0 : 1;
          const result = await _updateProjectTemplate(this.form);
          if (result.code === 1200) {
            this.$message({
              type: 'success',
              message: '提交成功!'
            });

            this.$refs.uploadRefImage.clearImage();
            this.$emit("save");
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


  export default {
    name: 'ProjectTemplateEdit',
    components: {UploadImage},
    props: {
      projectTemplateId: String,
      projectTemplateTypeId: String,
      enabledStatus: Boolean,
      isClearImage: Boolean
    },
    data() {
      return {
        projectTemplateTypes: [],
        form: {
          id: '',
          name: '',
          imgUrl: '',
          projectTemplateTypeId: '',
          enabled: 0,
          enabledStatus: true,
        },
        rules: {
          imgUrl: [
            {required: true, message: '请输入上传图片', trigger: 'change'},
          ],
          name: [
            {required: true, message: '请输入名称', trigger: 'blur'},
          ],
          projectTemplateTypeId: [
            {required: true, message: '请选择模板类型', trigger: 'change'}
          ],
        },
      }
    },
    computed: {},
    created: async function () {
      await this.getProjectTemplateTypes();
      this.getProjectTemplate(this.projectTemplateId);
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
      uploadFilesSucc,
      getProjectTemplateTypes,
      getProjectTemplate,
      save
    },
    watch: {
      projectTemplateId: async function (val) {
        await this.getProjectTemplateTypes();
        await this.getProjectTemplate(val);
        this.getProjectTemplate(val);
      },
      projectTemplateTypeId: async function (val) {
        await this.getProjectTemplateTypes();
        this.form.projectTemplateTypeId = val;
      },
      enabledStatus(val) {
        this.form.enabledStatus = val;
      },
      isClearImage(val) {
        this.$refs.uploadRefImage.clearImage();
      }
    },
    directives: {}
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }

  .avatar-uploader .el-upload:hover {
    border-color: #409EFF;
  }

  .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 100px;
    height: 100px;
    line-height: 100px;
    text-align: center;
    border: 1px solid #e1e1e1;
  }

  .avatar {
    width: 100px;
    height: 100px;
    display: block;
  }
</style>
