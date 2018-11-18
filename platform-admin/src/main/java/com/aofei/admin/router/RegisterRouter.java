package com.aofei.admin.router;

import com.aofei.base.router.BaseRouter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/register")
public class RegisterRouter extends BaseRouter {


    @Override
    protected String getPrefix() {
        return "/register";
    }

    @RequestMapping(value = "/active", method = RequestMethod.GET)
    public String active(String auth_code ) {



        return   getPrefix() + "/active";
    }
}
