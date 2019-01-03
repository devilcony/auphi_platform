package com.aofei.dataservice.entity;

import com.aofei.base.entity.DataEntity;
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
@TableName("DATASERVICE_AUTH")
public class ServiceAuth extends DataEntity<ServiceAuth> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "AUTH_ID", type = IdType.ID_WORKER)
    private Long authId;
    @TableField("USER_ID")
    private Long userId;
    @TableField("SERVICE_ID")
    private Long serviceId;
    @TableField("AUTH_IP")
    private String authIp;
    @TableField("USE_DESC")
    private String useDesc;
    @TableField("USE_DEPT")
    private String useDept;
    @TableField("USER_NAME")
    private String userName;



    @Override
    protected Serializable pkVal() {
        return this.authId;
    }

}
