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
 * HADOOP管理
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("DATASOURCE_HADOOP")
public class Hadoop extends DataEntity<Hadoop> {

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
    @TableField("ORGANIZER_ID")
    private Long organizerId;

    @TableField(exist = false)
    private String organizerName;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
