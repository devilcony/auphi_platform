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
package com.auphi.ktrl.schedule.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.auphi.ktrl.conn.util.ConnectionPool;
import com.auphi.ktrl.schedule.template.TemplateUtil;
import com.auphi.ktrl.schedule.tools.ComparatorSchema;
import com.auphi.ktrl.schedule.tools.RemoteMarketToosl;
import com.auphi.ktrl.schedule.view.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.Map.Entry;

public class MarketUtil {
  
@SuppressWarnings("unchecked")
public static String getSchemaNames(String schemaNamejson)
   {
	   JSONArray jsonArray= JSONArray.parseArray(schemaNamejson);
	   List<SchemaNameView> schemaNameViews= new ArrayList<SchemaNameView>();
	   List<String> lists= new ArrayList<String>();
	   if(jsonArray!=null){
		   for (int i = 0; i < jsonArray.size(); i++) {
			   RemoteMarketView remoteMarketView= JSON.parseObject(jsonArray.get(i).toString(), RemoteMarketView.class);	
		       lists.add(remoteMarketView.getSchemaName());
		   }
	   }
	   
   //去重复
	   HashSet<String> hs = new HashSet<String>(lists);
	   ComparatorSchema comparatorSchema= new ComparatorSchema();
	   for (String s : hs) {
		SchemaNameView schemaNameView= new SchemaNameView();
		schemaNameView.setName(s);
		schemaNameViews.add(schemaNameView);
	}
	   //排序
	   Collections.sort(schemaNameViews,comparatorSchema);
	   return JSON.toJSONString(schemaNameViews);
	   
   }
   
   public static String getSchemaNames(HttpServletRequest request)
   {
	  // String json =RemoteMarketToosl.remoteMarketJson("/datahub/rest/oracle/exportTable", null);	
	   String json=null;
	   String  schemaNames=null;
	   HttpSession session = request.getSession();
	   Object sessionJson=session.getAttribute("schemaNames");
	   if(sessionJson!=null)
	   {
		   schemaNames=(String) sessionJson;
		  
	   }else {
//			  json="[{\"schemaName\":\"sys\",\"tableName\":\"t_user\",\"type\":\"string\"},"
//				   		+ "{\"schemaName\":\"oracle\",\"tableName\":\"t_student\",\"type\":\"ingeger\"}]";	
//	       
		       json=RemoteMarketToosl.remoteMarketJson("/datahub/rest/oracle/exportTable", null);	
		       schemaNames=MarketUtil.getSchemaNames(json); 
		       session.setAttribute("schemaNames",schemaNames);
		       session.setAttribute("marketJson", json);
	}
	   return schemaNames;
   }
   
	public static String getSchemaTables(HttpServletRequest request,String schema) {
		// TODO Auto-generated method stub
		   String json=null;
		   String  schemaTables=null;
		   HttpSession session = request.getSession();
		   Object sessionJson=session.getAttribute("marketJson");
		   List<RemoteMarketView> remoteMarketViews=new ArrayList<RemoteMarketView>();
		   if(sessionJson!=null)
		   {
			   schemaTables=(String) sessionJson;
			   JSONArray jsonArray= JSONArray.parseArray(schemaTables);
               for (int i = 0; i < jsonArray.size(); i++) {
				   RemoteMarketView remoteMarketView= JSON.parseObject(jsonArray.get(i).toString(), RemoteMarketView.class);
				   if(remoteMarketView.getSchemaName().equals(schema))
				   {
					  remoteMarketViews.add(remoteMarketView);					   
				   }

			   }
			   		  
		   }else {	       
			       json=RemoteMarketToosl.remoteMarketJson("/datahub/rest/oracle/exportTable", null);	
				   JSONArray jsonArray= JSONArray.parseArray(json);
                   for (int i = 0; i < jsonArray.size(); i++) {
					   RemoteMarketView remoteMarketView= JSON.parseObject(jsonArray.get(i).toString(), RemoteMarketView.class);
					   if(remoteMarketView.getSchemaName().equals(schema))
					   {
						  remoteMarketViews.add(remoteMarketView);					   
					   }
				   }
	  		       session.setAttribute("marketJson", json);
		}
		//   String text=JSON.toJSONString(remoteMarketViews);
		return JSON.toJSONString(remoteMarketViews);
	}

	/**
	 * 根据数据库链接ID获取Database对象
	 * @param id
	 * @return
     */
	public static Database getDatabase(Integer id)
	{
		//List<FastConfigDatabaseView> fastConfigDatabaseViews= new ArrayList<FastConfigDatabaseView>();	
   	FastConfigDatabaseView fastConfigDatabaseView =new FastConfigDatabaseView();
   	String typeName=null;
		Database database= null;
		Connection connection=null;
		PreparedStatement smt=null;
		ResultSet rs= null;
       connection = ConnectionPool.getConnection();
		List<Map<String,String>> attributes = new ArrayList<>();
       try {

		   String sql = "SELECT *FROM R_DATABASE D JOIN R_DATABASE_TYPE T ON D.ID_DATABASE_TYPE=T.ID_DATABASE_TYPE  WHERE ID_DATABASE =?";

			smt=  connection.prepareStatement(sql);
		   	smt.setInt(1, id);
	        rs=smt.executeQuery();




	        while (rs.next()) 
	        {
				fastConfigDatabaseView.setIdDatase(rs.getInt("ID_DATABASE"));
				fastConfigDatabaseView.setName(rs.getString("NAME"));
				fastConfigDatabaseView.setIdDatabaseType(rs.getInt("ID_DATABASE_TYPE"));
				fastConfigDatabaseView.setIdDatabaseContype(rs.getInt("ID_DATABASE_CONTYPE"));
				typeName=rs.getString("DESCRIPTION");
				fastConfigDatabaseView.setHostName(rs.getString("HOST_NAME"));
				fastConfigDatabaseView.setDatabaseName(rs.getString("DATABASE_NAME"));
				fastConfigDatabaseView.setPort(rs.getInt("PORT"));
				fastConfigDatabaseView.setUserName(rs.getString("USERNAME"));
				fastConfigDatabaseView.setPassword(rs.getString("PASSWORD"));
				fastConfigDatabaseView.setServerName(rs.getString("SERVERNAME"));
				fastConfigDatabaseView.setDataTbs(rs.getString("DATA_TBS"));
				fastConfigDatabaseView.setIndexTbs(rs.getString("INDEX_TBS"));

	        }

		   /**
			* 查询选项信息
			*/
		   sql = "SELECT * FROM R_DATABASE_ATTRIBUTE  WHERE ID_DATABASE =?";
		   smt=  connection.prepareStatement(sql);
		   smt.setInt(1, id);
		   rs=smt.executeQuery();
		   while (rs.next()) {
			   Map<String,String> map = new HashMap<>();
			   map.put(KettleDatabaseRepository.FIELD_DATABASE_ATTRIBUTE_CODE,rs.getString(KettleDatabaseRepository.FIELD_DATABASE_ATTRIBUTE_CODE));
			   map.put(KettleDatabaseRepository.FIELD_DATABASE_ATTRIBUTE_VALUE_STR,rs.getString(KettleDatabaseRepository.FIELD_DATABASE_ATTRIBUTE_VALUE_STR));
			   attributes.add(map);
		   }

       } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     
      // Encr.decryptPasswordOptionallyEncrypted  	           
       DatabaseMeta databaseMeta = new DatabaseMeta(fastConfigDatabaseView.getName(),
       		typeName, "Native", fastConfigDatabaseView.getHostName(),
       		fastConfigDatabaseView.getDatabaseName(), fastConfigDatabaseView.getPort().toString(),
       		fastConfigDatabaseView.getUserName(), Encr.decryptPasswordOptionallyEncrypted(fastConfigDatabaseView.getPassword()));





		for(Map<String,String> map:attributes){
			databaseMeta.getAttributes().put(map.get(KettleDatabaseRepository.FIELD_DATABASE_ATTRIBUTE_CODE), Const.NVL(map.get(KettleDatabaseRepository.FIELD_DATABASE_ATTRIBUTE_VALUE_STR), ""));
		}

	    database = new Database(databaseMeta);
	    ConnectionPool.freeConn(rs, smt, null, connection);
		return  database;
	}


	public static TreeMap<Integer, FieldMappingView> getDestHashMap(String json)
	{
		TreeMap<Integer, FieldMappingView> destHashMap=new TreeMap<Integer, FieldMappingView>();
    	JSONArray jsonArray= JSONArray.parseArray(json);
    	
    	if (!jsonArray.isEmpty()&&jsonArray.size()>0) {
    		JSONObject json1=null;
    		if(jsonArray.get(1)!=null)
    		{
    		    json1=JSONObject.parseObject(jsonArray.get(1).toString());		      
    			if(json1.getString("structure").length()>2)
    				{
    				JSONArray ja=JSONArray.parseArray(json1.get("structure").toString());
    					for (int i=0;i<ja.size();i++) {
        		          	FieldMappingView dest=new FieldMappingView();
        		          	JSONObject jb= JSONObject.parseObject(ja.get(i).toString());	          	
        		           	 //得到源字段名称
        	               dest.setDestColumuName(jb.getString("colName"));                           
        	                 //得到源字段类型
        	               String typename=getColumuType(jb.getString("typeID"));
        	               dest.setDestColumnType(typename);
        	               //得到所有类型
        	               dest.setReference("");
        	              // dest.setDestColumuTypeViews(typeList);			                             
        	               dest.setSourceColumnName("");
        	               dest.setSourceColumnType("");
        	               dest.setStartIndex(0);
        	               dest.setEndIndex(0);
        	               dest.setReference("");
        	               dest.setIsPrimary(false);
        	               dest.setIsNullable(false);
        	               dest.setDestLength("");
        	               dest.setDestScale("-1");
        	               destHashMap.put(i, dest);
    				}}
    		      
    		}

    	}
    	
    	return destHashMap;		
	}
	//目标数据源类型
	public static TreeMap<Integer, FieldMappingView> getDestHashMap(FastConfigView fastConfigView,Database database) {
		TreeMap<Integer, FieldMappingView> destHashMap=new TreeMap<Integer, FieldMappingView>();
		 database=MarketUtil.getDatabase(fastConfigView.getIdDestDatabase());	
		 try {
			 database.connect();
			 if (database.checkTableExists(fastConfigView.getDestTableName()))
			 {
			 RowMetaInterface metaDest=database.getTableFields(fastConfigView.getDestTableName());
	           for (int i=0;i<metaDest.size();i++) {
	           	FieldMappingView dest=new FieldMappingView();
	           	 //得到源字段名称
	                dest.setDestColumuName(metaDest.getValueMeta(i).getName());                           
	                 //得到源字段类型
	                dest.setDestColumnType(metaDest.getValueMeta(i).getTypeDesc());
	               //得到所有类型
	              // dest.setDestColumuTypeViews(typeList);			                             
	               dest.setSourceColumnName("");
	               dest.setSourceColumnType("");
	               dest.setStartIndex(0);
	               dest.setEndIndex(0);
	               dest.setReference("");
	               dest.setIsPrimary(false);
	               dest.setIsNullable(false);
	               dest.setDestLength("");
	               dest.setDestScale("-1");
	              // dest.setDestColumuTypeViews(null);
	               destHashMap.put(i, dest);
	           }
	           }
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		   database.disconnect();
           return destHashMap;
	}
           
   public static HashMapAndArray getSourceHashMap(FastConfigView fastConfigView,Database database,ResultSet rs)
   {
	   HashMapAndArray hashMapAndArray= new HashMapAndArray();
	   TreeMap<Integer, FieldMappingView> sourceHashMap=new TreeMap<Integer, FieldMappingView>();
       database= MarketUtil.getDatabase(fastConfigView.getIdSourceDatabase());       
       try {
   		database.connect();
   	    
   		RowMetaInterface meta =null;
   		String dbType = database.getDatabaseMeta().getDatabaseTypeDesc();
   	    String sourceTableNameWithVariable=fastConfigView.getSourceTableName();
   	    
   	    Date date =new Date();
		long time = System.currentTimeMillis()-24*60*60*1000;//yesterday
		date.setTime(time);
		String sourceTableName = TemplateUtil.replaceVariable(sourceTableNameWithVariable,date,false);
		
   		if(dbType.equalsIgnoreCase("Hadoop Hive 2") || dbType.equalsIgnoreCase("Hadoop Hive") || dbType.equalsIgnoreCase("HIVE2") || dbType.equalsIgnoreCase("HIVE"))
		{	
   			database.execStatement("use "+database.getDatabaseMeta().getDatabaseName());
//   			try{
//   				meta = database.getTableFields(sourceTableName);
//   	   			}
//   			catch
//   			(Exception ex)
//   			{
//   				ex.printStackTrace();
//   			}
		}
   		
   		try{
   			meta = database.getTableFields(sourceTableName);
   		}catch(Exception e)
   		{
   			e.printStackTrace();
   		}finally
   		{
   			System.out.println("meta finished!");
   		}
   		
   		
   		for (int i=0;i<meta.size();i++) {
          	FieldMappingView source=new FieldMappingView();
          	String fieldName=meta.getValueMeta(i).getName();
          	 //得到源字段名称
               source.setSourceColumnName(fieldName); 
              // System.out.println("SELECT "+fieldName+" FROM "+fastConfigView.getSourceTableName());
            if(dbType.equalsIgnoreCase("Hadoop Hive 2") || dbType.equalsIgnoreCase("Hadoop Hive") || dbType.equalsIgnoreCase("HIVE2") || dbType.equalsIgnoreCase("HIVE"))
       		{
            	   source.setReference("");
            	   hashMapAndArray.setPrimarys(null);
       		}
            else
       		{
               database.setQueryLimit(1);
               rs=  database.openQuery("SELECT "+fieldName+" FROM "+sourceTableName);
   				while(rs.next())
   				  {
   					  //得到字段里面的内容
   				      source.setReference(rs.getString(fieldName));	
   				  }
               //找到主键,全局变量
   			   hashMapAndArray.setPrimarys( database.getPrimaryKeyColumnNames(sourceTableName));
                //得到源字段类型
       		}
               source.setSourceColumnType(meta.getValueMeta(i).getTypeDesc());
              //得到源字段类型长度
              Integer length=meta.getValueMeta(i).getLength();
              database.getPrimaryKeyColumnNames(sourceTableName);
              source.setDestColumuName("");
              source.setDestColumnType("");
            //  source.setDestColumuTypeViews(typeList);
              source.setDestLength(length.toString());
              Integer scale=meta.getValueMeta(i).getPrecision();
              source.setDestScale(scale.toString());
              source.setStartIndex(0);
              source.setEndIndex(0);
              source.setIsPrimary(false);
              source.setIsNullable(false);                    	
   		   	  sourceHashMap.put(i, source);
   		}
	} catch (Exception e) {
		e.printStackTrace();
	}finally{
		database.disconnect();
	}
	   hashMapAndArray.setHashMap(sourceHashMap);
	   return hashMapAndArray;
   }
   
   public static List<FieldMappingView> getSourceORdest(TreeMap<Integer, FieldMappingView> destHashMap,TreeMap<Integer, FieldMappingView> sourceHashMap)
   
   {
	   List<FieldMappingView> sourceORdest= new ArrayList<FieldMappingView>();

	   if(sourceHashMap.size()>=destHashMap.size())
       {
       	Iterator<Entry<Integer, FieldMappingView>> sourceKeyIterator = sourceHashMap.entrySet().iterator();

       	while (sourceKeyIterator.hasNext()) {
           	Entry<Integer, FieldMappingView> s = sourceKeyIterator.next();
           	FieldMappingView fieldMappingView= new FieldMappingView();
           	System.out.println("s.getKey()="+s.getKey());
           	fieldMappingView=s.getValue();
           	Iterator<Entry<Integer, FieldMappingView>> destKeyIterator = destHashMap.entrySet().iterator();
           	while(destKeyIterator.hasNext())
           	{
               	Entry<Integer, FieldMappingView> d = destKeyIterator.next();
               	if(s.getValue().getSourceColumnName().equalsIgnoreCase(d.getValue().getDestColumuName()))
               	{
               	   fieldMappingView.setDestColumuName(d.getValue().getDestColumuName());
               	   fieldMappingView.setDestColumnType(d.getValue().getDestColumnType());
               	   break;
               	}
           	}
           	sourceORdest.add(fieldMappingView);
			}
       }else if(sourceHashMap.size()<destHashMap.size())
       {

       	Iterator<Entry<Integer, FieldMappingView>> destKeyIterator = destHashMap.entrySet().iterator();
       	while (destKeyIterator.hasNext()) {
           	Entry<Integer, FieldMappingView> d = destKeyIterator.next();
           	FieldMappingView fieldMappingView= new FieldMappingView();
           	fieldMappingView=d.getValue();
           	Iterator<Entry<Integer, FieldMappingView>> sourceKeyIterator = sourceHashMap.entrySet().iterator();
           	while(sourceKeyIterator.hasNext())
           	{
               	Entry<Integer, FieldMappingView> s = sourceKeyIterator.next();
               	if(d.getKey()==s.getKey())
               	{
               	   fieldMappingView.setSourceColumnName(s.getValue().getSourceColumnName());
               	   fieldMappingView.setSourceColumnType(s.getValue().getSourceColumnType());
               	   fieldMappingView.setReference(s.getValue().getReference());
               	   fieldMappingView.setDestLength(s.getValue().getDestLength());
               	   fieldMappingView.setDestScale(s.getValue().getDestScale());                 
               	}
           	}
           	sourceORdest.add(fieldMappingView);
			}	
      
       }
	   
	   return sourceORdest;
   }

public static HashMapAndArray getSourceHashMap(String json) {
	// TODO Auto-generated method stub
	HashMapAndArray hashMapAndArray= new HashMapAndArray();
	String []primarys={};
	TreeMap<Integer, FieldMappingView> sourceHashMap=new TreeMap<Integer, FieldMappingView>();
	JSONArray jsonArray= JSONArray.parseArray(json);
	if (!jsonArray.isEmpty()&&jsonArray.size()>0) {
		JSONObject json1=null;
		if(jsonArray.get(0)!=null)
		{
			json1= JSONObject.parseObject( jsonArray.get(0).toString());
			//取得第一判断是否有，如果没有证明证明这个json1是空
			if(json1.getString("primaryKey").length()>2)
			{
				//由于找到的primaryKey是一个集合
				JSONArray ja=JSONArray.parseArray(json1.get("primaryKey").toString());	
				List<String> list= new ArrayList<String>();
				for (int i = 0; i < ja.size(); i++) {
					JSONObject jb= JSONObject.parseObject(ja.getString(i));				
					if(jb.getString("pkColName")!="")
					{
						list.add(jb.getString("pkColName"));
					}
				}
				primarys=(String[])list.toArray(new String[list.size()]);
			}
				
		}
		if(jsonArray.get(1)!=null)
		{
		    json1=JSONObject.parseObject(jsonArray.get(1).toString());		      
			if(json1.getString("structure").length()>2)
				{
				JSONArray ja=JSONArray.parseArray(json1.get("structure").toString());
					for (int i=0;i<ja.size();i++) {
		          	FieldMappingView source=new FieldMappingView();
					JSONObject jb= JSONObject.parseObject(ja.getString(i));	        	
		          	 //得到源字段名称
		              source.setSourceColumnName(jb.getString("colName")); 
		                //得到源字段类型
		              source.setReference("");
		              String typename=getColumuType(jb.getString("typeID"));
		              //System.out.println(typename);
		              source.setSourceColumnType(typename);
		              source.setDestColumuName("");
		              source.setDestColumnType("");
		            //  source.setDestColumuTypeViews(typeList);
		              source.setDestLength(jb.getString("colSize"));
		              source.setDestScale(jb.getString("colScale"));
		              source.setStartIndex(0);
		              source.setEndIndex(0);
		              source.setIsPrimary(false);
		              source.setIsNullable(false);
		              sourceHashMap.put(i, source);
				}}
		      
		}

	}
	hashMapAndArray.setPrimarys(primarys);
    hashMapAndArray.setHashMap(sourceHashMap);
	return hashMapAndArray;
}

	public static String getColumuType(String typeID) {
	   String typeName="";
	   Integer id;
	   if(typeID!=null)
	   {
		   
			switch (Integer.valueOf(typeID)) {
			case java.sql.Types.CHAR:
			case java.sql.Types.VARCHAR:
				id=ValueMetaInterface.TYPE_STRING;
				break;
			case java.sql.Types.BIGINT:
			case java.sql.Types.INTEGER:
			case java.sql.Types.NUMERIC:
			case java.sql.Types.SMALLINT:
			case java.sql.Types.TINYINT:
				id=ValueMetaInterface.TYPE_INTEGER;
				break;
			case java.sql.Types.DECIMAL:
			case java.sql.Types.DOUBLE:
			case java.sql.Types.FLOAT:
			case java.sql.Types.REAL:
				id= ValueMetaInterface.TYPE_NUMBER;
				break;
			case java.sql.Types.DATE:
			case java.sql.Types.TIME:
			case java.sql.Types.TIMESTAMP:
				id=ValueMetaInterface.TYPE_DATE;
				break;
			case java.sql.Types.BOOLEAN:
			case java.sql.Types.BIT:
				id=ValueMetaInterface.TYPE_BOOLEAN;
				break;
			default:
				id = ValueMetaInterface.TYPE_NONE;
				break;
			}

		   String []types=ValueMetaInterface.typeCodes;
	        for (int i = 0; i < types.length; i++) {
				if(i==id)
				{
					typeName=types[i];
				}
			}   
	   }

		return typeName;
	}

	//参考方法没有用到这个方法
	public RowMetaInterface getParameterMetaData(PreparedStatement ps) {
		RowMetaInterface par = new RowMeta();
		try {
			ParameterMetaData pmd = ps.getParameterMetaData();
			for (int i = 1; i <= pmd.getParameterCount(); i++) {
				String name = "par" + i;
				int sqltype = pmd.getParameterType(i);
				int length = pmd.getPrecision(i);
				int precision = pmd.getScale(i);
				ValueMeta val;

				switch (sqltype) {
				case java.sql.Types.CHAR:
				case java.sql.Types.VARCHAR:
					val = new ValueMeta(name, ValueMetaInterface.TYPE_STRING);
					break;
				case java.sql.Types.BIGINT:
				case java.sql.Types.INTEGER:
				case java.sql.Types.NUMERIC:
				case java.sql.Types.SMALLINT:
				case java.sql.Types.TINYINT:
					val = new ValueMeta(name, ValueMetaInterface.TYPE_INTEGER);
					break;
				case java.sql.Types.DECIMAL:
				case java.sql.Types.DOUBLE:
				case java.sql.Types.FLOAT:
				case java.sql.Types.REAL:
					val = new ValueMeta(name, ValueMetaInterface.TYPE_NUMBER);
					break;
				case java.sql.Types.DATE:
				case java.sql.Types.TIME:
				case java.sql.Types.TIMESTAMP:
					val = new ValueMeta(name, ValueMetaInterface.TYPE_DATE);
					break;
				case java.sql.Types.BOOLEAN:
				case java.sql.Types.BIT:
					val = new ValueMeta(name, ValueMetaInterface.TYPE_BOOLEAN);
					break;
				default:
					val = new ValueMeta(name, ValueMetaInterface.TYPE_NONE);
					break;
				}

				if (val.isNumeric() && (length > 18 || precision > 18)) {
					val = new ValueMeta(name, ValueMetaInterface.TYPE_BIGNUMBER);
				}

				par.addValueMeta(val);
			}
		}
		// Oops: probably the database or JDBC doesn't support it.
		catch (AbstractMethodError e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return par;
	}

	public static List<FieldMappingView> getSourceORdest(TreeMap<Integer, FieldMappingView> hashMap) {
		// TODO Auto-generated method stub
	   List<FieldMappingView> sourceORdest= new ArrayList<FieldMappingView>();
           	Iterator<Entry<Integer, FieldMappingView>> sourceKeyIterator = hashMap.entrySet().iterator();
           	while(sourceKeyIterator.hasNext())
           	{
               	FieldMappingView fieldMappingView= new FieldMappingView();
               	Entry<Integer, FieldMappingView> s = sourceKeyIterator.next();
               	fieldMappingView=s.getValue();
               	sourceORdest.add(fieldMappingView);
           	}
		return sourceORdest;
	}

}
