<template>
  <div style="margin: 0 0 10px 20px;" id="project-open-scrolltop">
    <div>
      <el-row :gutter="10" style="margin-right: 0">
        <el-col :span="16">
          <table class="report-table" cellspacing="0" cellpadding="0" style="400px">
            <tr>
              <th>绿色交付物 Green<i class="report-circle report-green" style="float:none;"></i></th>
              <th>黄色交付物 Yellow<i class="report-circle report-yellow" style="float:none;"></i></th>
              <th>红色交付物 Red<i class="report-circle report-red" style="float:none;"></i></th>
              <th>总计 Total</th>
            </tr>
            <tr>
              <td>{{greenTotal}}项目</td>
              <td>{{yellowTotal}}项目</td>
              <td>{{redTotal}}项目</td>
              <td>{{greenTotal + yellowTotal + redTotal}}项目</td>
            </tr>
          </table>
        </el-col>
        <el-col :span="6">
          <el-select v-model="projectMilestone" @change='getDatas'>
            <el-option
              v-for="item in projectMilestones"
              :key="item"
              :label="item"
              :value="item">
            </el-option>
          </el-select>
        </el-col>
        <el-col :span="2">
          <el-button type="text" icon="el-icon-download"
                     @click="tableToExcel">导出报表
          </el-button>
        </el-col>
      </el-row>
    </div>
    <div style="margin-top:20px;">
      <el-row>
        <el-col :span="24">
          <table style="
            border: 1px solid #9D9D9D;border-bottom-style:none;width: 98%;
            text-align: center;
            max-height: 500px;
            table-layout:fixed;
          "
                 cellspacing="0" cellpadding="0">
            <tr>
              <template v-for="(item,index) in tableColumns">
                <th
                  :style="
                   'height: 35px;'
                   + (
                     index > 0 ? 'border-left: 1px solid #9D9D9D;' : ''
                   ) + 'border-bottom: 1px solid #9D9D9D;'
                   " :key="index">{{item.label}}
                </th>
              </template>
            </tr>
            <template v-for="(data,i) in tableData">
              <tr :key="i">
                <template v-for="(colum,j) in tableColumns">
                  <td
                    v-if=" (colum.prop !== 'condition' && colum.prop !== 'total')
                            ||  data['condition'] !== null"
                    :rowspan="colum.prop === 'condition' || colum.prop === 'total'
                      ? data.rowspan
                      : 1"
                    :style="
                    'width:' + data.width +'px;word-wrap:break-word;'
                    + 'height: 20px;'
                    + (
                      j > 0 ? 'border-left: 1px solid #9D9D9D;' : ''
                    )
                    + ('border-bottom: 1px solid #9D9D9D;')
                    "
                    :key="i + j">

                    {{
                    colum.prop === 'condition' || colum.prop === 'taskName'
                    ? data[colum.prop]
                    : ''
                    }}
                    <i v-if="colum.prop !== 'condition' && colum.prop !== 'taskName' && data[colum.prop] === '6'"
                       class="report-circle report-green" style="float:none;" @click="go(data,colum)"></i>
                    <i v-if="colum.prop !== 'condition' && colum.prop !== 'taskName' && data[colum.prop] === '5'"
                       class="report-circle report-yellow" style="float:none;" @click="go(data,colum)"></i>
                    <i v-if="colum.prop !== 'condition' && colum.prop !== 'taskName' && data[colum.prop] === '4'"
                       class="report-circle report-red" style="float:none;" @click="go(data,colum)"></i>
                    <span v-if="colum.prop !== 'condition' && colum.prop !== 'taskName' && data[colum.prop] === '-1'">N/A</span>
                    <span
                      v-if="colum.prop !== 'condition'
                      && colum.prop !== 'taskName'
                      && data[colum.prop] !== '6'
                      && data[colum.prop] !== '5'
                      && data[colum.prop] !== '4'
                      && data[colum.prop] !== '-1'
                      ">/</span>
                  </td>
                </template>
              </tr>
            </template>

          </table>
        </el-col>
      </el-row>
    </div>


    <el-dialog
      @close="taskDialogVisible = false"
      :visible.sync="taskDialogVisible"
      width="580px"
      center
      append-to-body>

      <div slot="title" class="header-title">
        <span>{{taskEditNeed.proName}}</span>
      </div>
      <task-edit-read-only :task="taskEditNeed.taskInformation"
                           :taskAttr="taskEditNeed.cols"
                           :rolePlus="taskEditNeed.rolePlus"></task-edit-read-only>
      <!--      <task-edit :task="taskEditNeed.taskInformation"-->
      <!--                 :taskAttr="taskEditNeed.cols"-->
      <!--                 @save="taskDialogVisible=false"-->
      <!--                 @update="getPlatformOpenList"-->
      <!--                 :need="false"-->
      <!--                 :reBeginAuthority="taskEditNeed.reBeginAuthority"-->
      <!--                 :authority="taskEditNeed.authority"-->
      <!--                 :rolePlus="taskEditNeed.rolePlus"></task-edit>-->


      <!--      <div slot="title" class="header-title">-->
      <!--        <span>{{task.projectName}}<TaskStatus></TaskStatus></span>-->
      <!--      </div>-->
      <!--      <el-form ref="ruleForm" :model="task"  label-position="right" label-width="150px" style="width:580px">-->
      <!--        <el-form-item>-->
      <!--          <span slot="label">任务名称</span>-->
      <!--          <el-input v-model="task.name"-->
      <!--                    :readonly="true"-->
      <!--                    style="width: 300px"-->
      <!--                    maxlength="35"></el-input>-->
      <!--        </el-form-item>-->
      <!--     <el-form-item label="状态">-->
      <!--        <div style="width: 300px; text-align: center">-->
      <!--          <task-status :dictCode="'TASK_STATUS'" v-model="task.status"-->
      <!--                       position="bottom" width="150" :item="task" :isShow="true"-->
      <!--                       :hasArrow="true"></task-status>-->
      <!--        </div>-->
      <!--      </el-form-item>-->

      <!--      <el-form-item label="优先级">-->
      <!--        <div style="width: 300px; text-align: center">-->
      <!--          <task-status :dictCode="'PRIORITY'" v-model="task.priority"-->
      <!--                       position="bottom" width="150" :item="task" :isShow="true"-->
      <!--                       :hasArrow="true"></task-status>-->
      <!--        </div>-->
      <!--      </el-form-item>-->

      <!--      <el-form-item label="负责人">-->
      <!--        <el-tag-->
      <!--          type="info"-->
      <!--          size="medium"-->
      <!--          style="width: 200px;text-align: center;cursor: pointer"-->
      <!--          disable-transitions>-->
      <!--          {{task.reviewProjectRoleId===null?'-':task.reviewProjectRole.roleName}}-->
      <!--        </el-tag>-->
      <!--        <el-tag-->
      <!--          type="success"-->
      <!--          size="medium"-->
      <!--          style="margin-left: 5px;width: 95px;text-align: center;cursor: pointer"-->
      <!--          disable-transitions> {{task.reviewUser===null?'-':task.reviewUser.realName}}-->
      <!--        </el-tag>-->
      <!--      </el-form-item>-->

      <!--      <el-form-item label="审核人">-->
      <!--        <el-tag-->
      <!--          type="info"-->
      <!--          size="medium"-->
      <!--          style="width: 200px;text-align: center;cursor: pointer"-->
      <!--          disable-transitions>-->
      <!--           {{task.confirmProjectRoleId===null?'-':task.confirmProjectRole.roleName}}-->
      <!--        </el-tag>-->
      <!--        <el-tag-->
      <!--          type="success"-->
      <!--          size="medium"-->
      <!--          style="margin-left: 5px;width: 95px;text-align: center;cursor: pointer"-->
      <!--          disable-transitions>{{task.confirmUser===null?'-':task.confirmUser.realName}}-->
      <!--        </el-tag>-->
      <!--      </el-form-item>-->
      <!--       <el-form-item label="截止时间">-->
      <!--        <div style="background-color:#F2F4F5;color: #626365;text-align: center;width: 300px;height: 30px;line-height: 30px">-->
      <!--          {{task.estEndTime}}-->
      <!--        </div>-->
      <!--      </el-form-item>-->

      <!--      <el-form-item label="完成时间">-->
      <!--        <div style="background-color:#F2F4F5;color: #626365;text-align: center;width: 300px;height: 30px;line-height: 30px">-->
      <!--          {{task.actEndTime}}-->
      <!--        </div>-->
      <!--      </el-form-item>-->


      <!--      <el-form-item>-->
      <!--        <span slot="label">开阀条件</span>-->
      <!--        <el-input v-model="task.openConditions"-->
      <!--                  maxlength="50"-->
      <!--                  style="width: 300px;cursor: pointer"-->
      <!--                  type="textarea"-->
      <!--                  :readonly="true"-->
      <!--                  :rows="2"-->
      <!--                  ></el-input>-->
      <!--      </el-form-item>-->
      <!--      <el-form-item>-->
      <!--        <span slot="label">开阀描述</span>-->
      <!--        <el-input v-model="task.openDescription"-->
      <!--                  maxlength="50"-->
      <!--                  type="textarea"-->
      <!--                  :readonly="true"-->
      <!--                  :rows="2"-->
      <!--                  style="width: 300px;cursor: pointer"-->
      <!--                  ></el-input>-->
      <!--      </el-form-item>-->

      <!--      <el-form-item>-->

      <!--        <span slot="label">交付要求</span>-->
      <!--        <div style="cursor: pointer;text-align: center;width: 300px;border: 1px #e1e1e1 dashed;"-->
      <!--             class="o-hover">-->
      <!--          <span v-if="task.isRequirement!==1">O</span>-->
      <!--          <i class="el-icon-check" v-if="task.isRequirement===1"></i>-->
      <!--        </div>-->

      <!--      </el-form-item>-->

      <!--      <el-form-item v-if="task.isRequirement===1">-->
      <!--        <span slot="label">交付物</span>-->
      <!--        <div style="cursor: pointer;text-align: center;width: 300px;border: 1px #e1e1e1 dashed;"-->
      <!--             class="o-hover">-->
      <!--          <span>提交/审核交付物...</span>-->
      <!--        </div>-->
      <!--      </el-form-item>-->
      <!--      </el-form>-->
    </el-dialog>


  </div>
</template>

<script>


  import {
    _queryPlatformOpenListByProjectId
  } from '@/api/platformOpen';

  import {
    _getTaskByProjectIdAndTaskName
  } from '@/api/taskApi';


  // import TaskEdit from '../task/kanban/TaskEdit';
  import TaskStatus from '@/components/TaskStatus';
  import TaskEdit from "../task/kanban/TaskEdit";
  import TaskEditReadOnly from "./taskEditReadOnly";
  import {_getTreeListByProjectId} from "../../../../api/taskApi";


  /**
   * 获取数据
   */
  async function getPlatformOpenList() {
    this.taskDialogVisible = false;
    const projectIds = JSON.parse(JSON.stringify(this.projectIds)).join(',');
    const result = await _queryPlatformOpenListByProjectId({projectIds: projectIds});
    if (result.code === 1200) {
      console.log(this.projects)
      this.projects = result.data.projects;
      this.templateTasks = result.data.templateTasks;
      this.projectMilestone = '';
      this.getTableColumns();
      this.getProjectMilestones();
      this.getDatas();
    }
  }

  function getTableColumns() {
    const datas = this.projects;
    const tableColumns = [{
      prop: 'condition',
      label: '开阀条件',
      width: 250,
    }, {
      prop: 'taskName',
      label: '评审内容',
      width: 250,
    }];
    datas.forEach((data, index) => {
      tableColumns.push({
        prop: 'project_' + data.id,
        label: data.name,
        width: 250,
      });
    });
    tableColumns.push({
      prop: 'total',
      label: '',
      width: 50,
    });
    this.tableColumns = tableColumns;
  }

  /**
   * 里程碑下拉列表
   */
  function getProjectMilestones() {
    const datas = this.projects;
    const set = new Set();
    datas.forEach(data => {
      if (data.projectMilestones !== null) {
        data.projectMilestones.forEach(milestone => {
          set.add(milestone.name)
        })
      }
    });
    this.projectMilestones = Array.from(set);
    if (this.projectMilestones.length > 0) this.projectMilestone = this.projectMilestones[0];
  }


  function getDatas() {
    const milestone = this.projectMilestone;
    const _datas = JSON.parse(JSON.stringify(this.projects));
    if (milestone !== '') {
      this.projects.forEach((data, i) => {
        if (data.projectMilestones !== null) {
          _datas[i].projectMilestones = [];
          var is = data.projectMilestones.map((item, index) => {
            if (item.name !== milestone) return index; else return -1
          });
          is.forEach((j, n) => {
            if (j === -1) {
              _datas[i].projectMilestones.push(data.projectMilestones[n]);
            }
          });
        }
      });
    }
    // 报表
    this.getTableDatas(_datas, milestone === '' ? this.templateTasks : []);
    // 汇总
    this.getTotalDatas();
  }

  /**
   * 组装列表
   */
  function getTableDatas(datas, tasks) {
    const taskDatas = [];
    // 扁平化
    datas.forEach((data, index) => {
      if (data.projectMilestones !== null) {
        data.projectMilestones.forEach(milestone => {
          if (milestone.taskList !== null) {
            milestone.taskList.forEach(task => {
              const taskData = {
                taskId: task.id,
                condition: task.openConditions,
                taskName: task.name,
                status: task.forbidden === 1 || task.enabled === 0 ? null : task.status,
                forbidden: task.forbidden === 1 || task.enabled === 0 ? 1 : 0,
                project: data,
                projectId: data.id,
              };
              taskDatas.push(taskData);
            })
          }
        })
      }
    });
    tasks.forEach(task => {
      this.projects.forEach(project => {
        const taskData = {
          taskId: task.id,
          condition: task.openConditions,
          taskName: task.name,
          status: null,
          forbidden: 1,
          project: project,
          projectId: project.id,
        };
        taskDatas.push(taskData);
      })

    })


    // 里程碑分组
    let sorteds = this.groupBy(taskDatas, 'condition');
    const tableDatas = [];

    sorteds.forEach(sorted => {
      let n = 0;
      // 任务名分组
      let _sorteds = this.groupBy(sorted, 'taskName');
      _sorteds.forEach(__soreds => {
        const tableData = {
          condition: n === 0 ? (
            __soreds[0].condition === null ? '' : __soreds[0].condition
          ) : null,
          taskName: __soreds[0].taskName,
        };
        // 项目分组
        let ___sorteds = this.groupBy(__soreds, 'projectId');
        ___sorteds.forEach(____sorteds => {
          if (____sorteds[0].forbidden === null || ____sorteds[0].forbidden !== 0) tableData['project_' + ____sorteds[0].projectId] = '-1';
          else {
            const red = ____sorteds.filter(r => r.status !== null && r.status !== '6' && r.status !== '5').length > 0 ? 4 : -1;
            const yellow = ____sorteds.filter(y => y.status !== null && y.status === '5').length > 0 ? 5 : -1;
            const green = ____sorteds.filter(g => g.status !== null && g.status === '6').length > 0 ? 6 : -1;
            tableData['project_' + ____sorteds[0].projectId] = red > 0 ? red.toString()
              : (yellow > 0 ? yellow.toString()
                : (green > 0 ? green.toString() : ''));
            tableData['projectInfo_' + ____sorteds[0].projectId] = ____sorteds[0].project;
            tableData['taskId_' + ____sorteds[0].projectId] = ____sorteds[0].taskId;
          }
        });
        const red = sorted.filter(r => r.status !== null && r.status !== '6' && r.status !== '5').length > 0 ? 4 : -1;
        const yellow = sorted.filter(y => y.status !== null && y.status === '5').length > 0 ? 5 : -1;
        const green = sorted.filter(g => g.status !== null && g.status === '6').length > 0 ? 6 : -1;
        tableData['total'] = red > 0 ? red.toString()
          : (yellow > 0 ? yellow.toString()
            : (green > 0 ? green.toString() : ''));
        // 按评审内容合并
        tableData['rowspan'] = _sorteds.length;
        tableDatas.push(tableData);
        n++;
      });
    });
    console.log(tableDatas);
    this.tableData = tableDatas;
  }

  /**
   * 状态统计
   */
  function getTotalDatas() {
    const datas = this.tableData.filter(d => d.condition !== null);
    let red = datas.filter(r => r.total !== null && r.total === '4').length;
    let yellow = datas.filter(y => y.total !== null && y.total === '5').length;
    let green = datas.filter(g => g.total !== null && g.total === '6').length;
    this.greenTotal = green;
    this.yellowTotal = yellow;
    this.redTotal = red;
  }

  /**
   * 分组
   */
  function groupBy(array, id) {
    let groups = {};
    array.forEach(function (o) {
      let group = JSON.stringify(o[id]);
      groups[group] = groups[group] || [];
      groups[group].push(o);
    });
    return Object.values(groups);
  }

  function tableToExcel() {
    const tableData = JSON.parse(JSON.stringify(this.tableData));
    const tableColumns = JSON.parse(JSON.stringify(this.tableColumns));
    //列标题
    const tHeader = tableColumns.map(c => c.prop === 'total' ? '总计' : c.label);
    const tDatas = [];
    let condition = '';
    for (let i = 0; i < tableData.length; i++) {
      const td = tableData[i];
      condition = td.condition != null ? td.condition : condition;
      td.condition = condition;
      const tData = [];
      for (let j = 0; j < tableColumns.length; j++) {
        const _td = td[tableColumns[j].prop] === undefined ? '' : td[tableColumns[j].prop];
        let __td = '';
        if (tableColumns[j].prop === 'condition' || tableColumns[j].prop === 'taskName') {
          __td = _td;
        } else {
          switch (_td) {
            case '4':
              __td = '未完成'
              break;
            case '5':
              __td = '待条件通过'
              break;
            case '6':
              __td = '已完成'
              break;
            case '-1':
              __td = 'N/A'
              break;
            default:
              __td = '/';
              break;
          }
        }
        tData.push(__td);
      }
      tDatas.push(tData);
    }
    require.ensure([], () => {
      const {exportJsonToExcel} = require('@/vendor/Export2Excel');
      exportJsonToExcel(tHeader, tDatas, '平台开阀');
    })
  }


  async function go(data, col) {
    if (col.prop === "total") return;
    let projectId = "";
    // const taskName = data.taskName;
    this.taskEditNeed.proName = col.label;
    Object.keys(data).forEach(key => {
      if (key.indexOf("project_") !== -1) projectId = col.prop.substring(key.indexOf("_") + 1);
    });


    if (projectId === "") return;

    const taskId = data['taskId_' + projectId];
    // const projectInfo = data['projectInfo_' + projectId];

    // 权限部分，1.27版本要求只读
    // const reBeginArr = [projectInfo.circle.ownerUid, projectInfo.projectUserId];
    // this.taskEditNeed.reBeginAuthority = reBeginArr.indexOf(this.userId) === -1 ? false : true;
    // const authorityUser = [projectInfo.circle.ownerUid, projectInfo.projectUserId, projectInfo.createUserId];
    // this.taskEditNeed.authority = authorityUser.indexOf(this.userId) === -1 ? false : true;
    this.taskEditNeed.reBeginAuthority = false;
    this.taskEditNeed.authority = false


    const result = await _getTreeListByProjectId({projectId});
    // taskList    projectAttrs
    const cols = [];
    const projectAttrs = result.data.projectAttrs;
    const taskList = result.data.taskList;
    if (projectAttrs !== null && projectAttrs.length > 0) {
      projectAttrs.forEach(item => {
        cols.push({
          projectAttrId: item.id,
          label: item.name,
          type: item.type,
          isEditCols: false,
        });
      });
    }

    taskList.forEach(task => {
      task.isEdit = {
        name: false,
        openConditions: false,
        openDescription: false,
        type: false,
      };
      task.checked = false;

      task.confirmRoleName = task.confirmProjectRole === null ? '' : task.confirmProjectRole.roleName;
      task.reviewRoleName = task.reviewProjectRole === null ? '' : task.reviewProjectRole.roleName;

      task.type = task.type === null ? [] : task.type.split(',');

      if (task.type.length > 0) {
        const arrName = [];
        task.type.forEach(i => {
          arrName.push(this.types.filter(type => type.code === i)[0].name);
        })

        this.$set(task, 'typeName', arrName.join('-'));
      } else {
        this.$set(task, 'typeName', '-');
      }

      if (projectAttrs !== null && projectAttrs.length > 0) {
        projectAttrs.forEach(attr => {
          if (task.taskDataList.filter(taskData => taskData.projectAttrId === attr.id).length === 0) {
            const taskData = {
              attrType: attr.type,
              projectAttrId: attr.id,
              taskId: task.id,
              value: '',
            };
            task.taskDataList.push(taskData);
          }
          Array.from(task.taskDataList, x => x.isEditing = false);
        });
      }

      this.$set(task, 'childTask', []);
      task.childTask = taskList.filter(ch => ch.parentId !== null && ch.parentId === task.id).sort((a, b) => {
        return a.sort < b.sort ? -1 : 1;
      });
    });
    this.taskEditNeed.cols = cols;
    const taskInfo = taskList.filter(at => at.id === taskId)[0];
    console.log(taskInfo)

    this.taskEditNeed.taskInformation = JSON.parse(JSON.stringify(taskInfo))
    this.taskDialogVisible = true;

  }

  function editTask(item) {

    this.taskEditNeed.taskInformation = item
    this.taskDialogVisible = true;
  }


  export default {
    name: 'ProjectReport',
    props: {
      projectIds: Array
    },
    components: {
      TaskEditReadOnly,
      TaskEdit,
      TaskStatus,
    },
    data() {
      return {
        projects: [],
        templateTasks: [],
        projectMilestones: [],
        projectMilestone: '',
        tableColumns: [],
        tableData: [],
        greenTotal: 0,
        yellowTotal: 0,
        redTotal: 0,
        taskDialogVisible: false,
        types: [],
        userId: localStorage.getItem('id'),
        taskEditNeed: {
          cols: [],
          taskInformation: {},
          reBeginAuthority: false,
          authority: false,
          rolePlus: [],
          proName: ''
        }
        // task: {
        //   projectName: '',
        //   name: '',
        //   status: '',
        //   priority: '',
        //   reviewProjectRoleId: null,
        //   reviewProjectRole: {},
        //   reviewUser: null,
        //   confirmProjectRoleId: null,
        //   confirmProjectRole: {},
        //   confirmUser: null,
        // },
      }
    },
    computed: {},
    created: function () {

      const dictDataType = this.$store.getters.getDictionaryByKey('TASK_TYPE');
      this.types = dictDataType.sysDictDataList;

      this.getPlatformOpenList();
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
      getPlatformOpenList,
      getProjectMilestones,
      getTableColumns,
      getDatas,
      getTotalDatas,
      getTableDatas,
      groupBy,

      tableToExcel,
      go,
      editTask

    },
    watch: {
      projectIds(val) {
        this.getPlatformOpenList();
      }
    },
    computed: {},
    directives: {}
  }
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss" scoped>

  .report-table {
    border: 1px solid #4F4F4F;
    width: 98%;

    th:nth-child(n+2), td:nth-child(n+2) {
      border-left: 1px solid #9D9D9D;
    }

    th {
      border-bottom: 1px solid #9D9D9D;
    }

    td {
      text-align: center;
    }
  }

  .report-circle {
    width: 10px;
    height: 10px;
    display: inline-block;
    border-radius: 10px;
    cursor: pointer;
  }

  .report-green {
    background-color: #52c41a;
  }

  .report-yellow {
    background-color: yellow;
  }

  .report-red {
    background-color: red;
  }

  #project-open-scrolltop {
    overflow: hidden;
  }
</style>
