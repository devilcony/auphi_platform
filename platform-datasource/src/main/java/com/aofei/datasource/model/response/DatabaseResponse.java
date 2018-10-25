package com.aofei.datasource.model.response;

import lombok.Data;

/**
 * <p>
 * 本地数据库
 * </p>
 *
 * @author Tony
 * @since 2018-09-22
 */
@Data
public class DatabaseResponse  {

    private static final long serialVersionUID = 1L;

    private Long databaseId;
    private String name;
    private Integer databaseTypeId;

    private String databaseTypeName;

    private Integer databaseContypeId;

    private String databaseContypeName;

    private String hostName;
    private String databaseName;
    private Integer port;
    private String username;
    private String password;
    private String servername;
    private String dataTbs;
    private String indexTbs;

}
