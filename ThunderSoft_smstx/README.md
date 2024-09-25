# ShortUrl
smsx是一个发送短信的依赖库，提供了单个号码发送短信验证码和多个号码发送不同的消息内容2种方法


## 逻辑详情

### sendSms

单个号码发送验证码

入参：
* List<String> phone          手机号
* List<String> templateParam   随机6位验证码
* String      smsSdkAppId     应用ID
* String      signName        验证码签名名称
* String      temlateId         验证码模板ID


出参： 

### batchSendSms

批量发送短信消息

入参：
* 	String      smsSdkAppId     应用ID
*	String      signName        验证码签名名称
*	String      temlateId         验证码模板ID
*	List<String> phoneParam      手机号对应的文本内容


出参： 


## 使用步骤说明

1.  应用引用依赖库
2.  配置应用配置参数 （无需配置）
3.  逻辑调用示例截图

参考文档

## 应用演示链接

[使用了本依赖库的制品应用链接]
