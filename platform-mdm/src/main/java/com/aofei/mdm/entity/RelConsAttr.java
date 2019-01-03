package com.aofei.mdm.entity;

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
 *
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("MDM_REL_CONS_ATTR")
public class RelConsAttr extends Model {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_REL_CONS_ATTR", type = IdType.ID_WORKER)
    private Long relConsAttrId;
    @TableField("ID_CONSTAINT")
    private Long constaintId;
    @TableField("ID_ATTRIBUTE")
    private Long attributeId;


    @Override
    protected Serializable pkVal() {
        return this.relConsAttrId;
    }

}
