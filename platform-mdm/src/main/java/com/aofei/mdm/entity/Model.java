package com.aofei.mdm.entity;

import com.aofei.base.entity.DataEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 主数据模型
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("MDM_MODEL")
public class Model extends DataEntity<Model> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_MODEL", type = IdType.ID_WORKER)
    private Long modelId;
    @TableField("MODEL_NAME")
    private String modelName;
    @TableField("MODEL_DESC")
    private String modelDesc;
    @TableField("MODEL_STATUS")
    private String modelStatus;
    @TableField("MODEL_AUTHOR")
    private String modelAuthor;
    @TableField("MODEL_NOTE")
    private String modelNote;
    @TableField("MODEL_CODE")
    private String modelCode;
    /**
     * 组织ID
     */
    @TableField("ORGANIZER_ID")
    private Long organizerId;


    @TableField(exist = false)
    private String organizerName;


    @Override
    protected Serializable pkVal() {
        return this.modelId;
    }

}
