package com.aofei.utils;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * 发送邮件
 * @auther Tony
 * @create 2018-11-16 13:02
 */
@Component
public class SendMailUtil {

    @Value("#{propertiesReader['mail.from.name']}")
    private String mailFromName;

    @Value("#{propertiesReader['mail.active.url']}")
    private String mailActiveUrl;

    @Value("#{propertiesReader['mail.register.url']}")
    private String mailRegisterUrl;

    @Value("#{propertiesReader['mail.smtp.from']}")
    private String mailSmtpFrom;

    @Autowired
    private  JavaMailSenderImpl javaMailSender;

    /**
     *
     * @param toEmail 收件人
     * @param subject 邮件主题
     * @param content 邮件内容
     * @param filePath 附件路径
     * @throws MessagingException
     */
    public  void sendHtmlMail(String toEmail, String subject,
                                    String content, String filePath) throws MessagingException, IOException {
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "UTF-8");
        // 设置收件人
        messageHelper.setTo(toEmail);
        messageHelper.setSubject(subject);// 标题

        String nick="";
        try {
            nick=javax.mail.internet.MimeUtility.encodeText("傲飞数据整合平台");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        messageHelper.setFrom(new InternetAddress(nick+" <"+mailSmtpFrom+">"));
        // true 表示启动HTML格式的邮件
        messageHelper.setText(getMailContent(content), true);

        // 发送邮件
        javaMailSender.send(mailMessage);
    }


    public String getMailContent(String auth_code) throws IOException {
        StringWriter stringWriter = new StringWriter();
        // velocity引擎
        VelocityEngine velocityEngine = new VelocityEngine();
        // 设置文件路径属性
        Properties properties = new Properties();
        String dir = this.getClass().getResource("/").getPath();
        properties.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, dir + "template/");
        // 引擎初始化属性配置
        velocityEngine.init(properties);
        // 加载指定模版
        Template template = velocityEngine.getTemplate("register_active.vm", "utf-8");
        // 填充模板内容
        VelocityContext velocityContext = new VelocityContext();
        velocityContext.put("auth_code", auth_code);
        velocityContext.put("active_url", mailActiveUrl);
        velocityContext.put("register_url", mailRegisterUrl);
        // 写到模板
        template.merge(velocityContext, stringWriter);
        return stringWriter.toString();
    }

}
