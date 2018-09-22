package com.aofei.kettle.model.request;

import com.aofei.base.model.request.BaseRequest;
import lombok.Data;

/**
 * @auther Tony
 * @create 2018-09-21 15:17
 */
@Data
public class DatabaseMetaRequest extends BaseRequest {

    /**
     * 链接名
     */
    private String name;

    /**
     * 类型
     */
    private String type;


    private String access;

    /**
     * 主机名
     */
    private String hostname;

    /**
     * 数据库名
     */
    private String databaseName;

    /**
     * 数据库用户名
     */
    private String username;

    /**
     * 数据库密码
     */
    private String password;

    private Boolean streamingResults;

    private String dataTablespace;

    private String indexTablespace;

    private String sqlServerInstance;

    private String usingDoubleDecimalAsSchemaTableSeparator;

    private String SAPLanguage;

    private String SAPSystemNumber;

    private String SAPClient;




}
