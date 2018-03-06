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
package com.auphi.ktrl.mdm.domain;/**    * This class is used for ...    * @author Tony  * @version    *       1.0, 2016-2-25 下午5:02:33    *       匹配结果 */public class MdmMapingResult {		public final static int SUCCESS = 1;	public final static int FAILURE = 0;		private int id;//生成一个唯一id	private String original_key;//原主键	private String original_name;//原值	private String standard_key;//标准主键	private String standard_name;//标准值	private Long falg; //1成功 0 失败			public int getId() {		return id;	}	public void setId(int id) {		this.id = id;	}	public String getOriginal_key() {		return original_key;	}	public void setOriginal_key(String original_key) {		this.original_key = original_key;	}	public String getOriginal_name() {		return original_name;	}	public void setOriginal_name(String original_name) {		this.original_name = original_name;	}	public String getStandard_key() {		return standard_key;	}	public void setStandard_key(String standard_key) {		this.standard_key = standard_key;	}	public String getStandard_name() {		return standard_name;	}	public void setStandard_name(String standard_name) {		this.standard_name = standard_name;	}	public Long getFalg() {		return falg;	}	public void setFalg(Long falg) {		this.falg = falg;	}	}