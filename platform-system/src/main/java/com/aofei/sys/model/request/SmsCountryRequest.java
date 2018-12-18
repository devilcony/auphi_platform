package com.aofei.sys.model.request;

import com.aofei.base.model.request.BaseRequest;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Tony
 * @since 2018-12-18
 */
@Data
public class SmsCountryRequest extends BaseRequest<SmsCountryRequest> {

    private static final long serialVersionUID = 1L;

    private String countryCode;

    private String countryName;



}
