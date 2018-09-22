package com.aofei.authorization.repository;

/**
 * 通过Key获得用户模型的接口
 */
public interface UserModelRepository<T> {

    /**
     * 通过Key获得用户模型
     * @param username
     * @return
     */
    T getCurrentUser(String username);
}
