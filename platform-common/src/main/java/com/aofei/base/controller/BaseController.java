package com.aofei.base.controller;

import com.alibaba.fastjson.JSONObject;
import com.aofei.base.model.request.PageRequest;
import com.aofei.base.model.vo.DataGrid;
import com.baomidou.mybatisplus.plugins.Page;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.*;

/**
 * Created by Hao on 2017-03-24.
 */
public abstract class BaseController {

    public List<String> getGlobalErrors(BindingResult bindingResult) {
        List<String> globalErrors = new ArrayList<String>();
        for (ObjectError oe : bindingResult.getGlobalErrors()) {
            globalErrors.add(oe.getDefaultMessage());
        }
        return globalErrors;
    }

    public Map<String, List<String>> getFieldErros(BindingResult bindingResult) {
        Map<String, List<String>> fieldErrors = new HashMap<String, List<String>>();
        for (FieldError fe : bindingResult.getFieldErrors()) {
            String f = fe.getField();

            if (fieldErrors.get(f) != null) {
                fieldErrors.get(f).add(fe.getDefaultMessage());
            } else {
                List<String> list = new LinkedList<String>();
                list.add(fe.getDefaultMessage());
                fieldErrors.put(f, list);
            }
        }
        return fieldErrors;
    }

    public String getValidateErrorMessage(BindingResult bindingResult) {
        String error = JSONObject.toJSONString(getFieldErros(bindingResult));
        return error;
    }

    /**
     * 构建DataGrid.
     *
     * @param page
     * @return
     */
    public <T> DataGrid buildDataGrid(Page<T> page) {

        return new DataGrid(page);
    }



    protected <T> Page<T> getPagination(PageRequest pageRequest) {
        Page<T> page =  new Page<T>(pageRequest.getPage(), pageRequest.getRows());
        page.setAsc("asc".equalsIgnoreCase(pageRequest.getOrder()));//升序 降序
        page.setOrderByField(pageRequest.getSort());//排序字段名称
        return page;
    }
}
