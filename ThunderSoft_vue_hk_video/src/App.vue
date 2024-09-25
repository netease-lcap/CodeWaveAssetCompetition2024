<template>
  <div id="player"></div>
</template>
<script setup>
import {onMounted, reactive, ref} from 'vue'

const player = ref();
onMounted(() => {
  init();
  play();
});

const init = () => {
  player.value = new window.JSPlugin({
    // 需要英文字母开头 必填
    szId: 'player',
    // 必填,引用H5player.min.js的js相对路径
    szBasePath: '/nginx/h5play',
    // 当容器div#play_window有固定宽高时，可不传iWidth和iHeight，窗口大小将自适应容器宽高
    iWidth: '100vw',
    iHeight: '100vh',
    // 分屏播放，默认最大分屏4*4
    iMaxSplit: 16,
    iCurrentSplit: 1,
    // 样式
    oStyle: {
      border: '#343434',
      borderSelect: '#FFCC00',
      background: '#000'
    }
  })

}

/**
 *
 * 播放
 */
const play = () => {
  var url = window.location.toString();
  console.log(url)
  var arrObj = url.split("?");
  if (arrObj.length <= 1) {
    return;
  }
  var arr = arrObj[1].split("=");
  console.log(arr[1])
  const param = {
    playURL: arr[1],
    // 1：高级模式  0：普通模式，高级模式支持所以
    mode: 1
  }
  player.value.JS_Play(arr[1], param, 0).then(() => {
        console.log('播放成功')
      },
      (err) => {
        console.log('播放失败')
      })

}
</script>



