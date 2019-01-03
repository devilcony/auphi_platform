package com.aofei.schedule.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import com.aofei.base.entity.DataEntity;

import com.baomidou.mybatisplus.annotations.Version;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 调度用户表
 * </p>
 *
 * @author Tony
 * @since 2018-11-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("QRTZ_JOB_ORGANIZER")
public class JobOrganizer extends Model<JobOrganizer> {

    private static final long serialVersionUID = 1L;

    public JobOrganizer(){}

    public JobOrganizer(Long organizerId,String jobName,String jobGroup){
        setOrganizerId(organizerId);
        setJobName(jobName);
        setJobGroup(jobGroup);
    }


    /**
     * 组织ID
     */
    @TableField("ORGANIZER_ID")
    private Long organizerId;
    /**
     * 调度名称
     */
    @TableField("JOB_NAME")
    private String jobName;
    /**
     * 调度分组名称
     */
    @TableField("JOB_GROUP")
    private String jobGroup;


    @Override
    protected Serializable pkVal() {
        return null;
    }
}
