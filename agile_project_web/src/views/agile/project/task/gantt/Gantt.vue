<template>
  <div class="table-container">
    <gantt-elastic
      @tasks-changed="tasksUpdates"
      @options-changed="optionsUpdate"
      :options="options"
      :tasks="tasks">
      <gantt-header slot="header"></gantt-header>
    </gantt-elastic>
    <!--    点击显示任务的dialog-->
    <el-dialog
      @close="closeTask"
      :visible.sync="taskDialogVisible"
      width="580px"
      append-to-body>
      <div slot="title" class="header-title">
        <span>{{ taskInformation.name }}</span>
      </div>
      <task-edit :task="taskInformation"
                 :taskAttr="cols"
                 @save="closeTask"
                 @re="refreshDelivery"
                 @update="listInRedis"
                 :reBeginAuthority="reBeginAuthority"
                 :authority="authority"
                 :rolePlus="rolePlus"></task-edit>
    </el-dialog>
  </div>
</template>


<script>
import GanttElastic from "gantt-elastic";
import GanttHeader from "gantt-elastic-header";
import moment from "moment";

import {_getToPut} from '@/utils/taskUtils';
import TaskEdit from "../kanban/TaskEdit";


let that

async function listInRedis() {
  this.taskData = [];

  const result = await _getToPut(this.current.projectId, this.searchValue, this, this.types, this.person);
  const milestoneArray = result.milestoneArray;


  this.cols = result.cols;


  const childTask = result.childTaskList;
  const parents = []
  milestoneArray.forEach(x => {

    parents.push(...x.taskList);

    let maxTime;
    let minTime;

    x.taskList.forEach((t, index) => {

      delete t.parentId
      const time = moment(t.estEndTime).format('YYYY-MM-DD');

      const task = {
        id: t.id,
        name: t.name,
        start: time,
        duration: 24 * 60 * 60 * 1000,
        type: 'task',
        percent: 100,
        parentId: t.milestoneId,
        info: t,
      };

      if (index === 0 && t.estEndTime !== null) {
        maxTime = t.estEndTime;
        minTime = t.estEndTime;
      } else {
        if (t.estEndTime !== null && moment(t.estEndTime).isBefore(minTime)) {
          minTime = t.estEndTime;
        }
        if (t.estEndTime !== null && !moment(t.estEndTime).isBefore(maxTime)) {
          maxTime = t.estEndTime;
        }
      }

      childTask.filter(ch => ch.parentId === t.id).forEach(tch => {
        const childTask = {
          id: tch.id,
          name: tch.name,
          start: moment(tch.estEndTime).format('YYYY-MM-DD'),
          duration: 24 * 60 * 60 * 1000,
          type: 'task',
          percent: 100,
          parentId: t.id,
          info: tch
        }
        this.taskData.push(childTask)


        if (tch.estEndTime !== null && moment(tch.estEndTime).isBefore(minTime)) {
          minTime = tch.estEndTime;
        }
        if (tch.estEndTime !== null && !moment(tch.estEndTime).isBefore(maxTime)) {
          maxTime = tch.estEndTime;
        }

      })


      this.taskData.push(task);

    });

    console.log(moment(maxTime).diff(moment(minTime), 'days') + 1)
    const minDate = moment(minTime).format('YYYY-MM-DD');
    const maxDate = moment(maxTime).format('YYYY-MM-DD');

    const duration = (moment(maxDate).diff(moment(minDate), 'days') + 1) * 24 * 60 * 60 * 1000;

    const projectMilestone = x.projectMilestone;

    const milestone = {
      id: projectMilestone.id,
      name: projectMilestone.name,
      start: minDate,
      end: maxDate,
      duration: duration,
      type: 'milestone',
      percent: 0,
      collapsed: false,
    };
    this.taskData.push(milestone);


  });

  const ref = parents.concat(childTask)
  if (this.currentTaskId !== '' && ref.filter(r => r.id === this.currentTaskId).length !== 0) {
    this.taskInformation = ref.filter(r => r.id === this.currentTaskId)[0]
  }


  this.tasks = this.taskData;
  // if (this.taskData.length > 0) {
  //   this.tasksUpdates(this.taskData, 2);
  //   this.optionsUpdate(this.options, 2)
  // }
}


function closeTask() {
  this.taskDialogVisible = false;
  this.currentTaskId = ''
  this.listInRedis()

}

function refreshDelivery() {

  this.$parent.refreshDelivery()

}

function optionsUpdate(options, num) {

  if (num === 2) {
    // this.tasks = [];
    this.options = options;

  } else {
    return
  }

}

function tasksUpdates(tasks, num) {
  if (num === 2) {
    // this.tasks = [];
    this.tasks = tasks;
  } else {
    return
  }
}


let options = {
  taskMapping: {
    progress: "percent"
  },
  maxRows: 1000,
  maxHeight: 500,
  row: {
    height: 20
  },
  calendar: {
    hour: {
      display: false
    }
  },
  times: {
    timeZoom: 20,
  },
  chart: {
    progress: {
      bar: false
    },
    expander: {
      display: true
    }
  },
  taskList: {
    expander: {
      straight: false
    },
    columns: [
      {
        id: 1,
        label: "名称",
        value: "name",
        width: 200,
        expander: true,
        html: true,
        events: {
          click({data, column}) {
            // alert("description clicked!\n" + data.label);

            if (data.type === 'task') {
              that.taskInformation = data.info;
              that.currentTaskId = data.info.id
              that.taskDialogVisible = true;
            }
          }
        }
      },
      {
        id: 2,
        label: "截止时间",
        value: task => moment(task.start).format("YYYY-MM-DD"),
        width: 100,
      },
    ]
  },
  locale: {
    name: "en",
    Now: "Now",
    "X-Scale": "Zoom-X",
    "Y-Scale": "Zoom-Y",
    "Task list width": "Task list",
    "Before/After": "Expand",
    "Display task list": "Task list",
    weekdays: ["周日", "周一", "周二", "周三", "周四", "周五", "周六"],
    months: ["一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月"],
  }
};

export default {
//   name: "Gantt",
  components: {
    GanttElastic,
    GanttHeader,
    TaskEdit,
  },
  props: {
    searchValue: String,
    rolePlus: Array,
    authority: Boolean,
    project: Object,
    reBeginAuthority: Boolean,
    person: String
  },
  data() {
    return {
      taskData: [],
      current: {
        projectId: this.$route.query.id === undefined ? '' : this.$route.query.id,
      },
      tasks: [],
      options,

      cols: [],
      taskInformation: '',
      taskDialogVisible: false,
      types: [],
      currentTaskId: ''
    };
  },
  created() {
    const dictDataType = this.$store.getters.getDictionaryByKey('TASK_TYPE');
    this.types = dictDataType.sysDictDataList;
  },
  mounted() {
    that = this;
    if (this.current.projectId !== '') {
      this.listInRedis();
    }
  },
  computed: {},
  watch: {
    $route() {

      this.current.projectId = this.$route.query.id;
      if (this.current.projectId !== '') {

        this.listInRedis();
      }
    },
    searchValue: function (newVal, oldVal) {
      if (this.current.projectId !== '') {

        this.listInRedis();
      }
    },
    person: function (newVal, oldVal) {
      if (this.current.projectId !== '') {

        this.listInRedis();
      }
    }
  },
  methods: {
    listInRedis,
    optionsUpdate,
    tasksUpdates,
    closeTask,
    refreshDelivery
  }
}
;
</script>
<style scoped lang="scss">


.table-container {
  height: 100%;
  flex: 1;
  overflow-y: hidden;
  overflow-x: auto;
  position: relative;
  box-sizing: border-box;
  display: flex;
  margin-right: 10px;
}


</style>
