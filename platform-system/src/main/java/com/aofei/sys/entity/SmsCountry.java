package com.aofei.sys.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author Tony
 * @since 2018-12-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("SYS_SMS_COUNTRY")
public class SmsCountry extends Model<SmsCountry> {

    private static final long serialVersionUID = 1L;

    @TableField("COUNTRY_CODE")
    private String countryCode;
    @TableField("COUNTRY_NAME")
    private String countryName;



    @Override
    protected Serializable pkVal() {
        return this.countryCode;
    }

}
