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
      default: ["已通过", "待完成", "未通过"],
    },
    valueData: {
      type: Array,
      required: true,
      default: [],
    },
    dataColor: {
      type: Array,
      required: false,
      default: ["#2DB976", "#9F79EA", "#2DC3E0"], // 图表颜色集合
    },
    seriesName: {
      type: String,
      required: false,
      default: "",
    },
  },
  data() {
    return {
      option: {},
      chart: null,
    };
  },
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
          },
        ],
        series: [
          {
            name: _that.seriesName,
            type: "bar",
            barWidth: "60%",
            data: _that.valueData,
            itemStyle: {
              normal: {
                color: function (params) {
                  // 给出颜色组
                  var colorList = _that.dataColor;
                  return colorList[params.dataIndex];
                },
                label: {
                  show: false,
                },
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