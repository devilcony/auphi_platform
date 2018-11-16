package com.aofei.mdm.entity;

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
 * 主数据模型属性约束
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("MDM_MODEL_CONSTAINT")
public class ModelConstaint extends DataEntity<ModelConstaint> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_CONSTAINT", type = IdType.ID_WORKER)
    private Long constaintId;
    @TableField("CONSTAINT_ORDER")
    private Integer constaintOrder;
    /**
     * 1 唯一  2 非空  3外键
     */
    @TableField("CONSTAINT_TYPE")
    private Integer constaintType;
    /**
     * 约束名称
     */
    @TableField("CONSTAINT_NAME")
    private String constaintName;
    @TableField("ID_ATTRIBUTE")
    private Long attributeId;
    @TableField("REFERENCE_ID_MODEL")
    private Integer referenceModelId;
    @TableField("REFERENCE_ID_ATTRIBUTE")
    private Integer referenceAttributeId;
    /**
     * 是否为字符类型的唯一约束数据，创建别名表（0否   1 是）
     */
    @TableField("ALIAS_TABLE_FLAG")
    private Integer aliasTableFlag;



    @Override
    protected Serializable pkVal() {
        return this.constaintId;
    }

}
