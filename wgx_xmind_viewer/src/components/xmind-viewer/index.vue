  <template>
    <div class="strata-externum-xmind-container" >
      <div v-if="isComponentVisible" id="xmind-container"  > </div>
      <img v-if="showLoading" src="https://assets.xmind.cn/www/assets/images/share/xmind-b6f6b3ca68.svg" class="xmind-logo">
      <div v-if="showLoading" class="loading"></div>
    </div>

  </template>

<script>
export default {
  props: {
    fileUrl: {
      type: String,
      //default: 'http://localhost:8008/中心主题.xmind'
    }
  },
  data() {
    return {
      isComponentVisible: true,
      showLoading: true,
      viewer: null,
    }
  },
  computed: {
    isIDE() {
      return this.$env && this.$env.VUE_APP_DESIGNER;
    },
  },
  watch: {
    fileUrl: {
      immediate: false,
      handler: 'loadXMind'
    }
  },
  mounted() {
    this.loadXMind()
  },
  methods: {
    async loadXMind() {

      this.showLoading = true;
      this.isComponentVisible = true;

      if (this.isIDE) return;

      if (!this.fileUrl || this.fileUrl.trim() === '' || !this.fileUrl.endsWith('.xmind')) {
        this.onError(new Error(`fileUrl'${this.fileUrl} '无效参数,原因：为空或null，或者只包含空格，或者文件后缀不是xmind`));
        return;
      }

      let file;
      try {
        const response = await fetch(this.fileUrl);
        if (!response.ok) {
          this.onError(new Error('加载xmind文件失败: 通过' + this.fileUrl + ' ' + `获取xmind文件失败! HTTP error! status: ${response.status}`));
          return;
        }
        file = await response.arrayBuffer();

        if (!file) {
          this.onError(new Error('加载xmind文件失败: 从' + this.fileUrl + '获取的xmind文件为空!'));
          return;
        }

        if (!this.viewer) {
          const { XMindEmbedViewer } = await import('xmind-embed-viewer');

          // 确保 DOM 元素是空的
          const container = document.getElementById('xmind-container');
          if (container) {
            container.innerHTML = '';
          }
          this.viewer = new XMindEmbedViewer({
            el: '#xmind-container',
            region: 'cn'
          });
          this.viewer.setStyles({
            width: '100%',
            height: '100%'
          });
          this.viewer.addEventListener('map-ready', this.onMapReady);
          this.viewer.addEventListener('error', this.onError);
        }

        this.viewer.load(file);
      } catch (err) {
        this.onError(err);
      }

    },
    onMapReady() {
      this.showLoading = false
    },
    onError(err) {
      if (this.viewer) {
        // 移除事件监听器
        this.viewer.removeEventListener('map-ready', this.onMapReady);
        this.viewer.removeEventListener('error', this.onError);
        // 清空 DOM 元素
        const container = document.getElementById('xmind-container');
        if (container) {
          container.innerHTML = '';
        }
        // 设置为 null
        this.viewer = null;
      }
      this.isComponentVisible = false;
      this.showLoading = true;
      console.error('加载xmind文件出错:', err)
    }
  },
}
</script>

<style scoped>
.strata-externum-xmind-container {
  display: flex;
  height: 100% ;
  width: 100% ;
  min-height: 300px;
  align-items: center;
  justify-content: center;
  position: relative; /* 确保父元素是相对定位 */
  border: 1px dashed #ccc;
}

#xmind-container {
  display: flex;
  flex: 1; /* 让元素填充剩余空间 */
  align-items: center;
  justify-content: center;
  position: relative;
  height: 100% !important; /* 确保高度继承 */
}

.loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 6px;
  height: 10px;
  background-color: #000;
  animation: rectangle infinite 1s ease-in-out -0.2s;
}

.loading:before,
.loading:after {
  position: absolute;
  width: 6px;
  height: 10px;
  content: "";
  background-color: #000;
}

.loading:before {
  left: -14px;
  animation: rectangle infinite 1s ease-in-out -0.4s;
}

.loading:after {
  right: -14px;
  animation: rectangle infinite 1s ease-in-out;
}

@keyframes rectangle {

  0%,
  80%,
  100% {
    height: 20px;
    box-shadow: 0 0 #000;
  }

  40% {
    height: 30px;
    box-shadow: 0 -20px #000;
  }
}

.xmind-logo {
  position: absolute;
  top: 14px;
  left: 14px;
  height: auto;
}
</style>
