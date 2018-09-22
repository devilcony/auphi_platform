package com.aofei.sys.entity;

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
 * 系统日志
 * </p>
 *
 * @author Tony
 * @since 2018-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("KDI_SYS_PLATFORM_LOG")
public class PlatformLog extends Model<PlatformLog> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "LOG_ID", type = IdType.ID_WORKER)
    private Long logId;
    /**
     * 用户名
     */
    @TableField("USERNAME")
    private String username;

    /**
     * 模块
     */
    @TableField("MODULE")
    private String module;

    /**
     * 用户操作
     */
    @TableField("OPERATION")
    private String operation;
    /**
     * 请求方法
     */
    @TableField("METHOD")
    private String method;
    /**
     * 请求参数
     */
    @TableField("PARAMS")
    private String params;
    /**
     * IP地址
     */
    @TableField("IP")
    private String ip;
    /**
     * 创建时间
     */
    @TableField("CREATE_DATE")
    private Date createDate;
    /**
     * 是否删除  1：已删除  0：正常
     */
    @TableField("DEL_FLAG")
    private Integer delFlag;


    @Override
    protected Serializable pkVal() {
        return this.logId;
    }

}
