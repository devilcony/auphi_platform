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
package com.auphi.data.hub.core.util;

public class CloudConstants {
	/**
	 * XML文档风格<br>
	 * 0:节点属性值方式
	 */
	public static final String XML_Attribute = "0";

	/**
	 * XML文档风格<br>
	 * 1:节点元素值方式
	 */
	public static final String XML_Node = "1";

	/**
	 * 字符串组成类型<br>
	 * number:数字字符串
	 */
	public static final String S_STYLE_N = "number";

	/**
	 * 字符串组成类型<br>
	 * letter:字母字符串
	 */
	public static final String S_STYLE_L = "letter";

	/**
	 * 字符串组成类型<br>
	 * numberletter:数字字母混合字符串
	 */
	public static final String S_STYLE_NL = "numberletter";

	/**
	 * 格式化(24小时制)<br>
	 * FORMAT_DateTime: 日期时间
	 */
	public static final String FORMAT_DateTime = "yyyy-MM-dd HH:mm:ss";
	
	/**
	 * 格式化(12小时制)<br>
	 * FORMAT_DateTime: 日期时间
	 */
	public static final String FORMAT_DateTime_12 = "yyyy-MM-dd hh:mm:ss";

	/**
	 * 格式化<br>
	 * FORMAT_DateTime: 日期
	 */
	public static final String FORMAT_Date = "yyyy-MM-dd";

	/**
	 * 格式化(24小时制)<br>
	 * FORMAT_DateTime: 时间
	 */
	public static final String FORMAT_Time = "HH:mm:ss";
	
	/**
	 * 格式化(12小时制)<br>
	 * FORMAT_DateTime: 时间
	 */
	public static final String FORMAT_Time_12 = "hh:mm:ss";

	/**
	 * 换行符<br>
	 * \n:换行
	 */
	public static final String ENTER = "\n";

	/**
	 * 异常信息统一头信息<br>
	 * 非常遗憾的通知您,程序发生了异常
	 */
	public static final String Exception_Head = "\n非常遗憾的通知您,程序发生了异常.\n" + "异常信息如下:\n";

	/**
	 * Ext表格加载模式<br>
	 * \n:非翻页排序加载模式
	 */
	public static final String EXT_GRID_FIRSTLOAD = "first";

	/**
	 * Excel模板数据类型<br>
	 * number:数字类型
	 */
	public static final String ExcelTPL_DataType_Number = "number";

	/**
	 * Excel模板数据类型<br>
	 * number:文本类型
	 */
	public static final String ExcelTPL_DataType_Label = "label";

	/**
	 * HTTP请求类型<br>
	 * 1:裸请求
	 */
	public static final String PostType_Nude = "1";

	/**
	 * HTTP请求类型<br>
	 * 0:常规请求
	 */
	public static final String PostType_Normal = "0";

	/**
	 * Ajax请求超时错误码<br>
	 * 999:Ajax请求超时错误码
	 */
	public static final int Ajax_Timeout = 999;
	
	/**
	 * Ajax请求非法错误码<br>
	 * 998:当前会话userid和登录时候的userid不一致(会话被覆盖)
	 */
	public static final int Ajax_Session_Unavaliable = 998;
	
	/**
	 * 交易状态:成功
	 */
	public static final Boolean TRUE = new Boolean(true);
	
	/**
	 * 交易状态:失败
	 */
	public static final Boolean FALSE = new Boolean(false);
	
	/**
	 * 交易状态:成功
	 */
	public static final String SUCCESS = "1";
	
	/**
	 * 交易状态:失败
	 */
	public static final String FAILURE = "0";

	/**
	 * 分页查询分页参数缺失错误信息
	 */
	public static final String ERR_MSG_QUERYFORPAGE_STRING = "您正在使用分页查询,但是你传递的分页参数缺失!如果不需要分页操作,您可以尝试使用普通查询:queryForList()方法";
	
	/**
	 * Flash图标色彩数组
	 */
	public static String[] CHART_COLORS = {"AFD8F8","F6BD0F","8BBA00","008E8E","D64646","8E468E","588526","B3AA00","008ED6","9D080D","A186BE","1EBE38"};
	/**
	 * 启用状态<br>
	 * 1:启用
	 */
	public static final String ENABLED_Y = "1"; 
	
	/**
	 * 启用状态<br>
	 * 0:停用
	 */
	public static final String ENABLED_N = "0";
	
	/**
	 * 编辑模式<br>
	 * 1:可编辑
	 */
	public static final String EDITMODE_Y = "1"; 
	
	/**
	 * 编辑模式<br>
	 * 0:只读
	 */
	public static final String EDITMODE_N = "0";
	
	/**
	 * 锁定态<br>
	 * 1:锁定
	 */
	public static final String LOCK_Y = "1"; 
	
	/**
	 * 锁定状态<br>
	 * 0:解锁
	 */
	public static final String LOCK_N = "0";
	
	/**
	 * 强制类加载<br>
	 * 1:强制
	 */
	public static final String FORCELOAD_Y = "1"; 
	
	/**
	 * 强制类加载<br>
	 * 0:不强制
	 */
	public static final String FORCELOAD_N = "0";
	
	/**
	 * 树节点类型<br>
	 * 1:叶子节点
	 */
	public static final String LEAF_Y = "1"; 
	
	/**
	 * 树节点类型<br>
	 * 0:树枝节点
	 */
	public static final String LEAF_N = "0";
	
	/**
	 * 角色类型<br>
	 * 1:业务角色
	 */
	public static final String ROLETYPE_BUSINESS = "1";
	
	/**
	 * 角色类型<br>
	 * 2:管理角色
	 */
	public static final String ROLETYPE_ADMIN = "2";
	
	/**
	 * 角色类型<br>
	 * 3:系统内置角色
	 */
	public static final String ROLETYPE_EMBED = "3";
	
	/**
	 * 权限级别<br>
	 * 1:访问权限
	 */
	public static final String AUTHORIZELEVEL_ACCESS = "1"; 
	
	/**
	 * 权限级别<br>
	 * 2:管理权限
	 */
	public static final String AUTHORIZELEVEL_ADMIN = "2";
	
	/**
	 * 用户类型<br>
	 * 1:经办员
	 */
	public static final String USERTYPE_BUSINESS = "1";
	
	/**
	 * 用户类型<br>
	 * 2:管理员
	 */
	public static final String USERTYPE_ADMIN = "2";
	
	/**
	 * 用户类型<br>
	 * 3:系统内置用户
	 */
	public static final String USERTYPE_EMBED = "3";
	
	/**
	 * 根节点ID<br>
	 * 01:菜单树
	 */
	public static final String ROORID_MENU = "01";
	
	/**
	 * 帐户类型<br>
	 * 1:常规帐户
	 */
	public static final String ACCOUNTTYPE_NORMAL = "1";
	
	/**
	 * 帐户类型<br>
	 * 2:SUPER帐户
	 */
	public static final String ACCOUNTTYPE_SUPER = "2";
	
	/**
	 * 帐户类型<br>
	 * 3:DEVELOPER帐户
	 */
	public static final String ACCOUNTTYPE_DEVELOPER = "3";
	
	/**
	 * 操作员事件跟踪监控开关[1:打开;0:关闭]<br>
	 * 1:打开
	 */
	public static final String EVENTMONITOR_ENABLE_Y = "1";
	
	/**
	 * 操作员事件跟踪监控开关[1:打开;0:关闭]<br>
	 * 0:关闭
	 */
	public static final String EVENTMONITOR_ENABLE_N = "0";
	
	/**
	 * 切入点类型[1:BPO切入;2:DAO切入]<br>
	 * 1:BPO切入
	 */
	public static final String POINTCUTTYPE_BPO = "1";
	
	/**
	 * 切入点类型[1:BPO切入;2:DAO切入]<br>
	 * 2:DAO切入
	 */
	public static final String POINTCUTTYPE_DAO = "2";
	
	/**
	 * 通知类型[1:方法调用通知;2:异常捕获通知]<br>
	 * 1:方法调用通知
	 */
	public static final String ADVISETYPE_CALL = "1";
	
	/**
	 * 通知类型[1:方法调用通知;2:异常捕获通知]<br>
	 * 2:异常捕获通知
	 */
	public static final String ADVISETYPE_CATCH = "2";
	
	/**
	 * 菜单类型<br>
	 * 1:系统菜单
	 */
	public static final String MENUTYPE_SYSTEM = "1";
	
	/**
	 * 菜单类型<br>
	 * 0:业务菜单
	 */
	public static final String MENUTYPE_BUSINESS = "0";
	
	/**
	 * UI元素授权类型<br>
	 * 0:未授权
	 */
	public static final String PARTAUTHTYPE_NOGRANT = "0";
	
	public static final String DEFAULT_THEME = "default";

}
