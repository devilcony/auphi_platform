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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

/**
 * JSON 日期格式转换工具类
 * 
 * @author mac
 * 
 */
public class DateJsonValueProcessor implements JsonValueProcessor {

	public static final String Default_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private DateFormat dateFormat;

	public DateJsonValueProcessor(String datePattern) {
		try {
			dateFormat = new SimpleDateFormat(datePattern);
		} catch (Exception e) {
			dateFormat = new SimpleDateFormat(Default_DATE_PATTERN);
		}
	}

	public Object processArrayValue(Object value, JsonConfig jsonConfig) {
		return process(value);
	}

	public Object processObjectValue(String key, Object value,JsonConfig jsonConfig) {
		if (value == null) {
			return "";
		}
		if (value instanceof java.sql.Timestamp) {
			String str = new SimpleDateFormat(Default_DATE_PATTERN)
					.format((java.sql.Timestamp) value);
			return str;
		}
		if (value instanceof java.util.Date) {
			String str = new SimpleDateFormat(Default_DATE_PATTERN)
					.format((java.util.Date) value);
			return str;
		}

		return value.toString();
	}

	private Object process(Object value) {
		return dateFormat.format((Date) value);

	}

}
