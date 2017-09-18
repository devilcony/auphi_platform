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
package com.auphi.data.hub.core;


import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.CloudConstants;
import com.auphi.data.hub.core.util.CloudUtils;
import com.auphi.data.hub.core.util.SessionContainer;
import com.auphi.data.hub.core.util.WebUtils;
import com.auphi.data.hub.domain.Datasource;
import com.auphi.data.hub.service.DatasourceService;
import com.auphi.ktrl.mdm.domain.DataBaseType;
import com.auphi.ktrl.mdm.service.DataBaseTypeService;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础的controller类。封装所有controller类通用的操作，所有的controller都要从这个类中继承
 * 
 * @author jiafeng.zhang
 *
 */
public class BaseMultiActionController extends MultiActionController {

	public final String SESSION_MAPINGTABLE ="mapingTable";

	@Autowired
	private DatasourceService datasourceService;

	@Autowired
	private DataBaseTypeService dataBaseTypeService;

	private Map<Integer,Datasource> dataSourceMap = new HashMap<Integer,Datasource>();

	private Map<Integer,DataBaseType> dataBaseTypeMap = new HashMap<Integer,DataBaseType>();
	/**
	 * init upload parameter binder
	 * 
	 * @param request
	 * @param binder
	 */
	public void initBinder(HttpServletRequest request,
			ServletRequestDataBinder binder) {
		binder.registerCustomEditor(byte[].class,new ByteArrayMultipartFileEditor());
	}


	/**
	 * 设置一个Session属性对象
	 * @param request
	 * @param sessionKey
	 * @param objSessionAttribute
     */
	protected void setSessionAttribute(HttpServletRequest request, String sessionKey, Object objSessionAttribute) {
		HttpSession session = request.getSession();
		if (session != null)
			session.setAttribute(sessionKey, objSessionAttribute);
	}

	/**
	 * 移除Session对象属性值
	 * @param request
	 * @param sessionKey
     */
	protected void removeSessionAttribute(HttpServletRequest request, String sessionKey) {
		HttpSession session = request.getSession();
		if (session != null)
			session.removeAttribute(sessionKey);
	}
	
	/**
	 * 输出响应
	 * 
	 * @param str
	 * @throws IOException
	 */
	protected void write(String str, HttpServletResponse resp) throws IOException {
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(str);
		resp.getWriter().flush();
		resp.getWriter().close();
	}

	/**
	 * 获取一个SessionContainer容器,如果为null则创建之
	 * @param request
	 * @return
     */
	protected SessionContainer getSessionContainer(HttpServletRequest request) {
		SessionContainer sessionContainer = (SessionContainer) this.getSessionAttribute(request, "SessionContainer");
		if (sessionContainer == null) {
			sessionContainer = new SessionContainer();
			HttpSession session = request.getSession(true);
			session.setAttribute("SessionContainer", sessionContainer);
		}
		return sessionContainer;
	}

	/**
	 * 获取一个Session属性对象
	 * @param request
	 * @param sessionKey
     * @return
     */
	protected Object getSessionAttribute(HttpServletRequest request, String sessionKey) {
		Object objSessionAttribute = null;
		HttpSession session = request.getSession(false);
		if (session != null) {
			objSessionAttribute = session.getAttribute(sessionKey);
		}
		return objSessionAttribute;
	}
	
	
	protected String getParamValue(String pParamKey, HttpServletRequest request) {
		return WebUtils.getParamValue(pParamKey, request);
	}
	
	/**
	 * 设置页面翻页参数
	 * @param dto
	 * @param req
	 */
	protected void setPageParam(Dto dto,HttpServletRequest req){
		String start = req.getParameter("start");
		String pageSize = req.getParameter("limit");
		dto.put("start", Integer.parseInt(start) );
		dto.put("end", (Integer.parseInt(start) +   Integer.parseInt(pageSize)));
	}
	/**
	 * 操作成功的提示消息
	 * @param pMsg
	 * @param response
	 * @throws IOException
	 */
	protected void setOkTipMsg(String pMsg, HttpServletResponse response) throws IOException {
		Dto outDto = new BaseDto(CloudConstants.TRUE, pMsg);
		write(outDto.toJson(), response);
	}

	/**
	 * 操作成功的提示消息
	 * @param pMsg
	 * @param data 返回的数据
	 * @param response
	 * @throws IOException
     */
	protected void setOkTipMsg(String pMsg,Object data, HttpServletResponse response) throws IOException {
		Dto outDto = new BaseDto(CloudConstants.TRUE, pMsg);
		outDto.put("data",data);
		write(outDto.toJson(), response);
	}

	protected void setFailTipMsg(String pMsg, HttpServletResponse response) throws IOException {
		Dto outDto = new BaseDto(CloudConstants.FALSE, pMsg);
		write(outDto.toJson(), response);
	}	
	/**
	 * 上传文件，返回一个Map，包含一下key值{title,filesize,path}
	 * @param req
	 * @param resp
	 * @param fieldName
	 * @return
	 */
	public Dto<String,Object> uploadFile(HttpServletRequest req, HttpServletResponse resp, String fieldName) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) req;
		// 获得上传文件
		CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile(fieldName);
		Dto<String,Object> dto = new BaseDto();
		if (file != null && file.getSize() > 0) {
			String dir = getServletContext().getRealPath("/") + "uploaddata/"+ CloudUtils.getCurDate() + "/";
			File dirPath = new File(dir);
			if (!dirPath.exists()) {
				dirPath.mkdirs();
			}
			dto.put("title", file.getOriginalFilename());
			dto.put("filesize", file.getSize());
			String filePath = dir + file.getOriginalFilename();
			// 创建文件
			File uploadedFile = new File(filePath);
			try {
				FileCopyUtils.copy(file.getBytes(), uploadedFile);
				dto.put("path", filePath);
				return dto;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public Database createDatabase(int id_dataBase){
		Database database = null;
		try{

			if(dataSourceMap.size() == 0 ||  !dataSourceMap.containsKey(id_dataBase)){
				List<Datasource> dataSourceList = datasourceService.querySourceList();
				for(Datasource dataSource :dataSourceList){
					dataSourceMap.put(dataSource.getSourceId(), dataSource);
				}
			}
			if(dataBaseTypeMap.size() == 0){
				List<DataBaseType> dataBaseTypeList = dataBaseTypeService.queryAll();
				for(DataBaseType dataBaseType : dataBaseTypeList){
					dataBaseTypeMap.put(dataBaseType.getId_database_type(), dataBaseType);
				}
			}
			if(dataSourceMap.containsKey(id_dataBase)){
				Datasource datasource = dataSourceMap.get(id_dataBase);
				DataBaseType dataBaseType = dataBaseTypeMap.get(datasource.getSourceType());
				DatabaseMeta databaseMeta = new DatabaseMeta(
						datasource.getSourceName(),
						dataBaseType.getDescription(),
						"Native",
						datasource.getSourceIp(),
						datasource.getSourceDataBaseName(),
						datasource.getSourcePort(),
						datasource.getSourceUserName(),
						datasource.getSourcePassword());
				database = new Database(databaseMeta);
				database.connect();
				if(dataBaseType.getDescription().equalsIgnoreCase("Hadoop Hive 2") || dataBaseType.getDescription().equalsIgnoreCase("Hadoop Hive"))
				{
					database.execStatement("use "+datasource.getSourceDataBaseName());
				}
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		return database;
	}


}
