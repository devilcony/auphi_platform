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
 * InitSequenceGeneratorException
 * 前缀生成器
 * 因为创建的值用于作为ID的前缀，所以取名为PrefixGenerator.
 * 对于在集群环境部署的系统，通常需要给ID设置前缀，这样就不会出现主键冲突的情况.
 * PrefixGenerator 的实现要求线程序安全的 
 * 此代码源于开源项目E3,原作者：黄云辉
 * 
 */
public interface PrefixGenerator {
  public String create() throws CreatePrefixException;
}
 