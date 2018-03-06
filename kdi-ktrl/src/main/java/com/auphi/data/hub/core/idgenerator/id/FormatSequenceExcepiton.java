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
package com.auphi.data.hub.core.idgenerator.id;

/**
 * FormatSequenceExcepiton
 * 此代码源于开源项目E3,原作者：黄云辉
 * 
 * @see IDException
 */
public class FormatSequenceExcepiton extends IDException {

	public FormatSequenceExcepiton() {
		super("格式化序号异常!");
	}

	public FormatSequenceExcepiton(String message, Throwable cause) {
		super(message, cause);
	}

	public FormatSequenceExcepiton(String message) {
		super(message);
	}

	public FormatSequenceExcepiton(Throwable cause) {
		super(cause);
	}

}
