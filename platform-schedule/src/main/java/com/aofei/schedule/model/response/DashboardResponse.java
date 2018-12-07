package com.aofei.schedule.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DashboardResponse {


    @ApiModelProperty(value = "运行中作业")
    private int runCount;

    @ApiModelProperty(value = "运行完成作业")
    private int finishCount;

    @ApiModelProperty(value = "总作业数")
    private int allCount;

    @ApiModelProperty(value = "错误作业数")
    private int errorCount;

    @ApiModelProperty(value = "作业耗时(前五)")
    private RunTimesResponse runTimes;

    @ApiModelProperty(value = "近七天完成数和错误数量")
    private RunCountResponse runCounts;

}
