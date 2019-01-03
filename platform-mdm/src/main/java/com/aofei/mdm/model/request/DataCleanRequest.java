package com.aofei.mdm.model.request;

import com.aofei.base.model.request.BaseRequest;
import lombok.Data;


/**
 * @auther Tony
 * @create 2018-11-08 22:45
 */
@Data
public class DataCleanRequest extends BaseRequest<DataCleanRequest> {

    private Long id;
    private Long modelId;
    private String attributeModel;
    private String repositoryName;
    private String databaseId;
    private String schemaName;
    private String tableName;
    private String primaryKey;
    private String columnName;
    private String whereCondition;
    private Integer mapingMode;
    private Integer mapingIdDatabase;
    private String mapingSchemaName;
    private String mapingTableName;

}
