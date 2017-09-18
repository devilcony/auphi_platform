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
package com.auphi.ktrl.metadata.bean;

import java.util.List;
/**
 * 转换作业节点信息
 */
public class JobTransTreeNodeBean {
	
	private int id;  
	private int parentId;  
	private String nodePath;
	private String nodeParentPath;
	private String nodeType;
	private String nodeName;
	
	public JobTransTreeNodeBean(int id,int parentId,
						String nodePath,String nodeParentPath,
						String nodeType,String nodeName){  
	        this.id=id;  
	        this.parentId = parentId;
	        this.nodePath = nodePath;
	        this.nodeParentPath = nodeParentPath;
	        this.nodeType = nodeType;
	        this.nodeName = nodeName;
	} 
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public String getNodePath() {
		return nodePath;
	}
	public void setNodePath(String nodePath) {
		this.nodePath = nodePath;
	}
	public String getNodeParentPath() {
		return nodeParentPath;
	}
	public void setNodeParentPath(String nodeParentPath) {
		this.nodeParentPath = nodeParentPath;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	
	
}
