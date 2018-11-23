package com.aofei.schedule.model.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @auther Tony
 * @create 2018-10-05 20:44
 */
@Data
public class GroupResponse {

    private String groupId;

    /**
     * 分组名称
     */
    @ApiModelProperty(value = "分组名称")
    private String groupName;
    /**
     * 分组描述
     */
    @ApiModelProperty(value = "分组描述")
    private String description;
}
