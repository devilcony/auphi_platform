package com.aofei.sys.service.impl;

import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;
import com.aofei.log.annotation.Log;
import com.aofei.sys.entity.Menu;
import com.aofei.sys.mapper.MenuMapper;
import com.aofei.sys.model.request.MenuRequest;
import com.aofei.sys.model.response.MenuResponse;
import com.aofei.sys.service.IMenuService;
import com.aofei.utils.BeanCopier;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 菜单管理 服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-09-14
 */
@Service
public class MenuService extends BaseService<MenuMapper, Menu> implements IMenuService {

    @Override
    public Page<MenuResponse> getPage(Page<Menu> page, MenuRequest request) {
        List<Menu> list = baseMapper.findList(page, request);
        page.setRecords(list);
        return convert(page, MenuResponse.class);
    }

    /**
     * 获取 Menu 列表
     * @param request
     * @return
     */
    @Override
    public List<MenuResponse> getMenus(MenuRequest request) {
        List<Menu> list = baseMapper.findList(request);
        return BeanCopier.copy(list,MenuResponse.class);
    }

    @Override
    @Log(module = "系统菜单", description = "新建菜单信息")
    @Transactional
    public MenuResponse save(MenuRequest request) {
        Menu Menu = BeanCopier.copy(request, Menu.class);
        Menu.preInsert();
        super.insert(Menu);
        return BeanCopier.copy(Menu, MenuResponse.class);
    }

    @Override
    @Log(module = "系统菜单", description = "修改菜单信息")
    @Transactional
    public MenuResponse update(MenuRequest request) {
        Menu existing = selectById(request.getMenuId());
        if (existing != null) {
            existing.setName(request.getName());
            existing.setUrl(request.getUrl());
            existing.setIcon(request.getIcon());
            existing.setPerms(request.getPerms());
            existing.setStatus(request.getStatus());
            existing.setOrderNum(request.getOrderNum());
            existing.setParentId(request.getParentId());
            existing.preUpdate();

            super.insertOrUpdate(existing);

            return BeanCopier.copy(existing, MenuResponse.class);
        } else {
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public MenuResponse get(Long menuId) {
        Menu existing = selectById(menuId);
        if(existing!=null){
            return BeanCopier.copy(existing, MenuResponse.class);
        }else{
            //不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    @Log(module = "系统菜单", description = "删除菜单信息")
    @Transactional
    public int del(Long menuId) {
        Menu existing = selectById(menuId);
        if (existing != null) {
            super.deleteById(menuId);
            return 1;
        } else {
            // 不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public List<MenuResponse> getMenusByUser(Long userId) {
        List<Menu> list = baseMapper.findMenusByUser(userId);
        return BeanCopier.copy(list,MenuResponse.class);
    }
}
