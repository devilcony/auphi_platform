package com.aofei.mdm.model.response;

import com.aofei.base.model.response.BaseResponse;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @auther Tony
 * @create 2018-11-07 12:28
 */
public class ModelAttributeResponse extends BaseResponse {

    private Long attributeId;

    @ApiModelProperty(value = "序号")
    private Integer attributeOrder;

    @ApiModelProperty(value = "属性名称")
    private String attributeName;
    /**
     * 1.枚举，2.计算数值 3非结构化文本 4其它
     */
    @ApiModelProperty(value = "属性类型;1:枚举，2:计算数值 3:非结构化文本 4:其它")
    private Integer statisticType;
    /**
     * 字段名称
     */
    @ApiModelProperty(value = "字段名称")
    private String fieldName;
    /**
     * kettle 里的数据类型编码
     */
    @ApiModelProperty(value = "kettle 里的数据类型编码")
    private Integer fieldType;
    /**
     * 字段长度
     */
    @ApiModelProperty(value = "字段长度")
    private Integer fieldLength;

    @ApiModelProperty(value = "是否是主键;Y:是, N:否")
    private String isPrimary;

    @ApiModelProperty(value = "字段精度")
    private Integer fieldPrecision;

    @ApiModelProperty(value = "约束列表")
    List<ModelConstaintResponse> constaints;
}
