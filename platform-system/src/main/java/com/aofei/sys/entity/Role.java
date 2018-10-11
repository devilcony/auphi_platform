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

/**
 * <p>
 * 角色
 * </p>
 *
 * @author Tony
 * @since 2018-10-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("SYS_ROLE")
public class Role extends DataEntity<Role> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "C_ROLE_ID", type = IdType.ID_WORKER)
    private Long roleId;
    /**
     * 角色名称
     */
    @TableField("C_ROLE_NAME")
    private String roleName;
    /**
     * 备注
     */
    @TableField("C_DESCRIPTION")
    private String description;

    @TableField("C_ORGANIZER_ID")
    private Long organizerId;

    @TableField(exist = false)
    private String organizerName;

    /**
     * 设计器权限
     */
    @TableField("C_PRIVILEDGES")
    private Long priviledges;
    /**
     * 是否是系统保留权限
     */
    @TableField("C_ISSYSTEMROLE")
    private Integer isSystemRole;



    @Override
    protected Serializable pkVal() {
        return this.roleId;
    }

}
