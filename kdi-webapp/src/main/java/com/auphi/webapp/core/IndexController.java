package com.auphi.webapp.core;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;


/**
 * @auther Tony
 * @create 2017-02-26 22:48
 */
@Controller
@RequestMapping(value="/")
public class IndexController {


    @RequestMapping(value = "/login")
    public String toLogin(HttpServletRequest request,@RequestParam(value="lang", defaultValue="") String lang,Model model) {
        setlang(request,lang);
        model.addAttribute("lang",lang);
        return "login";
    }


    private void setlang(HttpServletRequest request, String lang) {
        if(lang.equals("zh")){
            Locale locale = new Locale("zh", "CN");
            request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME,locale);
        }
        else if(lang.equals("en")){
            Locale locale = new Locale("en", "US");
            request.getSession().setAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME,locale);
        }

    }

    @RequestMapping(method= RequestMethod.GET, value="/index")
    public String index(HttpServletRequest request,@RequestParam(value="lang", defaultValue="") String lang,Model model) {
        setlang(request,lang);
        model.addAttribute("lang",lang);
        return "index";
    }


    @RequestMapping(method= RequestMethod.GET, value="/register")
    public String register(HttpServletRequest request,@RequestParam(value="lang", defaultValue="") String lang,Model model) {
        setlang(request,lang);
        model.addAttribute("lang",lang);
        return "register";
    }

}
