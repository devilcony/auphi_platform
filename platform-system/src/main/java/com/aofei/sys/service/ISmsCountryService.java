package com.aofei.sys.service;

import com.aofei.sys.entity.SmsCountry;
import com.aofei.sys.model.response.SmsCountryResponse;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Tony
 * @since 2018-12-18
 */
public interface ISmsCountryService extends IService<SmsCountry> {

    List<SmsCountryResponse> getSmsCountrys();
}
