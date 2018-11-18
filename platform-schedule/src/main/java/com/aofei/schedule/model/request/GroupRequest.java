package com.aofei.schedule.model.request;

import com.aofei.base.model.request.BaseRequest;
import com.baomidou.mybatisplus.annotations.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @auther Tony
 * @create 2018-10-05 20:45
 */
@Data
public class GroupRequest extends BaseRequest {


    private String groupId;

    /**
     * 组织ID
     */
    @ApiModelProperty(hidden = true)
    private Long organizerId;

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
