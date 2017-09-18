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
package com.auphi.ktrl.metadata.util;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.pentaho.di.core.exception.KettleException;

import com.auphi.ktrl.i18n.Messages;
import com.auphi.ktrl.metadata.bean.GraphBean;
import com.auphi.ktrl.metadata.bean.MetaDataConnBean;
import com.auphi.ktrl.util.Constants;

public class GraphUtil {
	private static Logger logger = Logger.getLogger(GraphUtil.class);
	
	private long nowTime;

	public GraphBean bean;

	public List<GraphBean> list = new ArrayList<GraphBean>();

	public GraphBean getBean() {
		return bean;
	}

	public void setBean(GraphBean bean) {
		this.bean = bean;
	}

	public String getGraph(boolean isfromLefttoRight){
		nowTime = System.currentTimeMillis();
		StringBuffer sb = new StringBuffer();
		sb.append("<div class=\"canvas\" id=\"mainCanvas"+"_"+nowTime+"\" " +
				"style=\"width: 900px; height: 400px; border: 0px solid black;\">");
      
		sb.append(createElement(bean,10,80));
		sb.append(setConnector(bean,isfromLefttoRight));
		
		sb.append("</div>");
		return sb.toString();
	}
	
	public String createElement(GraphBean bean,int left,int top){
		StringBuffer sb = new StringBuffer();
		if(!list.contains(bean)){
		sb.append("<h3 class=\"block draggable\" id=\"block_"+bean.getId()+"_"+nowTime+"\" ");
		sb.append("style=\"left: "+left+"px; top:"+top+"px;\">");
		
		sb.append("<table width=\"100%\">");
		sb.append("<tbody>");
		sb.append("<tr bar=\"yes\"><td><a href=\"#\" onclick=\"openClose(this);initPageObjects();\">+</a>"+bean.getTitle()+"</td></tr>");
		
		if(bean.getContext() != null)
		{
			String[] temStr = bean.getContext().split(";");
			for(int i=0;i<temStr.length;i++){
				sb.append("<tr style=\"display:none\"><td>"+temStr[i]+"</td></tr>");
			}
		}
		
		sb.append("</tbody>");
		sb.append("</table>");
		
		sb.append("</h3>");
		list.add(bean);
		}
		
		if(bean.getGraphBeans() != null && bean.getGraphBeans().length != 0){
			for(int i=0;i<bean.getGraphBeans().length;i++){
				sb.append(createElement(bean.getGraphBeans()[i], left+200,i*50));
			}
		}
		return sb.toString();
	}
	public boolean isCreate(GraphBean tbean){
		boolean isCreate = false;
		
		return isCreate;
	}
	public String setConnector(GraphBean bean,boolean isfromLefttoRight){
		StringBuffer sb = new StringBuffer();
		if(null!=bean.getGraphBeans() && bean.getGraphBeans().length != 0){
			for(int i=0;i<bean.getGraphBeans().length;i++){
				sb.append("<div class=\"connector block_"+bean.getId()+"_"+nowTime+" block_"+bean.getGraphBeans()[i].getId()+"_"+nowTime+" \">");
				if(isfromLefttoRight){
					sb.append("<img src=\"common/js-graph-it/arrow.gif\" class=\"connector-end\"> ");
				}else{
					sb.append("<img src=\"common/js-graph-it/arrow.gif\" class=\"connector-start\"> ");
				}
				sb.append("</div>");
				
				if(bean.getGraphBeans()[i].getGraphBeans() != null && bean.getGraphBeans()[i].getGraphBeans().length != 0){
					sb.append(setConnector(bean.getGraphBeans()[i],isfromLefttoRight));
				}
			}
		}

	    return sb.toString();
	}
	
	public GraphBean getGraphBean4Influence(boolean syn,MetaDataConnBean beanInfo,String datasource,String schemas,String tables,String fields){
        
	    GraphBean bean = new GraphBean();
        Random random = new Random();
        KettleImpactLineageUtil impact = null;
        try
        {
            impact = new KettleImpactLineageUtil(beanInfo.getName(), Constants.get("LoginUser"), Constants.get("LoginPassword"));
            impact.init();
            
            if(syn)
            {
                impact.saveData();
                impact.saveRelationship();
            }
            
            bean.setId(Math.abs(random.nextInt()) % 1000);
            bean.setTitle(Messages.getString("KettleImpact.Impact.Info"));
            bean.setContext(Messages.getString("KettleImpact.Impact.Database")+datasource+";"+
                Messages.getString("KettleImpact.Impact.Schema")+schemas+";"+Messages.getString("KettleImpact.Impact.Table")+tables+";"+
                Messages.getString("KettleImpact.Impact.Column")+fields);
            
            List<GraphBean> result = impact.getQueryImpact(datasource, schemas.equalsIgnoreCase(Messages.getString("Metadata.Message.DefaultName"))
                ?null:schemas,
                tables, fields);
            if (null != result && !result.isEmpty())
            {
                GraphBean[] graphBeans = new GraphBean[result.size()];
                for (int i = 0; i < result.size(); i++)
                    graphBeans[i] = result.get(i);
                bean.setGraphBeans(graphBeans);
            }
            return bean;
        }
        catch (KettleException e)
        {
            // TODO Auto-generated catch block
        	logger.error(e.getMessage(), e);
            return bean;
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
        	logger.error(e.getMessage(), e);
            return bean;
        }
        finally
        {
            if(null!=impact)
                impact.dispose();
        }
	}
	
	public GraphBean getGraphBean4Descent(boolean syn,MetaDataConnBean beanInfo,String datasource,String schemas,String tables,String fields){	    
        GraphBean bean = new GraphBean();
        Random random = new Random();
        KettleImpactLineageUtil lineage = null;
        try
        {
            lineage = new KettleImpactLineageUtil(beanInfo.getName(), "admin", "admin");
            lineage.init();
            
            if(syn)
            {
                lineage.saveData();
                lineage.saveRelationship();
            }
            
            bean.setId(Math.abs(random.nextInt()) % 1000);
            bean.setTitle(Messages.getString("KettleImpact.Impact.Info"));
            bean.setContext(Messages.getString("KettleImpact.Impact.Database")+datasource+";"+
                Messages.getString("KettleImpact.Impact.Schema")+schemas+";"+Messages.getString("KettleImpact.Impact.Table")+tables+";"+
                Messages.getString("KettleImpact.Impact.Column")+fields);
            List<GraphBean> result = lineage.getQueryLineage(datasource, schemas.equalsIgnoreCase(Messages.getString("Metadata.Message.DefaultName"))
                ?null:schemas,
                tables, fields);
            
            if (null != result && !result.isEmpty())
            {
                GraphBean[] graphBeans = new GraphBean[result.size()];
                for (int i = 0; i < result.size(); i++)
                    graphBeans[i] = result.get(i);
                bean.setGraphBeans(graphBeans);
            }

            return bean;
        }
        catch (KettleException e)
        {
            // TODO Auto-generated catch block
        	logger.error(e.getMessage(), e);
            return bean;
        }
        catch (SQLException e)
        {
            // TODO Auto-generated catch block
        	logger.error(e.getMessage(), e);
            return bean;
        }
        finally
        {
            lineage.dispose();
        }
	}	
}
