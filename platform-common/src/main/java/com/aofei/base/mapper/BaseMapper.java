package com.aofei.base.mapper;

import com.aofei.base.model.request.BaseRequest;
import com.baomidou.mybatisplus.plugins.Page;

import java.util.List;

/**
 * @auther Tony
 * @create 2018-09-14 20:21
 */
public interface BaseMapper<T> extends com.baomidou.mybatisplus.mapper.BaseMapper<T>{

    List<T> findList(Page<T> page, BaseRequest entity);

    List<T> findList(BaseRequest entity);
}
