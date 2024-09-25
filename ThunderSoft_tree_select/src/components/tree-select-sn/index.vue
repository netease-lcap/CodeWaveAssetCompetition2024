<template>
  <div class="all-container">
    <div class="transfer-container">
      <!-- 左侧区域 -->
      <div class="left-panel">
        <el-tabs v-model="activeTab" :before-leave="handleBeforeLeave" @tab-click="handleTabChange" class="tabs">
          <el-tab-pane v-for="(tab, index) in tabs" :key="index" :label="tab" :name="tab">
          </el-tab-pane>
        </el-tabs>
        <div class="tree-container">
          <el-tree
            :data="treeData"
            :props="defaultProps"
            :expand-on-click-node="false"
            :load="loadNode"
            lazy
            node-key="id"
            ref="availableTree"
            @check-change="handleAvailableCheckChange"
            show-checkbox
          >
            <template #default="{ node, data }">
              <span>
                <template v-if="data[avatarKey] === avatarValue">
                  <img v-if="data[avatar]" :src="data[avatar]" alt="头像" class="avatar">
                  <i v-else class="el-icon-user" style="margin-right: 5px;"></i>
                </template>
                <template v-else>
                  <i class="" style="margin-right: 5px;"></i>
                </template>
                {{ data[label] }}
              </span>
            </template>
          </el-tree>
        </div>
      </div>

      <!-- 中间区域 -->
      <div class="center-panel">
        <el-button type="primary" class='el-button' @click="addSelected" :disabled="selectedAvailable.length === 0">添加 ->></el-button>
        <el-button type="danger" class='el-button' @click="removeSelected" :disabled="checkedItems.length === 0"><<- 删除</el-button>
        <el-button type="primary" class='el-button' @click="addAll">添加全部</el-button>
        <el-button type="primary" class='el-button' @click="clearSelected" :disabled="selectedItems.length === 0">清空已选</el-button>
      </div>

      <!-- 右侧区域 -->
      <div class="right-panel selected-list">
        <div class='selectTitle'>已选列表</div>
        <div class="tree-list">
          <ul>
            <li v-for="item in selectedItems" :key="item.id">
              <el-checkbox
                v-model="checkedItems"
                :label="item.id"
              >
                <span>
                  <template v-if="item[avatarKey] === avatarValue">
                    <img v-if="item[avatar]" :src="item[avatar]" alt="头像" class="avatar">
                    <i v-else class="el-icon-user icon"></i>
                  </template>
                  <template v-else>
                    <i class=""></i>
                  </template>
                  {{ item[label]}}
                </span>
              </el-checkbox>
            </li>
          </ul>
        </div>
      </div>
    </div>
    <div style='text-align: right;width: 100%'>
      <el-button type="primary" class='el-button' @click="handleConfirm" :disabled="selectedItems.length === 0" >提交</el-button>
    </div>
  </div>
</template>

<script>
import { Tabs, TabPane, Tree, Button, Checkbox } from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';

export default {
  name: 'TreeSelectSn',
  components: {
    ElButton: Button,
    ElCheckbox: Checkbox,
    ElTabs: Tabs,
    ElTabPane: TabPane,
    ElTree: Tree
  },

  props: {

    tabs: {
      type: Array,
      default: () => ['选项卡一', '选项卡二']
    },

    treeDataFromProps: {
      type: Array,
      default: () => [
        { id: 1, label: '公司A', type: '1', leaf: false, children:  [
            { id: 11, label: '子部门', type: '1', leaf: false, children: [] },
            { id: 12, label: '员工', type: '2', leaf: true, avatar: 'https://picx.zhimg.com/80/v2-1a1ef8a54b7ab6637d11a6b0caa0f2ac_720w.webp?source=2c26e567', children: [] }
          ] },
        { id: 2, label: '公司B', type: '1', leaf: false, children:  [
            { id: 21, label: '子部门', type: '1', leaf: false, children: [] },
            { id: 22, label: '员工', type: '2', leaf: true, avatar: '', children: [] }
          ] }
      ]
    },
    childrenFromProps: {
      type: Array,
      default: () => [
      ]
    },

    label: {
      type: String,
      default: () => 'label'
    },

    leaf: {
      type: String,
      default: () => 'leaf'
    },
    avatar: {
      type: String,
      default: () => 'avatar'
    },
    avatarKey: {
      type: String,
      default: () => 'type'
    },
    avatarValue: {
      type: String,
      default: () => '2'
    },
    isFilter: {
      type: Boolean,
      default: () => false
    },
    filterKey: {
      type: String,
      default: () => 'type'
    },
    filterValue: {
      type: String,
      default: () => '2'
    },



  },
  data() {
    return {
      treeData: []  ,
      newChildren:this.childrenFromProps,
      activeTab: this.tabs[0],
      currentTab: this.tabs[0],
      selectedAvailable: [],
      selectedItems: [],
      checkedItems: [],
      isSwitchTab:true,
      defaultProps: {
        isLeaf: this.leaf ,
      }
    };
  },
  watch: {
    treeDataFromProps(newValue) {
      this.treeData = newValue;
    },
    childrenFromProps(newValue) {
      this.newChildren = newValue;
    },
    selectedItems(newValue,old) {
      if(newValue.length === 0){
        this.isSwitchTab = true;
      }
      if(newValue.length !== old.length && newValue.length>0){
        this.isSwitchTab = false;
      }
    },
  },
  created() {
    // 初始化 treeData
   // this.treeData = JSON.parse(JSON.stringify(this.treeDataFromProps));
    this.treeData = this.treeDataFromProps;
    this.treeConfig = this.treeConfigFromProps;
  },
  methods: {
    handleBeforeLeave(newTab, oldTab) {
      if (!this.isSwitchTab) {
        // 如果条件不满足，阻止切换选项卡
        alert("请先提交已选数据");
        return false;
      }
      return true;
    },

    handleAvailableCheckChange() {
      this.selectedAvailable = this.$refs.availableTree.getCheckedKeys();
    },
    addSelected() {
      const selectedKeys = this.selectedAvailable;
      const selectedNodes = this.findNodesById(this.treeData, selectedKeys);
      const existingIds = this.selectedItems.map(item => item.id);
      const newNodes = selectedNodes.filter(node => !existingIds.includes(node.id));
      this.selectedItems = [...this.selectedItems, ...newNodes];
    },
    addAll() {
      this.selectedItems = [];
      const allNodes = this.getAllNodes(this.treeData);
      this.selectedItems = [...this.selectedItems, ...allNodes];
    },
    getAllNodes(nodes) {
      let result = [];
      nodes.forEach(node => {

        if(this.isFilter){
          if ( node[this.filterKey] === this.filterValue) {
            result.push(node);
          }
        }else{
          result.push(node);
        }

        if (node.children && node.children.length > 0) {
          result = result.concat(this.getAllNodes(node.children));
        }
      });
      return result;
    },
    removeSelected() {
      this.selectedItems = this.selectedItems.filter(item => !this.checkedItems.includes(item.id));
      this.checkedItems = [];
    },
    clearSelected() {
      this.selectedItems = [];
    },
    findNodesById(nodes, ids) {
      let result = [];
      nodes.forEach(node => {

        if (ids.includes(node.id)){
          if(this.isFilter){
            if ( node[this.filterKey] === this.filterValue) {
              result.push(node);
            }
          }else{
            result.push(node);
          }
        }

        if (node.children) {
          result = [...result, ...this.findNodesById(node.children, ids)];
        }
      });
      return result;
    },

    loadNode(node, resolve) {
      const currentTabData = this.treeData;
      if (node.level === 0) {
        resolve(currentTabData);
      } else {
        const children = node.data.children;
        if (children.length > 0) {
          resolve(children);
        } else {
          this.handleLoadChildren(node.data.id);
          setTimeout(() => {
            const newChildren = this.newChildren;
            this.updateTreeData(node.data.id, newChildren);
            resolve(newChildren);
          }, 500);
        }
      }
    },
    handleLoadChildren(parentId) {
      const activeTabName = this.currentTab;
     const formData ={
        'parentId':parentId,
        'activeTab': activeTabName
      }
      this.$emit('handleloadchildren', { formData });
    },
    updateTreeData(id, newChildren) {
      const findNode = (nodes) => {
        for (const node of nodes) {
          if (node.id === id) {
            node.children = newChildren;
            return true;
          }
          if (node.children && node.children.length > 0) {
            if (findNode(node.children)) {
              return true;
            }
          }
        }
        return false;
      };
      findNode(this.treeData);
    },

    handleConfirm() {
      const activeTabName = this.currentTab;
      const formData ={
        'activeTab' : activeTabName,
        'selected':this.selectedItems
      }
      this.$emit('handleconfirm', { formData });
      setTimeout(() => {
        this.isSwitchTab = true;
      }, 1000);
    },

    handleTabChange(tab) {
      if(this.isSwitchTab){
        const formData ={
          'isSwitchTab':this.isSwitchTab,
          'activeTab':tab.name
        }
        this.currentTab = tab.name;
        //单个字符串可以正常传递
        this.$emit('handletabchange', { formData });
        this.checkedItems = [];
        this.selectedItems = [];
      }

    }
  }
};
</script>

<style scoped>
.transfer-container {
  display: flex;
  justify-content: space-between;
}

.all-container {
  width: 60vw;
  border: 2px solid black; /* 2px 宽的黑色实线边框 */
  padding: 20px; /* 内边距 */
  border-radius: 5px; /* 可选：给边框加上圆角 */
  position: relative;
}

.left-panel, .right-panel {
  width: 45%;
  padding: 10px;
  box-sizing: border-box;
}

.left-panel {
  border-right: 1px solid #e0e0e0;
}

.right-panel {
  border-left: 1px solid #e0e0e0;
  background-color: #f9f9f9;
}

.center-panel {
  padding: 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
}

.tabs {
  margin-bottom: 10px;
}

.tree-container {
  margin-bottom: 20px;
}

.el-button {
  margin: 5px;
}

.selected-list {
  background-color: #fff;
}
.selectTitle{
  height: 40px;
  line-height: 40px;
  position: relative;
}
.selectTitle::after{
  content: "";
  position: absolute;
  left: 0;
  bottom: 0;
  width: 100%;
  height: 2px;
  background-color: #E4E7ED;
  z-index: 1;
}

.tree-list ul {
  list-style: none;
  padding: 0;
}

.tree-list li {
  margin: 5px 0;
}

.avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  margin-right: 3px;
}

.icon {
  font-size: 16px;
}

.inner-box {
  height: 30px;          /* 高度 30px */
  /* 绝对定位 */
  bottom: 0;             /* 固定在外层 div 的底部 */
  left: 0;               /* 水平从左边开始 */
}
</style>
