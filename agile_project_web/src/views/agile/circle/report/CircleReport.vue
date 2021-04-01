<template>
  <div class="body-container">
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="24">
        <el-card class="box-card">

<!--          <div slot="header" class="clearfix">-->
<!--            <span>圈子报告</span>-->
<!--          </div>-->
          <el-col :span="12" style="height: 400px">
            <simple-chart :option="chart.newCircleNumPerMonthOption"></simple-chart>
          </el-col>
          <el-col :span="12" style="height: 400px">
            <simple-chart :option="chart.topTenTaskNumInCircleOption"></simple-chart>
          </el-col>
          <el-col :span="12" style="height: 400px">
            <simple-chart :option="chart.topTenUserNumInCircleOption"></simple-chart>
          </el-col>

          <el-col :span="24">
            <p>多圈子任务新增数据比较</p>
            <el-form :model="circleForm" :rules="rules" ref="circleForm">
              <el-form-item label="圈子" prop="selectedOptions">
                <el-cascader
                  style="width: 600px"
                  v-model="circleForm.selectedOptions"
                  :options="circleList"
                  :props="props"
                  :show-all-levels="false"
                  filterable
                  clearable></el-cascader>
              </el-form-item>
              <el-form-item label="日期" prop="dateTime">
                <el-date-picker
                  style="width: 600px"
                  v-model="circleForm.dateTime"
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
            <simple-chart :option="chart.newTaskNumPerDayInCircleIdsOption"></simple-chart>
          </el-col>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>

  import SimpleChart from '@/components/SimpleChart.vue';
  import {
    _getNewCircleNumPerMonth,
    _getTopTenTaskNumInCircle,
    _getTopTenUserNumInCircle,
    _getNewTaskNumPerDayInCircleIds
  } from '@/api/circleChartApi';

  import {
    _queryCirclesMenus,
    _queryChildrenCircles
  } from '@/api/circleApi';

  import {_getDayRange, _getMonthRange} from '@/utils/echartsUtils';
  import moment from "moment";

  /**
   * 圈子统计 每月新增圈子数
   * @returns {Promise<void>}
   */
  async function getNewCircleNumPerMonth() {
    const result = await _getNewCircleNumPerMonth();
    if (result.code === 1200) {
      const data = result.data.month;
      let count = 0;
      data.forEach(x => {
        this.newCircleNumPerMonth.xAxisData.push(x.dateTime);
        this.newCircleNumPerMonth.seriesBarData.push(x.num);
        count = count + x.num;
        this.newCircleNumPerMonth.seriesLineData.push(count);
      })

      this.chart.newCircleNumPerMonthOption = {
        title: {
          text: '圈子新增统计',
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['每月新增圈子数', '圈子总数']
        },
        color: ['#003f8a', '#ff9500'],
        xAxis: {
          type: 'category',
          data: this.newCircleNumPerMonth.xAxisData,
          name: '日期'
        },
        yAxis: [
          {
            type: 'value',
            name: '每月新增圈子数',
          },
          {
            type: 'value',
            name: '圈子总数',
          }
        ],
        series: [
          {
            name: '每月新增圈子数',
            data: this.newCircleNumPerMonth.seriesBarData,
            type: 'bar',
            markPoint: {
              data: [
                {type: 'max', name: '最大值'},
                {type: 'min', name: '最小值'}
              ]
            },
          },
          {
            name: '圈子总数',
            data: this.newCircleNumPerMonth.seriesLineData,
            yAxisIndex: 1,
            type: 'line'
          }
        ]
      };
    }
  }

  /**
   * 圈子任务数Top10
   * @returns {Promise<void>}
   */
  async function getTopTenTaskNumInCircle() {
    const result = await _getTopTenTaskNumInCircle();
    if (result.code === 1200) {
      const data = result.data.topTenTaskNumInCircle;
      data.forEach(x => {
        this.topTenTaskNumInCircle.xAxisData.push(x.name);
        this.topTenTaskNumInCircle.seriesBarData.push(x.taskNum);
      })

      this.chart.topTenTaskNumInCircleOption = {
        title: {
          text: '圈子任务数Top10',
        },
        tooltip: {
          trigger: 'axis'
        },
        legend: {
          data: ['任务数']
        },
        color: ['#003f8a'],
        xAxis: {
          type: 'category',
          data: this.topTenTaskNumInCircle.xAxisData,
          name: '圈子名称',
          axisLabel: {interval: 2},
        },
        yAxis: [
          {
            type: 'value',
            name: '任务数',
          }
        ],
        series: [
          {
            name: '任务数',
            data: this.topTenTaskNumInCircle.seriesBarData,
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
   * 圈子参与人数Top10
   * @returns {Promise<void>}
   */
  async function getTopTenUserNumInCircle() {
    const result = await _getTopTenUserNumInCircle();
    if (result.code === 1200) {
      const data = result.data.topTenUserNumInCircle;
      data.forEach(x => {
        this.topTenUserNumInCircle.xAxisData.push(x.name);
        this.topTenUserNumInCircle.seriesBarData.push(x.userNum);
      })

      this.chart.topTenUserNumInCircleOption = {
        title: {
          text: '圈子参与人数Top10',
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
          data: this.topTenUserNumInCircle.xAxisData,
          name: '圈子名称',
          axisLabel: {interval: 2},
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
            data: this.topTenUserNumInCircle.seriesBarData,
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
   * 获取父圈
   * @returns {Promise<void>}
   */
  async function getCircleList() {
    const result = await _queryCirclesMenus();
    if (result.code === 1200) {
      const circleList = result.data.circles;
      this.circleList = this.getTreeData(circleList);
      console.log(this.circleList)
    }
  }

  /**
   * 递归判断列表，把最后的children设为undefined
   * @param circleList
   * @returns {*}
   */
  function getTreeData(circleList) {
    circleList.forEach(circle => {
      // children若为空数组，则将children设为undefined
      if (circle.children.length < 1) {
        circle.children = undefined;
      } else {
        // children若不为空数组，则继续 递归调用 本方法
        this.getTreeData(circle.children);
      }
    });
    return circleList;
  }

  // 生成随机颜色
  function randomColor() {

    return "#" + Math.random().toString(16).slice(-6);

  }

  /**
   * 提交多圈子查询
   */
  function onSubmit() {

    this.$refs.circleForm.validate(async (valid) => {
      if (valid) {
        const query = {
          circleIds: this.circleForm.selectedOptions,
          startDate: this.circleForm.dateTime[0],
          endDate: this.circleForm.dateTime[1],
        };


        const result = await _getNewTaskNumPerDayInCircleIds(query);
        if (result.code === 1200) {
          const dataList = result.data.listMap;

          if (moment(query.endDate).diff(query.startDate, 'days') <= 30) {
            const betweenDays = _getDayRange(query.startDate, query.endDate);
            this.newTaskNumPerDayInCircleIds.xAxisData = betweenDays;

            const circleDataMap = [];
            this.newTaskNumPerDayInCircleIds.legendData = [];
            dataList.forEach(data => {
              const obj = circleDataMap.find((v) => {
                return v.name === data.circleName;
              });

              if (obj) {
                const dataArr = obj.data;
                dataArr[betweenDays.indexOf(data.dateTime)] = data.num;
                obj.data = dataArr;

              } else {
                const newArr = new Array(betweenDays.length).fill(0);
                newArr[betweenDays.indexOf(data.dateTime)] = data.num;
                this.newTaskNumPerDayInCircleIds.legendData.push(data.circleName);
                circleDataMap.push({
                  name: data.circleName,
                  type: 'bar',
                  data: newArr,
                });
              }

            });


            this.newTaskNumPerDayInCircleIds.seriesBarData = circleDataMap;

          } else {


            const betweenMonths = _getMonthRange(query.startDate, query.endDate);
            this.newTaskNumPerDayInCircleIds.xAxisData = betweenMonths;


            const circleDataMap = [];
            this.newTaskNumPerDayInCircleIds.legendData = [];
            dataList.forEach(data => {

              const obj = circleDataMap.find((v) => {
                return v.name === data.circleName;
              });

              if (obj) {

                const dataArr = obj.data;
                dataArr[betweenMonths.indexOf(moment(data.dateTime).format('YYYY-MM'))] += data.num;
                obj.data = dataArr;

              } else {
                const newArr = new Array(betweenMonths.length).fill(0);
                newArr[betweenMonths.indexOf(moment(data.dateTime).format('YYYY-MM'))] = data.num;
                this.newTaskNumPerDayInCircleIds.legendData.push(data.circleName);
                circleDataMap.push({
                  name: data.circleName,
                  type: 'bar',
                  data: newArr,
                });
              }

            });


            this.newTaskNumPerDayInCircleIds.seriesBarData = circleDataMap;
          }

          const randomArr = [];
          this.newTaskNumPerDayInCircleIds.legendData.forEach(ld => randomArr.push(randomColor()))


          this.chart.newTaskNumPerDayInCircleIdsOption = {
            title: {
              text: '多圈子任务新增数据比较',
            },
            tooltip: {
              trigger: 'axis'
            },
            legend: {
              data: this.newTaskNumPerDayInCircleIds.legendData
            },
            color: randomArr,
            xAxis: {
              type: 'category',
              data: this.newTaskNumPerDayInCircleIds.xAxisData,
              name: '日期'
            },
            yAxis: [
              {
                type: 'value',
                name: '任务数',
              }
            ],
            series: this.newTaskNumPerDayInCircleIds.seriesBarData,
          };


          console.log(this.chart.newTaskNumPerDayInCircleIdsOption)

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
        circleForm: {
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
        circleList: [],
        rules: {
          selectedOptions: [
            {required: true, message: '请至少选择一个圈子', trigger: 'change'},
          ],
          dateTime: [
            {required: true, message: '请选择日期', trigger: 'change'},
          ],
        },
        chart: {
          newCircleNumPerMonthOption: {},
          topTenTaskNumInCircleOption: {},
          topTenUserNumInCircleOption: {},
          newTaskNumPerDayInCircleIdsOption: {},
        },
        newCircleNumPerMonth: {
          xAxisData: [],
          seriesBarData: [],
          seriesLineData: [],
        },
        topTenTaskNumInCircle: {
          xAxisData: [],
          seriesBarData: [],
        },
        topTenUserNumInCircle: {
          xAxisData: [],
          seriesBarData: [],
        },
        newTaskNumPerDayInCircleIds: {
          xAxisData: [],
          seriesBarData: [],
          legendData: [],
        },
      };
    },
    methods: {
      getNewCircleNumPerMonth,
      getTopTenTaskNumInCircle,
      getTopTenUserNumInCircle,
      getCircleList,
      getTreeData,
      onSubmit,
      changeDate,
      randomColor
    },
    mounted() {
      this.getCircleList();
    },
    created() {
      this.getNewCircleNumPerMonth();
      this.getTopTenTaskNumInCircle();
      this.getTopTenUserNumInCircle();
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
