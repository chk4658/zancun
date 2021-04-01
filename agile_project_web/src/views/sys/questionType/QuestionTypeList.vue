<template>
  <div class="body" style="overflow-y: auto">
    <main-top-part>
      <template v-slot:title>
        <div style="display: flex">
          <i :class="userIcon" class="menu-icon" style="color: #01408B;font-size: 32px;">
          </i>问题类型管理
        </div>
      </template>
      <!--      <template v-slot:search>-->
      <!--        <el-input-->
      <!--          size="small"-->
      <!--          placeholder="搜索"-->
      <!--          @input="search"-->
      <!--          style="width: 328px;"-->
      <!--          v-model="searchQuestionTypeValue">-->
      <!--          <i slot="prefix" class="el-input__icon el-icon-search"></i>-->
      <!--        </el-input>-->
      <!--      </template>-->
    </main-top-part>

    <el-row style="margin: 30px 20px 20px;">
      <el-col :span="24">
        <el-table
          class="own-table"
          :data="tableData"
          style="width: 100%;"
          row-key="id"
          :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
          <el-table-column
            prop="name"
            label="问题类型名称">
          </el-table-column>

          <el-table-column
            prop="createAt"
            label="创建时间">
          </el-table-column>
          <el-table-column
            align="right">
            <template slot="header">
              <i class="el-icon-circle-plus"
                 style="font-size: 20px;position: relative;top: 2px;cursor: pointer;"
                 v-has="'QUESTION_TYPE_ADD'"
                 @click="addQuestionType"></i>

            </template>
            <template slot-scope="scope">
              <div class="table-operation-wrap">
                <el-button type="text"
                           v-has="'QUESTION_TYPE_EDIT'"
                           @click="editQuestionType(scope.row.id)">编辑
                </el-button>
                <el-button type="text"
                           v-has="'QUESTION_TYPE_DEL'"
                           @click="deleteQuestionType(scope.row.id)">删除
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-col>

    </el-row>
    <el-dialog
      @close="closeForm"
      :visible.sync="form.visible"
      :modal-append-to-body=false
      :title="form.title">
      <question-type-edit
        v-if="form.visible"
        :question-type-id="form.data.id"
        :on-cancel-handler="closeForm"
        :on-submit-handler="closeForm"></question-type-edit>

    </el-dialog>

  </div>
</template>

<script>
  import {
    _listQuestionType,
    _deleteById
  } from '@/api/sysQuestionType.js';

  import MainTopPart from '@/components/MainTopPart.vue';
  import QuestionTypeEdit from "./QuestionTypeEdit";

  async function listQuestionType() {

    const result = await _listQuestionType();
    if (result.code === 1200) {
      this.tableData = result.data.questionTypeList;
    }

  }


  function closeForm() {
    this.form.data.id = null;
    this.form.visible = false;
    this.listQuestionType();
  }

  function editQuestionType(questionTypeId) {
    this.form.data.id = questionTypeId;
    this.form.visible = true;
    this.form.title = '更新问题类别';
  }

  function addQuestionType() {
    this.form.data.id = null;
    this.form.title = '新增问题类别';
    this.form.visible = true;
  }

  async function deleteQuestionType(questionTypeId) {
    this.$confirm('是否删除?', '问题类别删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(async () => {
        const result = await _deleteById(questionTypeId);
        if (result.code === 1200) {
          this.$message({
            type: 'success',
            message: '删除成功!',
            center: true,
          });
          this.listQuestionType();
        }
      });
  }


  // async function searchQuestionType(a) {
  //
  // }

  export default {
    name: 'QuestionTypeList',
    data() {
      return {
        tableData: [],
        form: {
          data: {
            id: null,
          },
          visible: false,
          title: '',
        },
        searchQuestionTypeValue: '',
        userIcon: '',
      };
    },
    components: {
      QuestionTypeEdit,
      MainTopPart,
    },
    methods: {
      // search() {
      //   this.searchQuestionType(this.searchQuestionTypeValue);
      // },
      //
      // searchQuestionType,
      listQuestionType,
      closeForm,
      editQuestionType,
      addQuestionType,
      deleteQuestionType
    },
    created() {
      this.listQuestionType();
      const userQuestionTypes = JSON.parse(localStorage.getItem('MENUS'))
        .filter(menu => menu.code === 'QUESTION_TYPE');
      this.userIcon = userQuestionTypes.length === 0 ? 'fa fa-bars' : userQuestionTypes[0].icon;
    },
  };
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->

<style>
  .menu_menubar .department_title span {
    float: right;
  }

  div .cell {
    height: 23px;
  }

  .el-select-dropdown__wrap {
    margin-bottom: -3px !important;
  }
</style>
<style lang="scss">
  .own-table > {

    .el-table__header-wrapper {

      .el-table__header {

        .has-gutter {
          th {
            background-color: #eee;
          }
        }
      }
    }
  }
</style>
