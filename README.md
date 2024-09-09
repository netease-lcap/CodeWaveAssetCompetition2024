# 网易低代码平台依赖库开源仓库
## CodeWaveSummerCompetition2024
网易低代码开发者活动-低代码大赛2024
![WechatIMG1229](https://github.com/user-attachments/assets/db3920be-9574-4db1-ad26-2491f3a5fd88)

欢迎开发者们参加2024网易低代码大赛，活动官网介绍：https://lcapcompetition.codewave.163.com/CodeWaveCompetition/schedule

参赛命题:


—赛事规则

1、参赛机制：

  1）一个命题可允许最多3名开发者参加，开发者需要在命题需求发布帖子下方评论区进行报名领取需求；

  2）一个命题涉及到多个资产提交，根据资产的质量度进行梯队评估激励，每个资产会有一个激励池总额，最佳质量资产（第一名）将获得85%资产池的激励，剩下15%作为其他资产激励；

  3）一个开发者最多同时参加三个命题需求领取，需求开发完提交验收通过后，会释放领取额度继续申领新需求；

  4）一个开发者在活动期间超过三次占用命题参加额度，但未提交资产，则后续不可以参与资产命题需求的申领（自由资产提供不限制）；

2、验收机制
![image](https://github.com/netease-lcap/CodeWaveAssetCompetition2024/assets/158463965/1b02393f-3df9-41f5-a187-64e71f7cef9f)

3、赛事激励：

激励内容：购物卡
![WechatIMG1230](https://github.com/user-attachments/assets/27acff07-0f7f-4557-a05d-fc3a9807d6fd)


严选平台优质商品推荐：https://docs.popo.netease.com/lingxi/d493efea289d4cafbf38ebde626d06f6



## README
### 项目名称
lcap_library_nexus是一个低代码平台依赖库开源仓库，用于存放低代码平台前端依赖库和服务端依赖库。
* 服务端扩展依赖库是指开发者使用Java编写的平台扩展能力库，用于满足服务端定制化需求和功能扩展。
* 前端依赖库主要提供扩展组件和前端自定义逻辑编写能力，满足前端定制化需求和功能扩展。

#### 官方依赖库

| 依赖库名称 | 依赖库类型 | 文件夹名称 | 描述                  | 资产市场地址                                                                                                                            |
| ------- | ------- | ------- |---------------------|-----------------------------------------------------------------------------------------------------------------------------------|
| excel解析 | 后端  | excel-parser   | 自动解析excel文档到IDE数据表格 | https://community.codewave.163.com/CommunityParent/CodeWareMarketLibraryDetail?id=2840513069648896&isLatest=false&isClassics=true |
| http请求客户端 | 后端   | httpclient   | 扩展平台原有api功能         | https://community.codewave.163.com/CommunityParent/CodeWareMarketLibraryDetail?id=2883851125296640&isLatest=false&isClassics=false 
| 音频依赖库 | 前端   | cw_audio_library   | 提供音频播放/录制功能         | 暂无
| 签名板依赖库 | 前端   | cw_signature_view   | 提供组件签名能力        | 暂无 
| 大文件上传依赖库 | 前端   | cw_large_file_uploader   | 提供分片上传能力        | 暂无
| markdown依赖库 | 前端   | cw_markdown   | 提供markdown编辑/预览能力        | 暂无   
| 数据脱敏依赖库 | 前端   | cw_desensitization   | 提供前端数据脱敏能力        | 暂无
| 全局水印依赖库 | 前端   | cw_watermark   | 提供页面全局水印的能力        | 暂无                                                                                                                                |
| redis依赖库 | 后端   | redis-template-tool   |支持各种模式的redis集群         | https://community.codewave.163.com/CommunityParent/CodeWareMarketLibraryDetail?id=2849760328948736&isLatest=false&isClassics=false
| dubbo依赖库 | 后端   | dubbo-tool   | 用于dubbo服务发现         | https://community.codewave.163.com/CommunityParent/CodeWareMarketLibraryDetail?id=2811501029676800&isLatest=false&isClassics=false
|EasyExcel导出数据依赖库| 后端   |  EasyExcel |支持各种模式的redis集群         | 暂无
|map有序化依赖库 | 后端   | sort-map |1. CodeWave当前支持的map都是无序的hashmap，这导致有些业务场景无法满足。所以这里提供了一个转为有序map的依赖库。2. 当前支持的Json都是无序的，这里提供了一个调整json顺序的方法，满足调用第三方客户接口的json顺序要求。         | https://community.codewave.163.com/CommunityParent/CodeWareMarketLibraryDetail?id=2849760328948736&isLatest=false&isClassics=false
|Freemaker依赖库| 后端   |  freemarker-tool |支持模板引擎         | https://community.codewave.163.com/CommunityParent/CodeWareMarketLibraryDetail?id=2884580661931520&isLatest=false&isClassics=false|
|防重放依赖库|后端|custon-api-filter|支持配置自定义uri的接口的防重放功能|https://community.codewave.163.com/CommunityParent/CodeWareMarketLibraryDetail?id=2890986475426048&isLatest=false&isClassics=true|
|runtime_tasks运行时定时任务依赖库|后端|runtime_tasks_ext|支持运行时定时任务配置||


### 贡献指南
[开发提交作品路径说明](%E5%BC%80%E5%8F%91%E6%8F%90%E4%BA%A4%E4%BD%9C%E5%93%81%E8%B7%AF%E5%BE%84%E8%AF%B4%E6%98%8E.md)

### 依赖库开发规范指导
[依赖库开发规范指导](%E4%BE%9D%E8%B5%96%E5%BA%93%E5%BC%80%E5%8F%91%E8%A7%84%E8%8C%83%E6%8C%87%E5%AF%BC.md)

### 版本管理
项目初始版本是 1.0.0。
项目进行 Bug 修正时，最后一位加 1，比如 1.0.1。
项目有新特性发布时，中间一位数加 1，同时最后一位复位为 0，比如 1.1.0。
项目有重大特性发布，同时结构可能不向下兼容时，第一位数字加 1，其他位数复位为0，比如2.0.0。
依赖库做任何代码修改时，都需要更新版本号。

### 测试用例
建议测试用例覆盖率达到70%以上。

### 许可证
本项目遵循网易贡献许可协议 - 有关详细信息，请参阅 [LICENSE](./LICENSE) 文件。



