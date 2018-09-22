package com.aofei.sys.entity;

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
 * 部门管理
 * </p>
 *
 * @author Tony
 * @since 2018-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("KDI_SYS_DEPT")
public class Dept extends DataEntity<Dept> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "DEPT_ID", type = IdType.ID_WORKER)
    private Long deptId;
    /**
     * 上级部门ID，一级部门为0
     */
    @TableField("PARENT_ID")
    private Long parentId;
    /**
     * 部门名称
     */
    @TableField("NAME")
    private String name;
    /**
     * 排序
     */
    @TableField("ORDER_NUM")
    private Integer orderNum;



    @Override
    protected Serializable pkVal() {
        return this.deptId;
    }

}
