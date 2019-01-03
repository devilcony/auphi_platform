/*******************************************************************************
 *
 * Auphi Data Integration PlatformKettle Platform
 * Copyright C 2011-2018 by Auphi BI : http://www.doetl.com

 * Support：support@pentahochina.com
 *
 *******************************************************************************
 *
 * Licensed under the LGPL License, Version 3.0 the "License";
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    https://opensource.org/licenses/LGPL-3.0

 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************/
package com.aofei.authorization.manager.impl;

import javax.sql.DataSource;
import java.sql.*;

/**
 * 使用MySQL存储Token
 */
public class MySQLTokenManager extends AbstractTokenManager {

    /**
     * 数据源
     */
    protected DataSource dataSource;

    /**
     * 存放鉴权信息的表名
     */
    protected String tableName;

    /**
     * 存放Key的字段名
     */
    protected String keyColumnName;

    /**
     * 存放Token的字段名
     */
    protected String tokenColumnName;

    /**
     * 存放过期时间的字段名
     */
    protected String expireAtColumnName;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setKeyColumnName(String keyColumnName) {
        this.keyColumnName = keyColumnName;
    }

    public void setTokenColumnName(String tokenColumnName) {
        this.tokenColumnName = tokenColumnName;
    }

    public void setExpireAtColumnName(String expireAtColumnName) {
        this.expireAtColumnName = expireAtColumnName;
    }

    @Override
    public void delSingleRelationshipByKey(String key) {
        String sql = String.format("delete from %s where %s = ?", tableName, keyColumnName);
        update(sql, key);
    }

    @Override
    public void delRelationshipByToken(String token) {
        String sql = String.format("delete from %s where %s = ?", tableName, tokenColumnName);
        update(sql, token);
    }

    @Override
    protected void createMultipleRelationship(String key, String token) {
        String sql = String.format("insert into %s (%s, %s, %s) values(?, ?, ?)", tableName, keyColumnName, tokenColumnName, expireAtColumnName);
        update(sql, key, token, new Timestamp(System.currentTimeMillis() + tokenExpireSeconds * 1000));
    }

    @Override
    protected void createSingleRelationship(String key, String token) {
        String select = String.format("select count(*) from %s where %s = ?", tableName, keyColumnName);
        Number count = query(Number.class, select, key);
        if (count != null && count.intValue() > 0) {
            String sql = String.format("update %s set %s = ?, %s = ? where %s = ?", tableName, tokenColumnName, expireAtColumnName, keyColumnName);
            update(sql, token, new Timestamp(System.currentTimeMillis() + tokenExpireSeconds * 1000), key);
        } else {
            String sql = String.format("insert into %s (%s, %s, %s) values(?, ?, ?)", tableName, keyColumnName, tokenColumnName, expireAtColumnName);
            update(sql, key, token, new Timestamp(System.currentTimeMillis() + tokenExpireSeconds * 1000));
        }
    }

    @Override
    public String getKeyByToken(String token) {
        String sql = String.format("select %s from %s where %s = ? and %s > ? limit 1", keyColumnName, tableName, tokenColumnName, expireAtColumnName);
        return query(String.class, sql, token, new Timestamp(System.currentTimeMillis()));
    }

    @Override
    protected void flushExpireAfterOperation(String key, String token) {
        String flushExpireAtSql = String.format("update %s set %s = ? where %s = ?", tableName, expireAtColumnName, tokenColumnName);
        update(flushExpireAtSql, new Timestamp(System.currentTimeMillis() + tokenExpireSeconds * 1000), token);
    }

    private void update(String sql, Object... args) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int i = 1;
            for (Object arg : args) {
                statement.setObject(i++, arg);
            }
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private <T> T query(Class<T> clazz, String sql, Object... args) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            int i = 1;
            for (Object arg : args) {
                statement.setObject(i++, arg);
            }
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                Object obj = resultSet.getObject(1);
                if (clazz.isInstance(obj)) {
                    return (T)obj;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
