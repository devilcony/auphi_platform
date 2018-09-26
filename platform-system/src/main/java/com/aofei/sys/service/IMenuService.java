package com.aofei.sys.service;

import com.aofei.sys.entity.Menu;
import com.aofei.sys.model.request.MenuRequest;
import com.aofei.sys.model.response.MenuResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 * 菜单管理 服务类
 * </p>
 *
 * @author Tony
 * @since 2018-09-14
 */
public interface IMenuService extends IService<Menu> {

    /**
     * 获取 Menu 列表
     * @param page
     * @param request
     * @return
     */
    Page<MenuResponse> getPage(Page<Menu> page, MenuRequest request);

    /**
     * 获取 Menu 列表
     * @param request
     * @return
     */
    List<MenuResponse> getMenus(MenuRequest request);
    /**
     * 保存 Menu 信息
     * @param request
     * @return
     */
    MenuResponse save(MenuRequest request);

    /**
     * 更新 Menu 信息
     * @param request
     * @return
     */
    MenuResponse update(MenuRequest request);

    /**
     * 根据Id 查询 Menu
     * @param menuId
     * @return
     */
    MenuResponse get(Long menuId);
    /**
     * 根据Id 删除 Menu
     * @param menuId
     * @return
     */
    int del(Long menuId);

    /**
     * 获取用户菜单
     * @param userId
     * @return
     */
    List<MenuResponse> getMenusByUser(Long userId);
}
