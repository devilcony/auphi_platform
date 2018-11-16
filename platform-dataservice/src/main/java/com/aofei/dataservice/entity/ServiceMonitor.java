package com.aofei.dataservice.entity;

import com.aofei.base.entity.DataEntity;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author Tony
 * @since 2018-11-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("DATASERVICE_MONITOR")
public class ServiceMonitor extends DataEntity<ServiceMonitor> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "MONITOR_ID", type = IdType.ID_WORKER)
    private Long monitorId;
    @TableField("SERVICE_ID")
    private Long serviceId;
    @TableField("START_TIME")
    private Date startTime;
    @TableField("END_TIME")
    private Date endTime;
    @TableField("STATUS")
    private String status;
    private String userName;
    private String systemName;



    @Override
    protected Serializable pkVal() {
        return this.monitorId;
    }

}
