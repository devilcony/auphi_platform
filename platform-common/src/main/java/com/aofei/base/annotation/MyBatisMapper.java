package com.aofei.base.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 标识MyBatis的Mapper,方便{@link org.mybatis.spring.mapper.MapperScannerConfigurer}的扫描。
 * @auther Tony
 * @create 2018-09-14 20:35
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Component
public  @interface MyBatisMapper {
}
