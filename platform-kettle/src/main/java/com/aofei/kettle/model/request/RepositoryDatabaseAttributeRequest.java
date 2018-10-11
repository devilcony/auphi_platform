package com.aofei.kettle.model.request;

import com.aofei.base.model.request.BaseRequest;
import com.aofei.kettle.entity.RepositoryDatabaseAttribute;
import lombok.Data;

/**
 * <p>
 * 资源库链接属性
 * </p>
 *
 * @author Tony
 * @since 2018-09-21
 */
@Data
public class RepositoryDatabaseAttributeRequest extends BaseRequest<RepositoryDatabaseAttribute> {

    private static final long serialVersionUID = 1L;


    /**
     * 资源数据库链接ID
     */
    private Long repositoryConnectionId;
    /**
     * 属性名
     */
    private String code;
    /**
     * 属性值
     */
    private String valueStr;

    public RepositoryDatabaseAttributeRequest() {

    }

    public RepositoryDatabaseAttributeRequest(String code, String valueStr) {
        this.code = code;
        this.valueStr = valueStr;
    }
}
