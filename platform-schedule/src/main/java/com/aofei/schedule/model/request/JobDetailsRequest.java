package com.aofei.schedule.model.request;

import com.aofei.base.model.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author Tony
 * @since 2018-11-18
 */
@Data
public class JobDetailsRequest extends BaseRequest<JobDetailsRequest> {

    private static final long serialVersionUID = 1L;

    /**
     * 组织ID
     */
    @ApiModelProperty(hidden = true)
    private Long organizerId;

    private String jobName;

    private String jobGroup;





}
