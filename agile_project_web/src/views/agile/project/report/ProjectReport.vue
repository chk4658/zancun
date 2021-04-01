<template>
  <div class="body-container">
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="24">
        <el-card class="box-card">

          <el-col :span="12" style="height: 400px">
            <simple-chart :option="chart.newProjectNumPerMonthOption"></simple-chart>
          </el-col>
          <el-col :span="12" style="height: 400px">
            <simple-chart :option="chart.topTenTaskNumInProjectOption"></simple-chart>
          </el-col>
          <el-col :span="12" style="height: 400px">
            <simple-chart :option="chart.topTenUserNumInProjectOption"></simple-chart>
          </el-col>
          <el-col :span="12" style="height: 400px">
            <simple-chart :option="chart.projectNumByStatusOption"></simple-chart>
          </el-col>

          <el-col :span="24">
            <p>多项目任务新增数据比较</p>
            <el-form :model="projectForm" :rules="rules" ref="projectForm">
              <el-form-item label="项目" prop="selectedOptions">
                <el-cascader
                  style="width: 600px"
                  v-model="projectForm.selectedOptions"
                  :options="projectList"
                  :props="props"
                  :show-all-levels="false"
                  filterable
                  clearable></el-cascader>
              </el-form-item>
              <el-form-item label="日期" prop="dateTime">
                <el-date-picker
                  style="width: 600px"
                  v-model="projectForm.dateTime"
                  type="daterange"
                  align="center"
                  value-format="yyyy-MM-dd"
                  unlink-panels
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
                  clearable
                  size="medium"
                  @change="changeDate"
                  :picker-options="pickerOptions">
                </el-date-picker>
                <span style="color: red;margin-left: 20px">* 时间跨度不得超过12个月</span>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="onSubmit">查询</el-button>
              </el-form-item>
            </el-form>
          </el-col>

          <el-col :span="24" style="height: 400px">
            <simple-chart :option="chart.newTaskNumPerDayInProjectIdsOption"></simple-chart>
          </el-col>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>

  import SimpleChart from '@/components/SimpleChart.vue';

  import {
    _getNewProjectNumPerMonth,
    _getTopTenTaskNumInProject,
    _getTopTenUserNumInProject,
    _getProjectNumByStatus,
    _getNewTaskNumPerDayInProjectIds
  } from '@/api/projectChartApi';

  import {_queryAllProjectList} from '@/api/projectApi';


  import {_getDayRange, _getMonthRange} from '@/utils/echartsUtils';
  import moment from "moment";


  /**
   * 项目统计  每月新增项目数
   * @returns {Promise<void>}
   */
  async function getNewProjectNumPerMonth() {
    const result = await _getNewProjectNumPerMonth();
    if (result.code === 1200) {
      const data = result.data.month;
      let count = 0;
      data.forEach(x => {
        this.newProjectNumPerMonth.xAxisData.push(x.dateTime);
        this.newProjectNumPerMonth.seriesBarData.push(x.num);
        count = count + x.num;
        this.newProjectNumPerMonth.seriesLineData.push(count);
      })

      this.chart.newProjectNumPerMonthOption = {
        title: {
          text: '项目新增统计',
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['每月新增项目数', '项目总数']
        },
        grid: {
          y2: 60
        },
        color: ['#003f8a', '#ff9500'],
        xAxis: {
          type: 'category',
          data: this.newProjectNumPerMonth.xAxisData,
          name: '日期',
          // axisLabel: {
          //   interval: 0,//横轴信息全部显示
          //   rotate: -45,//-30度角倾斜显示
          // },
        },
        yAxis: [
          {
            type: 'value',
            name: '每月新增项目数',
            splitLine: {     //网格线
              "show": false
            },
          },
          {
            type: 'value',
            name: '项目总数',
            splitLine: {     //网格线
              "show": false
            },
          }
        ],
        series: [
          {
            name: '每月新增项目数',
            data: this.newProjectNumPerMonth.seriesBarData,
            type: 'bar',
            markPoint: {
              data: [
                {type: 'max', name: '最大值'},
                {type: 'min', name: '最小值'}
              ]
            },
          },
          {
            name: '项目总数',
            data: this.newProjectNumPerMonth.seriesLineData,
            yAxisIndex: 1,
            type: 'line'
          }
        ]
      };


      console.log(this.chart.newProjectNumPerMonthOption)
    }
  }

  /**
   * 项目任务数Top10
   * @returns {Promise<void>}
   */
  async function getTopTenTaskNumInProject() {
    const result = await _getTopTenTaskNumInProject();
    if (result.code === 1200) {
      const data = result.data.topTenTaskNumInProject;
      data.forEach(x => {
        this.topTenTaskNumInProject.xAxisData.push(x.name);
        this.topTenTaskNumInProject.seriesBarData.push(x.taskNum);
      })

      const that = this;
      this.chart.topTenTaskNumInProjectOption = {
        title: {
          text: '项目任务数Top10',
        },
        tooltip: {
          trigger: 'axis'
        },
        grid: {
          y2: 60
        },
        legend: {
          data: ['任务数']
        },
        color: ['#003f8a'],
        xAxis: {
          type: 'category',
          data: this.topTenTaskNumInProject.xAxisData,
          name: '项目名称',
          axisLabel: {interval: 2},
          // axisLabel: {
          //   interval: 0,//横轴信息全部显示
          //   // rotate: -10,
          //   fontSize: 10,
          //   formatter: function (params) {
          //
          //     var newParamsName = '' // 最终拼接成的字符串
          //     var paramsNameNumber = params.length // 实际标签的个数
          //     var provideNumber = 6 // 每行能显示的字的个数
          //     var rowNumber = Math.ceil(paramsNameNumber / provideNumber) // 换行的话，需要显示几行，向上取整
          //     // 条件等同于rowNumber>1
          //     if (paramsNameNumber > provideNumber) {
          //       /** 循环每一行,p表示行 */
          //       for (var p = 0; p < rowNumber; p++) {
          //         var tempStr = "" // 表示每一次截取的字符串
          //         var start = p * provideNumber // 开始截取的位置
          //         var end = start + provideNumber // 结束截取的位置
          //         // 此处特殊处理最后一行的索引值
          //         if (p === rowNumber - 1) {
          //           tempStr = params.substring(start, paramsNameNumber)
          //         } else {
          //           // 每一次拼接字符串并换行
          //           tempStr = params.substring(start, end) + "\n"
          //         }
          //         newParamsName += tempStr // 最终拼成的字符串
          //       }
          //     } else {
          //       // 将旧标签的值赋给新标签
          //       newParamsName = params
          //     }
          //     return newParamsName
          //
          //
          //   }
          // },


        },
        yAxis: [
          {
            type: 'value',
            name: '任务数',
            splitLine: {     //网格线
              "show": false
            },
          }

        ],
        series: [
          {
            name: '任务数',
            data: this.topTenTaskNumInProject.seriesBarData,
            type: 'bar',
            markPoint: {
              data: [
                {type: 'max', name: '最大值'},
                {type: 'min', name: '最小值'}
              ]
            },
          }
        ]
      };
    }
  }

  /**
   * 项目参与人数Top10
   * @returns {Promise<void>}
   */
  async function getTopTenUserNumInProject() {
    const result = await _getTopTenUserNumInProject();
    if (result.code === 1200) {
      const data = result.data.topTenUserNumInProject;
      data.forEach(x => {
        this.topTenUserNumInProject.xAxisData.push(x.name);
        this.topTenUserNumInProject.seriesBarData.push(x.userNum);
      })

      this.chart.topTenUserNumInProjectOption = {
        title: {
          text: '项目参与人数Top10',
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['人数']
        },
        color: ['#003f8a'],
        xAxis: {
          type: 'category',
          data: this.topTenUserNumInProject.xAxisData,
          name: '项目名称',
          axisLabel: {interval: 2},
          // axisLabel: {
          //   interval: 0,//横轴信息全部显示
          //   // rotate: -10,
          //   fontSize: 10,
          //   formatter: function (params) {
          //
          //     var newParamsName = '' // 最终拼接成的字符串
          //     var paramsNameNumber = params.length // 实际标签的个数
          //     var provideNumber = 6 // 每行能显示的字的个数
          //     var rowNumber = Math.ceil(paramsNameNumber / provideNumber) // 换行的话，需要显示几行，向上取整
          //     // 条件等同于rowNumber>1
          //     if (paramsNameNumber > provideNumber) {
          //       /** 循环每一行,p表示行 */
          //       for (var p = 0; p < rowNumber; p++) {
          //         var tempStr = "" // 表示每一次截取的字符串
          //         var start = p * provideNumber // 开始截取的位置
          //         var end = start + provideNumber // 结束截取的位置
          //         // 此处特殊处理最后一行的索引值
          //         if (p === rowNumber - 1) {
          //           tempStr = params.substring(start, paramsNameNumber)
          //         } else {
          //           // 每一次拼接字符串并换行
          //           tempStr = params.substring(start, end) + "\n"
          //         }
          //         newParamsName += tempStr // 最终拼成的字符串
          //       }
          //     } else {
          //       // 将旧标签的值赋给新标签
          //       newParamsName = params
          //     }
          //     return newParamsName
          //
          //
          //   }
          // },
        },
        yAxis: [
          {
            type: 'value',
            name: '人数',
          }
        ],
        series: [
          {
            name: '人数',
            data: this.topTenUserNumInProject.seriesBarData,
            type: 'bar',
            markPoint: {
              data: [
                {type: 'max', name: '最大值'},
                {type: 'min', name: '最小值'}
              ]
            },
          }
        ]
      };


    }
  }


  /**
   * 按状态统计项目
   * @returns {Promise<void>}
   */
  async function getProjectNumByStatus() {
    const result = await _getProjectNumByStatus();
    if (result.code === 1200) {
      const data = result.data.projectNumByStatus;
      data.forEach(x => {
        const task = this.dateStatusList.filter((dateStatus => dateStatus.code === x.status))[0];
        const status = task === undefined ? {name: '未开始', color: '#ccc'} : task;
        this.projectNumByStatus.xAxisData.push(status.name);
        this.projectNumByStatus.seriesBarData.push(x.num);
      })

      this.chart.projectNumByStatusOption = {
        title: {
          text: '项目状态统计',
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['项目数']
        },
        color: ['#003f8a'],
        xAxis: {
          type: 'category',
          data: this.projectNumByStatus.xAxisData,
          name: '状态',
          axisLabel: {
            interval: 0,//横轴信息全部显示
          },
        },
        yAxis: [
          {
            type: 'value',
            name: '项目数',
          }
        ],
        series: [
          {
            name: '项目数',
            data: this.projectNumByStatus.seriesBarData,
            type: 'bar',
            markPoint: {
              data: [
                {type: 'max', name: '最大值'},
                {type: 'min', name: '最小值'}
              ]
            },
          }
        ]
      };


    }
  }

  /**
   * 获取项目列表
   * @returns {Promise<void>}
   */
  async function getProjectList() {
    const result = await _queryAllProjectList();
    if (result.code === 1200) {
      const projectList = result.data.allVisibleProject;
      this.projectList = projectList;
    }
  }

  // 生成随机颜色
  function randomColor() {

    return "#" + Math.random().toString(16).slice(-6);

  }

  /**
   * 提交多项目查询
   */
  function onSubmit() {

    console.log(this.projectForm.selectedOptions)

    this.$refs.projectForm.validate(async (valid) => {
      if (valid) {
        const query = {
          projectIds: this.projectForm.selectedOptions,
          startDate: this.projectForm.dateTime[0],
          endDate: this.projectForm.dateTime[1],
        };


        const result = await _getNewTaskNumPerDayInProjectIds(query);
        if (result.code === 1200) {
          const dataList = result.data.listMap;

          if (moment(query.endDate).diff(query.startDate, 'days') <= 30) {
            const betweenDays = _getDayRange(query.startDate, query.endDate);
            this.newTaskNumPerDayInProjectIds.xAxisData = betweenDays;

            const projectDataMap = [];
            this.newTaskNumPerDayInProjectIds.legendData = [];
            dataList.forEach(data => {
              const obj = projectDataMap.find((v) => {
                return v.name === data.project.name;
              });

              if (obj) {
                const dataArr = obj.data;
                dataArr[betweenDays.indexOf(data.dateTime)] = data.num;
                obj.data = dataArr;

              } else {
                const newArr = new Array(betweenDays.length).fill(0);
                newArr[betweenDays.indexOf(data.dateTime)] = data.num;
                this.newTaskNumPerDayInProjectIds.legendData.push(data.project.name);
                projectDataMap.push({
                  name: data.project.name,
                  type: 'bar',
                  data: newArr,
                });
              }

            });


            this.newTaskNumPerDayInProjectIds.seriesBarData = projectDataMap;

          } else {


            const betweenMonths = _getMonthRange(query.startDate, query.endDate);
            this.newTaskNumPerDayInProjectIds.xAxisData = betweenMonths;


            const projectDataMap = [];
            this.newTaskNumPerDayInProjectIds.legendData = [];
            dataList.forEach(data => {

              const obj = projectDataMap.find((v) => {
                return v.name === data.project.name;
              });

              if (obj) {

                const dataArr = obj.data;
                dataArr[betweenMonths.indexOf(moment(data.dateTime).format('YYYY-MM'))] += data.num;
                obj.data = dataArr;

              } else {
                const newArr = new Array(betweenMonths.length).fill(0);
                newArr[betweenMonths.indexOf(moment(data.dateTime).format('YYYY-MM'))] = data.num;
                this.newTaskNumPerDayInProjectIds.legendData.push(data.project.name);
                projectDataMap.push({
                  name: data.project.name,
                  type: 'bar',
                  data: newArr,
                });
              }

            });


            this.newTaskNumPerDayInProjectIds.seriesBarData = projectDataMap;
          }

          const randomArr = [];
          this.newTaskNumPerDayInProjectIds.legendData.forEach(ld => randomArr.push(randomColor()))

          this.chart.newTaskNumPerDayInProjectIdsOption = {
            title: {
              text: '多项目任务数比较',
            },
            tooltip: {
              trigger: 'axis'
            },
            legend: {
              data: this.newTaskNumPerDayInProjectIds.legendData
            },
            color: randomArr,
            xAxis: {
              type: 'category',
              data: this.newTaskNumPerDayInProjectIds.xAxisData,
              name: '日期'
            },
            yAxis: [
              {
                type: 'value',
                name: '任务数',
              }
            ],
            series: this.newTaskNumPerDayInProjectIds.seriesBarData,
          };


          console.log(this.chart.newTaskNumPerDayInProjectIdsOption)

        }

      } else {
        return false;
      }
    });

  }

  /**
   * 日期变更
   * @param e
   */
  function changeDate(e) {
    if (e === null) {
      this.minDate = "";
      this.maxDate = "";
      this.pickerOptions = {
        disabledDate: (time) => {
          // 返回是否禁用
          // 当前时间的时间戳
          let curDate = new Date().getTime();
          console.log(curDate)
          // 能查看几年前的时间 单位是年 （当前是10年）
          let cutTime = 365 * 10 * 24 * 3600 * 1000;
          // 最小时间的时间戳
          let minTime = curDate - cutTime;
          // 时间跨度 当前是6个月
          let month = 30 * 6 * 24 * 3600 * 1000;
          // 当前的23点59分59秒
          let nowLast = new Date(new Date(new Date().getTime()).setHours(23, 59, 59, 999)).getTime();
          if (this.minDate) {
            // 判断
            // 1.遍历时间大于今天（23:59:59）会返回true
            // 2.遍历时间小于最小时间会返回true
            // 3.遍历时间大于 选中的时间较小的一个 加上 时间跨度 会返回true
            // 4.遍历时间小于 选中的时间较小的一个 减去 时间跨度 会返回true
            return (
              time.getTime() > nowLast ||
              time.getTime() < minTime ||
              time > new Date(this.minDate.getTime() + month) ||
              time < new Date(this.minDate.getTime() - month)
            );
          }
          return time.getTime() > Date.now() || time.getTime() < minTime;
        },
      };
    }
  }


  export default {
    components: {SimpleChart},
    data() {
      return {
        props: {
          value: 'id',
          label: 'name',
          children: 'children',
          multiple: true,
          checkStrictly: true,
          emitPath: false,
        },
        projectForm: {
          selectedOptions: [],
          dateTime: '',
        },
        minDate: "",
        maxDate: "",
        pickerOptions: {
          shortcuts: [{
            text: '最近一周',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近一个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          }, {
            text: '最近三个月',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit('pick', [start, end]);
            }
          }],
          onPick: ({maxDate, minDate}) => {
            this.minDate = minDate;//选中的时间较小的一个
            this.maxDate = maxDate;//选中的时间较大的一个
          },
          disabledDate: (time) => {// 设置禁用状态，参数为当前日期，要求返回 Boolean 返回为true禁用
            // 当前时间的时间戳
            let curDate = new Date().getTime();
            // 能查看几年前的时间 单位是年 （当前是10年）
            let cutTime = 365 * 10 * 24 * 3600 * 1000;
            // 最小时间的时间戳
            let minTime = curDate - cutTime;
            // 时间跨度 当前是6个月
            let month = 30 * 12 * 24 * 3600 * 1000;
            // 当前的23点59分59秒
            let nowLast = new Date(new Date(new Date().getTime()).setHours(23, 59, 59, 999)).getTime();
            if (this.minDate) {
              // 判断
              // 1.遍历时间大于今天（23:59:59）会返回true
              // 2.遍历时间小于最小时间会返回true
              // 3.遍历时间大于 选中的时间较小的一个 加上 时间跨度 会返回true
              // 4.遍历时间小于 选中的时间较小的一个 减去 时间跨度 会返回true
              return (
                time.getTime() > nowLast ||
                time.getTime() < minTime ||
                time > new Date(this.minDate.getTime() + month) ||
                time < new Date(this.minDate.getTime() - month)
              );
            }
            return time.getTime() > Date.now() || time.getTime() < minTime;
          },
        },
        projectList: [],
        rules: {
          selectedOptions: [
            {required: true, message: '请至少选择一个圈子', trigger: 'change'},
          ],
          dateTime: [
            {required: true, message: '请选择日期', trigger: 'change'},
          ],
        },
        chart: {
          newProjectNumPerMonthOption: {},
          topTenTaskNumInProjectOption: {},
          topTenUserNumInProjectOption: {},
          newTaskNumPerDayInProjectIdsOption: {},
          projectNumByStatusOption: {},
        },
        newProjectNumPerMonth: {
          xAxisData: [],
          seriesBarData: [],
          seriesLineData: [],
        },
        topTenTaskNumInProject: {
          xAxisData: [],
          seriesBarData: [],
        },
        topTenUserNumInProject: {
          xAxisData: [],
          seriesBarData: [],
        },
        newTaskNumPerDayInProjectIds: {
          xAxisData: [],
          seriesBarData: [],
          legendData: [],
        },
        projectNumByStatus: {
          xAxisData: [],
          seriesBarData: [],
        },
        dateStatusList: [],
      };
    },
    methods: {
      getNewProjectNumPerMonth,
      getTopTenTaskNumInProject,
      getTopTenUserNumInProject,
      getProjectList,
      onSubmit,
      changeDate,
      getProjectNumByStatus,
      randomColor
    },
    mounted() {
      this.getProjectList();
    },
    created() {
      const dictData = this.$store.getters.getDictionaryByKey('TASK_STATUS');
      this.dateStatusList = dictData.sysDictDataList;

      this.getNewProjectNumPerMonth();
      this.getTopTenTaskNumInProject();
      this.getTopTenUserNumInProject();
      this.getProjectNumByStatus();
    },
  };
</script>

<style scoped>
  .body-container {
    overflow: auto;
    overflow-x: hidden;
    height: 100%;
    flex: 1;
  }
</style>
