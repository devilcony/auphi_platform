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
package com.auphi.data.hub.core.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

public class JsonHelper {

	private static Log log = LogFactory.getLog(JsonHelper.class);

	/**
	 * 将不含日期时间格式的Java对象系列化为Json资料格式
	 * 
	 * @param pObject  传入的Java对象
	 * @return
	 */
	public static final String encodeObject2Json(Object pObject) {
		String jsonString = "[]";
		if (CloudUtils.isEmpty(pObject)) {
			 log.warn("传入的Java对象为空,不能将其序列化为Json资料格式.请检查!");
		} else {
			JsonConfig config = new JsonConfig();  
            config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss")); 
			if (pObject instanceof ArrayList) {
				JSONArray jsonArray = JSONArray.fromObject(pObject,config);
				jsonString = jsonArray.toString();
			} else {
				JSONObject jsonObject = JSONObject.fromObject(pObject,config);
				jsonString = jsonObject.toString();
			}
		}
		if (log.isInfoEnabled()) {
			log.info("序列化后的JSON资料输出:\n" + jsonString);
		}
		return jsonString;
	}

	/**
	 * 将含有日期时间格式的Java对象系列化为Json资料格式
	 * Json-Lib在处理日期时间格式是需要实现其JsonValueProcessor接口,所以在这里提供一个重载的方法对含有<br>
	 * 日期时间格式的Java对象进行序列化
	 * 
	 * @param pObject 传入的Java对象
	 * @return
	 */
	public static final String encodeObject2Json(Object pObject, String pFormatString) {
		String jsonString = "[]";
		if (CloudUtils.isEmpty(pObject)) {
			 log.warn("传入的Java对象为空,不能将其序列化为Json资料格式.请检查!");
		} else {
			JsonConfig cfg = new JsonConfig();
			cfg.registerJsonValueProcessor(java.sql.Timestamp.class, new JsonValueProcessorImpl(pFormatString));
			cfg.registerJsonValueProcessor(java.util.Date.class, new JsonValueProcessorImpl(pFormatString));
			cfg.registerJsonValueProcessor(java.sql.Date.class, new JsonValueProcessorImpl(pFormatString));
			if (pObject instanceof ArrayList) {
				JSONArray jsonArray = JSONArray.fromObject(pObject, cfg);
				jsonString = jsonArray.toString();
			} else {
				JSONObject jsonObject = JSONObject.fromObject(pObject, cfg);
				jsonString = jsonObject.toString();
			}
		}
		if (log.isInfoEnabled()) {
			log.info("序列化后的JSON资料输出:\n" + jsonString);
		}
		return jsonString;
	}

	/**
	 * 将分页信息压入JSON字符串
	 * 此类内部使用,不对外暴露
	 * @param JSON字符串
	 * @param totalCount
	 * @return 返回合并后的字符串
	 */
	private static String encodeJson2PageJson(String jsonString, Integer totalCount) {
		jsonString = "{TOTALCOUNT:" + totalCount + ", ROOT:" + jsonString + "}";
		if (log.isInfoEnabled()) {
			log.info("合并后的JSON资料输出:\n" + jsonString);
		}
		return jsonString;
	}

	/**
	 * 直接将List转为分页所需要的Json资料格式
	 * 
	 * @param list 需要编码的List对象
	 * @param totalCount  记录总数
	 * @param pDataFormat 时间日期格式化,传null则表明List不包含日期时间属性
	 */
	public static final String encodeList2PageJson(List list, Integer totalCount, String dataFormat) {
		String subJsonString = "";
		if (CloudUtils.isEmpty(dataFormat)) {
			subJsonString = encodeObject2Json(list);
		} else {
			subJsonString = encodeObject2Json(list, dataFormat);
		}
		String jsonString = "{TOTALCOUNT:" + totalCount + ", ROOT:" + subJsonString + "}";
		if (log.isInfoEnabled()) {
			log.info("序列化后的JSON资料输出:\n" + jsonString);
		}
		return jsonString;
	}

	/**
	 * 将数据系列化为表单数据填充所需的Json格式
	 * 
	 * @param pObject 待系列化的对象
	 * @param pFormatString 日期时间格式化,如果为null则认为没有日期时间型字段
	 * @return
	 */
	public static String encodeDto2FormLoadJson(Dto pDto, String pFormatString) {
		String jsonString = "";
		String sunJsonString = "";
		if (CloudUtils.isEmpty(pFormatString)) {
			sunJsonString = encodeObject2Json(pDto);
		} else {
			sunJsonString = encodeObject2Json(pDto, pFormatString);
		}
		jsonString = "{success:"
				+ (CloudUtils.isEmpty(pDto.getAsString("success")) ? "true" : pDto.getAsString("success")) + ",data:"
				+ sunJsonString + "}";
		if (log.isInfoEnabled()) {
			log.info("序列化后的JSON资料输出:\n" + jsonString);
		}
		return jsonString;
	}

	/**
	 * 将单一Json对象解析为DTOJava对象
	 * 
	 * @param jsonString  简单的Json对象
	 * @return dto
	 */
	public static Dto parseSingleJson2Dto(String jsonString) {
		Dto dto = new BaseDto();
		if (CloudUtils.isEmpty(jsonString)) {
			return dto;
		}
		JSONObject jb = JSONObject.fromObject(jsonString);
		dto = (BaseDto) JSONObject.toBean(jb, BaseDto.class);
		return dto;
	}

	/**
	 * 将复杂Json资料格式转换为List对象
	 * 
	 * @param jsonString
	 *            复杂Json对象,格式必须符合如下契约
	 *            {"1":{"name":"成龙","age":"27"},
	 *            "2":{"name":"李连杰","age":"72"}}
	 * @return List
	 */
	public static List parseJson2List(String jsonString) {
		List list = new ArrayList();
		JSONObject jbJsonObject = JSONObject.fromObject(jsonString);
		Iterator iterator = jbJsonObject.keySet().iterator();
		while (iterator.hasNext()) {
			Dto dto = parseSingleJson2Dto(jbJsonObject.getString(iterator.next().toString()));
			list.add(dto);
		}
		return list;
	}
}
