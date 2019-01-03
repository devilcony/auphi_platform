package com.aofei.base.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.aofei.base.common.UserUtil;
import com.aofei.base.model.response.CurrentUserResponse;
import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlTransient;

/**
 * @auther Tony
 * @create 2018-09-13 14:54
 */
@Setter
@Getter
public abstract class DataEntity<T> extends BaseEntity<T> {

    /**
     * 创建者ID
     */
    @TableField("CREATE_USER")
    private String createUser;
    /**
     * 创建者ID
     */
    @TableField("UPDATE_USER")
    private String updateUser;


    public   void preInsert() {
        super.preInsert();
        if(getCurrentUser()!=null){
            setCreateUser(getCurrentUser().getUsername());
            setUpdateUser(getCreateUser());
        }
    }


    @JSONField(serialize = false)
    @XmlTransient
    public CurrentUserResponse getCurrentUser() {
        return UserUtil.getSessionUser();
    }

    /**
     * 更新之前执行方法，子类实现
     */
    public void preUpdate() {

        super.preUpdate();
        if(getCurrentUser()!=null){
            setUpdateUser(getCurrentUser().getUsername());

        }
    }
}
