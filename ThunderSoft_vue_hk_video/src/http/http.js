/**
 * 封装axios ，通过回调方式的http调用工具类
 */
import Axios from './axios.js'

const http = {
  get (url, data, successCallback, faildCallback) {
    this.config('get', url, data, successCallback, faildCallback)
  },
  post (url, data, successCallback, faildCallback) {
    this.config('post', url, data, successCallback, faildCallback)
  },
  put (url, data, successCallback, faildCallback) {
    this.config('put', url, data, successCallback, faildCallback)
  },
  delete (url, data, successCallback, faildCallback) {
    this.config('delete', url, data, successCallback, faildCallback)
  },
  request (config, successCallback, faildCallback) {
    Axios.request({
      method: config.method,
      url: config.url,
      data: config.data,
      headers: config.headers
    }).then(successCallback).catch()
  },

  config (method, url, data, successCallback, faildCallback) {
    const opt = {
      method: method,
      url: url,
      data: data
    }
    // debugger
    return new Promise((resolve, reject) => {
      Axios.request(opt).then((response) => {
        if (successCallback) {
          successCallback(response)
        }
      }).catch(error => {
        if (faildCallback) {
          faildCallback(error)
        } else {
          console.log('回调异常')
        }
      })
    })
  }
}

export default http
