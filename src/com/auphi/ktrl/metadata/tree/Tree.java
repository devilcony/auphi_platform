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
package com.auphi.ktrl.metadata.tree;



import java.util.ArrayList;  
import java.util.Iterator;  
import java.util.List;  

import com.auphi.ktrl.metadata.bean.JobTransTreeNodeBean;
  
  
public class Tree {  
	public StringBuffer returnStr=new StringBuffer();
    public List nodeList =new ArrayList();  
    public Tree(List<JobTransTreeNodeBean> treeList){
    	nodeList = treeList;
    }
    
    /**
     * 递归函数
     * @param list 要递归的节点对象集合
     * @param node 要进行递归的节点
     */
    public void recursionFn(List list , JobTransTreeNodeBean node){    
        if(hasChild(list,node)){    
            returnStr.append("{text: '");  
            returnStr.append(node.getNodePath());  
            returnStr.append("',id:");  
            returnStr.append(node.getId());  
            returnStr.append(",parentId:");  
            returnStr.append(node.getParentId()); 
            returnStr.append(",expanded: true");  
            returnStr.append(",children:[");    
            List childList = getChildList(list,node);    
            Iterator it = childList.iterator();    
            while(it.hasNext()){    
            	JobTransTreeNodeBean n = (JobTransTreeNodeBean)it.next();    
                recursionFn(list,n);    
            }    
            returnStr.append("]},");    
        }else{
            returnStr.append("{ text: '");  
            returnStr.append(node.getNodeName()); 
            returnStr.append("',id:");  
            returnStr.append(node.getId());  
            returnStr.append(",parentId:");  
            returnStr.append(node.getParentId()); 
			if("".equals(node.getNodeType())){
				returnStr.append(",expandable: false"); 
				returnStr.append(",leaf:false},"); 
			}else{
            returnStr.append(",leaf:true},");
			}
        }    
            
    }    
    
    /**
     * 判断是否有孩子
     * @param list
     * @param node
     * @return
     */
    public boolean hasChild(List list, JobTransTreeNodeBean node){  //判断是否有子节点  
        return getChildList(list,node).size()>0?true:false;  
    }  
    
    /**
     * 找去node的所有子节点
     * @param list 进行遍历的节点
     * @param node 要找孩子的节点
     * @return
     */
    public List getChildList(List list , JobTransTreeNodeBean node){  //得到子节点列表  
        List li = new ArrayList();    
        Iterator it = list.iterator();    
        while(it.hasNext()){    
        	JobTransTreeNodeBean n = (JobTransTreeNodeBean)it.next();    
            if(n.getParentId()==node.getId()){    
                li.add(n);    
            }    
        }    
        return li;    
    }  
    public String modifyStr(String returnStr){//修饰一下才能满足Extjs的Json格式  
        return ("["+returnStr+"]").replaceAll(",]", "]");  
          
    }  
    public static void main(String[] args) {    
//        Tree r = new Tree();    
//        r.recursionFn(r.nodeList, new Node(1,0));    
//        System.out.println(r.modifyStr(r.returnStr.toString())); 
    }    
}  





