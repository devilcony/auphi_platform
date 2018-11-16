package com.aofei.joblog.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author Tony
 * @since 2018-11-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("LOG_JOB_STEP")
public class LogJobStep extends Model<LogJobStep> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "JOB_STEP_ID", type = IdType.ID_WORKER)
    private Long jobStepId;

    @TableField("JOB_ID")
    private Long jobId;
    @TableField("CHANNEL_ID")
    private String channelId;
    @TableField("LOG_DATE")
    private Date logDate;
    /**
     * 父JOB的名称
     */
    @TableField("JOBNAME")
    private String jobname;
    @TableField("STEPNAME")
    private String stepname;
    @TableField("LINES_READ")
    private Long linesRead;
    @TableField("LINES_WRITTEN")
    private Long linesWritten;
    @TableField("LINES_UPDATED")
    private Long linesUpdated;
    @TableField("LINES_INPUT")
    private Long linesInput;
    @TableField("LINES_OUTPUT")
    private Long linesOutput;
    @TableField("LINES_REJECTED")
    private Long linesRejected;
    @TableField("ERRORS")
    private Long errors;
    @TableField("RESULT")
    private Long result;
    @TableField("NR_RESULT_ROWS")
    private Integer nrResultRows;
    @TableField("NR_RESULT_FILES")
    private Long nrResultFiles;
    /**
     * 日志字段为这个特定的工作条目包含错误日志日志LOG_FIELD
     */
    @TableField("LOG_FIELD")
    private Long logField;
    @TableField("COPY_NR")
    private Long copyNr;
    @TableField("SETP_LOG")
    private String setpLog;


    @Override
    protected Serializable pkVal() {
        return this.jobStepId;
    }

}
