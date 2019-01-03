package com.aofei.datasource.model.response;

import com.aofei.base.model.response.BaseResponse;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

/**
 * <p>
 * HADOOP管理
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Data
public class HadoopResponse extends BaseResponse {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.ID_WORKER)
    private Long id;
    @TableField("SERVER")
    private String server;
    @TableField("PORT")
    private Integer port;
    @TableField("USERID")
    private String userid;
    @TableField("PASSWORD")
    private String password;


}
