package com.aofei.datasource.controller;

import com.aofei.base.controller.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  FTP管理 前端控制器
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@Log4j
@Api(tags = { "数据源管理-FTP Server管理模块接口" })
@RestController
@RequestMapping(value = "/datasource/ftp", produces = {"application/json;charset=UTF-8"})
public class FtpServerController extends BaseController {

}
