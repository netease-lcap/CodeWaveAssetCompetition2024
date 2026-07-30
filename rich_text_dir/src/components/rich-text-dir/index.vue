<template>
  <div class="rich-text-toc-container" :style="{ height: height }">
    <!-- 左侧目录区域 -->
    <div class="toc-sidebar" ref="sidebarRef" :style="{ width: sidebarWidth }">
      <div class="toc-title">{{ title }}</div>
      <ul class="toc-list">
        <div v-if="!tocItems.length" class="empty-text">暂无数据</div>
        <li
          v-for="(item, index) in tocItems"
          :key="index"
          :class="['toc-item', { 'is-active': currentActiveId === item.id }]"
          :style="{ paddingLeft: `${(item.level - 1) * 20 + 5}px`, paddingRight: '5px' }"
          @click="scrollToHeading(item.id)"
        >
          <span v-html="item.text"></span>
        </li>
      </ul>
    </div>

    <!-- 右侧内容区域 -->
    <div class="content-container" ref="contentRef">
      <div v-if="content" class="content-wrapper" v-html="content" ref="htmlContentRef"></div>
      <div v-else class="empty-text">暂无数据</div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    /**
     * 富文本内容
     * 包含 HTML 格式的文本内容，将作为目录的源内容
     * 内容中应包含 h1-h6 标签作为目录项
     * @required
     */
    content: {
      type: String,
    },
    // 目录标题
    title: {
      type: String,
      default: '目录',
    },

    /**
     * 容器高度
     * 设置目录组件的整体高度
     * 支持所有有效的 CSS 高度值（如：'500px'、'100vh'、'80%'等）
     * @default '500px'
     */
    height: {
      type: String,
      default: '500px',
    },

    // 左侧目录宽度
    sidebarWidth: {
      type: String,
      default: '240px',
    },

    /**
     * 滚动偏移量
     * 设置标题滚动到顶部时的偏移距离
     * 用于控制标题在视口中的位置，避免标题紧贴顶部
     * @default 20
     */
    scrollOffset: {
      type: Number,
      default: 20,
    },
  },

  data() {
    return {
      tocItems: [],
      currentActiveId: null,
      scrollTimer: null,
      isManualScrolling: false,
      scrollLockTimeout: null,
      headerKey: null,
    };
  },

  mounted() {
    this.headerKey = Date.now();
    this.setupScrollListener();
  },

  watch: {
    content: {
      immediate: true,
      async handler() {
        this.generateTOC();
        // 初始检查
        await this.$nextTick();
        this.updateActiveHeading();
      },
    },
  },

  beforeDestroy() {
    if (this.$refs.contentRef) {
      this.$refs.contentRef.removeEventListener('scroll', this.handleScroll);
    }
    if (this.scrollLockTimeout) {
      clearTimeout(this.scrollLockTimeout);
    }
  },

  methods: {
    /**
     * 生成目录结构
     * 1. 查找所有标题元素
     * 2. 为每个标题生成唯一ID
     * 3. 计算每个标题相对于内容容器的位置
     * 4. 构建目录项数组
     */
    async generateTOC() {
      this.tocItems = [];
      await this.$nextTick();
      if (!this.$refs.htmlContentRef || !this.$refs.contentRef) return;

      const headings = this.$refs.htmlContentRef.querySelectorAll('h1, h2, h3, h4, h5, h6');
      const contentRect = this.$refs.contentRef.getBoundingClientRect();

      headings.forEach((heading, index) => {
        const level = parseInt(heading.tagName.charAt(1));
        const text = heading.textContent || '';
        const id = `heading-${index}-${this.headerKey}`;

        heading.id = id;

        // 计算标题相对于内容容器的位置
        const rect = heading.getBoundingClientRect();
        const offsetTop = rect.top - contentRect.top + this.$refs.contentRef.scrollTop;

        this.tocItems.push({
          id,
          level,
          text,
          element: heading,
          offsetTop,
        });
      });
    },

    /**
     * 设置滚动监听器
     * 监听内容容器的滚动事件，用于更新当前活动标题
     */
    setupScrollListener() {
      if (!this.$refs.contentRef) return;
      this.$refs.contentRef.addEventListener('scroll', this.handleScroll);
    },

    /**
     * 处理滚动事件
     * 使用节流优化性能，避免频繁更新
     * 当手动滚动时（点击目录项）不触发更新
     */
    handleScroll() {
      if (this.isManualScrolling) return;

      clearTimeout(this.scrollTimer);
      this.scrollTimer = setTimeout(() => {
        this.updateActiveHeading();
      }, 10);
    },

    /**
     * 滚动到指定标题
     * 1. 计算目标标题的位置
     * 2. 设置滚动锁定状态
     * 3. 执行滚动
     * 4. 滚动结束后更新状态
     * @param {string} id - 目标标题的ID
     */
    scrollToHeading(id) {
      const element = document.getElementById(id);
      if (!element || !this.$refs.contentRef) return;

      // 清除之前的锁定状态
      if (this.scrollLockTimeout) {
        clearTimeout(this.scrollLockTimeout);
      }

      // 设置滚动锁定和记录点击的标题
      this.isManualScrolling = true;
      this.currentActiveId = id;

      // 计算目标位置
      const contentRect = this.$refs.contentRef.getBoundingClientRect();
      const elementRect = element.getBoundingClientRect();
      const targetScrollTop = this.$refs.contentRef.scrollTop + (elementRect.top - contentRect.top) - this.scrollOffset;

      // 直接设置滚动位置，不使用平滑滚动
      this.$refs.contentRef.scrollTop = targetScrollTop;

      // 滚动结束后重置状态
      this.scrollLockTimeout = setTimeout(() => {
        this.isManualScrolling = false;
      }, 100);
    },

    /**
     * 更新当前活动标题
     * 根据滚动位置找到最接近视口顶部的标题
     * 使用距离计算来确定最合适的标题
     */
    updateActiveHeading() {
      if (!this.$refs.contentRef || this.tocItems.length === 0) return;

      const scrollTop = this.$refs.contentRef.scrollTop;
      const contentRect = this.$refs.contentRef.getBoundingClientRect();
      const viewportTop = scrollTop + this.scrollOffset;

      // 找到第一个在视口顶部以下的标题
      let currentHeading = null;
      for (let i = 0; i < this.tocItems.length; i++) {
        const item = this.tocItems[i];
        const rect = item.element.getBoundingClientRect();
        const itemTop = scrollTop + (rect.top - contentRect.top);

        if (itemTop > viewportTop) {
          // 如果当前标题已经超出视口顶部，使用前一个标题
          currentHeading = i > 0 ? this.tocItems[i - 1] : item;
          break;
        }
        // 如果是最后一个标题，使用它
        if (i === this.tocItems.length - 1) {
          currentHeading = item;
        }
      }

      // 更新活动标题
      if (currentHeading && currentHeading.id !== this.currentActiveId) {
        this.currentActiveId = currentHeading.id;
      }
    },
  },
};
</script>

<style scoped>
/* 样式保持不变 */
.rich-text-toc-container {
  display: flex;
  /* height: 100%; */
  overflow-y: hidden;
}

.toc-sidebar {
  height: 100%;
  overflow-y: auto;
  border-right: 1px solid #e5e7eb;
  padding: 1rem;
  background-color: #f9fafb;
}

.toc-title {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #374151;
}

.toc-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.toc-item {
  margin: 0.25rem 0;
  padding: 0.25rem 0;
  cursor: pointer;
  transition: all 0.2s;
  color: #6b7280;
  line-height: 1.5;
}

.toc-item:hover {
  color: #3b82f6;
  background-color: #eff6ff;
  border-radius: 0.25rem;
}

.toc-item.is-active {
  color: #2563eb;
  font-weight: 500;
  background-color: #dbeafe;
  border-radius: 0.25rem;
}

.empty-text {
  margin: 0.25rem 0;
  padding: 0.25rem 0;
  transition: all 0.2s;
  color: #6b7280;
  line-height: 1.5;
}

.content-container {
  flex: 1;
  height: 100%;
  overflow-y: auto;
  padding: 0 20px;
}

.content-wrapper {
  max-width: 800px;
  margin: 0 auto;
}
</style>
