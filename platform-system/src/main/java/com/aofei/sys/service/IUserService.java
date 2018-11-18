package com.aofei.sys.service;

import com.aofei.sys.entity.User;
import com.aofei.sys.model.request.RegisterRequest;
import com.aofei.sys.model.request.UserRequest;
import com.aofei.sys.model.response.UserResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 系统用户 服务类
 * </p>
 *
 * @author Tony
 * @since 2018-09-14
 */
public interface IUserService extends IService<User> {

    /**
     * 更新用户登录信息
     * @param userRequest
     */
    void updateLogin(UserRequest userRequest);

    @Transactional
    UserResponse auth(String username, String password);

    /**
     * 根据用户ID获取用户对象
     * @param userId 用户ID
     * @return
     */
    UserResponse get(Long userId);

    /**
     * 根据用户名获取用户对象
     * @param username 用户登录名
     * @return
     */
    UserResponse get(String username);


    /**
     * 查询用户列表
     * @param page
     * @param request
     * @return
     */
    Page<UserResponse> getPage(Page<User> page, UserRequest request);

    /**
     * 保存用户信息
     * @param request
     * @return
     */
    UserResponse save(UserRequest request);

    /**
     * 更新用户信息
     * @param request
     * @return
     */
    UserResponse update(UserRequest request);

    /**
     * 删除用户
     * @param userId
     * @return
     */
    int del(Long userId);

    /**
     * 查询角色下的用户列表
     * @param page
     * @param roleId
     * @return
     */
    Page<UserResponse> getUsers(Page<User> page, Long roleId);


    /**
     * 修改密码
     * @param userId
     * @param originalPassword
     * @param newPassword
     * @return
     */
    Integer modifyPassword(Long userId, String originalPassword, String newPassword);

    Integer register(RegisterRequest request);
}
