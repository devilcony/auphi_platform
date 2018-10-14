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
@TableName("PROFILE_TABLE_COLUMN")
public class ProfileTableColumn extends DataEntity<ProfileTableColumn> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_PROFILE_TABLE_COLUMN", type = IdType.ID_WORKER)
    private Long profileTableColumnId;
    @TableField("PROFILE_TABLE_COLUMN_NAME")
    private String profileTableColumnName;
    @TableField("ID_PROFILE_TABLE")
    private Long profileTableId;
    @TableField("PROFILE_TABLE_COLUMN_DESC")
    private String profileTableColumnDesc;
    @TableField("PROFILE_TABLE_COLUMN_ORDER")
    private Integer profileTableColumnOrder;



    @Override
    protected Serializable pkVal() {
        return this.profileTableColumnId;
    }

}
