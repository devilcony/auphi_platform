package com.aofei.dataservice.entity;

import com.aofei.base.entity.DataEntity;
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
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("DATASERVICE_INTERFACE")
public class ServiceInterface extends DataEntity<ServiceInterface> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "SERVICE_ID", type = IdType.ID_WORKER)
    private Long serviceId;
    @TableField("SERVICE_NAME")
    private String serviceName;
    /**
     * Client调用时唯一识别的标示
     */
    @TableField("SERVICE_IDENTIFY")
    private String serviceIdentify;

    @TableField("SERVICE_URL")
    private String serviceUrl;
    /**
     * 1表示job，2表示trans，3表示自定义
     */
    @TableField("JOB_TYPE")
    private Integer jobType;

    @TableField("TRANS_NAME")
    private String transName;
    /**
     * 用户可以自己选的，只支持FTP和Webservice
            1表示FTP，2表示Webservice

     */
    @TableField("RETURN_TYPE")
    private Integer returnType;

    @TableField("DATASOURCE")
    private String datasource;
    /**
     * 服务接口生成的结果数据超时时间，超过这个时间就要删除数据，单位分钟
     */
    @TableField("TIMEOUT")
    private Integer timeout;
    /**
     * 1表示压缩，0表示不压缩
     */
    @TableField("IS_COMPRESS")
    private Integer isCompress;
    @TableField("TABLENAME")
    private String tablename;
    @TableField("DELIMITER")
    private String delimiter;
    @TableField("FIELDS")
    private String fields;
    @TableField("CONDITIONS")
    private String conditions;

    @TableField("CREATEDATE")
    private Date createdate;

    @TableField("INTERFACE_DESC")
    private String interfaceDesc;

    @TableField("ID_DATABASE")
    private String databaseId;

    @TableField(exist = false)
    private String databaseName;


    @TableField("job_Config_Id")
    private Integer jobConfigId;



    @Override
    protected Serializable pkVal() {
        return this.serviceId;
    }

}
