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
 * 资源库链接属性
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("KDI_SYS_REPOSITORY_DATABASE_ATTRIBUTE")
public class RepositoryDatabaseAttribute extends Model<RepositoryDatabaseAttribute> {

    private static final long serialVersionUID = 1L;

    /**
     * 资源库属性ID
     */
    @TableId(value = "REPOSITORY_DATABASE_ATTRIBUTE_ID", type = IdType.ID_WORKER)
    private Long repositoryDatabaseAttributeId;
    /**
     * 资源库ID
     */
    @TableField("REPOSITORY_CONNECTION_ID")
    private Long repositoryConnectionId;
    /**
     * 属性名
     */
    @TableField("CODE")
    private String code;
    /**
     * 属性值
     */
    @TableField("VALUE_STR")
    private String valueStr;


    @Override
    protected Serializable pkVal() {
        return this.repositoryDatabaseAttributeId;
    }

}
