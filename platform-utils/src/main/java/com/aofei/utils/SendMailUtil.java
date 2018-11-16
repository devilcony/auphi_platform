package com.aofei.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
/**
 * 发送邮件
 * @auther Tony
 * @create 2018-11-16 13:02
 */
@Component
public class SendMailUtil {

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
                                    String content, String filePath) throws MessagingException {
        MimeMessage mailMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage,
                true, "utf-8");
        // 设置收件人
        messageHelper.setTo(toEmail);
        messageHelper.setSubject(subject);// 标题
        // true 表示启动HTML格式的邮件
        messageHelper.setText(content, true);
        if(null!=filePath){
            FileSystemResource file = new FileSystemResource(new File(filePath));
            if(file.exists()){
                // 这里的方法调用和插入图片是不同的。
                messageHelper.addAttachment("test.doc", file);
//				messageHelper.addInline("imgName", file);//插入图片需调用此方法
            }
        }
        // 发送邮件
        javaMailSender.send(mailMessage);
    }


}
