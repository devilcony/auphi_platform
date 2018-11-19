package com.aofei.datasource.entity;

import com.baomidou.mybatisplus.activerecord.Model;
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
 * 本地数据库
 * </p>
 *
 * @author Tony
 * @since 2018-09-22
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("R_DATABASE")
public class DatabaseEntity extends Model<DatabaseEntity> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID_DATABASE", type = IdType.ID_WORKER)
    private Long databaseId;
    @TableField("NAME")
    private String name;
    @TableField("ID_DATABASE_TYPE")
    private Integer databaseTypeId;

    @TableField(exist = false)
    private String databaseTypeName;

    @TableField("ID_DATABASE_CONTYPE")
    private Integer databaseContypeId;

    @TableField(exist = false)
    private String databaseContypeName;

    @TableField("HOST_NAME")
    private String hostName;
    @TableField("DATABASE_NAME")
    private String databaseName;
    @TableField("PORT")
    private Integer port;
    @TableField("USERNAME")
    private String username;
    @TableField("PASSWORD")
    private String password;
    @TableField("SERVERNAME")
    private String servername;
    @TableField("DATA_TBS")
    private String dataTbs;
    @TableField("INDEX_TBS")
    private String indexTbs;


    @Override
    protected Serializable pkVal() {
        return this.databaseId;
    }

}
