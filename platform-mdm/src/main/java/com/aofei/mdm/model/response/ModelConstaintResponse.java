package com.aofei.mdm.model.response;

import com.aofei.base.model.response.BaseResponse;
import io.swagger.annotations.ApiModelProperty;

/**
 * @auther Tony
 * @create 2018-11-07 12:36
 */
public class ModelConstaintResponse extends BaseResponse {

    @ApiModelProperty(value = "序号")
    private Integer constaintOrder;
    /**
     * 1 唯一  2 非空  3外键
     */
    @ApiModelProperty(value = "约束类型;1:唯一, 2:非空 ,3:外键")
    private Integer constaintType;
    /**
     * 约束名称
     */
    @ApiModelProperty(value = "约束名称")
    private String constaintName;

    @ApiModelProperty(value = "参照模型ID")
    private Long referenceModelId;

    @ApiModelProperty(value = "参照模型字段ID")
    private Long referenceAttributeId;
    /**
     * 是否为字符类型的唯一约束数据，创建别名表（0否   1 是）
     */
    @ApiModelProperty(value = "是否为字符类型的唯一约束数据，创建别名表（0否   1 是）")
    private Integer aliasTableFlag;
}
