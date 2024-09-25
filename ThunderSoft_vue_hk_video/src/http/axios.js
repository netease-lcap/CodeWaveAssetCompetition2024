
import axios from 'axios'
// 这里要export vue实例对象，才能引用
// import context from '../main.js'

/* 2.全局默认配置 */
let baseURL='/api'

// 方法一
// 配置axios的属性
// axios.defaults.timeout = 6000;    // 请求超时时间1分钟
// axios.defaults.baseURL =baseURL; // 你的接口地址
// axios.defaults.responseType="json";
// axios.defaults.withCredentials=false; //是否允许带cookie这些

/*
方法二   下面使用这种方式
创建axios实例进行配置，
*/
const Axios = axios.create({
  baseURL: baseURL, // 后台服务地址
  timeout: 600000, // 请求超时时间1分钟
  responseType: 'json',
  withCredentials: false // 是否允许带cookie这些
})

/* 3.设置拦截器 */
Axios.interceptors.request.use((config) => {
  return config
}, (error) => {
  return Promise.reject(error)
})

// 这里需要根据业务响应更改，
Axios.interceptors.response.use((res) => {
  if (res.config.direct) {
    return res.data
  }
  return Promise.resolve(res)
}, (error) => {
  return Promise.reject(error)
})

export default Axios
