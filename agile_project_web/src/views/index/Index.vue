<template>
  <div>
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="8">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
<!--            <span v-dict="'SEX-MALE'">1121212</span>-->
<!--            {{ $dict('SEX-MALE')}}-->
          </div>
          <div v-for="o in 4" :key="o" class="text item">
            {{'列表内容 ' + o }}
          </div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="24">
        <el-card class="box-card">

          <div slot="header" class="clearfix">
            <span>执行情况</span>
          </div>
          <el-col :span="4" style="height: 400px">
            <simple-chart :option="chart.option0"></simple-chart>
          </el-col>
          <el-col :span="4" style="height: 400px">
            <simple-chart :option="chart.option1"></simple-chart>
          </el-col>
          <el-col :span="4" style="height: 400px">
            <simple-chart :option="chart.option2"></simple-chart>
          </el-col>
          <el-col :span="6" style="height: 400px">
            <simple-chart :option="chart.option1"></simple-chart>
          </el-col>
          <el-col :span="6" style="height: 400px">
            <simple-chart :option="chart.option1"></simple-chart>
          </el-col>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="20" style="margin-bottom: 20px">
      <el-col :span="24">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>日历</span>
          </div>
          <yearly-calendar :year="2020" :days="[]"></yearly-calendar>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>

import SimpleChart from '../../components/SimpleChart.vue';
import YearlyCalendar from '../../components/YearlyCalendar.vue';

export default {
  components: { YearlyCalendar, SimpleChart },
  data() {
    return {
      value: ['1'],
      chart: {
        option0: {

          angleAxis: {},
          radiusAxis: {
            type: 'category',
            data: ['周一', '周二', '周三', '周四'],
            z: 10,
          },
          polar: {},
          series: [{
            type: 'bar',
            data: [1, 2, 3, 4],
            coordinateSystem: 'polar',
            name: 'A',
            stack: 'a',
          }, {
            type: 'bar',
            data: [2, 4, 6, 8],
            coordinateSystem: 'polar',
            name: 'B',
            stack: 'a',
          }, {
            type: 'bar',
            data: [1, 2, 3, 4],
            coordinateSystem: 'polar',
            name: 'C',
            stack: 'a',
          }],
          legend: {
            show: true,
            data: ['A', 'B', 'C'],
          },
        },
        option1: {
          xAxis: {
            type: 'category',
            data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
          },
          yAxis: {
            type: 'value',
          },
          series: [{
            data: [820, 932, 901, 934, 1290, 1330, 1320],
            type: 'line',
          }],
        },
        option2: {
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b} : {c} ({d}%)',
          },
          series: [
            {
              name: '访问来源',
              type: 'pie',
              radius: '55%',
              center: ['50%', '50%'],
              data: [
                { value: 335, name: '直接访问' },
                { value: 310, name: '邮件营销' },
                { value: 274, name: '联盟广告' },
                { value: 235, name: '视频广告' },
                { value: 400, name: '搜索引擎' },
              ].sort((a, b) => a.value - b.value),
              roseType: 'radius',
              label: {
                color: 'rgba(255, 255, 255, 0.3)',
              },
              labelLine: {
                lineStyle: {
                  color: 'rgba(255, 255, 255, 0.3)',
                },
                smooth: 0.2,
                length: 10,
                length2: 20,
              },
              itemStyle: {
                color: '#c23531',
                shadowBlur: 200,
                shadowColor: 'rgba(0, 0, 0, 0.5)',
              },
              animationType: 'scale',
              animationDelay() {
                return Math.random() * 200;
              },
            },
          ],
        },
      },
    };
  },
  methods: {},
};
</script>

<style scoped>
</style>
