package com.aofei.profile.controller;

import com.aofei.base.controller.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 数据质量-数据剖析管理
 * </p>
 *
 * @author Tony
 * @since 2018-09-14
 */
@Log4j
@Api(tags = { "数据质量-数据剖析管理模块接口" })
@RestController
@RequestMapping(value = "/quality/profile", produces = {"application/json;charset=UTF-8"})
public class DataProfileController extends BaseController {


}
