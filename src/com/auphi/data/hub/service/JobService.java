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
import java.util.Map;

import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.Dto;


/**
 * 任务业务接口 
 * 
 * @author zhangjf
 *
 */
public interface JobService {
	/**
	 * 保存任务
	 * 
	 * @param pDto
	 * @return
	 */
	public void saveJob(Dto pDto);

	/**
	 * 删除任务
	 * 
	 * @param pDto
	 * @return
	 */
	public void deleteJob(Dto pDto);

	/**
	 * 修改任务
	 * 
	 * @param pDto
	 * @return
	 */
	public void updateJob(Dto pDto);


	
	/**
	 * 查询任务，支持分页
	 * @param dto
	 * @return
	 */
	public PaginationSupport<Dto<String,Object>> queryJob(Dto dto) throws SQLException;
	
	
	public List<Dto<String,Object>> getSlaveHosts();
	
	
	/**
	 * 保存转换运行的信息
	 * @param trans
	 * @param setps
	 */
	public void saveTransLog(Map<String,String> trans,List<Map<String,String>> setps);
	
	/**
	 * 保存转换运行的信息
	 * @param trans
	 * @param setps
	 */
	public void saveJobLog(Map<String,String> job);
	
	public PaginationSupport<Dto<String,Object>> queryJobMonitorList(Dto dto) throws SQLException;
	
	public PaginationSupport<Dto<String,Object>> queryTransMonitorList(Dto dto) throws SQLException;
	
	public String getTransLoginfoById(String transId);
	
	public PaginationSupport<Dto<String,Object>> getTransSetpListByTransId(String transId);
	
	
	public String getJobLoginfoById(String jobId);
	
	public PaginationSupport<Dto<String,Object>> getJobSetpListByTransId(String jobId);
	
	
	public PaginationSupport<Dto<String,Object>> getTriggersList(Dto dto) throws SQLException;
	
	
}
