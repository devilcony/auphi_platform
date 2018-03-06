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
package com.auphi.ktrl.conn.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.MSSQLServerDatabaseMeta;
import org.pentaho.di.core.database.MySQLDatabaseMeta;
import org.pentaho.di.core.database.OracleDatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;

import com.auphi.ktrl.conn.bean.ConnConfigBean;
import com.auphi.ktrl.mdm.domain.MdmModelAttribute;
import com.auphi.ktrl.util.Constants;
import com.auphi.ktrl.util.InitServlet;
import com.auphi.ktrl.util.StringUtil;

public class DataBaseUtil {
	
	private static final int PAGE_SIZE = Integer.parseInt(Constants.get("PageSize", "15"));
	private static Logger logger = Logger.getLogger(DataBaseUtil.class);
	
	public static final String MYSQL = "MySQL";
	public static final String ORACLE = "Oracle";
	public static final String SQLSERVER = "MS SQL Server";
	public static final String DB2 = "IBM DB2";
	public static final String KINGBASE= "KingbaseES";
	public static final String POSTGRESQL= "PostgreSQL";
	
	public static ConnConfigBean connConfig = null;
	 
	static{
		try {
			connConfig = getQuartzConfig();
		} catch (Exception e) { 
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * get quartz connection
	 * @return
	 */
	public static ConnConfigBean getQuartzConfig(){
		Properties quartzProp = new Properties();
		try{
			InputStream quartzPropInputStream = DataBaseUtil.class.getResourceAsStream("/quartz.properties");
			quartzProp.load(quartzPropInputStream);
			quartzPropInputStream.close();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		ConnConfigBean connConfigBean = new ConnConfigBean();
		String dsName = quartzProp.getProperty("org.quartz.jobStore.dataSource");
		
		String dbms = quartzProp.getProperty("org.quartz.dataSource." + dsName + ".dbms");
		String ip = quartzProp.getProperty("org.quartz.dataSource." + dsName + ".ip");
		String port = quartzProp.getProperty("org.quartz.dataSource." + dsName + ".port");
		String database = quartzProp.getProperty("org.quartz.dataSource." + dsName + ".database");
		
		connConfigBean.setIp(ip);
		connConfigBean.setPort(port);
		connConfigBean.setDatabase(database);
		connConfigBean.setId(StringUtil.createNumberString(8));
		connConfigBean.setName(dbms);
		connConfigBean.setDbms(dbms);
		connConfigBean.setUsername(quartzProp.getProperty("org.quartz.dataSource." + dsName + ".user"));
		connConfigBean.setPassword(quartzProp.getProperty("org.quartz.dataSource." + dsName + ".password"));
		connConfigBean.setValidateQuery(quartzProp.getProperty("org.quartz.dataSource." + dsName + ".validationQuery"));
		connConfigBean.setDriverclass(quartzProp.getProperty("org.quartz.dataSource." + dsName + ".driver"));
		connConfigBean.setMaxconn(quartzProp.getProperty("org.quartz.dataSource." + dsName + ".maxConnections"));
		connConfigBean.setMaxidle(quartzProp.getProperty("org.quartz.dataSource." + dsName + ".maxIdle"));
		connConfigBean.setMaxwait(quartzProp.getProperty("org.quartz.dataSource." + dsName + ".maxWait"));
		
		String url = "";
		if(MYSQL.equals(dbms)){
			url = "jdbc:mysql://" + ip + ":" + port + "/" + database + "?useUnicode=true&characterEncoding=utf-8";
		}else if(ORACLE.equals(dbms)){
			url = "jdbc:oracle:thin:@" + ip + ":" + port + ":" + database;
		}else if(SQLSERVER.equals(dbms)){
			url = "jdbc:sqlserver://" + ip + ":" + port + ";databaseName=" + database + ";SelectMethod=Cursor";
		}else if(KINGBASE.equals(dbms)){
			url = "jdbc:kingbase://" + ip + ":" + port + "/" + database;
		}else if(DB2.equals(dbms)){
			url = "jdbc:db2://" + ip + ":" + port + "/" + database;
		}else if(POSTGRESQL.equals(dbms)){
			url = "jdbc:postgresql://" + ip + ":" + port + "/" + database;
		}
		
		connConfigBean.setUrl(url);
		
		return connConfigBean;
	}
	
	/**
	 * generate page sql for all kinds of dbms
	 * @param sql
	 * @param page
	 * @return
	 */
	public static String generatePagingSQL(String sql, int page, String orderby, String order){
		int start = PAGE_SIZE * (page - 1);
		int end = PAGE_SIZE * page;
		
		if(ORACLE.equals(connConfig.getDbms())){
			if(!"".equals(orderby)){
				sql = sql + " ORDER BY " + orderby + " " + order;
			}
			
			sql = "SELECT * FROM(SELECT A.*, ROWNUM RN FROM (" + sql + ") A WHERE ROWNUM<=" + end + ") WHERE RN>" + start;
		}else if(SQLSERVER.equals(connConfig.getDbms())){
			if(!"".equals(orderby)){
				sql = "SELECT D.* FROM (SELECT C.* FROM (SELECT B.*,ROW_NUMBER()OVER(ORDER BY " + orderby + " " + order + ") MY_ROWNUM FROM (SELECT 0 AS TEMPCOLUMN,A.* FROM (" + sql + ") A) B) C WHERE C.MY_ROWNUM<=" + end + ") D WHERE D.MY_ROWNUM>" + start;
			}else {
				sql = "SELECT D.* FROM (SELECT C.* FROM (SELECT B.*,ROW_NUMBER()OVER(ORDER BY B.TEMPCOLUMN) MY_ROWNUM FROM (SELECT 0 AS TEMPCOLUMN,A.* FROM (" + sql + ") A) B) C WHERE C.MY_ROWNUM<=" + end + ") D WHERE D.MY_ROWNUM>" + start;
			}
		}else if(MYSQL.equals(connConfig.getDbms()) || 
				 DB2.equals(connConfig.getDbms()) || 
				 KINGBASE.equals(connConfig.getDbms()) ||
				 POSTGRESQL.equals(connConfig.getDbms())
				)
		{
			if(!"".equals(orderby)){
				sql = sql + " ORDER BY " + orderby + " " + order;
			}
			
			sql = "SELECT A.* FROM (" + sql + ") A LIMIT " + PAGE_SIZE + " OFFSET " + start;
		}
		
		return sql;
	}
	
	public static void executeSQLScript(Statement stmt, String filePath) throws IOException,SQLException {
		String classPath = InitServlet.class.getResource("/").getFile();
		File classDir = new File(classPath).getAbsoluteFile();
		File webcontentDir = classDir.getParentFile().getParentFile();
		String webcontentPath = webcontentDir.getAbsolutePath();
		filePath = webcontentPath + "/" + filePath;
		File inputFile = new File(filePath);
		InputStream is = new FileInputStream(inputFile);
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			boolean done = false;
			while (!done) {
				StringBuilder command = new StringBuilder();
				while (true) {
					String line = in.readLine();
					if (line == null) {
						done = true;
						break;
					}
					// Ignore comments and blank lines.
					if (isSQLCommandPart(line)) {
						command.append(" ").append(line.trim());
					}
					if (line.trim().endsWith(";")) {
						break;
					}
				}
				if (!done && !command.toString().equals("")) {
					// Remove last semicolon when using Oracle or DB2 to prevent
					// "invalid character error"
					// if (DbConnectionManager.getDatabaseType() ==
					// DbConnectionManager.DatabaseType.oracle
					// || DbConnectionManager.getDatabaseType() ==
					// DbConnectionManager.DatabaseType.db2) {
					// command.deleteCharAt(command.length() - 1);
					// }
					command.deleteCharAt(command.length() - 1);
					stmt.execute(command.toString());
				}
			}
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}
	
	private static boolean isSQLCommandPart(String line) {
		line = line.trim();
		if (line.equals("")) {
			return false;
		}
		// Check to see if the line is a comment. Valid comment types:
		// "//" is HSQLDB
		// "--" is DB2 and Postgres
		// "#" is MySQL
		// "REM" is Oracle
		// "/*" is SQLServer
		return !(line.startsWith("//") || line.startsWith("--") || line.startsWith("#") || line.startsWith("REM ")
				|| line.startsWith("/*") || line.startsWith("*"));
	}
	
	/**
	 * 生成建表语句
	 * @param tableName
	 * @param list
	 * @param database
	 * @return
	 */
	public static String getCreateTableStatement(String schemaName,String tableName, List<MdmModelAttribute> list, Database database){

		StringBuilder retval = new StringBuilder("CREATE TABLE ");

		String type = database.getDatabaseMeta().getPluginId();
		if(schemaName!=null && !"".equals(schemaName)){
			retval.append(schemaName).append(".");
		}
		retval.append(tableName + Const.CR);
		retval.append("(").append(Const.CR);
		String primary_key = "";
		int i;
		for(i = 0; i < list.size(); ++i) {
			if(i > 0) {
				retval.append(" , ");
			} else {
				retval.append("  ");
			}
			MdmModelAttribute mdmModelAttribute = list.get(i);

			ValueMeta ValueMeta = new ValueMeta(mdmModelAttribute.getField_name(),mdmModelAttribute.getField_type());
			ValueMeta.setLength(mdmModelAttribute.getField_length());
			ValueMeta.setPrecision(mdmModelAttribute.getField_precision());
			if("Y".equalsIgnoreCase(mdmModelAttribute.getIs_primary())){
				primary_key = primary_key + mdmModelAttribute.getField_name()+",";
			}
			retval.append(database.getDatabaseMeta().getFieldDefinition(ValueMeta, null, null, true));
			if(database.getDatabaseMeta().getDatabaseInterface() instanceof MySQLDatabaseMeta){
				retval.append("COMMENT '").append(mdmModelAttribute.getAttribute_name()).append("'").append(Const.CR);
			}
		}

		if(primary_key.length()>0 ){
			primary_key = primary_key.substring(0,primary_key.length()-1);
			retval.append(", PRIMARY KEY (").append(primary_key).append(")").append(Const.CR);
		}
		retval.append(")").append(Const.CR);

		//Oracle字段描述
		if(database.getDatabaseMeta().getDatabaseInterface() instanceof OracleDatabaseMeta){
			//COMMENT ON COLUMN "XYAPP"."<table_name>"."A" IS 'AA';
			retval.append(";");
			for(MdmModelAttribute attribute : list){
				retval.append("COMMENT ON COLUMN \"").append(schemaName).append("\".\"").append(tableName).append("\".\"");
				retval.append(attribute.getField_name()).append("\" IS ").append("'").append(attribute.getAttribute_name()).append("';").append(Const.CR);
			}

		}

		//Microsoft SQL Server字段描述
		if(database.getDatabaseMeta().getDatabaseInterface() instanceof MSSQLServerDatabaseMeta){
			for(MdmModelAttribute attribute : list){
				retval.append("EXEC sp_addextendedproperty 'MS_Description', '").append(attribute.getAttribute_name()).append("', 'SCHEMA', '");
				retval.append(schemaName).append("', 'TABLE', '").append(tableName).append("', 'COLUMN', '").append(attribute.getField_name()).append("' ").append(Const.CR);
			}
			retval.append(";");
		}

		return retval.toString();

	}


	/**
	 * 生成Alter 表语句
	 * @param tableName
	 * @param list
	 * @param database
	 * @return
	 */
	public static String getAlterTableStatement(String schemaName,String tableName, List<MdmModelAttribute> list, Database database) throws KettleDatabaseException {

		StringBuilder retval = new StringBuilder();

		String tablename = (schemaName!=null && "".equals(schemaName))?schemaName+"."+tableName:tableName;

		RowMetaInterface tabFields = database.getTableFields(tablename); ///database.getTableFields(tableName);
		database.getDatabaseMeta().quoteReservedWords(tabFields);
		List<MdmModelAttribute> missing = new ArrayList<>();

		int surplus;
		MdmModelAttribute modify;
		for(surplus = 0; surplus < list.size(); ++surplus) {
			modify = list.get(surplus);
			if(tabFields.searchValueMeta(modify.getField_name()) == null) {
				missing.add(modify);
			}
		}

		if(missing.size() != 0) {
			for(surplus = 0; surplus < missing.size(); ++surplus) {
				modify = missing.get(surplus);
				ValueMeta ValueMeta = new ValueMeta(modify.getField_name(),modify.getField_type());
				ValueMeta.setLength(modify.getField_length());
				ValueMeta.setPrecision(modify.getField_precision());

				String pk = "Y".equals(modify.getIs_primary()) ? modify.getField_name() : null;
				String temp = database.getDatabaseMeta().getAddColumnStatement(tableName, ValueMeta, null, true, pk , true);


				if(database.getDatabaseMeta().getDatabaseInterface() instanceof MySQLDatabaseMeta){
					retval.append(temp.substring(0,temp.indexOf(";")));
					retval.append(" COMMENT '").append(modify.getAttribute_name()).append("';").append(Const.CR);
				}
				//Oracle字段描述
				if(database.getDatabaseMeta().getDatabaseInterface() instanceof OracleDatabaseMeta){
					retval.append(temp);
					retval.append("COMMENT ON COLUMN \"").append(schemaName).append("\".\"").append(tableName).append("\".\"");
					retval.append(modify.getField_name()).append("\" IS ").append("'").append(modify.getAttribute_name()).append("';").append(Const.CR);
				}
				//Microsoft SQL Server字段描述
				if(database.getDatabaseMeta().getDatabaseInterface() instanceof MSSQLServerDatabaseMeta){
					retval.append(temp.substring(0,temp.indexOf(";")));
					retval.append("EXEC sp_addextendedproperty 'MS_Description', '").append(modify.getAttribute_name()).append("', 'SCHEMA', '");
					retval.append(schemaName).append("', 'TABLE', '").append(tableName).append("', 'COLUMN', '").append(modify.getField_name()).append("' ");
					retval.append(";").append(Const.CR);
				}

			}
		}

		RowMeta var18 = new RowMeta();

		ValueMetaInterface i;
		int var19;
		for(var19 = 0; var19 < tabFields.size(); ++var19) {
			i = tabFields.getValueMeta(var19);
			if(!hasFieldName(list,i.getName())) {
				var18.addValueMeta(i);
			}
		}

		if(var18.size() != 0) {
			for(var19 = 0; var19 < var18.size(); ++var19) {
				i = var18.getValueMeta(var19);
				retval.append(database.getDatabaseMeta().getDropColumnStatement(tableName, i, null, true, null, true));
			}
		}


		List<MdmModelAttribute> var21 = new ArrayList<>();

		ValueMetaInterface v;
		int var20;
		for(var20 = 0; var20 < list.size(); ++var20) {
			MdmModelAttribute attribute = list.get(var20);
			ValueMetaInterface currentField = tabFields.searchValueMeta(attribute.getField_name());
			if(currentField != null && currentField != null) {

				String pk = "Y".equals(attribute.getIs_primary()) ? attribute.getField_name() : null;

				v = new ValueMeta(attribute.getField_name(),attribute.getField_type());
				v.setLength(attribute.getField_length());
				v.setPrecision(attribute.getField_precision());

				String desiredDDL = database.getDatabaseMeta().getFieldDefinition(v, null, pk, true);
				String currentDDL = database.getDatabaseMeta().getFieldDefinition(currentField, null , pk, true);
				boolean mod = !desiredDDL.equalsIgnoreCase(currentDDL);
				if(mod) {
					var21.add(attribute);
				}
			}
		}

		if(var21.size() > 0) {
			for(var20 = 0; var20 < var21.size(); ++var20) {
				MdmModelAttribute attribute  = var21.get(var20);
				v = new ValueMeta(attribute.getField_name(),attribute.getField_type());
				v.setLength(attribute.getField_length());
				v.setPrecision(attribute.getField_precision());
				String pk = "Y".equals(attribute.getIs_primary()) ? attribute.getField_name() : null;
				retval.append(database.getDatabaseMeta().getModifyColumnStatement(tableName, v, null, true, pk, true));
			}
		}

		return retval.toString();

	}

	private static boolean hasFieldName(List<MdmModelAttribute> list, String name) {
		if(list ==null || list.isEmpty() || name == null || "".equals(name)){
			return false;

		}
		for(MdmModelAttribute  attribute : list){

			if(attribute.getField_name()!=null && attribute.getField_name().equals(name)){

				return  true;

			}
		}
		return false;
	}


	public static void main(String[] args) {


	}
}
