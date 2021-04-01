<template>
  <div class="table-container">
    <div class="calender-task2">
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
          <div class="task-name" v-for="(item,index) in more(data)" :key="item.id" v-show="index <=0 ">
            <span v-if="item.name.length <= 10" @click="toTaskEdit(item)">{{item.name}}</span>
            <el-tooltip v-else class="item" effect="dark" :content="item.name" placement="top-start">
              <span @click="toTaskEdit(item)">{{item.name.substring(0,9)}}....</span>
            </el-tooltip>
          </div>


          <el-popover
            v-if="more(data).length > 1"
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
                 @update="cesh"
                 :need="false"
                 :reBeginAuthority="reBeginAuthority"
                 :authority="authority"
                 :rolePlus="rolePlus"></task-edit>
    </el-dialog>
  </div>
</template>

<script>
  import moment from 'moment';
  import {_getToPut} from '@/utils/taskUtils';
  import TaskEdit from "../kanban/TaskEdit";

  async function getTasks(projectId, searchName, initialization) {
    const result = await _getToPut(projectId, searchName, this, this.types, this.person);

    this.cols = result.cols;
    const pTask = [];
    result.milestoneArray.forEach(MA => {
      MA.taskList.forEach(tl => {
        pTask.push(tl)
      })
    })


    const allTask = pTask.concat(result.childTaskList)

    if (this.currentTaskId !== '' && allTask.filter(r => r.id === this.currentTaskId).length !== 0) {
      this.taskInformation = allTask.filter(r => r.id === this.currentTaskId)[0]
    }

    if(initialization === 'initialization') {


      const afterSort = allTask.filter(at => at.estEndTime !== null).sort((a, b) => {
        return new Date(a.estEndTime).getTime() < new Date(b.estEndTime).getTime() ? -1 : 1;
      })

      if (afterSort.length === 0) {
        this.currentDate = new Date()
      }else {
        this.currentDate = new Date(afterSort[0].estEndTime)
      }

      this.currentDate1 = moment(this.currentDate).format('YYYY年MM月');

      const rangeDate = this.getRangeDate(this.currentDate);
      this.startDate = rangeDate[0];
      this.endDate = rangeDate[1];
    }

    this.tasks = allTask.filter(at => {
      if (at.estEndTime !== null) {
        const curr = new Date(at.estEndTime);
        const sta = new Date(this.startDate);
        const end = new Date(this.endDate);

        return curr > sta && curr < end
      }
    })

  }

  function cesh() {

    this.getTasks(this.current.projectId, this.searchValue);
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

    this.taskInformation = task;
    this.currentTaskId = task.id;
    this.taskDialogVisible = true
  }

  function closeTask() {
    this.taskDialogVisible = false;
    this.currentTaskId = '';
    this.getTasks(this.current.projectId, this.searchValue);
  }

  export default {
    components: {TaskEdit},
    props: {
      isAddTask: Boolean,
      searchValue: String,
      authority: Boolean,
      reBeginAuthority: Boolean,
      project: Object,
      rolePlus: Array,
      person: String
    },
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
        types: [],
        current: {
          item: '',
          projectId: '',
          type: '',
          roleType: '',
          roleId: '',
        },
        currentTaskId: ''
      };
    },
    created() {

      const dictDataType = this.$store.getters.getDictionaryByKey('TASK_TYPE');
      this.types = dictDataType.sysDictDataList;


      this.current.projectId = this.$route.query.id === undefined ? '' : this.$route.query.id;
      if (this.current.projectId !== '') {
        this.getTasks(this.current.projectId, this.searchValue, 'initialization');

      }


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
      cesh,
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
        this.getTasks(this.current.projectId, this.searchValue);
      },

      searchValue(val) {

        this.getTasks(this.current.projectId, val)

      },
      person(val) {

        this.getTasks(this.current.projectId, this.searchValue)

      }
    },
  };
</script>

<style lang="scss">
  .calender-task2 {
    .el-calendar__header {
      padding: 6px 20px;
    }

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
      height: 96px;
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

  .table-container {
    height: 100%;
    flex: 1;
    overflow: auto;
    overflow-y: hidden;
    position: relative;
    display: flex;
    flex-direction: column;
  }
</style>
