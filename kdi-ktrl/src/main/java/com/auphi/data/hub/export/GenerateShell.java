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
package com.auphi.data.hub.export;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.auphi.data.hub.core.properties.PropertiesFactory;
import com.auphi.data.hub.core.properties.PropertiesFile;
import com.auphi.data.hub.core.properties.PropertiesHelper;

/**
 * 用户生成每次数据导出脚本的程序
 * 
 * @author zhangfeng
 * 
 */
public class GenerateShell {

	/**
	 * 执行shell脚本
	 * 
	 * @param colsep
	 * @param fields
	 * @param tableName
	 * @param filePath
	 * @param path
	 */
	public static String exectorShell(final String colsep, final String fields,
			final String tableName, final String filePath, final String path) {

		String result = "";
		String exportLineCount = "exportLineCount";// 文件记录了导出数量
		try {
			Runtime.getRuntime().exec("chmod 777 /home/oracle/" + path + "/spool.sql");
			Runtime.getRuntime().exec("chmod 777 /home/oracle/" + path + "/export.sh");

			Process process = null;
			String cmd = "/home/oracle/" + path + "/export.sh";
			System.out.println("。。。程序正在导出数据，请等待。。。");
			try {
				// 使用Runtime来执行command，生成Process对象
				Runtime runtime = Runtime.getRuntime();
				long start = System.currentTimeMillis();
				process = runtime.exec(cmd);
				// 取得命令结果的输出流
				InputStream is = process.getInputStream();
				// 用一个读输出流类去读
				InputStreamReader isr = new InputStreamReader(is);
				// 用缓冲器读行
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
				}
				long end = System.currentTimeMillis();
				is.close();
				isr.close();
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("。。。程序数据导出结束。。。");
			BufferedReader input = new BufferedReader(new InputStreamReader(
					new FileInputStream("/home/oracle/exportLineCount")));
			String line = "";
			while ((line = input.readLine()) != null) {
				result = line;
			}
			//删掉这个文件
			
			System.out.println("============executor shell script success!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 创建路径
	 * 
	 * @param path
	 */
	public static void mkdir(final String path) {
		try {			
			Runtime.getRuntime().exec("mkdir -p /home/oracle/" + path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成sql文件 全量抽取
	 * 
	 * @param colsep
	 * @param fields
	 * @param tableName
	 * @param filePath
	 * @param path
	 */
	public static void createSQL(final String colsep, final String fields,
			final String tableName, final String filePath, final String path,
			String conditions) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("set heading off;\n");
		buffer.append("set echo off;\n");
		buffer.append("set feedback off;\n");
		buffer.append("set verify off;\n");
		buffer.append("set wrap off;\n");
		buffer.append("set pagesize 0;\n");
		buffer.append("set linesize 2500;\n");
		buffer.append("set trimout on;\n");
		buffer.append("set termout on;\n");
		buffer.append("set trimspool on;\n");
		buffer.append("set colsep '" + colsep + "';\n");
		buffer.append("spool " + filePath + ";\n");
		if (fields != null && !fields.equals("")) {
			buffer.append("select " + fields + " from " + tableName);
		} else {
			buffer.append("select *  from " + tableName);
		}
		if (conditions != null && !conditions.equals("")) {
			buffer.append(" where " + conditions + " ;\n");
		} else {
			buffer.append(" ;\n");
		}
		buffer.append("set heading on;\n");
		buffer.append("set echo off;\n");
		buffer.append("set feedback on;\n");
		buffer.append("spool off ;\n");
		buffer.append("exit ;\n");
		try {

			File f = new File("/home/oracle/" + path + "/spool.sql");
			if (!f.exists()) {
				File file = new File("/home/oracle/" + path);
				if (!file.exists()) {
					GenerateShell.mkdir(path);
				}
				f.createNewFile();
			}
			FileWriter fw = new FileWriter(f);
			fw.write(buffer.toString());
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成sql语句 增量抽取
	 * 
	 * @param colsep 分隔符
	 * @param fields   返回字段
	 * @param tableName  表名
	 * @param filePath  生成的sql文件路径
	 * @param path  生成的shell文件放置的目录
	 * @param incrementField  增量时间字段
	 * @param startTime 增量时间字段的开始时间
	 * @param conditions  查询条件
	 * @return
	 */
	public static String createSQL_Increment(final String colsep,
			final String fields, final String tableName, final String filePath,
			final String path, String incrementField, String startTime,
			String conditions) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("set heading off;\n");
		buffer.append("set echo off;\n");
		buffer.append("set feedback off;\n");
		buffer.append("set verify off;\n");
		buffer.append("set wrap off;\n");
		buffer.append("set pagesize 0;\n");
		buffer.append("set linesize 2500;\n");
		buffer.append("set trimout on;\n");
		buffer.append("set termout on;\n");
		buffer.append("set trimspool on;\n");
		buffer.append("set colsep '" + colsep + "';\n");
		buffer.append("spool " + filePath + ";\n");
		if (startTime.length() > 19) {
			startTime = startTime.substring(0, startTime.lastIndexOf("."));
		}
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String endTime = df.format(new Date());// new Date()为获取当前系统时间

		if (fields != null && !fields.equals("")) {
			buffer.append("select " + fields + " from " + tableName + " where "
					+ incrementField + " > to_date('" + startTime
					+ "','yyyy-mm-dd hh24:mi:ss') and " + incrementField
					+ " < to_date('" + endTime + "','yyyy-mm-dd hh24:mi:ss') ");
		} else {
			buffer.append("select *  from " + tableName + " where "
					+ incrementField + " > to_date('" + startTime
					+ "','yyyy-mm-dd hh24:mi:ss') and " + incrementField
					+ " < to_date('" + endTime + "','yyyy-mm-dd hh24:mi:ss') ");
		}
		if (conditions != null && !conditions.equals("")) {
			buffer.append(" and " + conditions + " ;\n");
		} else {
			buffer.append(" ;\n");
		}
		buffer.append("set heading on;\n");
		buffer.append("set echo off;\n");
		buffer.append("set feedback on;\n");
		buffer.append("spool off ;\n");
		buffer.append("exit ;\n");
		try {
			File f = new File("/home/oracle/" + path + "/spool.sql");
			if (!f.exists()) {
				File file = new File("/home/oracle/" + path);
				if (!file.exists()) {
					GenerateShell.mkdir(path);
				}
				f.createNewFile();
			}
			FileWriter fw = new FileWriter("/home/oracle/" + path + "/spool.sql");
			fw.write(buffer.toString());
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return endTime;

	}

	/**
	 * 创建Shell脚本
	 * 
	 * @param path
	 * @param filePath
	 */
	public static void createShell(final String path, final String filePath) {

		// 创建sql文件

		PropertiesHelper pHelper = PropertiesFactory.getPropertiesHelper(PropertiesFile.ODB);
		StringBuffer buffer = new StringBuffer();

		buffer.append("#!/bin/bash\n");
		buffer.append("ORACLE_BASE=" + pHelper.getValue("ORACLE_BASE"));
		buffer.append("export ORACLE_BASE\n");
		buffer.append("ORACLE_HOME=" + pHelper.getValue("ORACLE_HOME"));
		buffer.append("export ORACLE_HOME\n");
		buffer.append("ORACLE_HOME_LISTNER="
				+ pHelper.getValue("ORACLE_HOME_LISTNER"));
		buffer.append("export ORACLE_HOME_LISTNER\n");
		buffer.append("LD_LIBRARY_PATH=" + pHelper.getValue("LD_LIBRARY_PATH"));
		buffer.append("export LD_LIBRARY_PATH\n");
		buffer.append("ORACLE_SID=" + pHelper.getValue("ORACLE_SID"));
		buffer.append("export ORACLE_SID\n");
		buffer.append("ORA_NLS33=" + pHelper.getValue("ORA_NLS33"));
		buffer.append("export ORA_NLS33\n");
		buffer.append("NLS_LANG=" + pHelper.getValue("NLS_LANG"));
		buffer.append("export NLS_LANG\n");
		buffer.append("PATH=" + pHelper.getValue("PATH"));
		buffer.append("export PATH\n");
		buffer.append("ORACLE_CLIENT_HOME="
				+ pHelper.getValue("ORACLE_CLIENT_HOME"));
		buffer.append("export ORACLE_CLIENT_HOME\n");
		buffer.append("sleep 3s \n");
		buffer.append("sqlplus " + pHelper.getValue("USERNAME") + "/" + pHelper.getValue("PASSWORD") + " @/home/oracle/" + path
				+ "/spool.sql\n");
		buffer.append("sleep 2s \n");
		buffer.append("wc -lc " + filePath
				+ " | awk '{print $1}' > /home/oracle/exportLineCount\n");
		buffer.append("rm -rf /home/oracle/" + path + "\n");
		buffer.append("exit 0\n");
		try {
			FileWriter fw = new FileWriter("/home/oracle/" + path + "/export.sh");
			fw.write(buffer.toString());
			fw.flush();
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
