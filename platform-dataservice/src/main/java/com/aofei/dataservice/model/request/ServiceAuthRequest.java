package com.aofei.dataservice.model.request;

import com.aofei.base.entity.DataEntity;
import com.aofei.base.model.request.BaseRequest;
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
public class ServiceAuthRequest extends BaseRequest<ServiceAuthRequest> {

    private static final long serialVersionUID = 1L;

    private Long authId;

    private Long userId;

    private Long serviceId;

    private String authIp;

    private String useDesc;

    private String useDept;

    private String userName;


}
