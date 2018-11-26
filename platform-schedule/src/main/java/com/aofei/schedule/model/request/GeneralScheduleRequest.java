package com.aofei.schedule.model.request;


import com.aofei.base.model.request.BaseRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 普通调度
 * @auther Tony
 * @create 2018-09-28 13:24
 */
@Data
public class GeneralScheduleRequest extends BaseRequest {

    @ApiModelProperty(value = "所属组")
    private String jobGroup;

    @ApiModelProperty(value = "调度名称")
    private String jobName;


    @ApiModelProperty(value = "备注")
    private String description;

    @ApiModelProperty(value = "版本(固定值v3.9)")
    private String version;

    @ApiModelProperty(value = "资源库名称")
    private String repository;

    @ApiModelProperty(value = "发送错误通知用户(用户名,多用户用','分割)")
    private String errorNoticeUser;

    @ApiModelProperty(value = "运行方式(1:本地运行,2:远程运行,3:集群运行;4:HA集群运行)")
    private Integer execType;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "开始时间(格式:yyyy-MM-dd HH:mm:ss)")
    private Date startTime;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    @ApiModelProperty(value = "结束日期(格式:yyyy-MM-dd HH:mm:ss);结束日期为空,表示不结束;周期为执行一次结束时间为空")
    private Date endTime;

    @ApiModelProperty(value = "周期(1:执行一次;2:秒;3:分钟;4:小时;5:天;6:周;7:月;8:年;)")
    private Integer cycle;

    @ApiModelProperty(value = "周期模式;\n" +
            "1:执行一次:cycleNum为空\n" +
            "2:秒:cycleNum为整数;表示几秒执行一次\n" +
            "3:分钟:cycleNum为整数;表示几分钟享执行一次\n" +
            "4:小时:cycleNum为整数;表示几小时享执行一次\n" +
            "5:天:cycleNum为整数;表示几天执行一次;-1表示每个工作日执行\n" +
            "6:周:cycleNum为字符串;1-7分别表示 周日-周六(周日开始)\n" +
            "7:月:分两种情况,第一种:用1-31表示每月的几号执行,L表示最后一天 第二种: cycleNum为空,用weekNum,dayNum表示每月的第几个星期的星期几 \n" +
            "8:年:分两种情况,第一种:yearType=1;用格式MM-dd表示每年的几月几号执行 第二种:yearType=2;cycleNum为空,用monthNum,weekNum,dayNum表示每年第几个月的第几个星期的星期几")
    private String cycleNum;

    @ApiModelProperty(value = "周期为天时区分类型,\n dayType=1:表示按天周期执行,cycleNum天执行一次 \n dayType=2:cycleNum为空,只在工作日执行")
    private Integer dayType;

    @ApiModelProperty(value = "周期为月时区分类型,\n monthType=1:用1-31表示每月的几号执行,L表示最后一天 \n monthType=2: cycleNum为空,用weekNum,dayNum表示每月的第几个星期的星期几")
    private Integer monthType;

    @ApiModelProperty(value = "周期为年时区分类型,\n yearType=1;用格式MM-dd表示每年的几月几号执行 \n yearType=2;cycleNum为空,用monthNum,weekNum,dayNum表示每年第几个月的第几个星期的星期几")
    private Integer yearType;

    @ApiModelProperty(value = "1-12表示1-12月份;")
    private Integer monthNum;

    @ApiModelProperty(value = "第几个星期 1-4表示 L表示最后一个星期")
    private String weekNum;

    @ApiModelProperty(value = "1-7分别表示 周日-周六(周日开始);和weekNum同时使用表示第几个星期的星期几 ")
    private Integer dayNum;


    @ApiModelProperty(value = "执行的转换或者作业名")
    private String file;

    @ApiModelProperty(value = "执行的转换或者作业path")
    private String filePath;

    @ApiModelProperty(value = "TRANSFORMATION or JOB")
    private String fileType;

    @ApiModelProperty(hidden = true)
    private Long organizerId;//组织ID

    @ApiModelProperty(hidden = true)
    private String username;//用户名

//    @ApiModelProperty(value = "参数")
//    List<ParamRequest> params;

}
