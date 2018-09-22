package com.aofei.sys.model.request;

import com.aofei.base.model.request.BaseRequest;
import lombok.Getter;
import lombok.Setter;


/**
 * @auther Tony
 * @create 2018-09-16 22:50
 */
@Getter
@Setter
public class MenuRequest extends BaseRequest {

    private Long menuId;
    /**
     * 父菜单ID，一级菜单为0
     */
    private Long parentId;
    /**
     * 菜单名称
     */
    private String name;
    /**
     * 菜单URL
     */
    private String url;
    /**
     * 授权(多个用逗号分隔，如：user:list,user:create)
     */
    private String perms;
    /**
     * 类型   0：目录   1：菜单   2：按钮
     */
    private Integer type;
    /**
     * 菜单图标
     */
    private String icon;
    /**
     * 排序
     */
    private Integer orderNum;
    /**
     * 状态
     */
    private Integer status;
}
