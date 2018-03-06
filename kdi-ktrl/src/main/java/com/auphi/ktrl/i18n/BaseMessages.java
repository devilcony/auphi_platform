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
package com.auphi.ktrl.i18n;

import java.util.MissingResourceException;

public class BaseMessages
{
	public static String getString(String packageName, String key)
	{
		try
		{
			return GlobalMessages.getString(packageName, key);
		}
		catch (MissingResourceException e)
		{
			return GlobalMessages.getSystemString(key);
		}
	}

	public static String getString(String packageName, String key, String param1)
	{
		try
		{
			return GlobalMessages.getString(packageName, key, param1);
		}
		catch (MissingResourceException e)
		{
			return GlobalMessages.getSystemString(key, param1);
		}
	}

	public static String getString(String packageName, String key, String param1, String param2)
	{
		try
		{
			return GlobalMessages.getString(packageName, key, param1, param2);
		}
		catch (MissingResourceException e)
		{
			return GlobalMessages.getSystemString(key, param1, param2);
		}
	}

	public static String getString(String packageName, String key, String param1, String param2, String param3)
	{
		try
		{
			return GlobalMessages.getString(packageName, key, param1, param2, param3);
		}
		catch (MissingResourceException e)
		{
			return GlobalMessages.getSystemString(key, param1, param2, param3);
		}
	}

	public static String getString(String packageName, String key, String param1, String param2, String param3, String param4)
	{
		try
		{
			return GlobalMessages.getString(packageName, key, param1, param2, param3, param4);
		}
		catch (MissingResourceException e)
		{
			return GlobalMessages.getSystemString(key, param1, param2, param3, param4);
		}
	}

	public static String getString(String packageName, String key, String param1, String param2, String param3, String param4, String param5)
	{
		try
		{
			return GlobalMessages.getString(packageName, key, param1, param2, param3, param4, param5);
		}
		catch (MissingResourceException e)
		{
			return GlobalMessages.getSystemString(key, param1, param2, param3, param4, param5);
		}
	}

	public static String getString(String packageName, String key, String param1, String param2, String param3, String param4, String param5, String param6)
	{
		try
		{
			return GlobalMessages.getString(packageName, key, param1, param2, param3, param4, param5, param6);
		}
		catch (MissingResourceException e)
		{
			return GlobalMessages.getSystemString(key, param1, param2, param3, param4, param5, param6);
		}
	}

}
