package com.aofei.datasource.model.request;

import com.aofei.base.model.request.BaseRequest;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * FTP管理
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Data
public class FtpRequest extends BaseRequest<FtpRequest> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_FTP", type = IdType.ID_WORKER)
    private Long ftpId;
    @TableField("NAME")
    private String name;
    @TableField("HOST_NAME")
    private String hostName;
    @TableField("PORT")
    private Integer port;
    @TableField("USERNAME")
    private String username;
    @TableField("PASSWORD")
    private String password;

    @ApiModelProperty(hidden = true)
    private Long organizerId;


}
