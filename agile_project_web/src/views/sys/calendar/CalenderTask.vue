<template>
  <div>
    <div class="calender-task">
      <div class="el-calendar__header">
        <div class="el-calendar__title">
          {{currentDate1}}
        </div>
        <div class="el-calendar__button-group">
          <div class="el-button-group">
            <el-date-picker style="width:150px"
                            v-model="currentDate"
                            type="date"
                            placeholder="选择日期"
                            @change="changeCurrentDate"
                            :clearable="false">
            </el-date-picker>
          </div>
        </div>
      </div>
      <el-calendar v-model="currentDate" :first-day-of-week="7">
        <template
          slot="dateCell"
          slot-scope="{date, data}">
          <p :class="data.isSelected ? 'is-selected' : ''">
            {{ data.day.split('-').slice(1).join('-') }}
          </p>
          <div class="task-name" v-for="(item,index) in more(data)" :key="item.id" v-show="index <=2 ">
            <span v-if="item.name.length <= 10" @click="toTaskEdit(item)">{{item.name}}</span>
            <el-tooltip v-else class="item" effect="dark" :content="item.name" placement="top-start">
              <span @click="toTaskEdit(item)">{{item.name.substring(0,9)}}....</span>
            </el-tooltip>
          </div>


          <el-popover
            v-if="more(data).length > 3"
            placement="right"
            :visible-arrow="false"
            trigger="click">
            <div v-for="item in more(data)" :key="item.id">
              <div class="show_style_label" @click="toTaskEdit(item)">
                {{item.name}}
              </div>
            </div>


            <el-button class="task-name" type="text" slot="reference">更多...</el-button>

          </el-popover>


        </template>
      </el-calendar>
    </div>
    <!--    点击显示任务的dialog-->

    <el-dialog
      @close="closeTask"
      :visible.sync="taskDialogVisible"
      width="580px"
      append-to-body>
      <div slot="title" class="header-title">
        <span>{{taskInformation.name}}</span>
      </div>
      <task-edit :task="taskInformation"
                 :taskAttr="cols"
                 @save="closeTask"
                 @update="getTasks"
                 :need="false"
                 :reBeginAuthority="reBeginAuthority"
                 :authority="authority"
                 :rolePlus="rolePlus"></task-edit>
    </el-dialog>
  </div>
</template>
<script>
  import moment from 'moment';

  import {_listTaskByToken} from '@/api/taskApi';
  import {_getToPut} from '@/utils/taskUtils';
  import TaskEdit from "../../agile/project/task/kanban/TaskEdit";

  async function getTasks() {
    const result = await _listTaskByToken({startDate: this.startDate, endDate: this.endDate});
    this.tasks = result.data.taskList;
  }

  function more(data) {
    const tasks = this.tasks;
    const _tasks = [];
    tasks.forEach(task => {
      if (task.estEndTime !== null && task.estEndTime === data.day + ' 00:00:00') {
        _tasks.push(task);
      }
    });
    return _tasks;
  }

  async function toTaskEdit(task) {


    const result = await _getToPut(task.projectId, '', this, this.types);
    this.cols = result.cols;

    let fou = []
    result.milestoneArray.forEach(MA => {
      if (MA.taskList.filter(tl => tl.id === task.id).length > 0) {
        fou.push(MA.taskList.filter(tl => tl.id === task.id)[0])
      }
    })
    fou = fou.concat(result.childTaskList.filter(ch => ch.id === task.id))

    this.taskInformation = fou[0];

    this.taskDialogVisible = true
  }

  function closeTask() {
    this.taskDialogVisible = false;
    this.getTasks();
  }

  export default {
    components: {TaskEdit},
    data() {
      return {
        userId: localStorage.getItem('id'),
        currentDate: new Date(),
        startDate: null,
        endDate: null,
        tasks: [],
        currentDate1: moment(new Date()).format('YYYY年MM月'),
        cols: [],
        taskDialogVisible: false,
        taskInformation: '',
        rolePlus: [],
        reBeginAuthority: false,
        authority: false,
        types: []
      };
    },
    created() {

      const dictDataType = this.$store.getters.getDictionaryByKey('TASK_TYPE');
      this.types = dictDataType.sysDictDataList;

      const rangeDate = this.getRangeDate(this.currentDate);
      this.startDate = rangeDate[0];
      this.endDate = rangeDate[1];
      this.getTasks();

    },
    mounted() {
      //监听鼠标滚动事件
      window.addEventListener('mousewheel', this.debounce(this.handleScroll, 300), true) || window.addEventListener("DOMMouseScroll", this.debounce(this.handleScroll, 300), false)
    },
    methods: {
      getRangeDate(date) {
        const nowMonth = date.getMonth();
        const nowYear = date.getFullYear();
        const start = new Date(nowYear, nowMonth, 1);
        start.setDate(start.getDate() - 10);
        const end = new Date(nowYear, nowMonth + 1, 0);
        end.setDate(end.getDate() + 20);
        return [start, end];
      },
      getTasks,
      more,
      closeTask,
      toTaskEdit,
      changeCurrentDate(val) {

        this.currentDate1 = moment(this.currentDate).format('YYYY年MM月');
      },
      //函数防抖
      debounce(func, wait) {
        let timeout;
        return function () {
          let context = this;
          let args = arguments;
          if (timeout) clearTimeout(timeout);
          timeout = setTimeout(() => {
            func.apply(context, args)
          }, wait);
        }
      },
      //判断滚动方向
      handleScroll(e) {

        const dateObj = new Date(this.currentDate)


        const currMonth = dateObj.getMonth();
        let direction = e.deltaY > 0 ? 'down' : 'up'
        if (direction === 'down') {
          dateObj.setMonth(currMonth + 1);
          this.currentDate = new Date(dateObj)


          this.changeCurrentDate()
        } else {
          dateObj.setMonth(currMonth - 1);
          this.currentDate = new Date(dateObj)


          this.changeCurrentDate()
        }
      },
    },
    watch: {
      currentDate(nVal) {
        const rangeDate = this.getRangeDate(nVal);
        this.startDate = rangeDate[0];
        this.endDate = rangeDate[1];
        this.getTasks();
      },
    },
  };
</script>

<style lang="scss">
  .calender-task {
    .is-selected {
      color: #1989FA;
    }

    .current {
      background-color: white;
    }

    .task-name {
      font-size: 1px;
    }

    .el-calendar-day {
      height: 135px;
    }

    .el-calendar {
      .el-calendar__header {
        display: none;
      }
    }
  }

</style>
<style scoped lang="scss">
  .show_style_label {
    cursor: pointer;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    height: 26px;
    font-size: 13px;
    line-height: 26px;
  }

  .show_style_label:hover {
    background-color: #DBE4EE;
    color: #01408B;
  }
</style>
