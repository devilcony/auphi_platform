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
package com.auphi.ktrl.system.mail.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import org.apache.log4j.Logger;

import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.system.mail.bean.MailBean;

public class MailUtil {

	/**
	 * 日志logger
	 */
	private static Logger logger = Logger.getLogger(MailUtil.class);

	/**
	 * get mail config from database
	 * @return
	 */
	public static MailBean getMailConfig(){
		MailBean mailBean = new MailBean();
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		String sql = "SELECT * FROM KDI_T_MAIL";
		
		try{
			conn = ConnectionPool.getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			if(rs.next()){
				mailBean.setSmtp_server(rs.getString("SMTP_SERVER"));
				mailBean.setSmtp_port(rs.getInt("SMTP_PORT"));
				mailBean.setUser_name(rs.getString("USER_NAME"));
				mailBean.setPasswd(rs.getString("PASSWD"));
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, null, conn);
		}
		
		return mailBean;
	}
	
	/**
	 * save mail config
	 * @param mailBean
	 */
	public static void saveMailConfig(MailBean mailBean){
		String sql_query = "SELECT * FROM KDI_T_MAIL";
		String sql_insert = "INSERT INTO KDI_T_MAIL VALUES(?,?,?,?)";
		String sql_update = "UPDATE KDI_T_MAIL SET SMTP_SERVER=?,SMTP_PORT=?,USER_NAME=?,PASSWD=?";
		Connection conn = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try{
			conn = ConnectionPool.getConnection();
			
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql_query);
			boolean haveData = false;
			if(rs.next()){
				haveData = true;
			}
			
			if(haveData){
				pstmt = conn.prepareStatement(sql_update);
			}else {
				pstmt = conn.prepareStatement(sql_insert);
			}
			
			pstmt.setString(1, mailBean.getSmtp_server());
			pstmt.setInt(2, mailBean.getSmtp_port());
			pstmt.setString(3, mailBean.getUser_name());
			pstmt.setString(4, mailBean.getPasswd());
			
			pstmt.executeUpdate();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}finally{
			ConnectionPool.freeConn(rs, stmt, pstmt, conn);
		}
	}
	
	public static void sendMail(String[] mailtos, String title, String content){
		try{
			MailBean mailBean = getMailConfig();
			if(mailBean==null){
				return;
			}
			
			Properties props = new Properties();
			props.put("mail.smtp.host", mailBean.getSmtp_server());
			props.put("mail.smtp.port", mailBean.getSmtp_port());
			props.put("mail.smtp.auth", "true");
			Session se=Session.getInstance(props);
			
			MimeMessage mail = new MimeMessage(se);
			mail.setFrom(new InternetAddress(mailBean.getUser_name()));
			for(String mailto : mailtos){
				if(mailto != null && !"".equals(mailto)){
					mail.addRecipient(Message.RecipientType.TO, new InternetAddress(mailto));
				}
			}
			mail.setSubject(MimeUtility.encodeText(title));
			mail.setContent(content, "text/html;charset=utf-8");
//			mail.setText(content);
			
			if(mail.getRecipients(Message.RecipientType.TO) != null){
				Transport transport = se.getTransport("smtp");
				transport.connect(mailBean.getSmtp_server(), mailBean.getSmtp_port(), mailBean.getUser_name(), mailBean.getPasswd());
				transport.sendMessage(mail, mail.getAllRecipients());
				transport.close();
			}
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
	}
	
	/**
	 * validate smtp port username password 
	 * @param mailBean
	 * @return
	 */
	public static boolean valiSMTP(MailBean mailBean){
		boolean validate = false;
		try{
			Properties props = new Properties();
			props.put("mail.smtp.host", mailBean.getSmtp_server());
			props.put("mail.smtp.port", mailBean.getSmtp_port());
			props.put("mail.smtp.auth", "true");
			Session se=Session.getInstance(props);
			
			Transport transport = se.getTransport("smtp");
			transport.connect(mailBean.getSmtp_server(), mailBean.getSmtp_port(), mailBean.getUser_name(), mailBean.getPasswd());
			transport.close();
			validate = true;
		}catch(Exception e){
			return validate;
		}
		
		return validate;
	}
}
