<template>
  <div ref="chart" :style="{ width: chartWidth, height: chartHeight }" />
</template>

<script>
import * as echarts from "echarts";

export default {
  name: "passing-charts",
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
    valueData: {
      type: Array,
      required: true,
      default: [
        // { value: 100, name: "已通过" },
        // { value: 10, name: "未通过" },
      ],
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
  created() {
    this.$nextTick(() => {
      // this.init();
    });
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
          formatter: "{b} \n{d}%（{c}）",
          textStyle: {
            lineHeight: 25,
            rich: {
              name: {
                fontWeight: "bold",
                lineHeight: 25,
              },
            },
          },
        },
        legend: {
          bottom: "0",
          left: "center",
        },
        color: _that.dataColor,
        series: {
          name: _that.seriesName,
          type: "pie",
          radius: ["50%", "70%"],
          avoidLabelOverlap: false,
          padAngle: 2,
          itemStyle: {
            borderRadius: 5,
          },
          label: {
            show: true,
            formatter: "{b} {d}%（{c}）",
          },
          labelLine: {
            show: true,
          },
          data: _that.valueData,
        },
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