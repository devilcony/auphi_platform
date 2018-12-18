package com.aofei.sys.service.impl;

import com.aofei.base.service.impl.BaseService;
import com.aofei.sys.entity.SmsCountry;
import com.aofei.sys.mapper.SmsCountryMapper;
import com.aofei.sys.model.request.SmsCountryRequest;
import com.aofei.sys.model.response.SmsCountryResponse;
import com.aofei.sys.service.ISmsCountryService;
import com.aofei.utils.BeanCopier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-12-18
 */
@Service
public class SmsCountryService extends BaseService<SmsCountryMapper, SmsCountry> implements ISmsCountryService {

    @Override
    public List<SmsCountryResponse> getSmsCountrys() {
        List<SmsCountry> list = baseMapper.findList(new SmsCountryRequest());
        return BeanCopier.copy(list, SmsCountryResponse.class);
    }
}
