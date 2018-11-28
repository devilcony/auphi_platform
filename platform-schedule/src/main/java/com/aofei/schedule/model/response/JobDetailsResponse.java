package com.aofei.schedule.model.response;

import lombok.Data;

import java.sql.Blob;

/**
 * <p>
 *
 * </p>
 *
 * @author Tony
 * @since 2018-11-18
 */
@Data
public class    JobDetailsResponse  {

    private static final long serialVersionUID = 1L;

    private String schedName;

    private String jobName;

    private String groupName;

    private String jobGroup;

    private String description;

    private String jobClassName;

    private String isDurable;

    private String isNonconcurrent;

    private String isUpdateData;

    private String requestsRecovery;

    private Blob jobData;

    private Long nextFireTime;

    private Long  prevFireTime;

    private Long  startTime;

    private Long  endTime;

    private String  triggerState;

}
