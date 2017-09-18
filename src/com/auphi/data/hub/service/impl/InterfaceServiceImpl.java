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
package com.auphi.data.hub.service.impl;



import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.domain.Datasource;
import com.auphi.data.hub.service.InterfaceService;


/**
 * 任务业务接口 
 * 
 * @author zhangjf
 *
 */
@Service("interfaceService")
public class InterfaceServiceImpl implements InterfaceService,Serializable {


	private static final long serialVersionUID = 6078563219513903168L;

	@Autowired
	SystemDao systemDao;

	
	public PaginationSupport<Object> queryServiceList(Dto dto)
			throws SQLException {
		List<Object> items = systemDao.queryForPage("Service.queryServiceList", dto);
		Integer total = (Integer)systemDao.queryForObject("Service.queryServiceListCount",dto);
		PaginationSupport<Object> page = new PaginationSupport<Object>(items, total);
		return page;
	}
	
	
	public void saveService(com.auphi.data.hub.domain.Service service){
		systemDao.save("Service.insertService",service);
	}
	
	public void updateService(com.auphi.data.hub.domain.Service service){
		systemDao.update("Service.updateService",service);
	}
	
	public void deleteServiceById(String serviceIds){
		Dto dto = new BaseDto();
		dto.put("serviceIds", serviceIds);
		systemDao.delete("Service.deleteServiceByIds",dto);
	}
	
	
	/*public List<Dto> queryAllJobAndTrans(){
		return systemDao.queryForList("Job.queryAllJobAndTrans");
	}*/
	
	
	public com.auphi.data.hub.domain.Service getServiceByIndetify(String indetify){
		Dto dto = new BaseDto();
		dto.put("indetify", indetify);
		Object obj = systemDao.queryForObject("Service.getServiceByIndetify",dto);
		if(obj != null ){
			return (com.auphi.data.hub.domain.Service)obj;
		}
		return null;
	}
	
	
	public com.auphi.data.hub.domain.Service getServiceById(String id){
		Dto dto = new BaseDto();
		dto.put("id", id);

		Object obj = systemDao.queryForObject("Service.getServiceById",dto);
		if(obj != null ){
			return (com.auphi.data.hub.domain.Service)obj;
		}
		return null;
	}

	public List<Datasource> getAllDatasource(){
		return systemDao.queryForList("Datasource.querySourceList");
	}
	
	public Datasource getDatasourceById(String sourceId){
		Dto dto = new BaseDto();
		dto.put("sourceId", sourceId);
		return (Datasource)systemDao.queryForObject("Datasource.querySourceById",dto);
	}

	//获得所有的服务列表
	public List<Dto> getAllService() {
		return systemDao.queryForList("Service.getAllService");
	}


	@Override
	public List<Dto> queryAllJob() {
		return systemDao.queryForList("Job.queryAllJob");
	}


	@Override
	public List<Dto> queryAllTrans() {
		return systemDao.queryForList("Job.queryAllTrans");
	}


	//保存服务监控信息
	public void saveServiceMonitor(Dto dto){
		systemDao.save("Service.insertServiceMonitor",dto);
	}


	//更新服务监控信息
	public void updateServiceMonitor(Dto dto){
		systemDao.update("Service.updateServiceMonitor",dto);
	}


	//获得服务监控列表
	public PaginationSupport<Object> queryServiceMonitorList(Dto dto)
			throws SQLException {
		List<Object> items = systemDao.queryForPage("Service.queryServiceMonitorList", dto);
		Integer total = (Integer)systemDao.queryForObject("Service.queryServiceMonitorCount",dto);
		PaginationSupport<Object> page = new PaginationSupport<Object>(items, total);
		return page;
	}
	
}
