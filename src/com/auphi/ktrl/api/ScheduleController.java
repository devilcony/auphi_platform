package com.auphi.ktrl.api;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.ktrl.schedule.bean.ScheduleBean;
import com.auphi.ktrl.schedule.util.QuartzUtil;
import com.auphi.ktrl.schedule.util.ScheduleUtil;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.system.user.util.UMStatus;
import com.auphi.ktrl.system.user.util.UserUtil;
import io.swagger.annotations.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @auther Tony
 * @create 2018-05-24 17:23
 */
@Api(value = "/api", description = "调度接口API文档")
@RestController
@RequestMapping(value = "/api", produces = {"application/json;charset=UTF-8"})
public class ScheduleController extends BaseMultiActionController {


    /**
     * 获取出库批次号
     * @return
     */
    @ApiOperation(value = "新增普通调度", notes = "创建一个普通周期调度", httpMethod = "POST", response = Integer.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "passwd", value = "密码", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "jobname", value = "调度名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "description", value = "描述", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "file", value = "作业/转换名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "filepath", value = "文件路径", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "repository", value = "资源库名称", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "execType", value = "执行方式", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "version", value = "固定值：V3.0", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "startdate", value = "开始日期(日期格式：yyyy-MM-dd)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "starttime", value = "开始时间(时间格式：HH:mm:SS)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "enddate", value = "结束日期(日期格式：yyyy-MM-dd)如果结束时间为空表示用不结束", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "cycle", value = "周期(1=执行一次;2=秒;3=分钟;4=小时;5=天;6=周;7=月;8=年)", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "cyclenum", value = "周期模式(间隔时间 )" +
                    "<br>cycle=6(周期为周)是可用 1=周日 2=周一。。。7=周六 多个日期用','如：1,2,3 " +
                    "<br>cycle=7 1-31表示每月的几号 L表示每月的最后一天" +
                    "<br>cycle=8 每年的执行日期(格式：MM-dd)", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "daytype", value = "cycle=5(周期为天)是可用 0:取cyclenum 1:每个工作日", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "monthtype", value = "cycle=7(周期为月)是可用 0:每月的几号 1:每月的第几星期的星期几", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "weeknum", value = "cycle=7/8(周期为月/年)是可用 1-4表示第几个星期 L表示最后一个星期 ", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "daynum", value = "cycle=7/8(周期为月/年)是可用 1-7 表示周日-周六   ", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "yeartype", value = "cycle=8(周期为年)是可用 0:每月的几号 1:每年几月的第几星期的星期几", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "monthnum", value = "cycle=8(周期为年)是可用 每年的几月1-12表示", paramType = "query", dataType = "String"),

    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "success")})
    @RequestMapping(value = "/v1/schedule/add.shtml", method = RequestMethod.POST)
    public void create(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String username = ServletRequestUtils.getStringParameter(request,"username",null);
        String passwd = ServletRequestUtils.getStringParameter(request,"passwd",null);
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(passwd)){
            this.setFailTipMsg("username and password can not be empty",response);
        }
        UserBean userBean = new UserBean() ;
        UMStatus umStatus = UserUtil.login(username, passwd, userBean) ;
        if(UMStatus.SUCCESS.getStatusCode() == umStatus.getStatusCode()){
            ScheduleBean scheduleBean = ScheduleUtil.createScheduleBeanFromRequest(request, userBean);
            QuartzUtil.create(scheduleBean);
            this.setOkTipMsg("success",response);
        }else{
            this.setFailTipMsg("username or password error",response);
        }

    }
}
