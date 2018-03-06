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

import java.io.Serializable;
import java.util.List;

/**
 * 分页对象
 * 
 * @author inc062977
 * @param <T>
 *
 */
public class PaginationSupport<T> implements Serializable{
	
	private static final long serialVersionUID = 1L;


	private boolean success = true;

	/**
	 * 记录总数
	 */
	private int total;

	/**
	 * 对象集合
	 */
	private List<T> rows;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getRows() {
		return rows;
	}

	public void setRows(List<T> rows) {
		this.rows = rows;
	}

	/**
	 * 根据数据集合和总记录数构造
	 * 
	 * @param items
	 * @param totalCount
	 */
	public PaginationSupport(List<T> items, int totalCount) {
		this.setTotal(totalCount);
		this.setRows(items);
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
}
