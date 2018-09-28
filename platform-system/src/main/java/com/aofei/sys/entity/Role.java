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
 * @since 2018-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("KDI_SYS_ROLE")
public class Role extends DataEntity<Role> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_ROLE", type = IdType.ID_WORKER)
    private Long roleId;
    /**
     * 角色名称
     */
    @TableField("ROLE_NAME")
    private String roleName;
    /**
     * 备注
     */
    @TableField("REMARK")
    private String remark;
    /**
     * 部门ID
     */
    @TableField("ID_DEPT")
    private Long deptId;

    /**
     * 部门名称
     */
    @TableField(exist = false)
    private String deptName;

    @Override
    protected Serializable pkVal() {
        return this.roleId;
    }

}
