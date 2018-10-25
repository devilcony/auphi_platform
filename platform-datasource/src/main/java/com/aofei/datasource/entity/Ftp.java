package com.aofei.datasource.entity;

import com.aofei.base.entity.DataEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * FTP管理
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("DATASOURCE_FTP")
public class Ftp extends DataEntity<Ftp> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_FTP", type = IdType.ID_WORKER)
    private Long ftpId;
    @TableField("NAME")
    private String name;
    @TableField("HOST_NAME")
    private String hostName;
    @TableField("PORT")
    private Integer port;
    @TableField("USERNAME")
    private String username;
    @TableField("PASSWORD")
    private String password;
    @TableField("ORGANIZER_ID")
    private Long organizerId;



    @Override
    protected Serializable pkVal() {
        return this.ftpId;
    }

}
