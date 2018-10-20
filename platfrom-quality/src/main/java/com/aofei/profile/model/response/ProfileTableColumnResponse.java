package com.aofei.profile.model.response;

import lombok.Data;

/**
 * <p>
 * 
 * </p>
 *
 * @author Tony
 * @since 2018-10-13
 */
@Data
public class ProfileTableColumnResponse  {

    private static final long serialVersionUID = 1L;

    private Long profileTableColumnId;
    private String profileTableColumnName;
    private String profileTableColumnType;
    private String profileTableColumnDesc;
    private Integer profileTableColumnOrder;



}
