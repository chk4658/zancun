<template>
  <div style="width: 100%;
              height: 100%;
              display: flex;">
    <div class="bar">

      <el-row :gutter="20" style="margin-top: 50px">
        <el-col :span="18">
          <span style="font-size: 20px;font-weight: bolder;margin-left: 15px;">项目模板类型</span>
        </el-col>
        <el-col :span="6">
          <i v-show="hasEdit" style="font-size: 25px;float: right;
          margin-right: 15px;color: #01408B;cursor: pointer" class="el-icon-circle-plus-outline"
             @click="add"></i>
        </el-col>
      </el-row>

      <el-row type="flex" justify="space-around" style="margin: 30px 15px">
        <el-input
          placeholder="请输入名称"
          v-model="searchName"
          size="middle"
          @input="searchBar">
          <i slot="prefix" class="el-input__icon el-icon-search"></i>
        </el-input>
      </el-row>

      <div class="block">
        <div
          v-for="(item,index) in listProjectTemplateTypesData"
          :key="index"
          :label="item.name"
          @mouseover="item.isHover=true"
          @mouseleave="item.isHover=false"
          @click="expose(item)"
          :class="[{'current':item.id === projectTemplateId},'list']">
          <div style="float: left;position: relative;
                      display: flex;
                      align-items: center;
                      justify-content: space-between;
                      ">
            <span
              v-if="!item.isEditing"
              style="-webkit-user-select: none;
                      -moz-user-select: none;
                      -ms-user-select: none;
                      user-select: none;
                      cursor: pointer;
                      ">{{item.name}}</span>

            <el-input type="text"
                      size="mini"
                      maxlength="15"
                      show-word-limit
                      v-model="item.name"
                      v-if="item.isEditing"
                      v-focus="item.isEditing"
                      @blur="update(item)"
                      @keyup.enter.native="$event.target.blur">

            </el-input>
          </div>
          <div style="float:left;margin-left: 20px">
            <i class="el-icon-edit"
               v-if="!item.isEditing && item.isHover && item.id !== 'all' && item.id !== 'enabled' && hasEdit"
               @click.stop="item.isEditing=true"
               style="
                -webkit-user-select: none;
                -moz-user-select: none;
                -ms-user-select: none;
                user-select: none;
                cursor: pointer;
                color: #01408B;"></i>
          </div>
          <div style="float: right">
            <i class="el-icon-delete" v-if="item.id !== 'all' && item.id !== 'enabled' && hasDel"
               @click.stop="del(item)"
               style="
                -webkit-user-select: none;
                -moz-user-select: none;
                -ms-user-select: none;
                user-select: none;
                cursor: pointer;
                color: #01408B;"></i>
          </div>
        </div>
      </div>
    </div>

    <router-view></router-view>

  </div>

</template>

<script>

import {
  _queryProjectTemplateTypes,
  _updateProjectTemplateType,
  _deleteProjectTemplateType,
} from '@/api/projectTempleTypeApi';


async function getProjectTemplateTypes() {
  this.listProjectTemplateTypesData = [
    {
      id: 'all',
      name: '全部',
      isCurrent: 'current',
      isHover: true,
      isEditing: false,
      enabled: 0,
    }, {
      id: 'enabled',
      name: '已禁用模板',
      isCurrent: '',
      isHover: false,
      isEditing: false,
      enabled: 1,
    }
  ];
  const result = await _queryProjectTemplateTypes({ name: this.searchName });
  if (result.code === 1200) {
    result.data.projectTemplateTypes.forEach((r) => {
      this.listProjectTemplateTypesData.splice(this.listProjectTemplateTypesData.length - 1, 0, {
        id: r.id,
        name: r.name,
        isCurrent: '',
        isHover: false,
        isEditing: false,
        enabled: 0,
      });
    });
    const queryId = this.projectTemplateId === undefined ? 'all' : this.projectTemplateId;
    this.$router.push({ path: 'project-template', query: { id: queryId } });
  }
}

/**
 * 搜索
 */
function searchBar() {
  this.getProjectTemplateTypes();
}

/**
 * 添加
 */
function add() {
  this.listProjectTemplateTypesData.splice(this.listProjectTemplateTypesData.length - 1, 0, {
    id: '',
    name: '',
    isCurrent: '',
    isHover: true,
    isEditing: true,
  });
}

/**
 * 更新
 */
async function update(val) {
  if (val.name.trim() === '' ) {

  } else {
    const result = await _updateProjectTemplateType(val);
    if (result.code === 1200) {
      this.$message({
        message: '模板类型更新成功',
        type: 'success',
        center: true,
      });
    }
  }
  this.getProjectTemplateTypes();
}

/**
 * 删除
 */
function del(val) {
  this.$confirm('这个操作将永久删除项目模板类型信息，您想继续吗？', '删除项目模板类型信息', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(async () => {
    if (val.id === '') {
      var i = this.listProjectTemplateTypesData.findIndex((value, index, arr) => {
        return value.name === val.name;
      });
      this.listProjectTemplateTypesData.splice(i, 1);
    } else {
      const result = await _deleteProjectTemplateType(val.id);
      if (result.code === 1200) {
        this.$message({
          type: 'success',
          message: '删除成功!',
        });
        this.getProjectTemplateTypes();
        const id = this.$route.query.id;
        if (id === val.id) {
         this.$router.push({ path: 'project-template', query: { id: 'all' } });
        }

      }
    }
  }).catch(() => {
    this.$message({
      type: 'info',
      message: '执行失败！',
    });
  });
}

function expose(item) {
  this.$router.push({ path: 'project-template', query: { id: item.id } });
}


export default {

  data() {
    return {
      listProjectTemplateTypesData: [],
      searchName: '',
      projectTemplateId: '',
      userId: this.$store.state.id,
      hasDel: false,
      hasEdit: false,
    };
  },
  computed: {
  },
  created() {
    this.projectTemplateId = this.$route.query.id;
    const bottoms = JSON.parse(localStorage.getItem('BUTTONS'));
    this.hasDel = bottoms.filter(b => b === "PROJECT_TEMPLATE_TYPE_DEL").length > 0;
    this.hasEdit = bottoms.filter(b => b === "PROJECT_TEMPLATE_TYPE_EDIT").length > 0;
    this.getProjectTemplateTypes('');
    
  },
  methods: {
    getProjectTemplateTypes,
    searchBar,
    add,
    update,
    del,
    expose,
  },
  watch: {
    $route() {
      this.projectTemplateId = this.$route.query.id;
    },
  },
  directives: {},
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>
  .list {
    height: 32px;
    line-height: 32px;
    margin: 0 15px;
    margin-bottom: 3px;
    font-size: 16px;
    border-radius: 3px;
    cursor: pointer;
    padding: 5px 8px;
  }

  .list:hover {
    background-color: #EDEDED;
  }

  .el-input--mini .el-input__inner {
    height: 23px;
  }

  .current {
    background-color: #E5EBF3;
  }

  .bar {

    width: 400px;
    min-width: 400px;
    height: 100%;
    background-color: #fff;
    float: left;
    border-right: 1px #e1e1e1 solid;
    overflow-y: auto;
    overflow-x: hidden;
  }



</style>
