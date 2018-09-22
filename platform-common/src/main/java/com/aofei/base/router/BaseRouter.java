package com.aofei.base.router;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Created by Hao on 2017-03-24.
 */
public abstract class BaseRouter {

    protected abstract String getPrefix();

    protected final String ADD = getPrefix() + "/add";
    protected final String EDIT = getPrefix() + "/edit";
    protected final String FORM = getPrefix() + "/form";
    protected final String VIEW = getPrefix() + "/view";
    protected final String LIST = getPrefix() + "/list";

    /**
     * 通用跳转到新增页面
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    protected String add() {
        return ADD;
    }


    /**
     * 通用跳转到编辑页面
     */
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String edit(Model model, @PathVariable("id") Long id) {
        model.addAttribute("id", id);
        return EDIT;
    }

    /**
     * 通用跳转到查看详情页面
     */
    @RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
    public String view(Model model, @PathVariable("id") Long id) {
        model.addAttribute("id", id);
        return VIEW;
    }

    /**
     * 通用跳转到查看详情页面
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String list() {
        return LIST;
    }


}
