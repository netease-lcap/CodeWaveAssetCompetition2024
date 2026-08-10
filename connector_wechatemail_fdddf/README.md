# 企业微信邮件连接器
企业微信邮件连接器 可连接企业微信邮件，可实现发送邮件、查询邮件列表及邮件内容等能力。

## 逻辑详情

### getAccessToken

获取access_token  

入参：无
出参：String access_token

### getUnreadEmails

获取指定成员邮箱当前未读邮件数量

入参：EmailUnreadRequest request, String token  
出参：EmailUnreadResponse

### readEmail

获取邮件内容 eml数据

入参：EmailReadRequest request, String token  
出参：EmailReadResponse

### getInboxEmails

获取应用的收件箱邮件列表

入参：EmailListRequest request, String token  
出参：EmailListResponse

### parseEml

解析eml邮件内容

入参：String emlContent  
出参：ParsedEmail

#### ParsedEmail
```
subject: 主题
from: 发送人
toRecipients: 收件人 List<String>
sendDate: 发送时间
attachments: 附件列表 List<EmlAttachment>
```

#### EmlAttachment
```
fileName: 文件名
mimeType: 文件类型
content: base64编码的文件内容
```

示例：

```
ParsedEmail{subject='Re: 测试带附件邮件', from=xxxx@126.com', toRecipients=[youjianzhushou@xxxx.com], sentDate=2024-09-29, body='Thanks mike, I’m black

> On Sep 29, 2024, at 23:15, 邮件助手 <youjianzhushou@xxxx.com> wrote:
> 
> 测试带附件邮件内容<20240928160103-Sqcf8q2k.jpg>

', attachments=[]}

```

### sendNormalEmail

发送普通邮件

入参：EmailRequest request, String token  
出参：WeChatResponse

### sendMeetingEmail

发送会议邮件

入参：EmailMeetingRequest request, String token  
出参：WeChatResponse

### sendScheduleEmail

发送日程邮件

入参：EmailScheduleRequest request, String token  
出参：WeChatResponse


### getUserIdByMobile

根据手机号获取userid

入参：GetUserIdByMobileRequest request, String token 
出参：GetUserIdResponse

### getUserIdByEmail

根据邮箱获取userid

入参：GetUserIdByEmailRequest request, String token  
出参：GetUserIdResponse


## 使用步骤说明

1.  应用集成中导入连接器 “企业微信邮件连接器”
2.  添加连接器，填写参数 cropid、secret , 参考 https://developer.work.weixin.qq.com/document/10013
3.  逻辑调用示例截图



## 应用演示链接

[使用了本依赖库的制品应用链接]
