package com.aofei.schedule.entity;

import lombok.Data;

/**
 * @auther Tony
 * @create 2018-10-01 17:03
 */
@Data
public class GeneralSchedule  {

    /**
     * 调度名称
     */
    private String jobName;
    /**
     * 描述
     */
    private String description;
    /**
     * 版本(固定值v3.9)
     */
    private String version;
    /**
     * 资源库名称
     */
    private String repository;
    /**
     * 发送错误通知用户
     */
    private String errorNoticeUser;
    /**
     * 运行方式(1:本地运行,2:远程运行,3:集群运行;4:HA集群运行)
     */
    private Integer execType;
    /**
     * 开始时间(格式:HH:mm:ss)
     */
    private String startTime;
    /**
     * 开始日期(格式:yyyy-MM-dd)
     */
    private String startDate;
    /**
     * 周期(1:执行一次;2:秒;3:分钟;4:小时;5:天;6:周;7:月;8:年;)
     */
    private Integer cycle;
    /**
     * 周期模式
     */
    private String cycleNum;
    /**
     * 周期为天时区分类型
     */
    private Integer dayType;
    /**
     * 周期为月时区分类型
     */
    private Integer monthType;
    /**
     * 周期为年时区分类型
     */
    private Integer yearType;
    /**
     * 1-12表示1-12月份;
     */
    private Integer monthNum;
    /**
     * 第几个星期 1-4表示 L表示最后一个星期
     */
    private Integer weekNum;
    /**
     * 1-7分别表示 周日-周六(周日开始)和weekNum同时使用表示第几个星期的星期几
     */
    private Integer dayNum;
    /**
     * 结束日期
     */
    private String endDate;
    /**
     * 执行的转换或者作业名
     */
    private String file;
    /**
     * 执行的转换或者作业path
     */
    private String filePath;


}
