package com.aofei.schedule.entity;

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
 * 调度分组
 * </p>
 *
 * @author Tony
 * @since 2018-10-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("KDI_QRTZ_GROUP")
public class Group extends DataEntity<Group> {

    private static final long serialVersionUID = 1L;

    /**
     * 分组ID
     */
    @TableId(value = "ID_GROUP", type = IdType.ID_WORKER)
    private Long groupId;
    /**
     * 分组名称
     */
    @TableField("GROUP_NAME")
    private String groupName;
    /**
     * 分组描述
     */
    @TableField("DESCRIPTION")
    private String description;


    @Override
    protected Serializable pkVal() {
        return this.groupId;
    }

}
