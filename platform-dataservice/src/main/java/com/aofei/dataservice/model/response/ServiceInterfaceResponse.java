package com.aofei.dataservice.model.response;

import com.aofei.base.entity.DataEntity;
import com.aofei.base.model.response.BaseResponse;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author Tony
 * @since 2018-11-11
 */
@Data
public class ServiceInterfaceResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    private Long serviceId;

    private String serviceName;
    /**
     * Client调用时唯一识别的标示
     */
    private String serviceIdentify;

    private String serviceUrl;
    /**
     * 1表示job，2表示trans，3表示自定义
     */
    private Integer jobType;

    private String transName;
    /**
     * 用户可以自己选的，只支持FTP和Webservice
            1表示FTP，2表示Webservice

     */
    private Integer returnType;

    private String datasource;
    /**
     * 服务接口生成的结果数据超时时间，超过这个时间就要删除数据，单位分钟
     */
    private Integer timeout;
    /**
     * 1表示压缩，0表示不压缩
     */
    private Integer isCompress;

    private String tablename;

    private String delimiter;

    private String fields;

    private String conditions;


    private String interfaceDesc;

    private String databaseId;

    private String databaseName;

    private Integer jobConfigId;



}
