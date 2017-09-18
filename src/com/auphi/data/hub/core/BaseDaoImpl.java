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

import java.sql.SQLException;
import java.util.List;


import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.CloudUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.support.SqlSessionDaoSupport;


/**
 * 数据访问实现类(封装后)
 * 基于Spring对iBatis的支持机制实现,支持实体对象的数据操作
 * 
 * @author zhangjiafeng
 *
 */
public class BaseDaoImpl extends SqlSessionDaoSupport implements BaseDao {

	private static Log log = LogFactory.getLog(BaseDaoImpl.class);
	
	public SqlSession getSession() {
		return this.getSqlSession();
	}

	/**
	 * 插入一条记录
	 * 
	 * @param SQL语句ID号
	 * @param parameterObject  要插入的对象(map javaBean)
	 */
	public void save(String statementName, Object parameterObject) {
		getSqlSession().insert(statementName, parameterObject);
	}

	/**
	 * 插入一条记录
	 * 
	 * @param SQL语句ID号
	 */
	public void save(String statementName) {
		getSqlSession().insert(statementName, new BaseDto());
	}

	/**
	 * 查询一条记录
	 * 
	 * @param SQL语句ID号
	 * @param parameterObject 查询条件对象(map javaBean)
	 */
	public Object queryForObject(String statementName, Object parameterObject) {
		return getSqlSession().selectOne(statementName, parameterObject);
	}

	/**
	 * 查询一条记录
	 * 
	 * @param SQL语句ID号
	 */
	public Object queryForObject(String statementName) {
		return getSqlSession().selectOne(statementName, new BaseDto());
	}

	/**
	 * 查询记录集合
	 * 
	 * @param SQL语句ID号
	 * @param parameterObject 查询条件对象(map javaBean)
	 */
	public List queryForList(String statementName, Object parameterObject) {
		return getSqlSession().selectList(statementName, parameterObject);
	}

	/**
	 * 查询记录集合
	 * 
	 * @param SQL语句ID号
	 */
	public List queryForList(String statementName) {
		return getSqlSession().selectList(statementName, new BaseDto());
	}

	/**
	 * 按分页查询
	 * 
	 * @param SQL语句ID号
	 * @param parameterObject 查询条件对象(map javaBean)
	 * @throws SQLException 
	 */
	public List queryForPage(String statementName, Dto qDto) throws SQLException {
		String start = qDto.getAsString("start");
		String limit = qDto.getAsString("limit");
		int startInt = 0;
		//如果开始页码不为空，将开始页码放到Dto对象中
		if (CloudUtils.isNotEmpty(start)) {
			startInt = Integer.parseInt(start);
			qDto.put("start", startInt);
		}
		//每页显示多少条记录不为空，将每页显示记录数放到Dto对象中
		if (CloudUtils.isNotEmpty(limit)) {
			int limitInt = Integer.parseInt(limit);
			qDto.put("end", limitInt);
		}
		
		Integer intStart = qDto.getAsInteger("start");
		Integer end = qDto.getAsInteger("end");
		//如果翻页参数开始页码或者结束页码为空，抛出异常信息
		if (CloudUtils.isEmpty(start) || CloudUtils.isEmpty(end)) {
			try {
				throw new RuntimeException("您正在使用分页查询,但是你传递的分页参数缺失!如果不需要分页操作,您可以尝试使用普通查询:queryForList()");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return getSqlSession().selectList(statementName, qDto);
	}

	/**
	 * 更新记录
	 * 
	 * @param SQL语句ID号
	 * @param parameterObject
	 *            更新对象(map javaBean)
	 */
	public int update(String statementName, Object parameterObject) {
		return getSqlSession().update(statementName, parameterObject);
	}

	/**
	 * 更新记录
	 * 
	 * @param SQL语句ID号
	 */
	public int update(String statementName) {
		return getSqlSession().update(statementName, new BaseDto());
	}

	/**
	 * 删除记录
	 * 
	 * @param SQL语句ID号
	 * @param parameterObject
	 *            更新对象(map javaBean)
	 */
	public int delete(String statementName, Object parameterObject) {
		return getSqlSession().delete(statementName, parameterObject);
	}

	/**
	 * 删除记录
	 * 
	 * @param SQL语句ID号
	 */
	public int delete(String statementName) {
		return getSqlSession().delete(statementName, new BaseDto());
	}



}