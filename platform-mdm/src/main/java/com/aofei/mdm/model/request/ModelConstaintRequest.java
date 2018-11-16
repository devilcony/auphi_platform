package com.aofei.mdm.model.request;

import com.aofei.base.model.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 主数据模型属性约束
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Data
public class ModelConstaintRequest extends BaseRequest<ModelConstaintRequest> {

    private static final long serialVersionUID = 1L;

    private Long constaintId;

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
