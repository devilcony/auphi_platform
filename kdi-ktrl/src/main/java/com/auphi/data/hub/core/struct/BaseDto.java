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
package com.auphi.data.hub.core.struct;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.data.hub.core.util.TypeConvertHelper;

/**
 * 数据传输对象(DateTransferObject)
 * 建议在参数传递过程中尽量使用Dto来传递
 * 
 * @author zhangjiafeng
 */
public class BaseDto extends HashMap<String,Object> implements Dto<String,Object>, Serializable {
	
	private static final long serialVersionUID = -5299749221985719634L;

	public BaseDto(){}
	
	public BaseDto(String key, Object value){
		put(key, value);
	}
	
	public BaseDto(Boolean success){
		setSuccess(success);
	}
	
	public BaseDto(Boolean success, String msg){
		setSuccess(success);
		setMsg(msg);
	}

	/**
	 * 以BigDecimal类型返回键值
	 * 
	 * @param key 键名
	 * @return BigDecimal 键值
	 */
	public BigDecimal getAsBigDecimal(String key) {
		Object obj = TypeConvertHelper.convert(get(key), "BigDecimal", null);
		if (obj != null) {
			return (BigDecimal) obj;
		} else {
			return null;
		}
	}

	/**
	 * 以Date类型返回键值
	 * 
	 * @param key
	 *            键名
	 * @return Date 键值
	 */
	public Date getAsDate(String key) {
		Object obj = TypeConvertHelper.convert(get(key), "Date", "yyyy-MM-dd");
		if (obj != null) {
			return (Date) obj;
		} else {
			return null;
		}
	}

	/**
	 * 以Integer类型返回键值
	 * 
	 * @param key
	 *            键名
	 * @return Integer 键值
	 */
	public Integer getAsInteger(String key) {
		Object obj = TypeConvertHelper.convert(get(key), "Integer", null);
		if (obj != null) {
			return (Integer) obj;
		} else {
			return null;
		}
	}

	/**
	 * 以Long类型返回键值
	 * 
	 * @param key
	 *            键名
	 * @return Long 键值
	 */
	public Long getAsLong(String key) {
		Object obj = TypeConvertHelper.convert(get(key), "Long", null);
		if (obj != null){
			return (Long) obj;
		} else {
			return null;
		}
	}

	/**
	 * 以String类型返回键值
	 * 
	 * @param key
	 *            键名
	 * @return String 键值
	 */
	public String getAsString(String key) {
		Object obj = TypeConvertHelper.convert(get(key), "String", null);
		if (obj != null){
			return (String) obj;
		} else {
			return "";
		}
	}
	
	/**
	 * 以List类型返回键值
	 * 
	 * @param key  键名
	 * @return List 键值
	 */
	public List getAsList(String key){
		return (List)get(key);
	}

	/**
	 * 以Timestamp类型返回键值
	 * 
	 * @param key  键名
	 * @return Timestamp 键值
	 */
	public Timestamp getAsTimestamp(String key) {
		Object obj = TypeConvertHelper.convert(get(key), "Timestamp", "yyyy-MM-dd HH:mm:ss");
		if (obj != null){
			return (Timestamp) obj;
		} else {
			return null;
		}
	}
	
	/**
	 * 以Boolean类型返回键值
	 * 
	 * @param key 键名
	 * @return Timestamp 键值
	 */
	public Boolean getAsBoolean(String key){
		Object obj = TypeConvertHelper.convert(get(key), "Boolean", null);
		if (obj != null) {
			return (Boolean) obj;
		} else {
			return null;
		}
	}

	/**
	 * 给Dto压入第一个默认List对象<br>
	 * 为了方便存取(省去根据Key来存取和类型转换的过程)
	 * 
	 * @param pList 压入Dto的List对象
	 */
	public void setDefaultAList(List pList) {
		put("defaultAList", pList);
	}

	/**
	 * 给Dto压入第二个默认List对象<br>
	 * 为了方便存取(省去根据Key来存取和类型转换的过程)
	 * 
	 * @param pList 压入Dto的List对象
	 */
	public void setDefaultBList(List pList) {
		put("defaultBList", pList);
	}

	/**
	 * 获取第一个默认List对象<br>
	 * 为了方便存取(省去根据Key来存取和类型转换的过程)
	 * 
	 * @param pList  压入Dto的List对象
	 */
	public List getDefaultAList() {
		return (List) get("defaultAList");
	}

	/**
	 * 获取第二个默认List对象<br>
	 * 为了方便存取(省去根据Key来存取和类型转换的过程)
	 * 
	 * @param pList  压入Dto的List对象
	 */
	public List getDefaultBList() {
		return (List) get("defaultBList");
	}
	
    /**
     * 给Dto压入一个默认的Json格式字符串
     * @param jsonString
     */
	public void setDefaultJson(String jsonString){
    	put("defaultJsonString", jsonString);
    }
    
    /**
     * 获取默认的Json格式字符串
     * @return
     */
    public String getDefaultJson(){
    	return getAsString("defaultJsonString");
    }
	
	/**
	 * 将此Dto对象转换为Json格式字符串<br>
	 * 
	 * @return string 返回Json格式字符串
	 */
	public String toJson() {
		String strJson = null;
		strJson = JsonHelper.encodeObject2Json(this);
		return strJson;
	}
	
	/**
	 * 将此Dto对象转换为Json格式字符串(带日期时间型)<br>
	 * 
	 * @return string 返回Json格式字符串
	 */
	public String toJson(String pFormat){
		String strJson = null;
		strJson = JsonHelper.encodeObject2Json(this, pFormat);
		return strJson;
	}
	
	/**
	 * 设置交易状态
	 * 
	 * @param pSuccess
	 */
	public void setSuccess(Boolean pSuccess){
		put("success", pSuccess);		
	}
	
	/**
	 * 获取交易状态
	 * 
	 * @param pSuccess
	 */
	public Boolean getSuccess(){
		return getAsBoolean("success");
	}
	
	/**
	 * 设置交易提示信息
	 * 
	 * @param pSuccess
	 */
	public void setMsg(String pMsg){
		put("msg", pMsg);
	}
	
	/**
	 * 获取交易提示信息
	 * 
	 * @param pSuccess
	 */
	public String getMsg(){
		return getAsString("msg");
	}
	
	/**
	 * 打印DTO对象
	 * 
	 */
	public void println(){
		System.out.println(this);
	}


}

