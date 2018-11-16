package com.aofei.dataservice.model.response;

import com.aofei.base.entity.DataEntity;
import com.aofei.base.model.response.BaseResponse;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author Tony
 * @since 2018-11-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("DATASERVICE_USER")
public class ServiceUserResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private Long userId;
    private String username;
    private String password;
    private String systemName;
    private String systemIp;
    private String systemDesc;




}
