<template>
  <div class="information">
    <main-top-part>
      <template v-slot:title>
        <div style="display: flex">
          <i class="fa fa-th-list menu-icon" style="color: #01408B;font-size: 32px;">
          </i>项目模板
        </div>
      </template>

      <template v-slot:button>
        <div>

        </div>
      </template>
      <template v-slot:btn-search>
        <div class="btn-search">
          <el-button type="primary"
                     style="border-radius: 20px !important;"
                     size="medium"
                     @click="add('',
                projectTemplateTypeId === 'all' || projectTemplateTypeId === 'enabled' ? '' : projectTemplateTypeId
                )">
            新增
          </el-button>
          <el-input
            size="small"
            placeholder="搜索"
            @input="getProjectTemplates"
            style="width: 328px;margin-left: 10px"
            v-model="searchName">
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
        </div>

      </template>

    </main-top-part>

    <div style="padding: 0 20px 0; margin-bottom:20px; overflow-y: auto;overflow-x: hidden">
      <el-row>
        <el-col v-for="(item,index) in projectTemplateDatas" :key="index" :span="8" style="padding: 15px 20px 0 0;">
          <el-card class="box-card" body-style='padding: 1px;background-color:#F5F5F5'>
            <div style="width: 100%;">
              <el-image :src="'/api'+item.imgUrl" style="height:200px;width:100%">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </div>
            <div style="padding-left:10px;height:50px;">
              <el-tooltip class="item" effect="dark" :content="item.name" placement="top-start">
                <el-button type="text"
                           style="font-size: 15px;padding-top:5px;"
                           @click="click(item.id)">
                  {{item.name.length > 10 ? item.name.substring(0,9) : item.name}}
                </el-button>
              </el-tooltip>

              <div style="font-size: 13px;padding-top:5px;">
                {{item.projectTemplateType === null ? '' : item.projectTemplateType.name}}
              </div>
            </div>
            <div style="
              font-size: 10px;
              margin:20px 0 5px 10px;
              position:relative;
              ">
              {{item.createAt}}
              <div style="position:absolute;right:10px;bottom:-7px">
                <el-button type="text" icon="el-icon-edit" style="font-size: 16px;"
                           :disabled='userId !== item.createUserId && !hasEdit'
                           @click="add(item.id,item.projectTemplateType.id)"></el-button>
                <el-button type="text" icon="el-icon-delete" style="font-size: 16px;"
                           :disabled='userId !== item.createUserId && !hasDel'
                           @click="del(item.id)"></el-button>
                <el-button type="text" :icon="item.enabled === 0 ? 'el-icon-open' : 'el-icon-turn-off'"
                           style="font-size: 16px;"
                           :disabled='userId !== item.createUserId && !hasEnabled'
                           @click="enabled(item.id,item.enabled === 0 ? true : false)"></el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
    <el-dialog
      :title="projectTemplate.id === '' ? '新增项目模板' : '修改项目模板'"
      @close="close"
      :visible.sync="projectTemplate.dialogVisible"
      width='40%'
      append-to-body>
      <project-template-edit
        :isClearImage="isClearImage"
        :projectTemplateId='projectTemplate.id'
        :projectTemplateTypeId='projectTemplate.projectTemplateTypeId'
        :enabledStatus='projectTemplateTypeId !== "enabled"'
        @save='save'></project-template-edit>
    </el-dialog>
  </div>
</template>

<script>

  import {
    _listProjectTemplate,
    _listAllProjectTemplate,
    _listEnabledProjectTemplate,
    _deleteProjectTemplate,
    _enabledProjectTemplate
  } from '@/api/projectTempleApi';


  import ProjectTemplateEdit from './ProjectTemplateEdit';

  import MainTopPart from '@/components/MainTopPart';


  async function getProjectTemplates() {
    let result = {};
    if (this.projectTemplateTypeId === 'all') {
      result = await _listAllProjectTemplate({
        searchName: this.searchName
      });
    } else if (this.projectTemplateTypeId === 'enabled') {
      result = await _listEnabledProjectTemplate({
        searchName: this.searchName
      });
    } else {
      result = await _listProjectTemplate({
        projectTemplateTypeId: this.projectTemplateTypeId,
        searchName: this.searchName
      });
    }
    if (result !== null && result.code === 1200) {
      this.projectTemplateDatas = result.data.projectTemplates;

      console.log(this.projectTemplateDatas);

    }
  }

  function add(id, projectTemplateTypeId) {
    this.projectTemplate.id = id;
    this.projectTemplate.projectTemplateTypeId = projectTemplateTypeId;
    this.projectTemplate.dialogVisible = true;
  }

  function close() {
    this.isClearImage = !this.isClearImage;
    this.projectTemplate.id = '-1';
    this.projectTemplate.projectTemplateTypeId = '';
    this.projectTemplate.dialogVisible = false;
  }

  function save() {
    this.projectTemplate.id = '';
    this.projectTemplate.projectTemplateTypeId = '';
    this.getProjectTemplates();
    this.projectTemplate.dialogVisible = false;
  }

  async function del(id) {
    this.$confirm('这个操作将永久删除项目模板信息，您想继续吗？', '删除项目模板', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      const result = await _deleteProjectTemplate(id);
      if (result.code === 1200) {
        this.$message({
          type: 'success',
          message: '删除成功!',
        });
        this.getProjectTemplates();
      }
    }).catch(() => {
      this.$message({
        type: 'info',
        message: '执行失败！',
      });
    });
  }

  function click(id) {
    this.$router.push({path: 'projectTemplateMilestone', query: {id: id}});
  }


  async function enabled(id, flag) {
    this.$confirm('是否' + (!flag ? '开启' : '禁用') + '该项目模板？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    }).then(async () => {
      const result = await _enabledProjectTemplate({
        id: id,
        flag: flag,
      });
      if (result.code === 1200) {
        this.$message({
          type: 'success',
          message: '操作成功!',
        });
        this.getProjectTemplates();
      }
    }).catch(() => {
      this.$message({
        type: 'info',
        message: '执行失败！',
      });
    });

  }

  export default {
    name: 'ProjectTemplateList',
    components: {ProjectTemplateEdit, MainTopPart},
    data() {
      return {
        userId: this.$store.state.id,
        hasDel: false,
        hasEdit: false,
        hasEnabled: false,
        projectTemplateTypeId: '',
        searchName: '',
        projectTemplateDatas: [],
        projectTemplate: {
          id: '',
          projectTemplateTypeId: '',
          dialogVisible: false,
        },
        isClearImage: false,
      };
    },
    created() {
      const bottoms = JSON.parse(localStorage.getItem('BUTTONS'));
      this.hasDel = bottoms.filter(b => b === "PROJECT_TEMPLATE_TYPE_DEL").length > 0;
      this.hasEdit = bottoms.filter(b => b === "PROJECT_TEMPLATE_TYPE_EDIT").length > 0;
      this.hasEnabled = bottoms.filter(b => b === "PROJECT_TEMPLATE_TYPE_ENABLED").length > 0;
      this.projectTemplateTypeId = this.$route.query.id;
      this.getProjectTemplates();
    },
    methods: {
      getProjectTemplates,
      add,
      close,
      save,
      del,
      enabled,
      click
    },
    watch: {
      $route() {
        this.projectTemplateTypeId = this.$route.query.id;
        this.getProjectTemplates();
      },
    },
  };
</script>

<style scoped>
  .information {
    width: 100%;
    flex: 1;
    background-color: #fff;
    float: left;
    display: flex;
    flex-direction: column;
    position: relative;
    overflow: hidden;
  }
</style>
