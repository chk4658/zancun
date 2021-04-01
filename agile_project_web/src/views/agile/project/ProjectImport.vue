<template>
  <div>
    <el-row :gutter="20" style="width:100%">
      <el-col :span="10">
        <div>
          <p align="center">项目</p>
          <el-input style="margin-bottom:10px"
                    placeholder="搜索"
                    @input="getProjects"
                    v-model="searchName">
            <i slot="prefix" class="el-input__icon el-icon-search"></i>
          </el-input>
          <div class="project-import-el-col">
            <div v-for="(item,index) in projects"
                 :key="index"
                 :label="item.name + ( + '(' + item.status + ')')"
                 @click="expose(item)"
                 :class="[{'current':item.id === projectId},'list']">
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
      <el-col style="border-right:1px #e1e1e1 solid;border-left:1px #e1e1e1 solid;"
              :span="14">
        <p align="center">项目详情</p>
        <div class="project-import-el-col">
          <el-tree
            :data="resultTree"
            :props="defaultProps"
            show-checkbox
            node-key="id"
            ref="tree"
            @check-change='handleCheckChange'
            v-loading="treeLoading"
          ></el-tree>
        </div>

      </el-col>
    </el-row>
    <div style="margin-left: 50%;margin-top: 30px;">
      <el-button type="primary" @click="save()">提交</el-button>
    </div>
  </div>
</template>

<script>

  import {_queryAllProjectList, _treeResultProject} from '@/api/projectApi';

  async function getProjects() {
    this.resultTree = [];
    const result = await _queryAllProjectList({searchName: this.searchName});
    if (result.code === 1200) {
      this.projects = result.data.allVisibleProject;
    }
  }

  async function expose(val) {
    this.treeLoading = true;
    this.resultTree = [];
    this.projectId = val.id;
    const result = await _treeResultProject({projectId: val.id});
    if (result.code === 1200) {
      const resultTree = [result.data.treeResult];
      resultTree.forEach(tree => this.setTreesName(tree));
      this.resultTree = resultTree;
      this.treeLoading = false;
    }
  }

  function handleCheckChange(data, checked, indeterminate) {
    console.log(data, checked);
  };

  function setTreesName(tree) {
    tree.name += ' [' + (tree.type === "PROJECT" ? '项目' : tree.type === "PROJECT_MILESTONE" ? '里程碑' : '任务') + ']'
    if (tree.type !== 'PROJECT') {
      tree.name += ('(' + (tree.status === '1' ? '启用' : (tree.forbiddenStatus === '0' ? '未禁用' : '禁用')) + ')');
    }
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


    // nodes.forEach(node => node.status = 1);
    // console.log(this.resultTree);
    // this.$emit('getNodes',this.resultTree);

    const projectNodes = nodes.filter(n => n.type === 'PROJECT');
    const milestoneNodes = nodes.filter(n => n.type === 'PROJECT_MILESTONE');
    const taskNodes = nodes.filter(n => n.type === 'PROJECT_TASK');
    const taskChildNodes = nodes.filter(n => n.type === 'PROJECT_TASK_CHILD');

    const data = JSON.parse(JSON.stringify(projectNodes));
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
    this.$emit('getNodes', data);
  }


  export default {
    name: 'ProjectImport',
    data() {
      return {
        projects: [],
        projectId: '',
        treeResult: [],
        defaultProps: {
          children: 'children',
          label: 'name'
        },
        data: [],
        resultTree: [],
        treeLoading: false,
        searchName: '',
      }
    },
    computed: {},
    created: function () {
      this.getProjects();
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
      getProjects,
      handleCheckChange,
      expose,
      save,
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

  .project-import-el-col {
    height: 500px;
    background-color: #fff;
    overflow-y: auto;
    overflow-x: hidden;


    .el-radio-button__inner {
      border: 1px solid #DCDFE6;
    }


  }


</style>
