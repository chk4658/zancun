<template>
  <div class="chart-wrap">
    <div ref="chart" class="chart"></div>
    <resize-observer @notify="handleResize"/>
  </div>
</template>

<script>
import echarts from 'echarts';


export default {
  name: 'SimpleChart',
  props: {
    option: Object,
  },
  data() {
    return {
      chart: null,
      screenWidth: document.documentElement.clientWidth,
      screenHeight: document.documentElement.clientHeight,
    };
  },
  methods: {
    handleResize() {
      this.chart.resize();
    },
  },
  mounted() {
    this.chart = echarts.init(this.$refs.chart);
    this.chart.setOption(this.option, true);
  },
  watch: {
    option: {
      deep: true,
      handler(newVal) {
        this.chart.setOption(newVal, true);
      },
    },
    screenWidth() {
    },
    screenHeight() {
    },
  },
  beforeDestroy() {
    this.chart.dispose();
    this.chart = null;
  },
};
</script>

<style scoped>
.chart {
  width: 100%;
  height: 100%;
}

.chart-wrap {
  width: 100%;
  height: 100%;
}
</style>
