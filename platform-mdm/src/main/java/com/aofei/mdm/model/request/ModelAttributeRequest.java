package com.aofei.mdm.model.request;

import com.aofei.base.model.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * <p>
 * 主数据模型属性
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Data
public class ModelAttributeRequest extends BaseRequest<ModelAttributeRequest> {

    private static final long serialVersionUID = 1L;


    private Long attributeId;

    @ApiModelProperty(value = "所屬模型ID")
    private Long modelId;

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
    List<ModelConstaintRequest> constaints;



}
