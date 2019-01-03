package com.aofei.translog.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.aofei.base.entity.DataEntity;

import com.baomidou.mybatisplus.annotations.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Tony
 * @since 2018-11-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("LOG_TRANS_STEP")
public class LogTransStep extends Model<LogTransStep> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "LOG_STEP_ID", type = IdType.ID_WORKER)
    private Long logStepId;
    @TableField("LOG_TRANS_ID")
    private Long logTransId;
    @TableField("CHANNEL_ID")
    private String channelId;
    @TableField("LOG_DATE")
    private Date logDate;
    @TableField("TRANSNAME")
    private String transname;
    @TableField("STEPNAME")
    private String stepname;
    @TableField("STEP_COPY")
    private Integer stepCopy;
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
    @TableField("SETP_LOG")
    private String setpLog;
    @TableField("COSTTIME")
    private String costtime;
    @TableField("SPEED")
    private String speed;
    @TableField("STATUS")
    private String status;


    @Override
    protected Serializable pkVal() {
        return this.logStepId;
    }

}
