package com.aofei.profile.entity;

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
 * @since 2018-10-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("PROFILE_TABLE_GROUP")
public class ProfileTableGroup extends DataEntity<ProfileTableGroup> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_PROFIEL_TABLE_GROUP", type = IdType.ID_WORKER)
    private Long profielTableGroupId;
    @TableField("PROFIEL_TABLE_GROUP_NAME")
    private String profielTableGroupName;
    @TableField("PROFIEL_TABLE_GROUP_DESC")
    private String profielTableGroupDesc;



    @Override
    protected Serializable pkVal() {
        return this.profielTableGroupId;
    }

}
