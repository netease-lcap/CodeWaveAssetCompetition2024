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
        // { value: 0, name: "已通过" },
        // { value: 0, name: "未通过" },
      ],
    },
    dataColor: {
      type: Array,
      required: false,
      default: ["#2DB976", "#2DC3E0"], // 图表颜色集合
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
        },
        legend: {
          icon: "circle",
          left: "right",
          orient: "vertical",
          align: "left", // 文字位置
          top: "30%",
          padding: [0, 0],
          data: _that.valueData.map((item) => item.name),
          formatter(name) {
            let proportion = _that.getProportion(name).proportion;
            let value = _that.getProportion(name).value;
            return `{name|${name} }${proportion}（${value}）`;
          },
          textStyle: {
            fontWeight: "bold",
            lineHeight: 25,
            rich: {
              name: {
                color: "#999999",
                fontWeight: "bold",
                lineHeight: 25,
              },
            },
          },
        },
        color: _that.dataColor,
        series: [
          {
            name: _that.seriesName,
            type: "pie",
            center: ["25%", "50%"],
            radius: ["30%", "50%"],
            avoidLabelOverlap: false,
            padAngle: 3,
            label: {
              show: false,
              formatter: "{d}%（{c}）\n",
              alignTo: "",
              minMargin: 5,
              edgeDistance: 1,
              lineHeight: 15,
              textStyle: {
                color: "#999999",
                fontWeight: "bold",
              },
            },
            labelLine: {
              length: 20,
              length2: 15,
              maxSurfaceAngle: 100,
            },
            data: _that.valueData,
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
    getProportion(event) {
      const total = this.valueData.reduce(
        (accumulator, currentValue) => accumulator + currentValue.value,
        0
      );
      for (let item of this.valueData) {
        if (item.name == event) {
          return {
            value: item.value,
            proportion: ((item.value / total || 0) * 100).toFixed(2) + "%",
          };
        }
      }
    },
  },
};
</script>

<style>
</style>