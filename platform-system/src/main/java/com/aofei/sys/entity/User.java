package com.aofei.sys.entity;

import com.aofei.base.entity.DataEntity;
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
 * 用户表
 * </p>
 *
 * @author Tony
 * @since 2018-10-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("SYS_USER")
public class User extends DataEntity<User> {

    public static final Integer STATUS_NORMAL = 0;

    public static final Integer STATUS_DISABLE = 1;

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "C_USER_ID", type = IdType.ID_WORKER)
    private Long userId;
    /**
     * 用户名
     */
    @TableField("C_USER_NAME")
    private String username;
    /**
     * 密码
     */
    @TableField("C_PASSWORD")
    private String password;
    /**
     * 昵称（名称）
     */
    @TableField("C_NICK_NAME")
    private String nickName;
    /**
     * 邮箱
     */
    @TableField("C_EMAIL")
    private String email;

    /**
     * 磁盘空间 字节 默认   1073741824字节
     */
    @TableField("C_DISK_SPACE")
    private Long diskSpace;

    /**
     * 手机国家代码
     */
    @TableField("C_COUNTRY_CODE")
    private String countryCode;
    /**
     * 手机
     */
    @TableField("C_MOBILEPHONE")
    private String mobilephone;
    /**
     * 描述
     */
    @TableField("C_DESCRIPTION")
    private String description;
    /**
     * 系统用户
     */
    @TableField("C_IS_SYSTEM_USER")
    private Integer isSystemUser;
    /**
     * 组织ID
     */
    @TableField("C_ORGANIZER_ID")
    private Long organizerId;


    @TableField(exist = false)
    private String organizerName;

    /**
     * 用户状态
     */
    @TableField("C_USER_STATUS")
    private Integer userStatus;

    /**
     * 最后一次登录时间
     */
    @TableField("LAST_LOGIN_TIME")
    private Date lastLoginTime;
    /**
     * 最后一次登录IP
     */
    @TableField("LAST_LOGIN_IP")
    private String lastLoginIp;


    @Override
    protected Serializable pkVal() {
        return this.userId;
    }

}
