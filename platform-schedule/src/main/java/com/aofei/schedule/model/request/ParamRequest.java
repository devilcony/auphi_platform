package com.aofei.schedule.model.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @auther Tony
 * @create 2018-10-03 14:37
 */
@Data
public class ParamRequest {

    /**
     * key of this parameter
     */
    @ApiModelProperty(value = "命名参数")
    public String key;

    /**
     * Description of the parameter
     */
    @ApiModelProperty(value = "Description")
    public String description;

    /**
     * Default value for this parameter
     */
    @ApiModelProperty(value = "默认值")
    public String defaultValue;

    /**
     * Actual value of the parameter.
     */
    @ApiModelProperty(value = "值")
    public String value;
}
