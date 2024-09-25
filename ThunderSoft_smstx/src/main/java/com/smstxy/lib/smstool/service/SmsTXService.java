package com.smstxy.lib.smstool.service;

import com.smstxy.lib.smstool.config.SmsConfig;
import com.smstxy.lib.smstool.structure.PhoneParam;
import com.netease.lowcode.core.annotation.NaslLogic;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class SmsTXService {
    private static final Logger log = LoggerFactory.getLogger(SmsTXService.class);

    @Autowired
    private SmsConfig smsConfig;


    @NaslLogic(enhance = false)
    public  Map<String, List<String>> sendSms(List<String> phone, List<String> templateParam, String smsSdkAppId,
                                                    String signName, String templateId) {
//        log.info ("手机号列表："+phone.toString ());
        ArrayList<String> phoneSuccess = new ArrayList<>();
        ArrayList<String> phoneError = new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential(smsConfig.getSecretId(), smsConfig.getSecretKey());
            log.error("腾讯sdk异常--smsConfig.getSecretId():-----"+smsConfig.getSecretId());
            log.error("腾讯sdk异常--smsConfig.getSecretKey():---------"+smsConfig.getSecretKey());

            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            SmsClient client = new SmsClient(cred, smsConfig.getRegion(), clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet = phone.toArray(new String[phone.size()]);
            req.setPhoneNumberSet(phoneNumberSet);
            req.setSmsSdkAppId(smsSdkAppId);
            req.setSignName(signName);
            req.setTemplateId(templateId);
            String[] templateParamSet = templateParam.toArray(new String[templateParam.size()]);
            req.setTemplateParamSet(templateParamSet);
            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);
            SendStatus[] sendStatusSet = resp.getSendStatusSet();
            List<SendStatus> sendStatuses = Arrays.asList(sendStatusSet);
            sendStatuses.forEach(sendStatus -> {
                if ("Ok".equals(sendStatus.getCode())) {
                    phoneSuccess.add(sendStatus.getPhoneNumber());
                    log.info("发送成功手机号：" + sendStatus.getPhoneNumber());
                } else {
                    phoneError.add(sendStatus.getPhoneNumber());
                    log.info("发送失败手机号：" + sendStatus.getPhoneNumber());
                }
            });
            map.put("success", phoneSuccess);
            map.put("error", phoneError);
        } catch (TencentCloudSDKException e) {
            log.error("腾讯sdk异常--TencentCloudSDKException"+e.toString());
        } catch (Exception e) {
            log.error("未知异常--Exception"+e.toString());
        }
        return map;
    }

    @NaslLogic(enhance = false)
    public Map<String, List<String>> batchSendSms(List<PhoneParam> phoneParam, String smsSdkAppId,
                                                  String signName, String templateId) {
//        log.info ("手机号列表："+phone.toString ());
        ArrayList<String> phoneSuccess = new ArrayList<>();
        ArrayList<String> phoneError = new ArrayList<>();
        Map<String, List<String>> map = new HashMap<>();
        // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
        // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
        // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
        Credential cred = new Credential(smsConfig.getSecretId(), smsConfig.getSecretKey());

        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("sms.tencentcloudapi.com");
        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        // 实例化要请求产品的client对象,clientProfile是可选的
        SmsClient client = new SmsClient(cred, smsConfig.getRegion(), clientProfile);
        // 实例化一个请求对象,每个接口都会对应一个request对象
//            工具手机号实例化多个请求对象
        phoneParam.forEach(phoneSin -> {
            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet = {phoneSin.getPhone()};
            req.setPhoneNumberSet(phoneNumberSet);
            req.setSmsSdkAppId(smsSdkAppId);
            req.setSignName(signName);
            req.setTemplateId(templateId);
            req.setTemplateParamSet(phoneSin.getParam().toArray(new String[phoneSin.getParam().size()]));
            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = null;
            try {
                resp = client.SendSms(req);
            } catch (TencentCloudSDKException e) {
                e.printStackTrace();
            }
            SendStatus[] sendStatusSet = resp.getSendStatusSet();
            List<SendStatus> sendStatuses = Arrays.asList(sendStatusSet);
            sendStatuses.forEach(sendStatus -> {
                if ("Ok".equals(sendStatus.getCode())) {
                    phoneSuccess.add(sendStatus.getPhoneNumber());
                    log.info("发送成功手机号：" + sendStatus.getPhoneNumber());
                } else {
                    phoneError.add(sendStatus.getPhoneNumber());
                    log.info("发送失败手机号：" + sendStatus.getPhoneNumber());
                }
            });
            map.put("success", phoneSuccess);
            map.put("error", phoneError);
        });
        return map;
    }
}
