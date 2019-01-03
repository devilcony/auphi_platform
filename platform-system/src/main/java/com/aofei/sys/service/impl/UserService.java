package com.aofei.sys.service.impl;

import com.aofei.base.common.Const;
import com.aofei.base.exception.ApplicationException;
import com.aofei.base.exception.StatusCode;
import com.aofei.base.service.impl.BaseService;
import com.aofei.kettle.App;
import com.aofei.log.annotation.Log;
import com.aofei.sys.entity.Organizer;
import com.aofei.sys.entity.User;
import com.aofei.sys.exception.SystemError;
import com.aofei.sys.mapper.UserMapper;
import com.aofei.sys.model.request.PhoneRegisterRequest;
import com.aofei.sys.model.request.UserRequest;
import com.aofei.sys.model.response.UserResponse;
import com.aofei.sys.service.IUserService;
import com.aofei.utils.BeanCopier;
import com.aofei.utils.MD5Utils;
import com.aofei.utils.StringUtils;
import com.baomidou.mybatisplus.plugins.Page;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

/**
 * <p>
 * 系统用户 服务实现类
 * </p>
 *
 * @author Tony
 * @since 2018-09-14
 */
@Service
public class UserService extends BaseService<UserMapper, User> implements IUserService {

    @Value("#{propertiesReader['disk.root.dir']}")
    private String rootDir  ; //磁盘根目录

    /**
     * 更新用户登录信息
     * @param userRequest
     */

    @Override
    @Transactional
    @Log(module = "系统用户", description = "用户登录")
    public void updateLogin(UserRequest userRequest) {
        baseMapper.updateLoginInfo(userRequest);
    }

    @Override
    public UserResponse auth(String username, String password) {
        User existing = baseMapper.findByUsername(username);
        if (existing != null) {
            String encryData = MD5Utils.getStringMD5(password);
            if (existing.getPassword().equals(encryData)) {//验证密码是否正确

                if(User.STATUS_NORMAL.equals(existing.getUserStatus())){
                    return BeanCopier.copy(existing, UserResponse.class);
                }else{
                    //账户被禁用
                    throw new ApplicationException(SystemError.STATUS_DISABLED.getCode(), SystemError.STATUS_DISABLED.getMessage());
                }
            } else {
                //密码错误
                throw new ApplicationException(SystemError.LOGIN_FAILED.getCode(), SystemError.LOGIN_FAILED.getMessage());
            }
        } else {
            //用户不存在
            throw new ApplicationException(SystemError.LOGIN_FAILED.getCode(), SystemError.LOGIN_FAILED.getMessage());
        }
    }


    /**
     * 根据用户ID获取用户对象
     * @param userId 用户ID
     * @return
     */
    @Override
    public UserResponse get(Long userId) {
        User existing = selectById(userId);
        if(existing!=null){
            return BeanCopier.copy(existing, UserResponse.class);
        }else{
            //用户不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    /**
     * 根据用户名获取用户对象
     * @param username 用户登录名
     * @return
     */
    @Override
    public UserResponse get(String username) {
        User existing = baseMapper.findByUsername(username);
        if(existing!=null){
            return BeanCopier.copy(existing, UserResponse.class);
        }else{
            //用户不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    public Page<UserResponse> getPage(Page<User> page, UserRequest request) {

        List<User> list = baseMapper.findList(page,request);

        page.setRecords(list);
        return convert(page, UserResponse.class);
    }

    @Override
    @Transactional
    @Log(module = "系统用户", description = "添加用户信息")
    public UserResponse save(UserRequest request) {
        User existing = baseMapper.findByUsername(request.getUsername());
        if(existing != null){
            throw new ApplicationException(StatusCode.CONFLICT.getCode(), StringUtils.getMessage("System.Error.UsernameExist"));
        }
        User user = BeanCopier.copy(request, User.class);
        user.setPassword(MD5Utils.getStringMD5(request.getPassword()));//密码进行MD5加密
        user.preInsert();
        super.insert(user);
        return BeanCopier.copy(user, UserResponse.class);
    }

    /**
     * 修改用户信息
     * @param request
     * @return
     */
    @Override
    @Transactional
    @Log(module = "系统用户", description = "修改用户信息")
    public UserResponse update(UserRequest request) {

        User existing = selectById(request.getUserId());
        if (existing != null) {
            existing.setEmail(request.getEmail());
            existing.setDescription(request.getDescription());
            existing.setUserStatus(request.getUserStatus());
            existing.preUpdate();
            super.insertOrUpdate(existing);
            return BeanCopier.copy(existing, UserResponse.class);
        } else {
            //用户不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }

    }

    /**
     * 删除用户
     * @param userId
     * @return
     */
    @Override
    @Log(module = "系统用户", description = "删除用户信息")
    public int del(Long userId) {
        User existing = selectById(userId);
        if (existing != null) {
            super.deleteById(userId);
            return 1;
        } else {
            // 用户不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    /**
     * 修改密码
     * @param userId 用户ID
     * @param originalPassword 原密码
     * @param newPassword      新密码
     */
    @Override
    @Transactional
    @Log(module = "系统用户", description = "修改密码")
    public Integer modifyPassword(Long userId, String originalPassword, String newPassword) {
        User existing = selectById(userId);
        if (existing != null) {
            String encryData = MD5Utils.getStringMD5(originalPassword);
            if (existing.getPassword().equals(encryData)) {//验证原密码是否正确
                existing.setPassword(MD5Utils.getStringMD5(newPassword));
                super.insertOrUpdate(existing);
                return 1;
            } else {
                //原密码错误
                throw new ApplicationException(SystemError.ORIGINAL_PASSWORD_ERROR.getCode(), StringUtils.getMessage("System.Error.OriginalPasswordError"));
            }
        } else {
            //用户不存在
            throw new ApplicationException(StatusCode.NOT_FOUND.getCode(), StatusCode.NOT_FOUND.getMessage());
        }
    }

    @Override
    @Transactional
    public Integer register(PhoneRegisterRequest request) throws KettleException {

        Organizer organizer = new Organizer();
        organizer.setName(request.getOrganizerName());
        organizer.setStatus(1);
        organizer.setMobile(request.getMobilephone());
        organizer.insert();
        User existing = new User();
        existing.setCountryCode(request.getCountryCode());
        existing.setUserStatus(Const.NO);
        existing.setMobilephone(request.getMobilephone());
        existing.setUsername(request.getUsername());
        existing.setPassword(MD5Utils.getStringMD5(request.getPassword()));
        existing.setOrganizerId(organizer.getOrganizerId());
        baseMapper.insert(existing);
        Repository repository = App.getInstance().getRepository();
        RepositoryDirectoryInterface path = repository.findDirectory("/");
        repository.createRepositoryDirectory(path, String.valueOf(organizer.getOrganizerId()));

        File file=new File(Const.getUserDir(organizer.getOrganizerId()));
        if(!file.exists()){//如果文件夹不存在
            file.mkdir();//创建文件夹
        }

        return 1;
    }



    /**
     * 查询角色下的用户列表
     * @param page
     * @param roleId
     * @return
     */
    @Override
    public Page<UserResponse> getUsers(Page<User> page, Long roleId) {
        List<User> users = baseMapper.findUserByRoleCode(page, roleId);
        page.setRecords(users);
        return convert(page, UserResponse.class);
    }


}
