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
 * 菜单管理
 * </p>
 *
 * @author Tony
 * @since 2018-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("KDI_SYS_MENU")
public class Menu extends DataEntity<Menu> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_MENU", type = IdType.ID_WORKER)
    private Long menuId;
    /**
     * 父菜单ID，一级菜单为0
     */
    @TableField("ID_MENU_PARENT")
    private Long parentId;
    /**
     * 菜单名称
     */
    @TableField("NAME")
    private String name;
    /**
     * 菜单URL
     */
    @TableField("URL")
    private String url;
    /**
     * 授权(多个用逗号分隔，如：user:list,user:create)
     */
    @TableField("PERMS")
    private String perms;
    /**
     * 类型   0：目录   1：菜单   2：按钮
     */
    @TableField("TYPE")
    private Integer type;
    /**
     * 菜单图标
     */
    @TableField("ICON")
    private String icon;
    /**
     * 排序
     */
    @TableField("ORDER_NUM")
    private Integer orderNum;

    /**
     * 状态  0：正常   1：禁用
     */
    @TableField("STATUS")
    private Integer status;



    @Override
    protected Serializable pkVal() {
        return this.menuId;
    }

}
