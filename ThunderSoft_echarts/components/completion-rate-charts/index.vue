<template>
  <div ref="chart" :style="{ width: chartWidth, height: chartHeight }" />
</template>

<script>
import * as echarts from "echarts";

export default {
  name: "frequency-charts",
  props: {
    chartWidth: {
      type: String,
      required: true,
      default: "100%",
    },
    chartHeight: {
      type: String,
      required: true,
      default: "300px",
    },
    xAxisData: {
      type: Array,
      required: true,
      default: ["已通过", "未通过"],
    },
    averageValue: {
      type: Array,
      required: true,
      default: [], // 平均数据
    },
    myValue: {
      type: Array,
      required: true,
      default: [], // 我的数据
    },
    dataColor: {
      type: Array,
      required: false,
      default: ["#2DB976", "#2DC3E0"], // 图表颜色集合
    },
  },
  data() {
    return {
      option: {},
      chart: null,
    };
  },
  // created() {
  //   this.$nextTick(() => {
  //     this.init();
  //   });
  // },
  beforeDestroy() {
    window.removeEventListener("resize", this.resizeChart);
  },
  methods: {
    init() {
      const _that = this;
      _that.chart = echarts.init(_that.$refs.chart);
      _that.option = {
        tooltip: {
          trigger: "item",
          axisPointer: {
            type: "shadow",
          },
        },
        legend: {
          data: ["平均", "我的"],
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true,
        },
        xAxis: [
          {
            type: "category",
            data: _that.xAxisData,
            axisTick: {
              alignWithLabel: true,
            },
          },
        ],
        yAxis: [
          {
            type: "value",
            name: "(%)",
          },
        ],
        series: [
          {
            name: "平均",
            type: "bar",
            data: _that.averageValue,
            tooltip: {
              valueFormatter: function (value) {
                return value + " %";
              },
            },
          },
          {
            name: "我的",
            type: "bar",
            data: _that.myValue,
            tooltip: {
              valueFormatter: function (value) {
                return value + " %";
              },
            },
          },
        ],
      };
      _that.chart.setOption(_that.option);
      window.addEventListener("resize", this.resizeChart);
    },
    resizeChart() {
      this.$nextTick(() => {
        this.chart.resize();
      });
    },
  },
};
</script>

<style>
</style>