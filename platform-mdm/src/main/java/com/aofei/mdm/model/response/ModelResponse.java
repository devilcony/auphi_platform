package com.aofei.mdm.model.response;

import com.aofei.base.model.response.BaseResponse;
import io.swagger.annotations.ApiModelProperty;

/**
 * @auther Tony
 * @create 2018-11-07 12:26
 */
public class ModelResponse extends BaseResponse {

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
}
