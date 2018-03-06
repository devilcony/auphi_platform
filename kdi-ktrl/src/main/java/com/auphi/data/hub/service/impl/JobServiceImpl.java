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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.dao.SystemDao;
import com.auphi.data.hub.domain.Constant;
import com.auphi.data.hub.service.JobService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author mac
 *
 */
@Service("jobService")
public class JobServiceImpl  implements JobService,Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 76288977845571272L;
	
	@Autowired
    SystemDao systemDao;

	public void saveJob(Dto Job) {
		systemDao.save("Job.insertJob",Job);
	}

	public void deleteJob(Dto pDto) {
		systemDao.update("Job.deleteJob",pDto);
	}

	public void updateJob(Dto Job) {
		systemDao.update("Job.updateJob",Job);
	}

	public PaginationSupport<Dto<String, Object>> queryJob(Dto dto)
			throws SQLException {
		List<Dto<String,Object>> items = systemDao.queryForPage("Job.queryJob", dto);
		Integer total = (Integer)systemDao.queryForObject("Job.queryJobCount",dto);
		PaginationSupport<Dto<String,Object>> page = new PaginationSupport<Dto<String,Object>>(items, total);

		return page;
	}
	
	public List<Dto<String,Object>> getSlaveHosts(){
		List<Dto<String,Object>> items = systemDao.queryForList("Job.getSlaveHosts");
		List<Dto<String,Object>> results = new ArrayList<Dto<String,Object>>();
		for(Dto dt : items){
			Dto<String,Object> dto = new BaseDto();
			dto.put("slaveName", dt.get("name"));
			dto.put("slave", dt.get("host_name") + ":" +  dt.get("port"));
			results.add(dto);
		}
		return results;
	}
	
	/**
	 * 保存转换运行的信息
	 * @param trans
	 * @param setps
	 */
	public void saveTransLog(Map<String,String> trans,List<Map<String,String>> setps){
		systemDao.save("Job.insertTransLog",trans);
		String transLogId = trans.get("TRANS_ID");
		for(Map<String,String> setp : setps){
			setp.put("TRANS_ID", transLogId);
			systemDao.save("Job.insertTransSetpLog",setp);
		}
	}
	
	
	
	/**
	 * 保存转换运行的信息
	 * @param trans
	 * @param setps
	 */
	public void saveJobLog(Map<String,String> job){
		systemDao.save("Job.insertJobLog",job);

	}
	
	public PaginationSupport<Dto<String,Object>> queryJobMonitorList(Dto dto) throws SQLException{
		List<Dto<String,Object>> items = systemDao.queryForPage("Job.queryJobMonitorList", dto);
		Integer total = (Integer)systemDao.queryForObject("Job.queryJobMonitorListCount",dto);
		PaginationSupport<Dto<String,Object>> page = new PaginationSupport<Dto<String,Object>>(items, total);
		return page;
	}
	
	public PaginationSupport<Dto<String,Object>> queryTransMonitorList(Dto dto) throws SQLException{
		List<Dto<String,Object>> items = systemDao.queryForPage("Job.queryTransMonitorList", dto);
		Integer total = (Integer)systemDao.queryForObject("Job.queryTransMonitorListCount",dto);
		PaginationSupport<Dto<String,Object>> page = new PaginationSupport<Dto<String,Object>>(items, total);
		return page;
	}
	
	public String getTransLoginfoById(String transId){
		Dto dto = new BaseDto();
		dto.put("transId", transId);
		String logInfo = (String)systemDao.queryForObject("Job.selectTransLogInfo",dto);
		return logInfo;
	}
	
	
	public PaginationSupport<Dto<String,Object>> getTransSetpListByTransId(String transId){
		Dto dto = new BaseDto();
		dto.put("transId", transId);
		List<Dto<String,Object>> list = systemDao.queryForList("Job.selectTransSetpList",dto);
		PaginationSupport<Dto<String,Object>> page = new PaginationSupport<Dto<String,Object>>(list, list.size());
		return page;
	}
	
	
	public String getJobLoginfoById(String jobId){
		Dto dto = new BaseDto();
		dto.put("jobId", jobId);
		String logInfo = (String)systemDao.queryForObject("Job.selectJobLogInfo",dto);
		return logInfo;
	}
	
	public PaginationSupport<Dto<String,Object>> getJobSetpListByTransId(String jobId){
		Dto dto = new BaseDto();
		dto.put("jobId", jobId);
		List<Dto<String,Object>> list = systemDao.queryForList("Job.selectJobSetpList",dto);
		PaginationSupport<Dto<String,Object>> page = new PaginationSupport<Dto<String,Object>>(list, list.size());
		return page;

	}
	
	
	public PaginationSupport<Dto<String,Object>> getTriggersList(Dto dto) throws SQLException{		
		List<Dto<String,Object>> items = systemDao.queryForPage("Job.selectTriggerList", dto);
		long val = 0;
		String temp = null;
		for (Map<String, Object> map : items) {
			temp = MapUtils.getString(map, "TRIGGER_NAME");
			if(StringUtils.indexOf(temp, "&") != -1){
				map.put("display_name", StringUtils.substringBefore(temp, "&"));
			}else{
				map.put("display_name", temp);
			}
			
			val = MapUtils.getLongValue(map, "NEXT_FIRE_TIME");
			if (val > 0) {
				map.put("NEXT_FIRE_TIME", DateFormatUtils.format(val, "yyyy-MM-dd HH:mm:ss"));
			}

			val = MapUtils.getLongValue(map, "PREV_FIRE_TIME");
			if (val > 0) {
				map.put("PREV_FIRE_TIME", DateFormatUtils.format(val, "yyyy-MM-dd HH:mm:ss"));
			}

			val = MapUtils.getLongValue(map, "START_TIME");
			if (val > 0) {
				map.put("START_TIME", DateFormatUtils.format(val, "yyyy-MM-dd HH:mm:ss"));
			}
			
			val = MapUtils.getLongValue(map, "END_TIME");
			if (val > 0) {
				map.put("END_TIME", DateFormatUtils.format(val, "yyyy-MM-dd HH:mm:ss"));
			}
			
			map.put("JOB_NAME",MapUtils.getString(map, "JOB_NAME"));
			map.put("DESCRIPTION",MapUtils.getString(map, "DESCRIPTION"));
			
			map.put("TRIGGER_STATE", Constant.status.get(MapUtils.getString(map, "TRIGGER_STATE")));
		}
		Integer total = (Integer)systemDao.queryForObject("Job.selectTriggerListCount",dto);
		PaginationSupport<Dto<String,Object>> page = new PaginationSupport<Dto<String,Object>>(items, total);
		return page;

	}
}
