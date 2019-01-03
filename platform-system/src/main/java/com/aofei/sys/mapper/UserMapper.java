package com.aofei.sys.mapper;

import com.aofei.base.annotation.MyBatisMapper;
import com.aofei.base.mapper.BaseMapper;
import com.aofei.sys.entity.User;
import com.aofei.sys.model.request.UserRequest;
import com.baomidou.mybatisplus.plugins.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 系统用户 Mapper 接口
 * </p>
 *
 * @author Tony
 * @since 2018-09-15
 */
@MyBatisMapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * 更新用户的登录信息
     * @param userRequest
     * @return
     */
    int updateLoginInfo(UserRequest userRequest);

    /**
     * 根据用户名获取用户信息
     * @param username
     * @return
     */
    User findByUsername(@Param("username")String username);

    /**
     * 查询用户是所有权限
     * @param userId
     * @return
     */
    List<String> selectAllPerms(@Param("userId")Long userId);

    /**
     * 查询角色下的用户列表
     * @param page
     * @param roleId
     * @return
     */
    List<User> findUserByRoleCode(Page<User> page, @Param("roleId")Long roleId);
}
