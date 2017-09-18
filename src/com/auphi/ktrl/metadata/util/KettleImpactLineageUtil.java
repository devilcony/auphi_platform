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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.repository.LongObjectId;
import org.pentaho.di.trans.step.StepMeta;

import com.auphi.ktrl.i18n.Messages;
import com.auphi.ktrl.metadata.bean.GraphBean;

public class KettleImpactLineageUtil extends KettleImpactLineageDataManager
{
    
    private List<GraphBean> transRelationShip;

    public KettleImpactLineageUtil(String kettleRepname, String kettleUsername,
            String kettlePassword) throws KettleException
    {
        super(kettleRepname, kettleUsername, kettlePassword);
        // TODO Auto-generated constructor stub
        transRelationShip = new ArrayList<GraphBean>();
    }
    
    /**
     * 查询获取影响分析的转换
     * 输入步骤
     * @param database
     * @param schema
     * @param table
     * @param column
     * @return
     * @throws KettleDatabaseException
     * @throws SQLException
     * @throws KettleException
     */
    public List<GraphBean> getQueryImpact(String database,String schema,String table,String column)throws KettleDatabaseException, SQLException,KettleException
    {
        Database dataDB = getDataDB();
        String impactTableName = getImpactTableName();
        String sql = "SELECT DISTINCT TRANS_ID FROM "+impactTableName;
        if(null!=schema)
            sql+=" WHERE DATABASE_NAME=\'"+database+"\' AND SCHEMA_NAME=\'"+schema+"\' AND TABLE_NAME=\'"+table
            +"\' AND COLUMN_NAME=\'"+column+"\' AND OPERATION = \'input\'";
        else
            sql+=" WHERE DATABASE_NAME=\'"+database+"\' AND TABLE_NAME=\'"+table
            +"\' AND COLUMN_NAME=\'"+column+"\' AND OPERATION = \'input\'";

        PreparedStatement preparedStatement = dataDB.getConnection().prepareStatement(
                dataDB.getDatabaseMeta().stripCR(sql),ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = preparedStatement.executeQuery();
        if(null==rs)
        {
            return null;
        }

        List<GraphBean> impactList = new ArrayList<GraphBean>();
        while(rs.next())
        {
            impactList.add(getRelationship(rs.getLong(1),true,dataDB));
        }
        rs.close();
        dataDB.getConnection().close();
        return impactList;
    }
    
    /**
     * 查询获取血统分析的转换
     * 输出步骤
     * @param database
     * @param schema
     * @param table
     * @param column
     * @return
     * @throws KettleDatabaseException
     * @throws SQLException
     * @throws KettleException
     */
    public List<GraphBean> getQueryLineage(String database,String schema,String table,String column)throws KettleDatabaseException, SQLException,KettleException
    {
        Database dataDB = getDataDB();
        String impactTableName = getImpactTableName();
        String sql = "SELECT DISTINCT TRANS_ID FROM "+impactTableName;
        if(null!=schema)
            sql+=" WHERE DATABASE_NAME=\'"+database+"\' AND SCHEMA_NAME=\'"+schema+"\' AND TABLE_NAME=\'"+table
            +"\' AND COLUMN_NAME=\'"+column+"\' AND OPERATION = \'output\'";
        else
            sql+=" WHERE DATABASE_NAME=\'"+database+"\' AND TABLE_NAME=\'"+table
            +"\' AND COLUMN_NAME=\'"+column+"\' AND OPERATION = \'output\'";

        PreparedStatement preparedStatement = dataDB.getConnection().prepareStatement(
                dataDB.getDatabaseMeta().stripCR(sql),ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = preparedStatement.executeQuery();
        if(null==rs)
        {
            return null;
        }

        List<GraphBean> lineageList = new ArrayList<GraphBean>();
        while(rs.next())
        {            
            lineageList.add(getRelationship(rs.getLong(1),true,dataDB));
        }
        rs.close();
        dataDB.getConnection().close();
        return lineageList;
    }
    
    /**
     * 查询获取转换、作业间的关系
     * @param transid
     * @param first
     * @param dataDB
     * @return
     * @throws SQLException
     * @throws KettleException
     */
    public GraphBean getRelationship(Long transid,boolean first,Database dataDB) throws SQLException, KettleException
    {
        String sql = "";
        List<GraphBean> subBean = new ArrayList<GraphBean>();
        Random random = new Random();
        if(first)
        {
            GraphBean bean = new GraphBean();
            bean.setId(Math.abs(random.nextInt())%1000);
            bean.setTitle(getRepository().loadTransformation(new LongObjectId(transid), null).getName()+Messages.getString("KettleImpact.Info.Trans"));
            List<StepMeta> stepMetas = getRepository().loadTransformation(new LongObjectId(transid), null).getSteps();
            String context = Messages.getString("KettleImpact.Info.StepsDetail")+";";
            if(null!=stepMetas)
            {
                for(int i=0 ; i < stepMetas.size() ; i++)
                    context = context + Messages.getString("KettleImpact.Info.Steps")+(i+1)+" : "+stepMetas.get(i).toString()+";";
            }
            bean.setContext(context);
            sql = "SELECT JOB_ID FROM "+getRelationshipTableName()+" WHERE TRANS_ID = "+transid;
            PreparedStatement preparedStatement = dataDB.getConnection().prepareStatement(
                dataDB.getDatabaseMeta().stripCR(sql),ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = preparedStatement.executeQuery();
            


            while (rs.next())
            {
                subBean.add(getRelationship(rs.getLong(1), false, dataDB));
            }
            GraphBean[] graphBeans = new GraphBean[subBean.size()];
            for (int i = 0; i < subBean.size(); i++)
                graphBeans[i] = subBean.get(i);
            bean.setGraphBeans(graphBeans);

            return bean;
        }
        else
        {
            GraphBean bean = new GraphBean();
            bean.setId(Math.abs(random.nextInt())%1000);
            bean.setTitle(getRepository().loadJob(new LongObjectId(transid), null).getName()+Messages.getString("KettleImpact.Info.Job"));
            List<JobEntryCopy> jobEntry = getRepository().loadJob(new LongObjectId(transid), null).getJobCopies();
            String context = Messages.getString("KettleImpact.Info.JobEntryDetail")+";";
            if(null!=jobEntry)
            {
                for(int i=0 ; i < jobEntry.size() ; i++)
                    context = context + Messages.getString("KettleImpact.Info.JobEntry")+(i+1)+" : "+jobEntry.get(i).toString()+";";
            }
            bean.setContext(context);
            sql = "SELECT JOB_ID FROM "+getRelationshipTableName()+" WHERE JOB_SUB_ID = "+transid;
            PreparedStatement preparedStatement = dataDB.getConnection().prepareStatement(
                dataDB.getDatabaseMeta().stripCR(sql),ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = preparedStatement.executeQuery();




            while (rs.next())
            {
                subBean.add(getRelationship(rs.getLong(1), false, dataDB));
            }
            GraphBean[] graphBeans = new GraphBean[subBean.size()];
            for (int i = 0; i < subBean.size(); i++)
                graphBeans[i] = subBean.get(i);
            bean.setGraphBeans(graphBeans);

            return bean;
        }
    }
    
    /**
     * 重新组合血统分析的数据
     * @param first
     * @param bean
     */
    public void transRelationship(boolean first,GraphBean bean)
    {
        if(first)
        {
            if (null == bean)
                return;
            transRelationShip.clear();
        }
        
        GraphBean [] pubGraphBean = new GraphBean[1];
        GraphBean [] subGraphBean = bean.getGraphBeans();
        pubGraphBean[0] = bean;
        bean.setGraphBeans(null);
        
        
        if(null == subGraphBean)
        {
            transRelationShip.add(bean);
            return;
        }
        
        for(int i=0 ; i < subGraphBean.length ; i++)
        {            
            transRelationship(false,subGraphBean[i]);
            subGraphBean[i].setGraphBeans(pubGraphBean);
        }        
    }
    
    /**
     * 将重新组合血统分析的数据由List转换为数组
     * @return
     */
    public GraphBean[] getTransGraphBeans()
    {
        if(null == transRelationShip)
            return null;
        
        GraphBean [] graphBeans = new GraphBean[transRelationShip.size()]; 
        for(int i=0 ; i < transRelationShip.size() ; i++)
            graphBeans[i] = transRelationShip.get(i);
        return graphBeans;
    }

}
