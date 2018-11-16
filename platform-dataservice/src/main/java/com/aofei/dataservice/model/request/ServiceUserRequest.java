package com.aofei.dataservice.model.request;

import com.aofei.base.model.request.BaseRequest;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author Tony
 * @since 2018-11-11
 */
@Data
public class ServiceUserRequest extends BaseRequest<ServiceUserRequest> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "USER_ID", type = IdType.ID_WORKER)
    private Integer userId;
    @TableField("USERNAME")
    private String username;
    @TableField("PASSWORD")
    private String password;
    @TableField("SYSTEM_NAME")
    private String systemName;
    @TableField("SYSTEM_IP")
    private String systemIp;
    @TableField("SYSTEM_DESC")
    private String systemDesc;




}
