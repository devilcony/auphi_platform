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
package com.auphi.data.hub.service;



import java.sql.SQLException;
import java.util.List;

import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.domain.Datasource;
import com.auphi.data.hub.domain.Service;


/**
 * 任务业务接口 
 * 
 * @author zhangjf
 *
 */
public interface InterfaceService {
	
	public PaginationSupport<Object> queryServiceList(Dto dto)throws SQLException;
	
	public void saveService(Service service);
	
	public void updateService(Service service);
	
	public void deleteServiceById(String serviceIds);
	
	public List<Dto> queryAllJob();
	
	public List<Dto> queryAllTrans();
	
	public Service getServiceByIndetify(String indetify);
	
	public Service getServiceById(String id);
	
	public List<Datasource> getAllDatasource();
	
	public Datasource getDatasourceById(String sourceId);
	
	public List<Dto> getAllService();
	
	public PaginationSupport<Object> queryServiceMonitorList(Dto dto)throws SQLException;
	
	public void saveServiceMonitor(Dto dto);
	
	public void updateServiceMonitor(Dto dto);
}
