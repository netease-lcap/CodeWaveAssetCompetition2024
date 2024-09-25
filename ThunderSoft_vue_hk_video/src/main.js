import './assets/main.css'

import {createApp} from 'vue'
import App from './App.vue'
import Vant from 'vant';
// 2. 引入组件样式
import 'vant/lib/index.css';
createApp(App).use(Vant).mount('#app')


