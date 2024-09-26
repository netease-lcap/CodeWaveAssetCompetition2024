<template>
  <div class="detail-dir" :style="{ height: this.height }">
    <!-- 左侧-目录 -->
    <div class="detail-dir-left" :style="{ width: this.categoryWidth }">
      <h2>目录</h2>
      <!-- 空数据 -->
      <div v-if="!summary.length" class="detail-dir-empty">
        <slot name="empty-category"><div>暂无数据</div></slot>
      </div>
      <div v-else class="dir-body">
        <div
          class="dir-body-item"
          :class="activeSummary.i === item.i && 'active'"
          v-for="(item, index) in summary"
          :key="index"
          @click="onClickTitle(item, index)"
        >
          <div class="item-title">
            {{ item.label }}
          </div>
        </div>
      </div>
    </div>
    <!-- 右侧-内容 -->
    <div class="detail-dir-right">
      <!-- 空数据 -->
      <div v-if="!content" class="detail-dir-empty">
        <slot name="empty-content"><div>暂无数据</div></slot>
      </div>
      <div v-else class="detail-dir-content" v-html="content"></div>
    </div>
  </div>
</template>

<script>
import { debounce } from './util';

export default {
  name: 'DetailDir',
  props: {
    // 高度
    height: {
      type: String,
      default: '400px',
    },
    // 根据富文本内容自动生成标题dom选择器
    titleSelector: {
      type: String,
      default: 'h2',
    },
    // 目录宽度
    categoryWidth: {
      type: String,
      default: '240px',
    },
    // 详情富文本内容
    content: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      summary: [],
      summaryElList: [],
      activeSummary: {},
      gap: 40,
      debounceOnScroll() {},
    };
  },
  watch: {
    content: {
      async handler() {
        await this.$nextTick();
        this.init();
      },
      immediate: true,
    },
  },
  async mounted() {
    // 监听滚动事件，更新当前激活标题
    let el = document.querySelector('.detail-dir-content');
    if (!el) return;
    el.addEventListener('scroll', this.onScroll);
    // 程序化销毁事件
    this.$once('hook:beforeDestroy', () => {
      el.removeEventListener('scroll', this.onScroll);
      el = null;
    });
    this.debounceOnScroll = debounce(this.onScroll, 200);
  },

  methods: {
    // 初始化
    init() {
      this.summaryElList = Array.from(document.querySelectorAll(`.detail-dir-content > ${this.titleSelector}`));
      this.summary = this.summaryElList.map((item, i) => ({
        i,
        label: item.innerHTML.replaceAll(/&nbsp;/g, ''),
        offsetTop: item.offsetTop,
      }));
      // 激活标题初始值取首个
      this.activeSummary = this.summary[0];
    },
    // 点击目录滚动至对应位置
    onClickTitle(item, i) {
      this.activeSummary = item;
      document.querySelector('.detail-dir-content').scrollTop = this.summaryElList[i].offsetTop - this.gap;
    },
    // 更新激活标题事件回调
    onScroll() {
      let target = this.summary.find((item) => document.querySelector('.detail-dir-content').scrollTop < item.offsetTop - this.gap);
      if (target) {
        const index = target.i - 1;
        this.activeSummary = this.summary[index > -1 ? index : 0];
      } else {
        // 滚动最后一个标题以后
        this.activeSummary = this.summary[this.summary.length - 1];
      }
    },
  },
};
</script>

<style scoped>
.detail-dir {
  padding: 20px;
  background-color: #eee;
  display: flex;
}

.detail-dir-left {
  margin-right: 20px;
  padding: 20px;
  height: 100%;
  overflow-y: auto;
  width: 240px;
  background-color: #ffffff;
}
.detail-dir-right {
  flex: 1;
  padding: 20px 2%;
  overflow-x: hidden;
  height: 100%;
  background-color: #ffffff;
  position: relative;
}
.detail-dir-content {
  height: 100%;
  overflow-y: auto;
  padding: 0px 50px;
  font-size: 16px;
}
.dir-body-item {
  /* min-width: 500px; */
  position: relative;
  cursor: pointer;
  padding-left: 28px;
  padding-bottom: 20px;
}
.dir-body-item:before {
  content: '';
  position: absolute;
  left: 0;
  top: 5px;
  width: 12px;
  height: 12px;
  background: #fff;
  border-radius: 50%;
  z-index: 3;
}
.dir-body-item:after {
  content: '';
  position: absolute;
  top: 10px;
  left: 5px;
  height: 100%;
  border-left: 2px solid #fff;
}
.dir-body-item:last-of-type:after {
  content: none;
}
.dir-body-item.active .item-title {
  color: #337eff;
}
.dir-body-item.active:before {
  background: #337eff;
}
.detail-dir-right .detail-dir-empty {
  text-align: center;
  padding-top: 10px;
}
</style>
