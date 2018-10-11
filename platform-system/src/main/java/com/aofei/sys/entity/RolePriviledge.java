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
 * 设计器权限
 * </p>
 *
 * @author Tony
 * @since 2018-10-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("SYS_ROLE_PRIVILEDGE")
public class RolePriviledge extends DataEntity<RolePriviledge> {

    private static final long serialVersionUID = 1L;

    /**
     * 权限ID
     */
    @TableId(value = "ID_PRIVILEDGE", type = IdType.ID_WORKER)
    private Long priviledgeId;
    /**
     * 类型ID
     */
    @TableField("ID_RESOURCE_TYPE")
    private Long resourceTypeId;
    /**
     * 操作ID
     */
    @TableField("ID_OPERATION")
    private Long operationId;


    @Override
    protected Serializable pkVal() {
        return this.priviledgeId;
    }

}
