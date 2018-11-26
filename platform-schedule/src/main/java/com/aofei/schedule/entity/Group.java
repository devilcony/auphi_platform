package com.aofei.schedule.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableName;
import com.aofei.base.entity.DataEntity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 调度分组
 * </p>
 *
 * @author Tony
 * @since 2018-11-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("QRTZ_GROUP")
public class Group extends DataEntity<Group> {

    private static final long serialVersionUID = 1L;

    /**
     * 分组ID
     */
    @TableId(value = "ID_GROUP", type = IdType.ID_WORKER_STR)
    private String groupId;
    /**
     * 组织ID
     */
    @TableField("ORGANIZER_ID")
    private Long organizerId;
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
