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
     * 发送错误通知用户
     */
    private String errorNoticeUser;
    /**
     * 运行方式(1:本地运行,2:远程运行,3:集群运行;4:HA集群运行)
     */
    private Integer execType;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 执行的转换或者作业名
     */
    private String file;
    /**
     * 执行的转换或者作业path
     */
    private String filePath;


}
