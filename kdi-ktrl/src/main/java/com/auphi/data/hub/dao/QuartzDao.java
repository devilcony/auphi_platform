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
package com.auphi.data.hub.dao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.auphi.data.hub.domain.Constant;

@Repository("quartzDao")
@SuppressWarnings("unchecked")
public class QuartzDao {

	private DataSource dataSource;

	@Autowired
	public void setDataSource(@Qualifier("dataSource") DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public List<Map<String, Object>> getQrtzTriggers() {
		List<Map<String, Object>> results = getJdbcTemplate().queryForList("select * from QRTZ_TRIGGERS order by start_time");
		long val = 0;
		String temp = null;
		for (Map<String, Object> map : results) {
			temp = MapUtils.getString(map, "TRIGGER_NAME");
			if(StringUtils.indexOf(temp, "&") != -1){
				map.put("DISPLAY_NAME", StringUtils.substringBefore(temp, "&"));
			}else{
				map.put("DISPLAY_NAME", temp);
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
			
			map.put("JOBNAME",MapUtils.getString(map, "JOB_NAME"));
			map.put("DESCRIPTION",MapUtils.getString(map, "DESCRIPTION"));
			
			map.put("STATU",Constant.status.get(MapUtils.getString(map, "TRIGGER_STATE")));
		}

		return results;
	}
	
	

	private JdbcTemplate getJdbcTemplate() {
		return new JdbcTemplate(this.dataSource);
	}
}
