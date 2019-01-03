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

/**
 * <p>
 * 设计器权限类型
 * </p>
 *
 * @author Tony
 * @since 2018-10-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("SYS_ROLE_RESOURCE_TYPE")
public class RoleResourceType extends Model<RoleResourceType> {

    private static final long serialVersionUID = 1L;

    /**
     * 设计器资源ID
     */
    @TableId(value = "ID_RESOURCE_TYPE", type = IdType.ID_WORKER)
    private Long resourceTypeId;
    /**
     * 设计器资源名称(文件 数据库连接等)
     */
    @TableField("RESOURCE_TYPE_NAME")
    private String resourceTypeName;


    @Override
    protected Serializable pkVal() {
        return this.resourceTypeId;
    }

}
