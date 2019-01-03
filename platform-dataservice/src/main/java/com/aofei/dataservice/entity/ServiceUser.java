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
@TableName("DATASERVICE_USER")
public class ServiceUser extends DataEntity<ServiceUser> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "USER_ID", type = IdType.ID_WORKER)
    private Long userId;
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



    @Override
    protected Serializable pkVal() {
        return this.userId;
    }

}
