package com.aofei.utils;


import com.alibaba.fastjson.JSONException;
import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class TencentSmsSingleSender {

    private static Map<String,VerificationCode> captchas = new HashMap<>();

    @Value("#{propertiesReader['qcloudsms.appid']}")
    private int appid ; // 短信应用SDK AppID

    @Value("#{propertiesReader['qcloudsms.appkey']}")
    private String appkey  ; // 短信应用SDK AppKey

    @Value("#{propertiesReader['qcloudsms.templateId']}")
    private int templateId  ; // 短信模板ID，需要在短信应用中申请

    @Value("#{propertiesReader['qcloudsms.smsSign']}")
    private String smsSign ; // 签名

    private long valid = 1000*60*30;//验证码有效期


    /**
     * 生成短信验证码并发送
     * @param phoneNumber
     */
    public void sendSms(String phoneNumber){
        try {
            String[] params = {createCaptcha(phoneNumber),"30"};
            SmsSingleSender ssender = new SmsSingleSender(appid, appkey);
            SmsSingleSenderResult result = ssender.sendWithParam("86", phoneNumber,
                    templateId, params, smsSign, "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
            System.out.println(result);
        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
        }
    }

    private String createCaptcha(String phoneNumber){
        int a = (int)((Math.random()*9+1)*100000) ;//6为随机数

        String captcha = String.valueOf(a);
        VerificationCode verificationCode = new VerificationCode(captcha,phoneNumber,System.currentTimeMillis());
        captchas.put(phoneNumber,verificationCode);

        return captcha;

    }

    /**
     * 验证短信验证码
     * @param phoneNumber
     * @param captcha
     * @return
     */
    public boolean validate(String phoneNumber,String captcha){
        VerificationCode verificationCode = captchas.get("phoneNumber");
        long now  = System.currentTimeMillis();

        if(verificationCode!=null && now - verificationCode.getCreateTime() < valid ){
            return true;
        }else{
            return false;
        }
    }


    class VerificationCode{

       private String code;

       private String phoneNumber;

       private long createTime;

        VerificationCode(String code,String phoneNumber,long createTime){
            setCode(code);
            setCreateTime(createTime);
            setPhoneNumber(phoneNumber);
        }

        public long getCreateTime() {
            return createTime;
        }

        public String getCode() {
            return code;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}
