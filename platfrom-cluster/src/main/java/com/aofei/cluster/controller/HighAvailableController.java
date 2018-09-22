package com.aofei.cluster.controller;

import com.aofei.base.controller.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  数据库管理 前端控制器
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@Log4j
@Api(tags = { "HA集群管理-HA集群管理管理模块接口" })
@RestController
@RequestMapping(value = "/cluster/ha", produces = {"application/json;charset=UTF-8"})
public class HighAvailableController extends BaseController {


}
