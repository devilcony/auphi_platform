package com.aofei.base.model.response;

import lombok.Data;

/**
 * @auther Tony
 * @create 2018-09-18 12:45
 */
@Data
public class CurrentUserResponse {

    private Long userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 手机号
     */
    private String mobile;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;
}
