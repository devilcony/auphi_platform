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
package com.auphi.ktrl.mdm.controller;

import com.auphi.data.hub.core.BaseMultiActionController;
import com.auphi.data.hub.core.PaginationSupport;
import com.auphi.data.hub.core.struct.BaseDto;
import com.auphi.data.hub.core.struct.Dto;
import com.auphi.data.hub.core.util.JsonHelper;
import com.auphi.data.hub.domain.Datasource;
import com.auphi.data.hub.service.DatasourceService;
import com.auphi.ktrl.conn.util.DataBaseUtil;
import com.auphi.ktrl.mdm.domain.DataBaseType;
import com.auphi.ktrl.mdm.domain.MdmModelAttribute;
import com.auphi.ktrl.mdm.domain.MdmTable;
import com.auphi.ktrl.mdm.domain.TextValue;
import com.auphi.ktrl.mdm.service.DataBaseTypeService;
import com.auphi.ktrl.mdm.service.MdmModelAttributeService;
import com.auphi.ktrl.mdm.service.MdmTableService;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.database.Database;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMeta;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@ApiIgnore
@Controller("mdmTable")
public class MdmTableController extends BaseMultiActionController {

	private final static String INDEX = "admin/mdmTable";
	
	@Autowired
	private MdmTableService mdmTableService;
	
	@Autowired
	private DatasourceService datasourceService;
	
	@Autowired
	private DataBaseTypeService dataBaseTypeService;
	
	@Autowired
	private MdmModelAttributeService mdmModelAttributeService;
	
	private Map<Integer,Datasource> dataSourceMap = new HashMap<Integer,Datasource>();
	
	private Map<Integer,DataBaseType> dataBaseTypeMap = new HashMap<Integer,DataBaseType>();
	
	public ModelAndView index(HttpServletRequest req,HttpServletResponse resp){
		return new ModelAndView(INDEX);
	}
	
	
	public ModelAndView query(HttpServletRequest req,HttpServletResponse resp) throws IOException{		
		Dto<String,Object> dto = new BaseDto();
		try {
//			String queryFTPName = req.getParameter("queryFTPName");
//			dto.put("queryFTPName", queryFTPName);
			this.setPageParam(dto, req);
			PaginationSupport<MdmTable> page = mdmTableService.query(dto);
			String jsonString = JsonHelper.encodeObject2Json(page);	
			write(jsonString, resp);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public ModelAndView save(HttpServletRequest req,HttpServletResponse resp,MdmTable mdmTable) throws IOException{	
		try{
			Integer id = this.mdmTableService.queryMaxId(null);
	        if(id == null) id = 1;
	        else id = id+1;
	        mdmTable.setId_table(id);
			this.mdmTableService.save(mdmTable);
			this.setOkTipMsg("添加成功", resp);
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg("添加失败", resp);
		}
		return null;
	}

	
	public ModelAndView update(HttpServletRequest req,HttpServletResponse resp,MdmTable mdmTable) throws IOException{	
		try{
			this.mdmTableService.update(mdmTable);
			this.setOkTipMsg("编辑成功", resp);
		} catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg("编辑失败", resp);
		}
		return null;
	}
	
	

	public ModelAndView delete(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		try{
			String ids = req.getParameter("ids");
			Dto dto = new BaseDto();
			dto.put("ids",ids);
			this.mdmTableService.delete(dto);
			this.setOkTipMsg("删除成功", resp);
		}catch(Exception e){
			this.setOkTipMsg("删除失败", resp);
		}
		return null;
	}
	
	public ModelAndView getSchemaName(HttpServletRequest req,HttpServletResponse resp) throws IOException{	
		Database database = null;
		try {
			String id_database = req.getParameter("id_database");
			
			List<TextValue> list = new ArrayList<TextValue>();
			database = createDatabase(id_database);
			if(database != null){
				String[] schemaNames = database.getSchemas();
				if(schemaNames != null && schemaNames.length > 0){
					for(String schemaName : schemaNames){
						TextValue textValue = new TextValue();
						textValue.setText(schemaName);
						textValue.setValue(schemaName);
						list.add(textValue);
					}
				}
			}
			if(list.size() == 0){
				TextValue textValue = new TextValue();
				textValue.setText("");
				textValue.setValue("");
				list.add(textValue);
			}
			String jsonString = JsonHelper.encodeObject2Json(list);	
			write(jsonString, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(database!=null){
				try {
					database.closeConnectionOnly();
				} catch (KettleDatabaseException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public ModelAndView getTableName(HttpServletRequest req,HttpServletResponse resp) throws IOException{	
		Database database = null;
		try {
			String id_database = req.getParameter("id_database");
			String schema_name = req.getParameter("schema_name");
			List<TextValue> list = new ArrayList<TextValue>();
			database = createDatabase(id_database);
			if(database != null){
				
				String[] tableNames = null;
							
				if(schema_name!=null && !"".equals(schema_name)){
					tableNames = database.getTablenames(schema_name, false);
				}else{
					tableNames = database.getTablenames();
				}




				if(tableNames != null && tableNames.length > 0){
					for(String tableName : tableNames){
						TextValue textValue = new TextValue();
						textValue.setText(tableName);
						textValue.setValue(tableName);
						list.add(textValue);
					}
				}

			}
			if(list.size() == 0){
				TextValue textValue = new TextValue();
				textValue.setText("");
				textValue.setValue("");
				list.add(textValue);
			}else{
				Collections.sort(list,new Comparator<TextValue>(){

					@Override
					public int compare(TextValue o1, TextValue o2) {
						return o1.getValue().compareTo(o2.getValue());
					}
				});
			}
			String jsonString = JsonHelper.encodeObject2Json(list);	
			write(jsonString, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(database!=null){
				try {
					database.closeConnectionOnly();
				} catch (KettleDatabaseException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public ModelAndView check(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		Database database = null;
		try{
			String table_name = req.getParameter("table_name");
			String schema_name = req.getParameter("schema_name");
			String id_database = req.getParameter("id_database");
			String id_model = req.getParameter("id_model");
			List<MdmModelAttribute> list = getMdmModelAttributeList(id_model);
			database = createDatabase(id_database);
			boolean isSame = true;
			if(database != null || list.size() > 0){
				
				//获取主键
				DatabaseMetaData dbMeta = database.getConnection().getMetaData(); 
				ResultSet pkRSet = dbMeta.getPrimaryKeys(null, null, table_name); 
				List<Object> pkList = new ArrayList<Object>();
				while( pkRSet.next() ) { 
					pkList.add(pkRSet.getObject(4)); 
				} 
				ResultSet rs = dbMeta.getColumns(null, null, table_name.toUpperCase(), null);
				
				while(rs.next()){
					String colName = rs.getString("COLUMN_NAME");//列名
					String typeName = rs.getString("TYPE_NAME");//类型名称
					int length = rs.getInt("COLUMN_SIZE");//精度
					int precision = rs.getInt("DECIMAL_DIGITS");// 小数的位数
					
					for(MdmModelAttribute bean : list){
						int valtype = ValueMeta.getType(typeName);
						if(!(colName.equals(bean.getField_name()) && 
								bean.getField_type() == valtype &&
								bean.getField_length() == length &&
								bean.getField_precision() == precision)){
							isSame = false;
							break;
						}
						if(bean.getIs_primary().equals("Y")){
							if(!pkList.contains(bean.getField_name())){
								isSame = false;
								break;
							}
						}
					}
					if(!isSame) break;
				}
			}else{
				isSame = false;
			}
			if(isSame){
				this.setOkTipMsg("检查成功", resp);
			}else{
				this.setOkTipMsg("检查失败", resp);
			}
		}catch(Exception e){
			this.setOkTipMsg("检查失败", resp);
		}finally{
			if(database!=null){
				try {
					database.closeConnectionOnly();
				} catch (KettleDatabaseException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public ModelAndView createSQL(HttpServletRequest req,HttpServletResponse resp) throws IOException, KettleDatabaseException{
		Database database = null;
		try{
			String id_model = req.getParameter("id_model");
			String schema_name = req.getParameter("schema_name");
			String id_database = req.getParameter("id_database");
			String table_name = req.getParameter("table_name");
			if(table_name==null || "".equals(table_name)){
				this.setFailTipMsg("请输入表名!", resp);
				return null;
			}
			String createSQL = "";
			database = createDatabase(id_database);
			boolean hasPrimaryKey = false;
			if(database != null){
				List<MdmModelAttribute> list = getMdmModelAttributeList(id_model);
				String pk = null;
				RowMetaInterface rm = new RowMeta();

				for(MdmModelAttribute mdmModelAttribute : list){
					ValueMeta ValueMeta = new ValueMeta(mdmModelAttribute.getField_name(),mdmModelAttribute.getField_type());
					ValueMeta.setLength(mdmModelAttribute.getField_length());
					ValueMeta.setPrecision(mdmModelAttribute.getField_precision());

					rm.addValueMeta(ValueMeta);
					if(mdmModelAttribute.getIs_primary().equals("Y")){
						hasPrimaryKey = true;
						pk = mdmModelAttribute.getField_name();
					}
				}
				if(!hasPrimaryKey){//没有主键 不能生成sql
					this.setFailTipMsg("创建失败,模型属性需要有一个主键!", resp);
				}else{
					String tbname = table_name;

					if(schema_name!=null&& !"".equals(schema_name)){
						tbname = schema_name+"."+table_name;
					}
					if(!database.checkTableExists(tbname)){
						createSQL = DataBaseUtil.getCreateTableStatement(schema_name,table_name,list,database);
					}else{
						createSQL = DataBaseUtil.getAlterTableStatement(schema_name,table_name,list,database);
						if("".equals(createSQL)){
							this.setFailTipMsg(tbname+ "表已存在,且表结构和模型属性一致!无需创建/更新", resp);
							return null;
						}
					}

					this.setOkTipMsg(createSQL, resp);
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			this.setFailTipMsg(e.getMessage(), resp);
		}finally{
			if(database!=null){
				try {
					database.closeConnectionOnly();
				} catch (KettleDatabaseException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}



	
	public ModelAndView runSQL(HttpServletRequest req,HttpServletResponse resp) throws IOException, KettleDatabaseException{
		Database database = null;
		try{
			String table_name = req.getParameter("table_name");
			String schema_name = req.getParameter("schema_name");
			String id_database = req.getParameter("id_database");
			String sql = req.getParameter("sql");
			
			database = createDatabase(id_database);
			if(database != null){
				Result result = database.execStatements(sql);
				
			}
			//this.mdmTableService.delete(dto);s
			
			this.setOkTipMsg("运行成功", resp);
		}catch(Exception e){
			this.setOkTipMsg("运行失败:"+e.getMessage(), resp);
		}finally{
			if(database!=null){
				try {
					database.closeConnectionOnly();
				} catch (KettleDatabaseException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	private Database createDatabase(String id_database){
		Database database = null;
		try{
			int id_dataBase = 0;
			if(id_database !=null && !id_database.isEmpty()){
				id_dataBase = Integer.parseInt(id_database);
			}
			if(dataSourceMap.size() == 0 ||  !dataSourceMap.containsKey(id_dataBase)){
				List<Datasource> dataSourceList = datasourceService.querySourceList();
				for(Datasource dataSource :dataSourceList){
					dataSourceMap.put(dataSource.getSourceId(), dataSource);
				}
			}
			if(dataBaseTypeMap.size() == 0){
				List<DataBaseType> dataBaseTypeList = dataBaseTypeService.queryAll();
				for(DataBaseType dataBaseType : dataBaseTypeList){
					dataBaseTypeMap.put(dataBaseType.getId_database_type(), dataBaseType);
				}
			}
			if(dataSourceMap.containsKey(id_dataBase)){
				Datasource datasource = dataSourceMap.get(id_dataBase);
				DataBaseType dataBaseType = dataBaseTypeMap.get(datasource.getSourceType());
				DatabaseMeta databaseMeta = new DatabaseMeta(
						datasource.getSourceName(),
						dataBaseType.getDescription(),
						"Native",
						datasource.getSourceIp(),
						datasource.getSourceDataBaseName(),
						datasource.getSourcePort(),
						datasource.getSourceUserName(),
						datasource.getSourcePassword());
				database = new Database(databaseMeta);
				database.connect();
				if(dataBaseType.getDescription().equalsIgnoreCase("Hadoop Hive 2") || dataBaseType.getDescription().equalsIgnoreCase("Hadoop Hive"))
				{
					database.execStatement("use "+datasource.getSourceDataBaseName());
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return database;
	}
	
	
	
	
	private List<MdmModelAttribute> getMdmModelAttributeList(String id_model){
		List<MdmModelAttribute> list = null;
		try{
			Dto<String,Object> dto = new BaseDto();
			dto.put("id_model", id_model);
			list = mdmModelAttributeService.query4ComboBox(dto);
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	public ModelAndView getTableColumn(HttpServletRequest req,HttpServletResponse resp) throws IOException{	
		Database database = null;
		try {
			String id_database = req.getParameter("id_database");
			String tableName = req.getParameter("tableName");
			List<TextValue> list = new ArrayList<TextValue>();
			database = createDatabase(id_database);
			List<ValueMetaInterface> ls = database.getTableFields(tableName).getValueMetaList();
			for(ValueMetaInterface vm:ls){
				TextValue textValue = new TextValue();
				textValue.setText(vm.getName());
				textValue.setValue(vm.getName());
				list.add(textValue);
			}
			if(list.size() == 0){
				TextValue textValue = new TextValue();
				textValue.setText("");
				textValue.setValue("");
				list.add(textValue);
			}
			String jsonString = JsonHelper.encodeObject2Json(list);	
			write(jsonString, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(database!=null){
				try {
					database.closeConnectionOnly();
				} catch (KettleDatabaseException e) {
					e.printStackTrace();
				}
			}
		}
		
		return null;
	}
	
	private String getPrimaryKey(List<MdmModelAttribute> attributes) throws SQLException{
		
		String primary_key = "";
		for(MdmModelAttribute attribute:attributes){
			if("Y".equals(attribute.getIs_primary())){
				primary_key = attribute.getField_name();
				break;
			}
		}
		return primary_key;
	}
	
	public ModelAndView getTableData(HttpServletRequest req,HttpServletResponse resp) throws IOException{	
		Database database = null;
		try {
			Map<String,Object> jsonMap = new HashMap<String, Object>();
			String id_table = req.getParameter("id_table");//id
			String meta = req.getParameter("meta");
			Dto<String,Object> dto = new BaseDto();
			dto.put("id_table", id_table);
			MdmTable mdmTable = mdmTableService.queryById(dto);
			
			dto.put("id_model", mdmTable.getId_model());
			List<MdmModelAttribute> attributes = mdmModelAttributeService.query4ComboBox(dto);
			List<Map<String,Object>> colums = getColums(attributes);
			Map<String, Object> metaData = new HashMap<String, Object>();
			metaData.put("root", "rows");
			metaData.put("id", 0);
			metaData.put("totalProperty", "totalCount");
			metaData.put("fields", colums);
			String primary_key = getPrimaryKey(attributes);
			
			database = createDatabase(mdmTable.getId_database()+"");

			String tablename =mdmTable.getTable_name();
			if(mdmTable.getSchema_name()!=null && !"".equals(mdmTable.getSchema_name())){
				tablename = mdmTable.getSchema_name()+"."+tablename;
			}

			if(!database.checkTableExists(tablename)){
				this.setFailTipMsg("表不存在,请点击 编辑-运行SQL",resp);
				return null;
			}

			StringBuffer sql = new StringBuffer("select * from ");
			sql.append(tablename);
			if(primary_key!=null && !"".equals(primary_key)){
				sql.append(" order by ").append(primary_key);
			}
			ResultSet result = database.openQuery(sql.toString());
			List<Map<String,Object>> datas = new ArrayList<Map<String,Object>>();

			while (result.next()) {
				Map<String,Object> data = new HashMap<String, Object>();
				for(MdmModelAttribute attribute:attributes){
					data.put(attribute.getField_name(), result.getString(attribute.getField_name()));
					data.put("id_table", id_table);
				}
				datas.add(data);
			}
			jsonMap.put("metaData", metaData);
			jsonMap.put("rows", datas);
			jsonMap.put("totalCount", datas.size());
			jsonMap.put("success", true);
			String jsonString = JsonHelper.encodeObject2Json(jsonMap);
			
			write(jsonString, resp);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(database!=null){
				try {
					database.closeConnectionOnly();
				} catch (KettleDatabaseException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public ModelAndView addOrUpdateTableData(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		Database database = null;
		try {
			String id_table = req.getParameter("id_table");//id
			Dto<String,Object> dto = new BaseDto();
			dto.put("id_table", id_table);
			MdmTable mdmTable = mdmTableService.queryById(dto);
			dto.put("id_model", mdmTable.getId_model());
			List<MdmModelAttribute> attributes = mdmModelAttributeService.query4ComboBox(dto);
			database = createDatabase(mdmTable.getId_database()+"");
			String action = req.getParameter("action");
			if("add".equals(action)){
				StringBuffer sql = new StringBuffer("INSERT INTO ");
				if(mdmTable.getSchema_name()!=null && !"".equals(mdmTable.getSchema_name())){
					sql.append(mdmTable.getSchema_name()).append(".");
				}
				sql.append(mdmTable.getTable_name()).append(" ");
				StringBuffer columns = new StringBuffer("(");
				StringBuffer values = new StringBuffer("(");
				for (MdmModelAttribute attribute:attributes){
					columns.append(""+attribute.getField_name()+"").append(",");
					values.append("'"+req.getParameter(attribute.getField_name())+"'").append(",");
				}
				columns.deleteCharAt(columns.length()-1);  
				values.deleteCharAt(values.length()-1); 
				columns.append(")");
				values.append(")");
				sql.append(columns).append(" values ").append(values);
				System.out.println(sql);
				Result result = database.execStatement(sql.toString());
				this.setOkTipMsg("添加成功", resp);
				
			}else if("edit".equals(action)){
				String condition = req.getParameter("condition");

				if(!"".equals(condition) && null != condition){
					
					StringBuffer sql = new StringBuffer("update ");
					if(mdmTable.getSchema_name()!=null && !"".equals(mdmTable.getSchema_name())){
						sql.append(mdmTable.getSchema_name()).append(".");
					}
					sql.append(mdmTable.getTable_name()).append(" set ");
					String key = "";
					for (MdmModelAttribute attribute:attributes){
						sql.append(attribute.getField_name()).append("=").append("'").append(req.getParameter(attribute.getField_name())).append("'").append(",");
					}
					sql.deleteCharAt(sql.length()-1);  
					sql.append(" where ").append(condition);
					System.out.println(sql);
					database.execStatement(sql.toString());
					this.setOkTipMsg("修改成功", resp);
				}
			}
			
			
			
		} catch (Exception e) {
			this.setFailTipMsg(e.getMessage(), resp);
			e.printStackTrace();
		}finally{
			if(database!=null){
				try {
					database.closeConnectionOnly();
				} catch (KettleDatabaseException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		return null;
	}
	
	public ModelAndView deleteTableData(HttpServletRequest req,HttpServletResponse resp) throws IOException{
		Database database = null;
		try {
			String condition = req.getParameter("condition");

			if(condition!=null&& !"".equals(condition) && condition!=null && !"".equals(condition) ){
				String[] columns = condition.split("#");
				if(columns!=null && columns.length>0){
					String id_table = req.getParameter("id_table");
					Dto<String,Object> dto = new BaseDto();
					dto.put("id_table", id_table);
					MdmTable mdmTable = mdmTableService.queryById(dto);
					database = createDatabase(mdmTable.getId_database()+"");
					for(int i = 0 ; i< columns.length;i++){
						StringBuffer sql = new StringBuffer("DELETE FROM ");
						if(mdmTable.getSchema_name()!=null && !"".equals(mdmTable.getSchema_name())){
							sql.append(mdmTable.getSchema_name()).append(".");
						}
						sql.append(mdmTable.getTable_name()).append(" WHERE ").append(columns[i]).append(";");
						System.out.println(sql);
						database.execStatement(sql.toString());
					}
					this.setOkTipMsg("删除成功", resp);
				}else{
					this.setFailTipMsg("操作失败", resp);
				}

			}else{
				this.setFailTipMsg("操作失败", resp);
			}
			
			
		} catch (Exception e) {
			this.setFailTipMsg(e.getMessage(), resp);
		}finally{
			if(database!=null){
				try {
					database.closeConnectionOnly();
				} catch (KettleDatabaseException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	private List<Map<String, Object>> getColums(List<MdmModelAttribute> attributes) {
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		if(attributes!=null){
			for(MdmModelAttribute attribute:attributes){
				map = new HashMap<String, Object>();
				map.put("header", attribute.getAttribute_name());
				map.put("name", attribute.getField_name());
				map.put("dataIndex", attribute.getField_name());
				map.put("width", 120);
				map.put("hidden", false);
				list.add(map);
			}
		}
		
		return list;
	}
	
	
}
