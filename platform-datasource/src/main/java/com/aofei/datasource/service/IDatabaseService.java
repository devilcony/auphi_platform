package com.aofei.datasource.service;

import com.aofei.datasource.entity.DatabaseEntity;
import com.aofei.datasource.model.request.DatabaseRequest;
import com.aofei.datasource.model.response.DatabaseResponse;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;

import java.sql.SQLException;

/**
 * ${DESCRIPTION}
 *
 * @auther Tony
 * @create 2018-10-21 21:49
 */
public interface IDatabaseService extends IService<DatabaseEntity> {

    Page<DatabaseResponse> getPage(Page<DatabaseEntity> page, DatabaseRequest request) throws KettleException, SQLException;
}
