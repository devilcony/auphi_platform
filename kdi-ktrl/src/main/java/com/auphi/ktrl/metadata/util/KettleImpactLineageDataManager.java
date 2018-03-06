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

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import net.sf.jsqlparser.ktrl.Jsqlparser;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import org.apache.log4j.Logger;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.RowMetaAndData;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.util.Assert;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.LongObjectId;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.trans.DatabaseImpact;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.delete.DeleteMeta;
import org.pentaho.di.trans.steps.insertupdate.InsertUpdateMeta;
import org.pentaho.di.trans.steps.tableoutput.TableOutputMeta;
import org.pentaho.di.trans.steps.update.UpdateMeta;

import com.auphi.ktrl.conn.bean.ConnConfigBean;
import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.conn.util.DataBaseUtil;
import com.auphi.ktrl.i18n.Messages;
import com.auphi.ktrl.metadata.MetadataConstants;
import com.auphi.ktrl.system.repository.bean.RepositoryBean;
import com.auphi.ktrl.system.repository.util.RepositoryUtil;
import com.auphi.ktrl.util.Constants;

public class KettleImpactLineageDataManager {
    
    private static Logger logger = Logger.getLogger(KettleImpactLineageDataManager.class);
	
	//Record the impact database and the impact table
	private String impactTable;
	private String relationshipTable;
	
	//Record the repository information
	private String optionRepname, optionUsername, optionPassword;
	
	//Record the impact information
	private List<DatabaseImpact> impact;
	
	private Database repDb;
	private Database impactDb;
	private Repository rep;
	
	private boolean first;
	private int posTable,posSql,posColumn,posOriginStepName;
	
	private long columnId;
	private Connection connection;
	
	
	public KettleImpactLineageDataManager(String kettleRepname, String kettleUsername, String kettlePassword) throws KettleException
	{
		Assert.assertTrue(((kettleRepname != null && kettleRepname.length() > 0) && (kettleUsername != null && kettleUsername.length() > 0)
				&& (kettlePassword != null && kettlePassword.length() > 0)));
		
		optionRepname = kettleRepname;
		optionUsername = kettleUsername;
		optionPassword = kettlePassword;
		
		impact = new ArrayList<DatabaseImpact>();
		first = true;
		posTable = -1;
		posSql = -1;
		posColumn = -1;		
		posOriginStepName=-1;
		columnId = 1;
		
		impactTable  =  MetadataConstants.TABLE_IMPACTLINEAGE;
		relationshipTable = MetadataConstants.TABLE_RELATIONSHIP;		
	}
	
	/**
	 * Read the properteis file and set the variable
	 * @throws KettleException
	 */
	private void readProperties() throws KettleException
	{
		Properties p = new Properties();		
		try
		{
		    p.load(Constants.class.getResourceAsStream("/impactlineage.properties"));
		} 
		catch (IOException e) 
		{
			logger.error(e.getMessage(), e);
		}
		
	}
	
	/**
	 * Initialize the environment
	 * @return
	 * @throws KettleException
	 * @throws SQLException
	 */
	public boolean init() throws KettleException, SQLException
	{
		boolean success = true;		

        KettleEnvironment.init();
		
        try
        {
            getRepFromDatabase(optionRepname, optionUsername, optionPassword);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            success = false;
            logger.error(e.getMessage(), e);
            return success;
        }
		
		
		if(null==impactDb)
		{
            ConnConfigBean connConfig = DataBaseUtil.getQuartzConfig();
            DatabaseMeta databaseMeta = new DatabaseMeta(connConfig.getName(),
                connConfig.getName(), "Native", connConfig.getIp(),
                connConfig.getDatabase(), connConfig.getPort(),
                connConfig.getUsername(), connConfig.getPassword());
            impactDb = new Database(databaseMeta);
//            impactDb.connect();
//            impactDb.execStatement("selec ");
            connection = ConnectionPool.getConnection();
            impactDb.setConnection(connection);
            impactDb.connect();
		}
		else
		    if (impactDb.getConnection().isClosed())
		        impactDb.connect();

		return success;
	}
	

	/**
	 * Disconnect the database
	 * repository database and impact database
	 */
	public void dispose()
	{
		if(null!=repDb)
			repDb.disconnect();
		if(null!=impactDb)
		{
			impactDb.disconnect();
			ConnectionPool.freeConn(null, null, null, connection);
		}
		if(null!=rep)
			rep.disconnect();
	}
	
	/**
	 * Get impact data and save
	 * @throws KettleException
	 * @throws SQLException
	 */
	public void saveData() throws KettleException, SQLException {
		
		Properties p = new Properties();		
		try
		{
		    p.load(Constants.class.getResourceAsStream("/category.properties"));
		} 
		catch (IOException e) 
		{
			logger.error(e.getMessage(), e);
		}
		
		TransMeta transMeta = new TransMeta();
		RowMetaInterface insertRowMeta = impactDb.getTableFields(impactTable);

		String sql = "SELECT DISTINCT ID_TRANSFORMATION FROM R_STEP ";
		ResultSet transId = repDb.openQuery(sql);

		impactDb.truncateTable(impactTable);
		
		

		while (transId.next()) {
			long transformationId = transId.getLong("id_transformation");
			LongObjectId objTransId = new LongObjectId(transformationId);
			try {
				transMeta = rep.loadTransformation(objTransId, null);
			} catch (KettleException e) {
				logger.error(e.getMessage(), e);
				continue;
			}
			
			
			// analyseImpactProgress(transMeta);
			List<StepMeta> stepMetas = transMeta.getSteps();

			for (int s = 0; s < stepMetas.size(); s++) {
				StepMeta stepMeta = stepMetas.get(s);		
				
				if(null == p.getProperty(stepMeta.getStepID()))
				{
//					System.out.println(stepMeta.getStepID());
					continue;
				}

				String[] parts = (p.getProperty(stepMeta.getStepID()).trim()).split(":");
				String category =BaseMessages.getString(parts[0], parts[1]);
				
				boolean input = category.equalsIgnoreCase(BaseMessages.getString("org.pentaho.di.trans.step", "BaseStep.Category.Input"));
				boolean output = category.equalsIgnoreCase(BaseMessages.getString("org.pentaho.di.trans.step", "BaseStep.Category.Output"));
				
				if(!(input ||output))
					continue;
				
				String mark = "";
				if(input)
					mark="input";
				if(output)
					mark="output";

				RowMetaInterface prev = transMeta.getPrevStepFields(stepMeta);
				StepMetaInterface stepint = stepMeta.getStepMetaInterface();
				RowMetaInterface inform = null;
				StepMeta[] lu = transMeta.getInfoStep(stepMeta);
				if (lu != null) {
					inform = transMeta.getStepFields(lu);
				} else {
					inform = stepint.getTableFields();
				}

				impact.clear();
				stepint.analyseImpact(impact, transMeta, stepMeta, prev, null,null, inform);

				List<Object[]> rows = new ArrayList<Object[]>();
				RowMetaInterface rowMeta = null;

				for (int i = 0; i < impact.size(); i++) {
					DatabaseImpact ii = (DatabaseImpact) impact.get(i);
					RowMetaAndData row = ii.getRow();
					rowMeta = row.getRowMeta();
					Object[] rowData = row.getData();
					rows.add(rowData);
					if (first) {
						for (int k = 0; k < rowMeta.size(); k++) {
							if (rowMeta.getFieldNames()[k].toString().equals(BaseMessages.getString("org.pentaho.di.trans","DatabaseImpact.RowDesc.Label.Table")))
								posTable = k;

							if (rowMeta.getFieldNames()[k].toString().equals(BaseMessages.getString("org.pentaho.di.trans","DatabaseImpact.RowDesc.Label.SQL")))
								posSql = k;

							if (rowMeta.getFieldNames()[k].toString().equals(BaseMessages.getString("org.pentaho.di.trans","DatabaseImpact.RowDesc.Label.Field")))
								posColumn = k;

							if (rowMeta.getFieldNames()[k].toString().equals(BaseMessages.getString("org.pentaho.di.trans","DatabaseImpact.RowDesc.Label.ValueOrigin")))
								posOriginStepName = k;

							if (-1 != posTable && -1 != posSql&& -1 != posColumn&& -1 != posOriginStepName)
								break;
						}

						if (-1 == posTable || -1 == posSql || -1 == posColumn|| -1 == posOriginStepName) {
							throw new KettleException(Messages.getString("KettleImpact.Error.NoTableSqlDb"));
						}
						first = false;
					}

					RowMetaAndData dataMetaData = analyData(rowData, stepint,columnId, transformationId, transMeta.getName(),
							transMeta.getRepositoryDirectory().toString(),rowData[posOriginStepName].toString());

					if (null == dataMetaData)
						continue;
					
					dataMetaData.addValue("operation", ValueMetaInterface.TYPE_STRING, mark);

					
					impactDb.insertRow(null, impactTable, insertRowMeta,dataMetaData.getData());
					columnId++;
				}
			}
		}
		transId.close();
	}


	/**
	 * Analy repostitory table to get impact data
	 * 
	 * @param rowData
	 * @param stepint
	 * @param columnId
	 * @param transId
	 * @param transName
	 * @param transPath
	 * @param transOriginStep
	 * @return
	 * @throws KettleDatabaseException
	 * @throws SQLException
	 */
	private RowMetaAndData analyData(Object[] rowData,
			StepMetaInterface stepint, long columnId, long transId,
			String transName, String transPath, String transOriginStep)
			throws KettleDatabaseException, SQLException {
		boolean nullTable = false, addValue = false, addBySql = false;

		if (null == rowData[posTable])
			nullTable = true;
		else if (null != rowData[posSql]
				&& rowData[posTable].toString().isEmpty()
				&& !rowData[posSql].toString().isEmpty())
			addBySql = true;
		
		

		RowMetaAndData impactRow = new RowMetaAndData();
		impactRow.addValue("column_id", ValueMetaInterface.TYPE_INTEGER,columnId);

		DatabaseMeta[] databaseMeta = stepint.getUsedDatabaseConnections();
		Database database = null;
		PreparedStatement preparedStatement = null;
		String databaseName = "";
		try {
			for (int i = 0; i < databaseMeta.length; i++) {

				DatabaseMeta databaseMt = databaseMeta[i];
				ResultSetMetaData rsmd = null;
//				databaseName = databaseMt.getDatabaseName();
				databaseName = databaseMt.getName();

				if (nullTable)
					break;

				database = new Database(databaseMt);
				database.connect();

				String sql = "";

				if (addBySql)
					sql = rowData[posSql].toString()+"  WHERE 0=1";
				else
					sql = "SELECT * FROM " + rowData[posTable]+"  WHERE 0=1";
				preparedStatement = database.getConnection().prepareStatement(databaseMt.stripCR(sql), ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);

				if (!databaseMt.supportsPreparedStatementMetadataRetrieval())
					preparedStatement.execute();
				rsmd = preparedStatement.getMetaData();

				int nrcols = rsmd.getColumnCount();
				for (int k = 1; k <= nrcols; k++) {
					String name;
					if (databaseMt.isMySQLVariant()&& database.getDatabaseMetaData().getDriverMajorVersion() > 3) {
						name = new String(rsmd.getColumnLabel(k));
					} else {
						name = new String(rsmd.getColumnName(k));
					}
					if (name.equalsIgnoreCase(rowData[posColumn].toString())) {
						impactRow.addValue("database_name",ValueMetaInterface.TYPE_STRING,databaseName);
						String schemaname = rsmd.getSchemaName(k);
						String tablename = rsmd.getTableName(k);
						
						if(addBySql)
						{
						    if(!tablename.isEmpty())
						        ;
						    else
						    {
						        CCJSqlParserManager parserManager = new CCJSqlParserManager();
						        String b=Jsqlparser.paserFunction(sql);
						        //System.out.println("s="+b);
						        PlainSelect plainSelect = (PlainSelect) ((Select) parserManager.parse(new StringReader(b))).getSelectBody();
						        List ls=Jsqlparser.paserSql(plainSelect);
						        String[] parts;
						        for (int j= 0; j < ls.size(); j++) {
						            parts = (ls.get(j).toString().replace("\"","")).split("\\.");
						            if(3==parts.length && parts[2].equalsIgnoreCase(rowData[posColumn].toString()))
						            {
						                schemaname = schemaname.isEmpty()?parts[0]:schemaname;
						                tablename = parts[1];
						                break;
						            }
						        }
						    }
						}
						else
						    tablename = (String) rowData[posTable];
						
						if(stepint instanceof TableOutputMeta)
						    schemaname = ((TableOutputMeta)stepint).getSchemaName();
						if(stepint instanceof InsertUpdateMeta)
                            schemaname = ((InsertUpdateMeta)stepint).getSchemaName();
						if(stepint instanceof UpdateMeta)
                            schemaname = ((UpdateMeta)stepint).getSchemaName();
						if(stepint instanceof DeleteMeta)
                            schemaname = ((DeleteMeta)stepint).getSchemaName();
						
						impactRow.addValue("schema_name", ValueMetaInterface.TYPE_STRING,schemaname);
						impactRow.addValue("table_name",ValueMetaInterface.TYPE_STRING,tablename);
						
						impactRow.addValue("column_name",ValueMetaInterface.TYPE_STRING,rsmd.getColumnName(k));
						impactRow.addValue("column_label",ValueMetaInterface.TYPE_STRING,rsmd.getColumnLabel(k));
						impactRow.addValue("column_type",ValueMetaInterface.TYPE_INTEGER,(long) rsmd.getColumnType(k));
						impactRow.addValue("column_precision",ValueMetaInterface.TYPE_INTEGER,(long) rsmd.getPrecision(k));
						impactRow.addValue("column_scale",ValueMetaInterface.TYPE_INTEGER,(long) rsmd.getScale(k));
						impactRow.addValue("column_type_name",ValueMetaInterface.TYPE_STRING,rsmd.getColumnTypeName(k));
						impactRow.addValue("column_length",ValueMetaInterface.TYPE_INTEGER,(long) rsmd.getColumnDisplaySize(k));
						impactRow.addValue("repository_name",ValueMetaInterface.TYPE_STRING, optionRepname);
						impactRow.addValue("trans_path",ValueMetaInterface.TYPE_STRING, transPath);
						impactRow.addValue("trans_id",ValueMetaInterface.TYPE_INTEGER,(long) transId);
						impactRow.addValue("trans_name",ValueMetaInterface.TYPE_STRING, transName);
						impactRow.addValue("origin_step_name",ValueMetaInterface.TYPE_STRING,transOriginStep);
						addValue = true;
						break;
					}
				}
			}
			
			//Truncate 
			if ((!addValue && !nullTable) || nullTable) {
				impactRow.addValue("database_name",ValueMetaInterface.TYPE_STRING, databaseName);
				impactRow.addValue("schema_name",ValueMetaInterface.TYPE_STRING, "");
				impactRow.addValue("table_name",ValueMetaInterface.TYPE_STRING, rowData[posTable]);
				impactRow.addValue("column_name",ValueMetaInterface.TYPE_STRING,rowData[posColumn].toString());
				impactRow.addValue("column_label",ValueMetaInterface.TYPE_STRING,rowData[posColumn].toString());
				impactRow.addValue("column_type",ValueMetaInterface.TYPE_INTEGER, (long) -1);
				impactRow.addValue("column_precision",ValueMetaInterface.TYPE_INTEGER, (long) -1);
				impactRow.addValue("column_scale",ValueMetaInterface.TYPE_INTEGER, (long) -1);
				impactRow.addValue("column_type_name",ValueMetaInterface.TYPE_STRING, "");
				impactRow.addValue("column_length",ValueMetaInterface.TYPE_INTEGER, (long) -1);
				impactRow.addValue("repository_name",ValueMetaInterface.TYPE_STRING, optionRepname);
				impactRow.addValue("trans_path",ValueMetaInterface.TYPE_STRING, transPath);
				impactRow.addValue("trans_id", ValueMetaInterface.TYPE_INTEGER,(long) transId);
				impactRow.addValue("trans_name",ValueMetaInterface.TYPE_STRING, transName);
				impactRow.addValue("origin_step_name",ValueMetaInterface.TYPE_STRING, transOriginStep);
			}
			return impactRow;
		} 
		catch(Exception e)
		{
			return null;
		}
		finally {
			if (null != database)
				database.disconnect();
			if (null != preparedStatement)
				preparedStatement.close();
		}
	}
	
	public Database getRepDB()
	{
		return this.repDb;
	}
	
	public Database getDataDB()
	{
		return this.impactDb;
	}
	
	public String getImpactTableName()
	{
		return this.impactTable;
	}
	
	public String getRelationshipTableName()
	{
	    return this.relationshipTable;
	}
	
	public Repository getRepository()
	{
	    return this.rep;
	}
	
	public boolean saveRelationship() throws KettleException, SQLException
	{
		boolean result = true;
		
		String sql = "SELECT RESULT.*,R_JOBENTRY_ATTRIBUTE.VALUE_STR FROM \n" +
				" ( \n" +
				" SELECT  \n" +
				" A.ID_JOB,A.ID_JOBENTRY,A.CODE,B.CODE AS TYPE \n" +
				" FROM \n" +
				" R_JOBENTRY_ATTRIBUTE A, \n" +
				" (\n" +
				" SELECT\n" +
				" A.ID_JOBENTRY_TYPE,A.CODE,B.ID_JOBENTRY \n" +
				" FROM R_JOBENTRY B,R_JOBENTRY_TYPE A\n" +
				" WHERE A.ID_JOBENTRY_TYPE=B.ID_JOBENTRY_TYPE\n" +
				" )  B \n" +
				" WHERE \n" +
				"  (A.CODE=\'JOB_OBJECT_ID\' OR A.CODE=\'TRANS_OBJECT_ID\' OR A.CODE=\'NAME\' OR A.CODE=\'DIR_PATH\') AND A.ID_JOBENTRY=B.ID_JOBENTRY \n" +
				" GROUP BY A.ID_JOB,A.ID_JOBENTRY,A.CODE,B.CODE\n" +
				" ) \n" +
				" RESULT,R_JOBENTRY_ATTRIBUTE\n" +
				" WHERE (TYPE = \'TRANS\' OR TYPE = \'JOB\') \n" +
				" AND VALUE_STR IS NOT NULL AND RESULT.ID_JOB = R_JOBENTRY_ATTRIBUTE.ID_JOB\n" +
				" AND RESULT.ID_JOBENTRY = R_JOBENTRY_ATTRIBUTE.ID_JOBENTRY\n"+
				" AND RESULT.CODE = R_JOBENTRY_ATTRIBUTE.CODE\n"+
				" ORDER BY RESULT.ID_JOB,RESULT.ID_JOBENTRY,RESULT.CODE";
		ResultSet transjobId = repDb.openQuery(sql);
		RowMetaInterface insertRowMeta = impactDb.getTableFields(relationshipTable);
		RowMetaAndData insertRow = new RowMetaAndData();
		RowMetaAndData insertRowCP = null;
		RepositoryDirectoryInterface repositoryDirectory;
		TransMeta transMeta = new TransMeta();
		JobMeta jobMeta = new JobMeta();
		int job_id,job_sub_id=-1,trans_id=-1;
		String code,value,type,dir_path,name = null;
		impactDb.truncateTable(relationshipTable);
		while(transjobId.next())
		{
			
			insertRow.clear();
			
			job_id=job_sub_id=trans_id=-1;
			code=value=type=dir_path=name = null;
			
			job_id = transjobId.getInt("ID_JOB");
			code=transjobId.getString("CODE");
			value=transjobId.getString("VALUE_STR");
			type=transjobId.getString("TYPE");
			
			if(code.equalsIgnoreCase("trans_object_id"))
				trans_id=Integer.parseInt(value);
			else if(code.equalsIgnoreCase("job_object_id"))
				job_sub_id=Integer.parseInt(value);
			else if(type.equalsIgnoreCase("trans"))
			{
				dir_path = transjobId.getString("VALUE_STR");
				if(transjobId.next())
					name=transjobId.getString("VALUE_STR");
				repositoryDirectory = rep.findDirectory(dir_path);
				try
				{
					transMeta = rep.loadTransformation(name,	repositoryDirectory, null, true, null);
					trans_id = Integer.parseInt(transMeta.getObjectId().getId());
				}
				catch(Exception e)
				{
					continue;
				}
			}
			else
			{
				dir_path = transjobId.getString("VALUE_STR");
				if(transjobId.next())
					name=transjobId.getString("VALUE_STR");
				repositoryDirectory = rep.findDirectory(dir_path);
				try
				{
					jobMeta = rep.loadJob(name, repositoryDirectory, null, null);
					job_sub_id = Integer.parseInt(jobMeta.getObjectId().getId());
				}
				catch(Exception e)
				{
					continue;
				}
			}
			
			
			
			insertRow.addValue("job_id", ValueMetaInterface.TYPE_INTEGER,(long) job_id);
			insertRow.addValue("job_sub_id", ValueMetaInterface.TYPE_INTEGER,(long) job_sub_id);
			insertRow.addValue("trans_id", ValueMetaInterface.TYPE_INTEGER, (long)trans_id);		
			
			//To avoid the repeated transformations or jobs
			if(null!=insertRowCP)
			{
				if(insertRow.equals(insertRowCP))
					continue;
			}
			impactDb.insertRow(null, relationshipTable, insertRowMeta,insertRow.getData());
			
			//To record last insert data
			insertRowCP =  insertRow.clone();
		}		
		return result;
	}
	
	/**
     * Get repositories' meta data from database.
     * */
    private static RepositoriesMeta getRepsMetaFromDatabase(){
        RepositoriesMeta repsMeta = new RepositoriesMeta(); 
        
       try
        {
           List<RepositoryBean> reps = RepositoryUtil.getAllRepositories() ;
            for (int i = 0 ; i < reps.size() ; i ++)
            {
                RepositoryBean rep = reps.get(i) ;
                DatabaseMeta databaseMeta = new DatabaseMeta(rep.getRepositoryName(),rep.getDbType(),
                    rep.getDbAccess(),rep.getDbHost(),rep.getDbName(),rep.getDbPort(),rep.getUserName(),rep.getPassword()) ;
                KettleDatabaseRepositoryMeta repositoryMeta = new KettleDatabaseRepositoryMeta(String.valueOf(rep.getRepositoryID()),rep.getRepositoryName(),
                        rep.getDbType(),databaseMeta) ;
                repsMeta.addDatabase(databaseMeta);
                repsMeta.addRepository(repositoryMeta);
            }
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(),e);
        }
        return repsMeta;
    }
    
    /**
     * get connected repository
     * @param repName
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    private boolean getRepFromDatabase(String repName, String username, String password) throws Exception{
        
        boolean success = true;
        
        RepositoriesMeta repsMeta = getRepsMetaFromDatabase();        
        RepositoryMeta repMeta = repsMeta.findRepository(repName);        
        rep =  PluginRegistry.getInstance().loadClass(RepositoryPluginType.class, repMeta, Repository.class);      
        rep.init(repMeta);
        rep.connect(username, password);     
        

//        repDb = ((KettleDatabaseRepository)rep).getDatabase();

        
        //Get kettle repository database info
        //Because only one query can be used at the same time in the same connection with cursor emulation
        KettleDatabaseRepository kettleRep = new KettleDatabaseRepository();
        kettleRep.init(repMeta);
        repDb = kettleRep.getDatabase();
        repDb.connect();
        
        
        
        return success;
    }

}
