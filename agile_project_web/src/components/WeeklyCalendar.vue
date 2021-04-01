<template>
  <div class="calendar-wrap">
    <div class="month-wrap" v-for="month in months">
      <div v-text="month.month+1" class="month-label"></div>
      <div style="display: inline-block;" class="month-base">
        <div class="calendar-week">
          <div class="calendar-week-item">日</div>
          <div class="calendar-week-item">一</div>
          <div class="calendar-week-item">二</div>
          <div class="calendar-week-item">三</div>
          <div class="calendar-week-item">四</div>
          <div class="calendar-week-item">五</div>
          <div class="calendar-week-item">六</div>
        </div>
        <div class="calendar-days">
          <div
            :key="`${day.dateString}`"
            :class="['calendar-item',day.month!==month.month?'calendar-other-month':'']"
            v-for="day in month.days"
            v-on:click="editDay(day)">
            <div class="day">
              <slot :day="day"/>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import moment from 'moment';

/**
 * 根据年份切换计划详情
 */
function onYearChange(year) {
  this.currentDay.date.year(year);
  this.initDays();
}

/**
 * 初始化当前日期
 */
function initDays() {
  this.loading = true;
  const months = [];
  for (let i = 0; i < 12; i++) {
    // console.log(moment(this.currentDay.date)
    //   .date(1).month(i));
    const currentYearFirstDay = moment(this.currentDay.date)
      .date(1)
      .month(i);
    const currentMonthFirstDay = moment(currentYearFirstDay).date(1);
    // console.log(currentMonthFirstDay);
    const prevMonthDayInWeek = currentMonthFirstDay.weekday();
    // console.log(prevMonthDayInWeek);
    const month = {
      days: [],
      weeks: [],
      month: currentMonthFirstDay.month(),
    };
    for (let j = 0; j < 42; j++) {
      const date = moment(currentMonthFirstDay).add(
        j - prevMonthDayInWeek,
        'day',
      );
      month.days.push({
        date,
        dateString: date.format('YYYY-MM-DD'),
        day: date.date(),
        detail: [],
        month: date.month(),
      });
    }

    let isoWeek = moment(month.days[41].date).isoWeek();
    if (i === 11 && isoWeek < 6) {
      isoWeek = moment(month.days[0].date).isoWeek();
      for (let m = 0; m < 6; m++) {
        month.weeks.push(isoWeek + m);
      }
    } else {
      for (let n = 6; n > 0; n--) {
        month.weeks.push(isoWeek - n + 1);
      }
    }
    months.push(month);
  }
  this.activeDays.forEach((x) => {
    const newVar = {
      year: x.date.year(),
      month: x.date.month(),
      day: x.date.date(),
    };
    const firstDay = moment(newVar).date(1);
    const weekday = firstDay.weekday();
    const month = months[x.date.month()];
    const index = x.date.date() + weekday - 1;
    month.days[index].detail.push({
      title: x.title,
      description: x.description,
      planStatus: x.planStatus,
      // lubricationPlanId: x.lubricationPlanId,
    });
    month.days[index].content = x.description;
  });
  this.months = months;
  this.loading = false;
}

/**
 * 编辑天
 */
function editDay(day) {
  this.onDayClick(day);
}

export default {
  name: 'WeeklyCalendar',
  created() {
    this.currentDay.date = moment();
    this.currentDay.month = this.currentDay.date.month();
    this.currentDay.year = this.currentDay.date.year();
    this.initDays();
  },
  // inject: ['editCalendarDay'],
  methods: {
    initDays,
    editDay,
    onYearChange,
  },
  watch: {
    days() {
      this.activeDays = this.days;
      this.initDays();
    },
    year() {
      this.onYearChange(this.year);
    },
  },
  props: {
    year: Number,
    days: Array,
    onDayClick: Function,
    lubricationItem: String,
  },
  data() {
    return {
      loading: false,
      months: [],
      activeDays: this.days,
      currentDay: {
        year: this.year,
        date: null,
        month: null,
        day: 1,
      },
    };
  },
};
</script>

<style>
  .ivu-poptip-rel {
    display: block;
  }

  .ivu-poptip-rel,
  .ivu-poptip-rel {
    width: 100%;
  }

  .ivu-poptip,
  .ivu-poptip-rel {
    display: block;
  }

  .calendar-wrap {
    display: flex;
    flex-wrap: wrap;
  }

  .month-base {
    text-align: center;
    font-family: "Microsoft Yahei", serif;
    color: #4a4a4a;
    /* box-shadow: 2px 4px 5px #bdbdbd; */
    border-width: 1px 0 0 1px;
    border-color: #e6e4e0;
    /* border-style: solid; */
    float: left;
    -moz-user-select: none;
    -webkit-user-select: none;
    -ms-user-select: none;
    user-select: none;
    -webkit-text-size-adjust: none;
    font-size: 15px;
  }

  .calendar-header {
    height: 35px;
    border-bottom: 0;
  }

  .calendar-week {
    height: 20px;
    line-height: 20px;
    font-weight: 400;
    font-size: 8px;
    color: #4a4a4a;
  }

  .calendar-days {
    display: flex;
    flex-wrap: wrap;
  }

  .calendar-item {
    position: relative;
    float: left;
    font-weight: 400;
    background: rgba(0, 0, 0, 0.05);
    border: 1px solid #ffffff;
    color: #565555;
    width: 14.25%;
    box-sizing: border-box;
  }

  .calendar-item:nth-child(7n) .day,
  .calendar-item:nth-child(7n + 1) .day {
    color: #EF5350;
  }

  .calendar-other-month {
    color: #c1c0c0 !important;
  }

  .calendar-other-month .day,
  .calendar-other-month .calendar-detail {
    color: #c1c0c0 !important;
  }

  .calendar-active-day,
  .calendar-selected {
    border-color: orange;
    z-index: 1;
  }

  .calendar-today {
    background-color: orange;
    color: white;
    border: 1px solid orange;
  }

  .calendar-item .day.active {
    background-color: rgba(8, 224, 80, 0.74);
    color: #ffffff;
  }

  .calendar-item .day.beforeUndo {
    background-color: #EF5350;
    color: #ffffff;
  }

  .calendar-item .day.beforeDo {
    background-color: rgba(192, 192, 192, 0.74);
    color: #ffffff;
  }

  .calendar-item .day.after {
    background-color: rgba(65, 105, 225, 0.74);
    color: #ffffff;
  }

  .calendar-item .day.todayDo {
    background-color: rgba(255, 105, 225, 0.74);
    color: #ffffff;
  }

  .calendar-item .day.todayUndo {
    background-color: rgba(255, 97, 0, 0.74);
    color: #ffffff;
  }

  .calendar-item:hover {
    cursor: pointer;
    /* color: #ffffff;
    background: #e84848; */
  }

  .calendar-item:hover {
    cursor: pointer;
    /* color: #ffffff;
    background: #e84848; */
  }

  /* .calendar-item:hover .day {
    color: #ffffff;
  }

  .calendar-today .day {
    color: white !important;
  } */

  .calendar-item .calendar-detail {
    font-size: 15px;
    height: 30px;
    line-height: 15px;
    font-weight: normal;
    overflow: hidden;
    /* text-overflow: unset; */
    word-wrap: break-word;
    /* text-align: center; */
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: left;
  }

  .calendar-week-item {
    height: 20px;
    float: left;
    width: 14.25%; /* background-color: #FBEC9C; */
    border-width: 1px;
    -moz-box-sizing: border-box;
    font-size: 15px;
  }

  .calendar-week-item:last-child {
    border-width: 1px 1px 1px 1px;
  }

  .calendar-week-item:nth-child(8n),
  .calendar-week-item:nth-child(8n - 1) {
    color: #EF5350 !important;
  }

  .month-wrap {
    padding: 1%;
    width: 25%;
    position: relative;
    box-sizing: border-box;
    border: 1px solid #DCDFE6;
  }

  .month-wrap .month-label {
    font-size: 150px;
    font-weight: 500;
    line-height: 1;
    text-align: center;
    cursor: pointer;
    color: #6062661f;
    position: absolute;
    width: 100%;
    top: 25px;
  }

  .holiday {
    font-size: 14px;
    color: #409eff;
  }

  .calendar-item:nth-child(7n) .day.holiday,
  .calendar-item:nth-child(7n - 1) .day.holiday {
    color: #565555;
  }

  .calendar-item:nth-child(7n) .holiday,
  .calendar-item:nth-child(7n - 1) .holiday {
    color: #565555;
  }
</style>
