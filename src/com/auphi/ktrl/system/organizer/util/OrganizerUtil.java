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
package com.auphi.ktrl.system.organizer.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.pentaho.di.core.util.StringUtil;

import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.system.mail.util.MailUtil;
import com.auphi.ktrl.system.organizer.bean.OrganizerBean;
import com.auphi.ktrl.system.repository.util.RepositoryUtil;
import com.auphi.ktrl.system.user.bean.UserBean;
import com.auphi.ktrl.system.user.util.UserUtil;
import com.auphi.ktrl.util.Constants;
import com.auphi.ktrl.util.DBColumns;

import com.auphi.data.hub.domain.Organizer;

public class OrganizerUtil {
	public static int STATUS_ACTIVE = 1;
	public static int STATUS_NOT_ACTIVE = 0;
	
	private static Logger logger = Logger.getLogger(RepositoryUtil.class);
	
	/**
	 * 注册
	 * @param orgBean 组织机构
	 * @param request httprequest
	 * @return
	 */
	public static boolean register(OrganizerBean orgBean, HttpServletRequest request){
		boolean success = true;
		Connection conn=null;
		String verifyCode = StringUtil.generateRandomString(16, "", "", true);
		orgBean.setOrganizer_verify_code(verifyCode);
		orgBean.setOrganizer_status(STATUS_NOT_ACTIVE);
		try {
			conn = ConnectionPool.getConnection();
			Statement st = conn.createStatement();
			//create organizer
			String insertSql = "insert into " + DBColumns.TABLE_ORGANIZER + "("
					+ DBColumns.COLUMN_ORG_NAME + "," + DBColumns.COLUMN_ORG_CONTACT + "," 
					+ DBColumns.COLUMN_ORG_EMAIL + "," + DBColumns.COLUMN_ORG_TELPHONE + "," 
					+ DBColumns.COLUMN_ORG_MOBILE + "," + DBColumns.COLUMN_ORG_ADDRESS + ","
					+ DBColumns.COLUMN_ORG_VERIFYCODE + "," + DBColumns.COLUMN_ORG_STATUS + ") values( '" 
					+ orgBean.getOrganizer_name() + "','" + orgBean.getOrganizer_contact() + "','"
	                + orgBean.getOrganizer_email() + "','" + orgBean.getOrganizer_telphone() + "','" 
					+ orgBean.getOrganizer_mobile() + "','" + orgBean.getOrganizer_address() + "','" 
	                + orgBean.getOrganizer_verify_code() + "'," + orgBean.getOrganizer_status() + ")";
			
			st.execute(insertSql);
			st.close();
			
			//create user
			int orgId = getOrgIdByEmail(orgBean.getOrganizer_email());
			UserBean userBean = new UserBean();
			userBean.setUser_name(orgBean.getOrganizer_email());
			userBean.setPassword(orgBean.getOrganizer_passwd());
			userBean.setNick_name(orgBean.getOrganizer_email());
			userBean.setEmail(orgBean.getOrganizer_email());
			userBean.setMobilephone(orgBean.getOrganizer_mobile());
			userBean.setDescription(orgBean.getOrganizer_name());
			userBean.setOrgId(orgId);
			userBean.setStatus(UserUtil.STATUS_NOT_ACTIVE);
			UserUtil.createUser(userBean);
			
			//add role
			UserBean user = UserUtil.getUserByName(orgBean.getOrganizer_email());
			UserUtil.assignRolesToUser("0",String.valueOf(user.getUser_id()));
			
			//send mail
			sendActivationMail(orgBean, request);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			success = false;
		}finally{
			ConnectionPool.freeConn(null, null, null, conn);
		}	  
		return success;
	}
	
	/**
	 * 检查邮箱是否存在
	 * @param email 邮箱
	 * @return
	 */
	public static boolean checkEmail(String email){
		boolean result = true;
		Connection conn=null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = ConnectionPool.getConnection();
			st = conn.createStatement();
			String sql = "select count(1) from " + DBColumns.TABLE_ORGANIZER + 
					" where " + DBColumns.COLUMN_ORG_EMAIL + "='" + email + "'";
			rs = st.executeQuery(sql) ;
			if(rs.next()){
				int cnt = rs.getInt(1);
				if(cnt>0) result = false;
			}
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}finally {
			ConnectionPool.freeConn(rs, st, null, conn);
		}	  
		
		return result;
	}
	
	/**
	 * 检查组织结构是否存在
	 * @param orgName 组织机构名称
	 * @return
	 */
	public static boolean checkName(String orgName){
		boolean result = true;
		Connection conn=null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = ConnectionPool.getConnection();
			st = conn.createStatement();
			String sql = "select count(1) from " + DBColumns.TABLE_ORGANIZER + 
					" where " + DBColumns.COLUMN_ORG_NAME + "='" + orgName + "'";
			rs = st.executeQuery(sql) ;
			if(rs.next()){
				int cnt = rs.getInt(1);
				if(cnt>0) result = false;
			}
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}finally {
			ConnectionPool.freeConn(rs, st, null, conn);
		}	  
		
		return result;
	}
	
	/**
	 * 检查邮箱是否存在
	 * @param email 邮箱
	 * @return
	 */
	public static int getOrgIdByEmail(String email){
		int orgId = 0;
		Connection conn=null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = ConnectionPool.getConnection();
			st = conn.createStatement();
			String sql = "select " + DBColumns.COLUMN_ORG_ID + " from " + DBColumns.TABLE_ORGANIZER + 
					" where " + DBColumns.COLUMN_ORG_EMAIL + "='" + email + "'";
			rs = st.executeQuery(sql) ;
			if(rs.next()){
				orgId = rs.getInt(1);
			}
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}finally {
			ConnectionPool.freeConn(rs, st, null, conn);
		}	  
		
		return orgId;
	}
	
	/**
	 * 通过有邮箱获取orgBean
	 * @param email
	 * @return
	 */
	public static OrganizerBean getOrgByEmail(String email){
		OrganizerBean orgBean = new OrganizerBean();
		Connection conn=null;
		Statement st = null;
		ResultSet rs = null;
		try {
			conn = ConnectionPool.getConnection();
			st = conn.createStatement();
			String sql = "select * from " + DBColumns.TABLE_ORGANIZER + 
					" where " + DBColumns.COLUMN_ORG_EMAIL + "='" + email + "'";
			rs = st.executeQuery(sql) ;
			if(rs.next()){
				orgBean.setOrganizer_id(rs.getInt(DBColumns.COLUMN_ORG_ID));
				orgBean.setOrganizer_name(rs.getString(DBColumns.COLUMN_ORG_NAME));
				orgBean.setOrganizer_contact(rs.getString(DBColumns.COLUMN_ORG_CONTACT));
				orgBean.setOrganizer_email(rs.getString(DBColumns.COLUMN_ORG_EMAIL));
				orgBean.setOrganizer_telphone(rs.getString(DBColumns.COLUMN_ORG_TELPHONE));
				orgBean.setOrganizer_mobile(rs.getString(DBColumns.COLUMN_ORG_MOBILE));
				orgBean.setOrganizer_address(rs.getString(DBColumns.COLUMN_ORG_ADDRESS));
				orgBean.setOrganizer_verify_code(rs.getString(DBColumns.COLUMN_ORG_VERIFYCODE));
				orgBean.setOrganizer_status(rs.getInt(DBColumns.COLUMN_ORG_STATUS));
			}
			rs.close();
			rs = null;
			
			String sql_user = "select * from " + DBColumns.TABLE_USER + 
					" where " + DBColumns.COLUMN_USER_EMAIL + "='" + email + 
					"' and " + DBColumns.COLUMN_USER_ORGANIZERID + "=" + orgBean.getOrganizer_id();
			rs = st.executeQuery(sql_user) ;
			if(rs.next()){
				orgBean.setOrganizer_passwd(rs.getString(DBColumns.COLUMN_USER_PASSWORD));
			}
					
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}finally {
			ConnectionPool.freeConn(rs, st, null, conn);
		}	  
		
		return orgBean;
	}
	
	/**
	 * 获取orgBean
	 * @param request
	 * @return
	 */
	public static OrganizerBean getOrganizerBean(HttpServletRequest request)
    {
		OrganizerBean organizerBean = new OrganizerBean() ;
        String org_id =request.getParameter(DBColumns.COLUMN_ORG_ID); 
        if("".equals(org_id)||org_id==null)
        	org_id="0";
        try{
        	organizerBean.setOrganizer_id(Integer.parseInt(org_id)) ;
            organizerBean.setOrganizer_name(request.getParameter(DBColumns.COLUMN_ORG_NAME)==null?"":new String(request.getParameter(DBColumns.COLUMN_ORG_NAME).getBytes("ISO8859-1"), "UTF-8")) ;
            organizerBean.setOrganizer_contact(request.getParameter(DBColumns.COLUMN_ORG_CONTACT)==null?"":new String(request.getParameter(DBColumns.COLUMN_ORG_CONTACT).getBytes("ISO8859-1"), "UTF-8")) ;
            organizerBean.setOrganizer_email(request.getParameter(DBColumns.COLUMN_ORG_EMAIL)) ;
            organizerBean.setOrganizer_passwd(request.getParameter(DBColumns.COLUMN_ORG_PASSWD)) ;
            organizerBean.setOrganizer_telphone(request.getParameter(DBColumns.COLUMN_ORG_TELPHONE)) ;
            organizerBean.setOrganizer_mobile(request.getParameter(DBColumns.COLUMN_ORG_MOBILE)) ;
            organizerBean.setOrganizer_address(request.getParameter(DBColumns.COLUMN_ORG_ADDRESS)==null?"":new String(request.getParameter(DBColumns.COLUMN_ORG_ADDRESS).getBytes("ISO8859-1"), "UTF-8")) ;
        }catch (Exception e){
        	logger.error(e.getMessage(),e);
        }
        
        return organizerBean ;
    }
	
	/**
	 * send activation email
	 * @param orgBean
	 * @param request
	 */
	public static String sendActivationMail(OrganizerBean orgBean, HttpServletRequest request) {
		String result = "false";
		try{
			String content = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"
					+ "<html>"
					+ "<head>"
					+ "<style type=\"text/css\">"
					+ "<!--"
					+ ".text,td,th {"	
					+ "font-size: 12px;"
					+ "font-family: Arial, Helvetica, sans-serif;"
					+ "}"
					+ ".unsubscribe {"	
					+ "font-size: 10px;"
					+ "}"
					+ "th{text-align:left;}"
					+ ".right{text-align:right;}"
					+ "-->"
					+ "</style>"
					+ "</head>"
					+ "<body>"
					+ "<span class=\"text\">"
					+ "您好！<br />"
					+ "<br />"
					+ "感谢您注册傲飞数据整合平台帐户。为激活您的帐户，请单击下列链接。<br />"
					+ "<br />"
					+ "<a href=\"http://" + Constants.get(Constants.WEB_IP) + ":" + request.getLocalPort() + request.getContextPath()
					+ "/register?action=activation&email=" + orgBean.getOrganizer_email() 
					+ "&verifyCode=" + orgBean.getOrganizer_verify_code() + "\">"
					+ "http://" + Constants.get(Constants.WEB_IP) + ":" + request.getLocalPort() + request.getContextPath()
					+ "/register?action=activation&email=" + orgBean.getOrganizer_email() 
					+ "&verifyCode=" + orgBean.getOrganizer_verify_code()
					+ "</a><br />"
					+ "<br />"
					+ "如果单击链接没有反应，请将链接复制到浏览器窗口中，或直接输入链接。<br />"
					+ "<br />"
					+ "	致<br />"
					+ "敬！"
					+ "<br />"	
					+ "<i>傲飞数据整合平台团队</i><br />"
					+ "-----------------------------"
					+ "<br />"
					+ "<a href=\"http://www.doetl.com\">www.doetl.com</a><br />"
					+ "<br />"
					+ "北京傲飞商智软件有限公司<br />"
					+ "Tel. 010-62986313 * Email. support@pentahochina.com<br />"
					+ "<br />"
					+ "<span class=\"unsubscribe\"></span>"
					+ "<br />"
					+ "</span>"
					+ "</body>"
					+ "</html>";
			MailUtil.sendMail(new String[]{orgBean.getOrganizer_email()}, "傲飞数据整合平台 - 账户注册电子邮件确认", content);
			result = "true";
		}catch(Exception e){
			logger.error(e.getMessage(), e);
		}
		
		return result;
	}
	
	/**
	 * 邮箱激活
	 * @param email
	 * @return
	 */
	public static boolean activation(String email, String verifyCode){
		boolean success = false;
		Connection conn=null;
		Statement st = null;
		
		try{
			conn = ConnectionPool.getConnection();
			st = conn.createStatement();
			//update organizer status
			String sql = "update " + DBColumns.TABLE_ORGANIZER + " set " + DBColumns.COLUMN_ORG_STATUS + " = " + STATUS_ACTIVE
					+ " where " + DBColumns.COLUMN_ORG_EMAIL + "='" + email + "' "
				    + " and " + DBColumns.COLUMN_ORG_VERIFYCODE + " = '" + verifyCode + "'"; 
			int isUpdate = st.executeUpdate(sql) ;
			
			//update user status
			if(isUpdate!=0){
				int orgId = getOrgIdByEmail(email);
				sql = "update " + DBColumns.TABLE_USER + " set " + DBColumns.COLUMN_USER_STATUS + " =  " + UserUtil.STATUS_ACTIVE
						+ " where " + DBColumns.COLUMN_ORG_ID + "=" + orgId;
				st.executeUpdate(sql) ;
				success = true;
			}
			
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}finally {
			ConnectionPool.freeConn(null, st, null, conn);
		}	 
		
		return success;
	}
	
	public static boolean activeUser(String orgId, String status){
		boolean success = false;
		Connection conn=null;
		Statement st = null;
		
		try{
			conn = ConnectionPool.getConnection();
			st = conn.createStatement();
			String sql = "update " + DBColumns.TABLE_USER + " set " + DBColumns.COLUMN_USER_STATUS + " =  " + status
						+ " where " + DBColumns.COLUMN_ORG_ID + " in (" + orgId + ")";
			st.executeUpdate(sql) ;
			success = true;
			
		}catch (Exception e) {
			logger.error(e.getMessage(), e);
		}finally {
			ConnectionPool.freeConn(null, st, null, conn);
		}	 
		
		return success;
	}
	
	public static void registOrganizerUser(Organizer source){
		//add user
		int orgId = OrganizerUtil.getOrgIdByEmail(source.getOrganizer_email());
		UserBean userBean = new UserBean();
		userBean.setUser_name(source.getOrganizer_email());
		userBean.setPassword(source.getOrganizer_password());
		userBean.setNick_name(source.getOrganizer_email());
		userBean.setEmail(source.getOrganizer_email());
		userBean.setMobilephone(source.getOrganizer_mobile());
		userBean.setDescription(source.getOrganizer_name());
		userBean.setOrgId(orgId);
		userBean.setStatus(UserUtil.STATUS_NOT_ACTIVE);
		UserUtil.createUser(userBean);
				
		//add role
		UserBean user = UserUtil.getUserByName(source.getOrganizer_email());
		UserUtil.assignRolesToUser("0",String.valueOf(user.getUser_id()));
	}
}
