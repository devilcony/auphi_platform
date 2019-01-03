package com.aofei.schedule.model.request;

import com.aofei.base.model.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class MonitorRequest extends BaseRequest {

    /**
     * 组织ID
     */
    @ApiModelProperty(hidden = true)
    private Long organizerId;

    private String qrtzJobName;

    private String qrtzJobGroup;



}
