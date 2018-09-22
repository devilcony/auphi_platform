package com.aofei.compare.controller;

import com.aofei.base.controller.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * 数据质量-统计数据比对管理
 * </p>
 *
 * @author Tony
 * @since 2018-09-14
 */
@Log4j
@Api(tags = { "数据质量-统计数据比对管理模块接口" })
@RestController
@RequestMapping(value = "/quality/compare", produces = {"application/json;charset=UTF-8"})
public class DataCompareController extends BaseController {
}
