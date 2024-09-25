<template>
  <div class="duration-charts">
    <div
      class="duration-charts-item"
      style="background-color: #ebf1fe; color: #9e9e9e"
    >
      <div class="progress-index">排行榜</div>
      <div class="progress-name">{{ progressText }}</div>
      <div class="progress-container">学习时长</div>
      <div class="progress-time"></div>
    </div>
    <div
      class="duration-charts-item"
      v-for="(item, index) in progress"
      :key="index"
      :style="{ backgroundColor: index % 2 == 0 ? '#FAFAFA' : '#FFFFFF' }"
    >
      <div class="progress-index">
        <img src="./static/top1.png" alt="" v-if="index == 0" />
        <img src="./static/top2.png" alt="" v-else-if="index == 1" />
        <img src="./static/top3.png" alt="" v-else-if="index == 2" />
        <span v-else>{{ index + 1 }}</span>
      </div>
      <div class="progress-name">{{ valueData[index].name }}</div>
      <div class="progress-container">
        <div
          class="progress-bar"
          :style="{
            width: item.value + '%',
            backgroundColor: colorData[index] || '#3498db',
            height: `${height}px`,
          }"
        />
      </div>
      <div class="progress-time">
        <span v-if="item.houer > 0">
          <countTo
            :startVal="0"
            :endVal="item.houer"
            :decimals="2"
            :duration="2000"
          />
          小时
        </span>
        <span v-if="item.houer == 0 && item.min > 0">
          <countTo
            :startVal="0"
            :endVal="item.min"
            :decimals="2"
            :duration="2000"
          />
          分钟
        </span>
        <span v-if="item.houer == 0 && item.min == 0 && item.second > 0">
          <countTo
            :startVal="0"
            :endVal="item.second"
            :decimals="2"
            :duration="2000"
          />
          秒
        </span>
      </div>
    </div>
  </div>
</template>

<script>
import countTo from "vue-count-to";

export default {
  components: {
    countTo,
  },
  props: {
    valueData: {
      type: Array,
      default: [
        // { name: "王小华12312312321333333333333333333", value: 1503 },
        // { name: "李小红", value: 24 },
        // { name: "周行", value: 24 },
        // { name: "吴天行", value: 990 },
        // { name: "周天行", value: 568 },
        // { name: "周天行", value: 456 },
        // { name: "周天行", value: 450 },
        // { name: "周天行", value: 400 },
        // { name: "周天行", value: 300 },
        // { name: "周天行", value: 100 },
      ], // 时长数据,[{name:xxx,value:xxx},...],需要按照倒叙排列,value为时长(单位:秒)
    },
    colorData: {
      type: Array,
      default: [
        "#7A4AD9",
        "#9F79EA",
        "#1F51C4",
        "#4179FA",
        "#2DC3E0",
        "#2DB976",
        "#72E168",
        "#F2DC1B",
        "#ED6F21",
        "#E63322",
        "#33FFDD",
        "#33FFFF",
      ], // 颜色列表
    },
    height: {
      type: Number,
      default: 20, // 进度条组件高度
    },
    progressText: {
      type: String,
      default: "姓名", // name文字
    },
  },
  data() {
    return {
      proportions: [], // 进度条比例
      progress: [], // 进度条的初始值
    };
  },
  created() {
    this.init();
  },
  methods: {
    init() {
      this.proportions = this.valueData.map((item) => {
        return (Number(item.value) / Number(this.valueData[0].value)) * 100; // 单位转换为百分比
      });
      this.progress = this.valueData.map((item) => {
        return {
          value: 0,
          number: item.value,
          houer: (item.value / 3600).toFixed(2),
          min: ((item.value % 3600) / 60).toFixed(2),
          second: item.value % 60,
        }; // 初始进度为0
      });
      for (let i in this.progress) {
        const interval = setInterval(() => {
          if (this.progress[i].value < this.proportions[i]) {
            this.$set(this.progress, i, {
              ...this.progress[i],
              value: this.progress[i].value + 1,
            }); // 每次增加的进度值
          } else {
            clearInterval(interval); // 进度到100%后清除定时器
          }
        }, 0.5); // 每50毫秒更新一次进度
      }
    },
  },
};
</script>

<style scoped>
.duration-charts {
  min-height: 20px;
}
.duration-charts-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background-color: #fff;
  padding: 10px 0;
  border-top: 1px solid #eee;
}
.duration-charts-item .progress-container {
  width: 50%;
  /* margin-bottom: 10px; */
}
.duration-charts-item .progress-container .progress-bar {
  height: 20px;
  background-color: #3498db;
  transition: width 1s ease; /* 进度条宽度变化的动画效果 */
  width: 0; /* 初始宽度为0 */
  border-radius: 20px; /* 圆角 */
}
.duration-charts-item .progress-index {
  width: 10%;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
}
.duration-charts-item .progress-index img {
  width: 25px;
  height: 25px;
}
.duration-charts-item .progress-name {
  width: 15%;
  white-space: nowrap; /* 确保文本在一行内显示 */
  overflow: hidden; /* 隐藏超出容器的内容 */
  text-overflow: ellipsis; /* 使用省略号表示被截断的文本 */
}
.duration-charts-item .progress-time {
  width: 15%;
}
</style>