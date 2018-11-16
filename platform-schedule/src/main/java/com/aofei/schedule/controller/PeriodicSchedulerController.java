package com.aofei.schedule.controller;

import com.aofei.base.annotation.CurrentUser;
import com.aofei.base.controller.BaseController;
import com.aofei.base.model.response.CurrentUserResponse;
import com.aofei.base.model.response.Response;
import com.aofei.schedule.job.JobRunner;
import com.aofei.schedule.job.TransRunner;
import com.aofei.schedule.model.request.GeneralScheduleRequest;
import com.aofei.schedule.service.IQuartzService;
import com.aofei.sys.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.text.ParseException;

/**
 * @auther Tony
 * @create 2018-09-22 15:39
 */
@Api(tags = { "调度管理-周期调度" })
@RestController
@RequestMapping(value = "/schedule/cycle", produces = {"application/json;charset=UTF-8"})
public class PeriodicSchedulerController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(PeriodicSchedulerController.class);

    @Autowired
    private IQuartzService quartzService;

    @Autowired
    private IUserService userService;

    /**
     * 新建调度
     * @param request
     * @return
     */
    @ApiOperation(value = "添加普通调度", notes = "" +
            "<strong>jobName</strong> (string): 调度名称 ,</br>" +
            "<strong>description</strong> (string): 描述 ,</br>" +
            "<strong>repository</strong> (string): 资源库名称 ,</br>" +
            "<strong>startTime</strong> (string): 开始时间(格式:HH:mm:ss) </br>" +
            "<strong>startDate</strong> (string): 开始日期(格式:yyyy-MM-dd) </br>" +
            "<strong>cycle</strong> (integer): 周期(1:执行一次;2:秒;3:分钟;4:小时;5:天;6:周;7:月;8:年;) </br>" +
            "<strong>cycleNum </strong>(string): 周期模式; </br>" +
            "  1:执行一次:cycleNum为空 </br>" +
            "  2:秒:cycleNum为整数;表示几秒执行一次 </br>" +
            "  3:分钟:cycleNum为整数;表示几分钟享执行一次 </br>" +
            "  4:小时:cycleNum为整数;表示几小时享执行一次 </br>" +
            "  5:天:cycleNum为整数;表示几天执行一次;-1表示每个工作日执行 </br>" +
            "  6:周:cycleNum为整数;1-7分别表示 周日-周六(周日开始) </br>" +
            "  7:月:分两种情况,第一种:用1-31表示每月的几号执行,L表示最后一天 第二种: cycleNum为空,用weekNum,dayNum表示每月的第几个星期的星期几 </br>" +
            "  8:年:分两种情况,第一种:用格式MM-dd表示每年的几月几号执行 第二种:cycleNum为空,用monthNum,weekNum,dayNum表示每年第几个月的第几个星期的星期几 </br>" +
            "<strong>dayType</strong> (integer): 周期为天时区分类型,</br>dayType=1:表示按天周期执行 </br>dayType=2:只在工作日执行</br>" +
            "<strong>monthType</strong> (integer): 周期为月时区分类型,</br>dayType=1:用1-31表示每月的几号执行,L表示最后一天 </br>dayType=2: cycleNum为空,用weekNum,dayNum表示每月的第几个星期的星期几;</br>" +
            "<strong>yearType</strong> (integer): 周期为年时区分类型,</br>yearType=1:用格式MM-dd表示每年的几月几号执行 </br>yearType=2:cycleNum为空,用monthNum,weekNum,dayNum表示每年第几个月的第几个星期的星期几</br>" +
            "<strong>monthNum</strong> (integer): 1-12表示1-12月份;</br>" +
            "<strong>weekNum</strong> (integer): 第几个星期 1-4表示 L表示最后一个星期</br>"+
            "<strong>dayNum</strong> (integer): 1-7分别表示 周日-周六(周日开始);和weekNum同时使用表示第几个星期的星期几 </br>" +
            "<strong>endDate</strong> (string): 结束日期(格式:yyyy-MM-dd);结束日期为空,表示永不结束;周期为执行一次结束时间为空 </br>" +
            "<strong>errorNoticeUser</strong> (string): 发送错误通知用户(用户名,多用户用','分割) </br>" +
            "<strong>execType</strong> (integer): 运行方式(1:本地运行,2:远程运行,3:集群运行;4:HA集群运行) </br>" +
            "<strong>file</strong> (string): 执行的转换或者作业名 ,</br>" +
            "<strong>filePath</strong> (string): 执行的转换或者作业path </br>" +
            "<strong>fileType</strong> (string): transformation or job </br>" +
            "<strong>version</strong> (string): 版本(固定值v3.9) ,</br>" ,httpMethod = "POST")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Response<Boolean> add(
            @RequestBody GeneralScheduleRequest request,
            @ApiIgnore @CurrentUser CurrentUserResponse user) throws SchedulerException, ParseException {
        Class quartzExecuteClass = null;
        if(request.getFile().endsWith("kjb") || request.getFile().endsWith("KJB")){
            quartzExecuteClass = JobRunner.class;
        }else if(request.getFile().endsWith("ktr") || request.getFile().endsWith("KTR")){
            quartzExecuteClass = TransRunner.class;
        }




        quartzService.create(request, request.getGroup()  ,quartzExecuteClass);

        return Response.ok(true) ;
    }

    /**
     * 新建调度
     * @param request
     * @return
     */
    @ApiOperation(value = "添加普通调度", notes = "" +
            "<strong>jobName</strong> (string): 调度名称 ,</br>" +
            "<strong>description</strong> (string): 描述 ,</br>" +
            "<strong>repository</strong> (string): 资源库名称 ,</br>" +
            "<strong>startTime</strong> (string): 开始时间(格式:HH:mm:ss) </br>" +
            "<strong>startDate</strong> (string): 开始日期(格式:yyyy-MM-dd) </br>" +
            "<strong>cycle</strong> (integer): 周期(1:执行一次;2:秒;3:分钟;4:小时;5:天;6:周;7:月;8:年;) </br>" +
            "<strong>cycleNum </strong>(string): 周期模式; </br>" +
            "  1:执行一次:cycleNum为空 </br>" +
            "  2:秒:cycleNum为整数;表示几秒执行一次 </br>" +
            "  3:分钟:cycleNum为整数;表示几分钟享执行一次 </br>" +
            "  4:小时:cycleNum为整数;表示几小时享执行一次 </br>" +
            "  5:天:cycleNum为整数;表示几天执行一次;-1表示每个工作日执行 </br>" +
            "  6:周:cycleNum为整数;1-7分别表示 周日-周六(周日开始) </br>" +
            "  7:月:分两种情况,第一种:用1-31表示每月的几号执行,L表示最后一天 第二种: cycleNum为空,用weekNum,dayNum表示每月的第几个星期的星期几 </br>" +
            "  8:年:分两种情况,第一种:用格式MM-dd表示每年的几月几号执行 第二种:cycleNum为空,用monthNum,weekNum,dayNum表示每年第几个月的第几个星期的星期几 </br>" +
            "<strong>dayType</strong> (integer): 周期为天时区分类型,</br>dayType=1:表示按天周期执行 </br>dayType=2:只在工作日执行</br>" +
            "<strong>monthType</strong> (integer): 周期为月时区分类型,</br>dayType=1:用1-31表示每月的几号执行,L表示最后一天 </br>dayType=2: cycleNum为空,用weekNum,dayNum表示每月的第几个星期的星期几;</br>" +
            "<strong>yearType</strong> (integer): 周期为年时区分类型,</br>yearType=1:用格式MM-dd表示每年的几月几号执行 </br>yearType=2:cycleNum为空,用monthNum,weekNum,dayNum表示每年第几个月的第几个星期的星期几</br>" +
            "<strong>monthNum</strong> (integer): 1-12表示1-12月份;</br>" +
            "<strong>weekNum</strong> (integer): 第几个星期 1-4表示 L表示最后一个星期</br>"+
            "<strong>dayNum</strong> (integer): 1-7分别表示 周日-周六(周日开始);和weekNum同时使用表示第几个星期的星期几 </br>" +
            "<strong>endDate</strong> (string): 结束日期(格式:yyyy-MM-dd);结束日期为空,表示永不结束;周期为执行一次结束时间为空 </br>" +
            "<strong>errorNoticeUser</strong> (string): 发送错误通知用户 ,</br>" +
            "<strong>execType</strong> (integer): 运行方式(1:本地运行,2:远程运行,3:集群运行;4:HA集群运行) </br>" +
            "<strong>file</strong> (string): 执行的转换或者作业名 ,</br>" +
            "<strong>filePath</strong> (string): 执行的转换或者作业path </br>" +
            "<strong>fileType</strong> (string): transformation or job </br>" +
            "<strong>version</strong> (string): 版本(固定值v3.9) ,</br>" ,httpMethod = "POST")
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response<Boolean> update(
            @RequestBody GeneralScheduleRequest request,
            @ApiIgnore @CurrentUser CurrentUserResponse user) throws SchedulerException, ParseException {
        Class quartzExecuteClass = null;
        if(request.getFile().endsWith("kjb") || request.getFile().endsWith("KJB")){
            quartzExecuteClass = JobRunner.class;
        }else if(request.getFile().endsWith("ktr") || request.getFile().endsWith("KTR")){
            quartzExecuteClass = TransRunner.class;
        }
        quartzService.update(request,request.getGroup(), quartzExecuteClass);

        return Response.ok(true) ;
    }

    @ApiOperation(value = "删除调度", notes = "删除调度", httpMethod = "GET")
    @RequestMapping(value = "/delete/{name}/group/{group}", method = RequestMethod.GET)
    public Response<Boolean> delete(
            @ApiParam(value = "调度名称", required = true)@PathVariable String name,
            @ApiParam(value = "调度分组名称", required = true)@PathVariable String group,
            @ApiIgnore @CurrentUser CurrentUserResponse user) throws SchedulerException {

        return Response.ok(quartzService.removeJob(name,group)) ;
    }

    @ApiOperation(value = "暂停调度", notes = "暂停调度", httpMethod = "GET")
    @RequestMapping(value = "/pause/{name}/group/{group}", method = RequestMethod.GET)
    public Response<Boolean> pause(
            @ApiParam(value = "调度名称", required = true)@PathVariable String name,
            @ApiParam(value = "调度分组名称", required = true)@PathVariable String group,
            @ApiIgnore @CurrentUser CurrentUserResponse user) throws SchedulerException {

        return Response.ok(quartzService.pause(name,group)) ;
    }

    @ApiOperation(value = "还原调度", notes = "还原暂停的调度", httpMethod = "GET")
    @RequestMapping(value = "/resume/{name}/group/{group}", method = RequestMethod.GET)
    public Response<Integer> resume(
            @ApiParam(value = "调度名称", required = true)@PathVariable String name,
            @ApiParam(value = "调度分组名称", required = true)@PathVariable String group,
            @ApiIgnore @CurrentUser CurrentUserResponse user) throws SchedulerException {

        return Response.ok(quartzService.resume(name,group)) ;
    }

    @ApiOperation(value = "手动执行调度", notes = "手动执行调度", httpMethod = "POST")
    @RequestMapping(value = "/execute/{name}/group/{group}", method = RequestMethod.POST)
    public Response<Integer> execute(
            @ApiParam(value = "调度名称", required = true)@PathVariable String name,
            @ApiParam(value = "调度分组名称", required = true)@PathVariable String group,
            @ApiIgnore @CurrentUser CurrentUserResponse user) throws SchedulerException {

        return Response.ok(quartzService.execute(name,group,null)) ;
    }



}
