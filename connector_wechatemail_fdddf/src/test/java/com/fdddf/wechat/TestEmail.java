package com.fdddf.wechat;

import com.fdddf.wechat.contact.GetUserIdByEmailRequest;
import com.fdddf.wechat.contact.GetUserIdByMobileRequest;
import com.fdddf.wechat.contact.GetUserIdResponse;
import com.fdddf.wechat.email.*;
import com.fdddf.wechat.eml.ParsedEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestEmail {

    String token;

    List<String> emailTo = new ArrayList<>();

    @Test
    void contextLoads() {
    }

    @BeforeEach
    void init() {
        String cropid = System.getenv("cropid");
        String secret = System.getenv("secret");
        token = WeiXinUtil.getAccessToken(cropid, secret);
        emailTo.add("viporg@126.com");
    }

    @Test
    public void sendNormalEmail() {
        EmailRequest request = new EmailRequest(
                new EmailRecipient(emailTo, null),
                null,
                null,
                "测试邮件",
                "测试邮件内容",
                null,
                1,
                null
        );
        System.out.println("token="+token);
        WeChatResponse resp = Compose.sendNormalEmail(request, token);
        System.out.println(resp);
        assertEquals(resp.errcode, 0);
    }

    @Test
    public void sendAttachmentEmail() {
        List<String> fileUrls = new ArrayList<>();
        fileUrls.add("https://www.zixi.org/assets/uploads/202409/20240928160103-Sqcf8q2k.jpg");
        EmailRequest request = new EmailRequest(
                new EmailRecipient(emailTo, null),
                null,
                null,
                "测试带附件邮件",
                "测试带附件邮件内容",
                null,
                1,
                fileUrls
        );
        WeChatResponse resp = Compose.sendNormalEmail(request, token);
        System.out.println(resp);
        assertEquals(resp.errcode, 0);
    }

    @Test
    public void sendScheduleEmail() {
        EmailScheduleReminder reminder = new EmailScheduleReminder(1, 15, 0, 1, 8, 1, 1, null, null, null, null);

        EmailSchedule schedule = new EmailSchedule(null, "request", null, 1727784000, 1727787600, reminder);

        EmailScheduleRequest request = new EmailScheduleRequest(
                new EmailRecipient(emailTo, null),
                null,
                null,
                "测试日程邮件",
               "测试日程邮件内容",
                schedule,
                1
        );

        WeChatResponse resp = Compose.sendScheduleEmail(request, token);
        System.out.println(resp);
        assertEquals(resp.errcode, 0);
    }

    @Disabled("Meeting Error cannot be fixed yet")
    @Test
    public void sendMeetingEmail() {
        EmailScheduleReminder reminder = new EmailScheduleReminder(1, 15, 0, 1, 8, 1, 1, null, null, null, null);
        EmailSchedule schedule = new EmailSchedule(null, "request", null, 1727784000, 1727787600, reminder);

        EmailMeetingOption option = new EmailMeetingOption(
                "123123",
                0,
                false,
                false,
                0,
                false,
                1,
                1,
                0);
        List<String> userids = new ArrayList<>();
        userids.add("admin@fdddf.com");
        EmailMeetingUsers hosts = new EmailMeetingUsers(userids);
        EmailMeetingUsers admins = new EmailMeetingUsers(userids);
        EmailMeeting meeting = new EmailMeeting(option, null, admins);

        List<String> to = new ArrayList<>();
        to.add("admin@fdddf.com");
        to.add("service@fdddf.com");
        EmailMeetingRequest request = new EmailMeetingRequest(
                new EmailRecipient(to, null),
                "测试会议邮件",
                "测试会议邮件内容",
                schedule,
                meeting,
                1
        );
        System.out.println("token="+token);
        WeChatResponse resp = Compose.sendMeetingEmail(request, token);
        System.out.println(resp);
        assertEquals(resp.errcode, 0);
    }


    @Test
    public void getInboxEmails() {
        EmailListRequest request = new EmailListRequest(1725206311L, 1727624359L, null, 100);
        System.out.println("token="+token);
        EmailListResponse resp = QueryEmail.getInboxEmails(request, token);
        System.out.println(resp);
        assertEquals(resp.errcode, 0);
    }

    @Test
    public void getEmailContent() {
        String mail_id = "mlgY3eK56xuPtEgh7UsaWGSF_RzimSh_IPfvXt9B1JDyVIZDIQzZPEKOtGgGPkjV59";
        EmailReadRequest request = new EmailReadRequest(mail_id);
        EmailReadResponse resp = QueryEmail.readEmail(request, token);
        System.out.println(resp.mail_data);
        assertEquals(resp.errcode, 0);

        ParsedEmail email = EmlUtil.parseEml(resp.mail_data);
        System.out.println(email);
    }

    ///////////////////////contacts

    @Test
    public void getUserIdByMobile() {
        String mobile = "15591851122";
        GetUserIdByMobileRequest request = new GetUserIdByMobileRequest(mobile);
        GetUserIdResponse resp = Contact.getUserIdByMobile(request, token);
        System.out.println(resp.userid);
        assertNotNull(resp.userid);
    }

    @Test
    public void getUserIdByEmail() {
        String email = "admin@fdddf.com";
        GetUserIdByEmailRequest request = new GetUserIdByEmailRequest(email, 1);
        GetUserIdResponse resp = Contact.getUserIdByEmail(request, token);
        System.out.println(resp.userid);
        assertNotNull(resp.userid);
    }

    @Test
    public void getUnreadEmails() {
        EmailUnreadRequest request = new EmailUnreadRequest("admin@fdddf.com");
        EmailUnreadResponse resp = QueryEmail.getUnreadEmails(request, token);
        assertEquals(resp.errcode, 0);
        System.out.println(resp);
    }

}
