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
@TableName("DATASERVICE_MONITOR_STEP_INFO")
public class ServiceMonitorStepInfo extends DataEntity<ServiceMonitorStepInfo> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "STEP_ID", type = IdType.ID_WORKER)
    private Long stepId;
    @TableField("MONITOR_ID")
    private Long monitorId;
    @TableField("STEPNAME")
    private String stepname;
    @TableField("READRECORDCOUNT")
    private String readrecordcount;
    @TableField("RETURNRECORDCOUNT")
    private Boolean returnrecordcount;
    @TableField("STARTDATE")
    private Date startdate;
    @TableField("ENDDATE")
    private Date enddate;
    @TableField("COSTTIME")
    private Integer costtime;
    @TableField("STATUS")
    private Integer status;
    @TableField("LOGINFO")
    private String loginfo;


    @Override
    protected Serializable pkVal() {
        return this.stepId;
    }

}
