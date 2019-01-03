package com.aofei.schedule.model.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class MonitorResponse {

    private Long id;

    private String qrtzJobName;

    private String qrtzJobGroup;

    private String qrtzJobGroupName;

    private String status;

    private Long errors;

    private Date startdate;

    private Date enddate;

    private String fileType; //JOB or TRANSFORMATION

}
