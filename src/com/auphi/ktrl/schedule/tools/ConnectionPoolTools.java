/*******************************************************************************
 *
 * Auphi Data Integration PlatformKettle Platform
 * Copyright C 2011-2017 by Auphi BI : http://www.doetl.com 

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
package com.auphi.ktrl.schedule.tools;

import java.sql.Connection;

import javax.sql.DataSource;

import org.springframework.dao.support.DaoSupport;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.SQLExceptionTranslator;



public  class ConnectionPoolTools extends DaoSupport {
	
	public ConnectionPoolTools()
	{
	}
	private static JdbcTemplate jdbcTemplate;
	
	public  void setDataSource(DataSource dataSource) {
		if (jdbcTemplate == null || dataSource != jdbcTemplate.getDataSource()) {
			jdbcTemplate = createJdbcTemplate(dataSource);
			initTemplateConfig();
		}
	}

	protected JdbcTemplate createJdbcTemplate(DataSource dataSource) {
		return new JdbcTemplate(dataSource);
	}

	public static  DataSource getDataSource() {
		return jdbcTemplate == null ? null : jdbcTemplate.getDataSource();
	}

	public  void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		ConnectionPoolTools.jdbcTemplate = jdbcTemplate;
		System.out.println(ConnectionPoolTools.jdbcTemplate);
		initTemplateConfig();
	}

	public static JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	protected void initTemplateConfig() {
	}

	protected void checkDaoConfig() {
		if (jdbcTemplate == null)
			throw new IllegalArgumentException(
					"'dataSource' or 'jdbcTemplate' is required");
		else
			return;
	}

	protected  SQLExceptionTranslator getExceptionTranslator() {
		return getJdbcTemplate().getExceptionTranslator();
	}

	public static  Connection getConnection()

			throws CannotGetJdbcConnectionException {
		  //System.out.println("=================="+getDataSource());
		return DataSourceUtils.getConnection(getDataSource());
	}

	protected  void releaseConnection(Connection con) {
		DataSourceUtils.releaseConnection(con, getDataSource());
	}

}
