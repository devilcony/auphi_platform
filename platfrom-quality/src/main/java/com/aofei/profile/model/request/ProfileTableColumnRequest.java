package com.aofei.profile.model.request;

import com.aofei.base.model.request.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author Tony
 * @since 2018-10-13
 */
@Data
public class ProfileTableColumnRequest extends BaseRequest<ProfileTableColumnRequest> {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    private Long profileTableColumnId;

    @ApiModelProperty(value = "列名")
    private String profileTableColumnName;


    private String profileTableColumnDesc;

    private Integer profileTableColumnOrder;




}
