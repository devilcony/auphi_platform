package com.aofei.dataservice.model.request;

import com.aofei.base.model.request.BaseRequest;
import lombok.Data;

/**
 * <p>
 *
 * </p>
 *
 * @author Tony
 * @since 2018-11-11
 */
@Data
public class ServiceInterfaceRequest extends BaseRequest<ServiceInterfaceRequest> {

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

    private Integer jobConfigId;


}
