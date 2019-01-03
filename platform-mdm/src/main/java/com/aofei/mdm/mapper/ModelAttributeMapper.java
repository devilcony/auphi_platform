package com.aofei.mdm.mapper;

import com.aofei.base.annotation.MyBatisMapper;
import com.aofei.base.mapper.BaseMapper;
import com.aofei.mdm.entity.ModelAttribute;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 主数据模型属性 Mapper 接口
 * </p>
 *
 * @author Tony
 * @since 2018-10-25
 */
@MyBatisMapper
public interface ModelAttributeMapper extends BaseMapper<ModelAttribute> {

    int deleteByModelId(@Param("modelId")Long modelId);


    List<ModelAttribute> findListByModelId(@Param("modelId")Long modelId);
}
