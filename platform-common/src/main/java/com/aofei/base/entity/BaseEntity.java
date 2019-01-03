package com.aofei.base.entity;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @auther Tony
 * @create 2018-09-12 14:29
 */
@Setter
@Getter
public abstract class BaseEntity<T> extends Model{

    /**
     * 删除标记（0：正常；1：删除；2：审核；）
     */
    public static final Integer DEL_FLAG_NORMAL = 0;
    public static final Integer DEL_FLAG_DELETE = 1;
    public static final Integer DEL_FLAG_AUDIT = 2;


    /**
     * 创建时间
     */
    @TableField("CREATE_TIME")
    private Date createTime;
    /**
     * 最后时间更新
     */
    @TableField("UPDATE_TIME")
    private Date updateTime;
    /**
     * 是否删除  1：已删除  0：正常
     */
    @TableField("DEL_FLAG")
    private Integer delFlag;



    /**
     * 插入之前执行方法，子类实现
     */

    protected void preInsert() {
       setCreateTime(new Date());
       setUpdateTime(new Date());
       setDelFlag(DEL_FLAG_NORMAL);
    }


    /**
     * 更新之前执行方法，子类实现
     */
    protected void preUpdate() {
        setUpdateTime(new Date());
    }


    public BaseEntity() {
        super();
    }



}
