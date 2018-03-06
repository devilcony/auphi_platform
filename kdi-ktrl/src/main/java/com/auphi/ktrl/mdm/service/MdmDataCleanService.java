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
package com.auphi.ktrl.mdm.service;import com.auphi.data.hub.core.PaginationSupport;import com.auphi.data.hub.core.struct.Dto;import com.auphi.ktrl.mdm.domain.MdmDataClean;import com.auphi.ktrl.mdm.domain.MdmTable;import java.sql.SQLException;/**    * This class is used for ...    * @author Tony  * @version    *       1.0, 2016-2-25 下午8:20:43    */public interface MdmDataCleanService {	public void save(MdmDataClean object) throws SQLException;	public Integer queryMaxId(Dto dto) throws SQLException;	public PaginationSupport<MdmTable> query(Dto<String, Object> dto)throws SQLException;	public void delete(Dto dto)throws SQLException;	public MdmDataClean queryById(Dto dto)throws SQLException;	public void update(MdmDataClean mdmDataClean)throws SQLException;}