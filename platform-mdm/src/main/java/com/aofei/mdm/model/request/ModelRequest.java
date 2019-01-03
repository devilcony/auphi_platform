package com.aofei.mdm.model.request;

import com.aofei.base.model.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 主数据模型
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Data
public class ModelRequest extends BaseRequest<ModelRequest> {

    private static final long serialVersionUID = 1L;


    private Long modelId;

    @ApiModelProperty(value = "模型名称")
    private String modelName;

    @ApiModelProperty(value = "模型描述")
    private String modelDesc;

    @ApiModelProperty(value = "模型状态;0=草稿,1=发布")
    private String modelStatus;

    @ApiModelProperty(value = "创建人(遗留字段可忽略)")
    private String modelAuthor;

    @ApiModelProperty(value = "说明")
    private String modelNote;

    @ApiModelProperty(value = "模型编码")
    private String modelCode;

    @ApiModelProperty(value = "模型属性列表")
    List<ModelAttributeRequest> attributes;

    /**
     * 组织ID
     */
    @ApiModelProperty(hidden = true,value = "组织ID")
    private Long organizerId;


}
