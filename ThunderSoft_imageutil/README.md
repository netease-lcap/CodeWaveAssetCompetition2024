# 依赖库名称

具体描述调用腾讯云对象存储获取图片信息后，在通过压缩图片变成10-200kb文件后再生产base64

## 逻辑详情

## 使用步骤说明

1. 应用引用依赖库
   <dependency>
   <groupId>com.qcloud</groupId>
   <artifactId>cos_api</artifactId>
   <version>5.6.155</version>
   <exclusions>
   <exclusion>
   <groupId>com.tencentcloudapi</groupId>
   <artifactId>tencentcloud-sdk-java-common</artifactId>
   </exclusion>
   </exclusions>
   </dependency>
   </dependencies>
2. 配置应用配置参数
3. 逻辑调用示例截图
 

## 应用演示链接

使用了本依赖库的应用的链接。

