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
@TableName("LOG_TRANS")
public class LogTrans extends Model<LogTrans> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "LOG_TRANS_ID", type = IdType.ID_WORKER)
    private Long logTransId;
    /**
     * 对应转换表 R_TRANSFORMATION 中的ID_TRANSFORMATION 字段
     */
    @TableField("TRANS_CONFIG_ID")
    private Long transConfigId;
    @TableField("CHANNEL_ID")
    private String channelId;
    @TableField("TRANSNAME")
    private String transname;
    @TableField("STATUS")
    private String status;
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
    @TableField("STARTDATE")
    private Date startdate;
    @TableField("ENDDATE")
    private Date enddate;
    @TableField("LOGDATE")
    private Date logdate;
    @TableField("DEPDATE")
    private Date depdate;
    @TableField("REPLAYDATE")
    private Date replaydate;
    @TableField("LOG_FIELD")
    private String logField;
    @TableField("EXECUTING_SERVER")
    private String executingServer;
    @TableField("EXECUTING_USER")
    private String executingUser;
    /**
     * 1:表示本地，2表示远程，3表示集群
     */
    @TableField("EXCUTOR_TYPE")
    private Integer excutorType;
    @TableField("LOGINFO")
    private String loginfo;
    @TableField("TRANS_CN_NAME")
    private String transCnName;


    @Override
    protected Serializable pkVal() {
        return this.logTransId;
    }

}
