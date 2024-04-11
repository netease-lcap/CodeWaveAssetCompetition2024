# httpclient

简单http调用客户端

# 接口详情
## LCAPHttpClient.exchange 数据格式为非form
入参：
- url: 请求地址
- httpMethod: 请求方法
- header: 请求头
- body: 请求体（Map<String,String>格式，不支持form）

出参：第三方返回完整信息的String格式

## LCAPHttpClient.exchangeWithoutUriEncode 数据格式为非form，url不编码
入参：
- url: 请求地址
- httpMethod: 请求方法
- header: 请求头
- body: 请求体（Map<String,String>格式，不支持form）

出参：第三方返回完整信息的String格式

## LCAPHttpClient.exchangeAllBodyType 数据格式为form。支持证书校验忽略
入参：
- RequestParamAllBodyType：
    - url: 请求地址
    - httpMethod: 请求方法
    - header: 请求头
    - body: 请求体（String格式，支持form，不支持文件。示例：a=1&b=cc&d=aasd1231）
    - isIgnoreCrt:是否忽略证书校验
    -
出参：第三方返回完整信息的String格式

## LCAPHttpClient.exchangeCrt 数据格式为非form。支持证书校验忽略
入参：
- requestParam：
    - url: 请求地址
    - httpMethod: 请求方法
    - header: 请求头
    - body: 请求体（Map<String,String>格式，不支持form）
    - isIgnoreCrt:是否忽略证书校验
    - 
出参：第三方返回完整信息的String格式

## LCAPHttpClient.downloadFileUploadNos 下载文件，并上传到nos
入参：
- fileUrl: 请求地址
- header: 请求头
默认get请求，body为空

出参：上传到nos的文件地址

## LCAPHttpClient.uploadNosExchange 上传文件到nos，并调用第三方接口
入参：
- fileUrl: 文件地址（ide上传文件后返回，不包括域名）
- requestUrl: 当前请求地址（前端逻辑中使用js代码块window.location.href获取，目的是获取当前应用的域名）
- requestParam：
  - url: 请求地址
  - httpMethod: 请求方法
  - header: 请求头
  - body: 请求体（Map<String,String>格式，仅支持form）
  - isIgnoreCrt:是否忽略证书校验

出参：第三方返回完整信息的String格式