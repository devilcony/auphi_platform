package com.aofei.datasource.service.impl;

import com.aofei.base.common.Const;
import com.aofei.base.service.impl.BaseService;
import com.aofei.datasource.entity.DatabaseEntity;
import com.aofei.datasource.mapper.DatabaseMapper;
import com.aofei.datasource.model.request.DatabaseRequest;
import com.aofei.datasource.model.response.DatabaseResponse;
import com.aofei.datasource.service.IDatabaseService;
import com.aofei.kettle.App;
import com.aofei.utils.StringUtils;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.plugins.pagination.IDialect;
import com.baomidou.mybatisplus.plugins.pagination.PageHelper;
import com.baomidou.mybatisplus.plugins.pagination.dialects.DB2Dialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.MySqlDialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.OracleDialect;
import com.baomidou.mybatisplus.plugins.pagination.dialects.PostgreDialect;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther Tony
 * @create 2018-10-21 21:50
 */
@Service
public class DatabaseService extends BaseService<DatabaseMapper, DatabaseEntity> implements IDatabaseService {

    @Override
    public Page<DatabaseResponse> getPage(Page<DatabaseEntity> page, DatabaseRequest request) throws KettleException, SQLException {


        String countSQl = "SELECT COUNT(*) FROM R_DATABASE";

        StringBuffer listSQL = new  StringBuffer("SELECT a.ID_DATABASE AS databaseId,a.NAME AS NAME,a.ID_DATABASE_TYPE AS databaseTypeId,b.DESCRIPTION AS databaseTypeName,a.ID_DATABASE_CONTYPE AS databaseContypeId,c.DESCRIPTION AS databaseContypeName,a.HOST_NAME AS hostName,a.DATABASE_NAME AS databaseName,a.PORT AS PORT,a.USERNAME AS username,a.PASSWORD AS PASSWORD,a.SERVERNAME AS servername,a.DATA_TBS AS dataTbs,a.INDEX_TBS AS indexTbs FROM R_DATABASE a LEFT JOIN R_DATABASE_TYPE b ON b.ID_DATABASE_TYPE=a.ID_DATABASE_TYPE LEFT JOIN R_DATABASE_CONTYPE c ON c.ID_DATABASE_CONTYPE=a.ID_DATABASE_CONTYPE ");
        if(!StringUtils.isEmpty(request.getName())){
            listSQL.append(" WHERE a.NAME LIKE ").append("'%").append(request.getName()).append("%'");
        }
        KettleDatabaseRepository repository = (KettleDatabaseRepository) App.getInstance().getRepository();
        repository.connect(Const.REPOSITORY_USERNAME,Const.REPOSITORY_PASSWORD);
        List<DatabaseResponse> list = new ArrayList<>();
        Database database = repository.getDatabase();
        IDialect dialect = null;
        switch (database.getDatabaseMeta().getDriverClass()){
            case "com.mysql.jdbc.Driver":
            case "org.gjt.mm.mysql.Driver":
            case "com.mysql.cj.jdbc.Driver":
                dialect = new MySqlDialect(); break;
            case "oracle.jdbc.driver.OracleDriver":
                dialect = new OracleDialect(); break;
            case "com.ibm.db2.jcc.DB2Driverr":
                dialect = new DB2Dialect(); break;
            case "org.postgresql.Driver":
                dialect = new PostgreDialect(); break;
        }

        String pageSQL = dialect.buildPaginationSql(listSQL.toString(), PageHelper.offsetCurrent(page), page.getSize());

        ResultSet objectrs = database.openQuery(pageSQL);
        while (objectrs.next()) {

            DatabaseResponse response = new DatabaseResponse();
            response.setDatabaseId(objectrs.getLong("databaseId"));
            response.setDatabaseName(objectrs.getString("name"));
            response.setDatabaseTypeId(objectrs.getInt("databaseTypeId"));
            response.setDatabaseTypeName(objectrs.getString("databaseTypeName"));
            response.setDatabaseContypeId(objectrs.getInt("databaseContypeId"));
            response.setDatabaseContypeName(objectrs.getString("databaseContypeName"));
            response.setHostName(objectrs.getString("hostName"));
            response.setDatabaseName(objectrs.getString("databaseName"));
            response.setUsername(objectrs.getString("username"));
            response.setPort(objectrs.getInt("port"));
            response.setServername(objectrs.getString("servername"));
            response.setIndexTbs(objectrs.getString("indexTbs"));
            list.add(response);
        }



        ResultSet countrs = database.openQuery(countSQl);
        int count = 0;
        while (countrs.next()) {
            count =  countrs.getInt(1);
        }
        Page<DatabaseResponse> responsePage = new Page<>(page.getCurrent(),count);
        responsePage.setRecords(list);
        objectrs.close();
        countrs.close();
        repository.disconnect();
        return responsePage;

    }
}
