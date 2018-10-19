package com.aofei.compare.model.request;

import com.aofei.base.model.request.BaseRequest;
import com.baomidou.mybatisplus.annotations.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author Tony
 * @since 2018-10-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("COMPARE_SQL_RESULT")
public class CompareSqlResultRequest extends BaseRequest<CompareSqlResultRequest> {

    private static final long serialVersionUID = 1L;

    private Long compareSqlResultId;

    private Long compareSqlColumnId;

    private String columnValue;

    private String referenceColumnValue;
    /**
     * 1 equals,   0  not equals
     */
    @ApiModelProperty(value = "1 equals,   0  not equals")
    private Integer compareResult;

    public CompareSqlResultRequest() {

    }

    public CompareSqlResultRequest(Long compareSqlColumnId) {
        setCompareSqlColumnId(compareSqlColumnId);
    }
}
