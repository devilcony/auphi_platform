package com.aofei.admin.router;

import com.aofei.base.router.BaseRouter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @auther Tony
 * @create 2018-08-30 22:34
 */
@Controller
@RequestMapping(value = "{admin.path}")
public class AdminRouter extends BaseRouter {

    @Override
    protected String getPrefix() {
        return null;
    }


}
