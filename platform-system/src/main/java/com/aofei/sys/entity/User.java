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
 * 系统用户
 * </p>
 *
 * @author Tony
 * @since 2018-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("KDI_SYS_USER")
public class User extends DataEntity<User> {


    public static final Integer STATUS_NORMAL = 0;

    public static final Integer STATUS_DISABLE = 1;


    private static final long serialVersionUID = 1L;

    @TableId(value = "USER_ID", type = IdType.ID_WORKER)
    private Long userId;
    /**
     * 用户名
     */
    @TableField("USERNAME")
    private String username;
    /**
     * 密码
     */
    @TableField("PASSWORD")
    private String password;
    /**
     * 邮箱
     */
    @TableField("EMAIL")
    private String email;
    /**
     * 手机号
     */
    @TableField("MOBILE")
    private String mobile;
    /**
     * 状态  0：正常   1：禁用
     */
    @TableField("STATUS")
    private Integer status;
    /**
     * 部门ID
     */
    @TableField(value = "DEPT_ID")
    private Long deptId;

    /**
     * 部门名称
     */
    @TableField(exist = false)
    private String deptName;


    /**
     * 最后登录IP
     */
    @TableField("LOGIN_IP")
    private String loginIp;
    /**
     * 最后登录时间
     */
    @TableField("LOGIN_TIME")
    private Date loginTime;



    @Override
    protected Serializable pkVal() {
        return this.userId;
    }

}
