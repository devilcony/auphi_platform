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
 * 主数据模型属性
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("MDM_MODEL_ATTRIBUTE")
public class ModelAttribute extends DataEntity<ModelAttribute> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_ATTRIBUTE", type = IdType.ID_WORKER)
    private Long attributeId;
    @TableField("ID_MODEL")
    private Long modelId;

    @TableField("ATTRIBUTE_ORDER")
    private Integer attributeOrder;
    @TableField("ATTRIBUTE_NAME")
    private String attributeName;
    /**
     * 1.枚举，2.计算数值 3非结构化文本 4其它
     */
    @TableField("STATISTIC_TYPE")
    private Integer statisticType;
    /**
     * 字段名称
     */
    @TableField("FIELD_NAME")
    private String fieldName;
    /**
     * kettle 里的数据类型编码
     */
    @TableField("FIELD_TYPE")
    private Integer fieldType;
    /**
     * 字段长度
     */
    @TableField("FIELD_LENGTH")
    private Integer fieldLength;
    @TableField("IS_PRIMARY")
    private String isPrimary;
    @TableField("FIELD_PRECISION")
    private Integer fieldPrecision;



    @Override
    protected Serializable pkVal() {
        return this.attributeId;
    }

}
