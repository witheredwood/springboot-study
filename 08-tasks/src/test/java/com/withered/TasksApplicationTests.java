package com.withered;

import com.sun.xml.internal.ws.api.message.Attachment;
import com.sun.xml.internal.ws.api.message.AttachmentSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 *
 */
@SpringBootTest
class TasksApplicationTests {
    @Autowired
    JavaMailSenderImpl mailSender;

    // 简单邮件测试
    @Test
    void simpleMail() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("这是一封简单邮件的主题");  // 主题
        message.setText("这是测试简单邮件发送的正文内容哦~~~");  // 正文
        message.setFrom("906185091@qq.com");  // 发件人
        message.setTo("906185091@qq.com");  // 收件人
        mailSender.send(message);
    }

    // 复杂邮件测试
    @Test
    void complexMail() throws MessagingException {
        MimeMessage mimeMessage =  mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject("这是一封复杂邮件的主题");  // 主题
        helper.setText("<p style='color:red'>这是测试复杂邮件发送的正文内容哦~~~</p>", true);  // 正文
        // 附件
        helper.addAttachment("1.jpg", new File("D:\\Desktop\\图片\\头像1.jpg"));
        helper.addAttachment("2.jpg", new File("D:\\Desktop\\图片\\头像2.jpg"));
        helper.setFrom("906185091@qq.com");  // 发件人
        helper.setTo("906185091@qq.com");  // 收件人
        mailSender.send(mimeMessage);
    }


    /**
     * @param subject
     * @param text
     * @param isHtml : 是否是html格式
     * @param from
     * @param to
     * @throws MessagingException
     */
    public void sendMail(String subject, String text, boolean isHtml, String from, String to) throws MessagingException {
        MimeMessage mimeMessage =  mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, isHtml);
        helper.setSubject(subject);  // 主题
        helper.setText(text, true);  // 正文
        // 附件
        helper.addAttachment("1.jpg", new File("D:\\Desktop\\图片\\头像1.jpg"));
        helper.addAttachment("2.jpg", new File("D:\\Desktop\\图片\\头像2.jpg"));
        helper.setFrom(from);  // 发件人
        helper.setTo(to);  // 收件人
        mailSender.send(mimeMessage);
    }

    @Test
    void contextLoads() {
    }

}
