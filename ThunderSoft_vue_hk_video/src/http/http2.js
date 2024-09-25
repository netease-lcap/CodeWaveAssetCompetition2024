/**
 * 封装axios，直接返回axios请求对象，保留了axios的操作方式
 */
import Axios from './axios.js'

const http = {
  get (url, data) {
    return this.config('get', url, data)
  },
  post (url, data) {
    return this.config('post', url, data)
  },
  put (url, data) {
    return this.config('put', url, data)
  },
  delete (url, data) {
    return this.config('delete', url, data)
  },
  request (config) {
    return Axios.request({
      method: config.method,
      url: config.url,
      data: config.data,
      headers: config.headers
    })
  },
  config (method, url, data) {
    const opt = {
      method: method,
      url: url,
      data: data
    }
    // debugger
    return Axios.request(opt)
  }
}

export default http
