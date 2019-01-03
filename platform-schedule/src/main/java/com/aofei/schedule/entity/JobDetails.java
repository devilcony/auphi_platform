package com.aofei.schedule.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
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
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("QRTZ_JOB_DETAILS")
public class JobDetails extends Model<JobDetails> {

    private static final long serialVersionUID = 1L;

    @TableField(value = "SCHED_NAME")
    private String schedName;
    @TableField("JOB_NAME")
    private String jobName;

    @TableField(exist = false)
    private String groupName;

    @TableField("JOB_GROUP")
    private String jobGroup;

    @TableField("DESCRIPTION")
    private String description;
    @TableField("JOB_CLASS_NAME")
    private String jobClassName;
    @TableField("IS_DURABLE")
    private String isDurable;
    @TableField("IS_NONCONCURRENT")
    private String isNonconcurrent;
    @TableField("IS_UPDATE_DATA")
    private String isUpdateData;
    @TableField("REQUESTS_RECOVERY")
    private String requestsRecovery;
    @TableField("JOB_DATA")
    private Blob jobData;
    @TableField(exist = false)
    private Long nextFireTime;
    @TableField(exist = false)
    private Long  prevFireTime;
    @TableField(exist = false)
    private Long  startTime;
    @TableField(exist = false)
    private Long  endTime;
    @TableField(exist = false)
    private String  triggerState;

    @Override
    protected Serializable pkVal() {
        return null;
    }

}
