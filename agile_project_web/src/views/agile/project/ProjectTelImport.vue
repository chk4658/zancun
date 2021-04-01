<template>
  <div>
    <el-row :gutter="20" style="width:100%">
      <el-col  :span="8" class="project-import-el-col">
        <div>
          <p align="center">项目模板类型</p>
          <div  v-for="(item,index) in projectTemplateTypes"
            :key="index"
            :label="item.name"
            @click="expose(item)"
            :class="[{'current':item.id === projectTemplateTypeId},'list']">
            <div style="float: left;position: relative;
                        display: flex;
                        align-items: center;
                        justify-content: space-between;
                        ">
              <span style="-webkit-user-select: none;
                -moz-user-select: none;
                -ms-user-select: none;
                user-select: none;
                cursor: pointer;
                ">{{item.name}}</span>
            </div>
          </div>
        </div>
      </el-col>
      <el-col style="border-right:1px #e1e1e1 solid;border-left:1px #e1e1e1 solid;"
      class="project-import-el-col"
         :span="8">
         <div>
          <p align="center">项目模板</p>
          <div  v-loading="projectTemplateLoading">
            <div  v-for="(item,index) in projectTemplates" 
              :key="index"
              :label="item.name"
              @click="radioChange(item)"
              :class="[{'current':item.id === projectTemplateId},'list']">
              <div style="float: left;position: relative;
                          display: flex;
                          align-items: center;
                          justify-content: space-between;
                          ">
                <span style="-webkit-user-select: none;
                  -moz-user-select: none;
                  -ms-user-select: none;
                  user-select: none;
                  cursor: pointer;
                  ">{{item.name}}</span>
              </div>
            </div>
          </div>
         
        </div>
      </el-col>
      <el-col
      class="project-import-el-col"
         :span="8">
          <p align="center">项目模板详情</p>
          <el-tree  style="padding:10px 0 10px 0" 
          :data="resultTree"
          :props="defaultProps"
            show-checkbox
            node-key="id"
            ref="tree"
            @check-change='handleCheckChange'
            v-loading="treeLoading"
          ></el-tree>
        
      </el-col>
    </el-row>
     <div style="margin-left: 50%;margin-top: 30px;">
      <el-button type="primary" @click="save()">提交 </el-button>
    </div>
  </div>
</template>

<script>

import { _queryProjectTemplateTypes } from '@/api/projectTempleTypeApi';

import { _templatesByTypeId,_TreeResultProjectTemplate } from '@/api/projectTempleApi';

async function getProjectTemplateTypes() {
  const result = await _queryProjectTemplateTypes({});
  if (result.code === 1200) {
    this.projectTemplateTypes = result.data.projectTemplateTypes;
  }
}

async function expose(val) {
  this.projectTemplateLoading = true;
  this.resultTree = [];
  this.projectTemplateId = '';
  this.projectTemplateTypeId = val.id;
  const result = await _templatesByTypeId({ projectTemplateTypeId: val.id });
  if (result.code === 1200) {
    this.projectTemplates = result.data.projectTemplates;
    this.projectTemplateLoading = false;
  }
}

function handleCheckChange(data, checked, indeterminate) {
  console.log(data, checked);
};

async function radioChange(val) {
  this.treeLoading = true;
  this.projectTemplateId = val.id;
  const result = await _TreeResultProjectTemplate({ projectTemplateId: val.id });
   if (result.code === 1200) {
    this.resultTree = result.data.treeResult;
    this.treeLoading = false;
  }
}

function setTreesName(tree) {
  tree.name+= ' [' + (tree.type === "PROJECT_TEMPLATE" ? '模板' : tree.type === "PROJECT_TEMPLATE_MILESTONE" ? '里程碑' : '任务') + ']';
  if (tree.children != null) {
    tree.children.forEach(c => {
      this.setTreesName(c);
    })
  }
}

async function save() {
  const nodes = [].concat(
                          this.$refs.tree.getHalfCheckedNodes(), 
                          this.$refs.tree.getCheckedNodes(),
                        );

  // const selectNodes = nodes.map(n => {
  //   return {
  //     id: n.id,
  //     type: n.type,
  //   };
  // });
  // this.$emit('getNodes',selectNodes);            

  // nodes.forEach(node => node.status = 1);
  // console.log(this.resultTree);
  // this.$emit('getNodes',this.resultTree);

  const templateNodes = nodes.filter(n => n.type === 'PROJECT_TEMPLATE');
  const milestoneNodes = nodes.filter(n => n.type === 'PROJECT_TEMPLATE_MILESTONE');
  const taskNodes = nodes.filter(n => n.type === 'PROJECT_TEMPLATE_TASK');
  const taskChildNodes = nodes.filter(n => n.type === 'PROJECT_TEMPLATE_TASK_CHILD');
  const data = JSON.parse(JSON.stringify(templateNodes));
  data.forEach(tl => {
    const _m = [];
    tl.children.forEach(m => {
      let mHas = false;
      milestoneNodes.forEach(__m => {
        if (__m.id === m.id) mHas = true;
      })
      if (mHas) _m.push(m);
      const _t = [];
      m.children.forEach(t => {
        let tHas = false;
        taskNodes.forEach(__t => {
          if (__t.id === t.id) tHas = true;
        })
        if (tHas) _t.push(t);
        const _tc = [];
        t.children.forEach(tc => {
          let tcHas = false;
          taskChildNodes.forEach(__tc => {
            if (__tc.id === tc.id) tcHas = true;
          });
          if (tcHas) _tc.push(tc);
        });
        t.children = _tc;
      });
      m.children = _t;
    });
    tl.children = _m;
  });
  console.log(data);
  this.$emit('getNodes',data);
}




export default {
  name: 'ProjectImport',
  data () {
    return {
      projectTemplateTypes: [],
      projectTemplateTypeId: '',
      projectTemplateId: '',
      projectTemplates: [],
      defaultProps: {
        children: 'children',
        label: 'name'
      },
      data: [],
      resultTree: [],
      projectTemplateLoading: false,
      treeLoading: false,
    }
  },
  computed: {
  },
  created: function () {
    this.getProjectTemplateTypes();
  },
  beforeMount: function () {},
  mounted: function () {},
  beforeDestroy: function () {},
  destroyed: function () {},
  methods: {
    getProjectTemplateTypes,
    handleCheckChange,
    expose,
    save,
    radioChange,
    setTreesName
  },
  watch: {},
  directives: {}
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss">

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

.current {
  background-color: #E5EBF3;
}

.project-import-el-col{
    height: 500px;
    background-color: #fff;
    overflow-y: auto;
    overflow-x: hidden;
    
    .el-radio-button__inner {
      border: 1px solid #DCDFE6;
    }

    
}



</style>
