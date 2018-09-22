package com.aofei.base.service.impl;


import com.aofei.base.mapper.BaseMapper;
import com.aofei.utils.BeanCopier;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import java.io.Serializable;
import java.util.List;

public abstract class BaseService<M extends BaseMapper<T>, T> extends ServiceImpl<M, T>  {

    public Page convert(Page source, Class destinationClass) {
        List result = BeanCopier.copy(source.getRecords(), destinationClass);
        source.setRecords(result);
        return source;
    }


}
