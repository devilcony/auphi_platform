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
package com.auphi.ktrl.metadata.util.test;

import com.auphi.ktrl.metadata.bean.GraphBean;
import com.auphi.ktrl.metadata.util.GraphUtil;

import junit.framework.TestCase;

public class GraphUtilTest extends TestCase {
	
	public void testGetGraph(){
		GraphUtil graphUtil = new GraphUtil();
		
		GraphBean tempbean = new GraphBean();
		tempbean.setId(1);
		tempbean.setTitle("转换1");
		tempbean.setContext("转换1的详细内容;项目1：***;项目2：***");
		
		GraphBean subbean1 = new GraphBean();
		subbean1.setId(2);
		subbean1.setTitle("步骤1");
		subbean1.setContext("步骤1的详细内容;项目1：***;项目2：***");
		
		GraphBean subbean2 = new GraphBean();
		subbean2.setId(3);
		subbean2.setTitle("步骤2");
		subbean2.setContext("步骤2的详细内容;项目1：***;项目2：***");
		
		GraphBean subbean23 = new GraphBean();
		subbean23.setId(4);
		subbean23.setTitle("步骤4");
		subbean23.setContext("步骤4的详细内容;项目1：***;项目2：***");
		GraphBean[] graphBeans23 = new GraphBean[1];
		graphBeans23[0] = subbean23;
		subbean2.setGraphBeans(graphBeans23);
		
		GraphBean[] graphBeans = new GraphBean[2];
		graphBeans[0] = subbean1;
		graphBeans[1] = subbean2;
		
		tempbean.setGraphBeans(graphBeans);
			
		
		graphUtil.setBean(tempbean);
		
		String reportFlow =  graphUtil.getGraph(false);
		
		System.out.println(reportFlow);
	}
}
