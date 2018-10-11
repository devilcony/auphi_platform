package com.aofei.sys.entity;

import com.aofei.base.entity.DataEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
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
 * @since 2018-10-09
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("SYS_USER_ROLE")
public class UserRole extends DataEntity<UserRole> {

    private static final long serialVersionUID = 1L;

    @TableField(value = "C_USER_ID")
    private Long userId;

    @TableField("C_ROLE_ID")
    private Long roleId;


    @Override
    protected Serializable pkVal() {
        return this.userId;
    }

}
