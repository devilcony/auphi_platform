package com.aofei.sys.mapper;

import com.aofei.base.annotation.MyBatisMapper;
import com.aofei.base.mapper.BaseMapper;
import com.aofei.sys.entity.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 菜单管理 Mapper 接口
 * </p>
 *
 * @author Tony
 * @since 2018-09-15
 */
@MyBatisMapper
public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> findMenusByUser(@Param("userId")Long userId);
}
