package com.aofei.kettle.core.database;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aofei.base.exception.ApplicationException;
import com.aofei.kettle.exception.KettleError;
import com.aofei.kettle.model.request.RepositoryDatabaseAttributeRequest;
import com.aofei.kettle.model.request.RepositoryDatabaseRequest;
import com.aofei.kettle.model.response.RepositoryDatabaseAttributeResponse;
import com.aofei.kettle.model.response.RepositoryDatabaseResponse;
import com.aofei.kettle.utils.StringEscapeHelper;
import com.google.common.collect.Lists;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.*;
import org.pentaho.di.core.database.sap.SAPR3DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.exception.KettleDatabaseException;
import org.pentaho.ui.database.Messages;
import org.springframework.util.StringUtils;

import java.util.*;

public class DatabaseCodec {



	
	public static JSONObject encode(DatabaseMeta databaseMeta) {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("name", databaseMeta.getDisplayName());
		jsonObject.put("type", databaseMeta.getPluginId());
		jsonObject.put("access", databaseMeta.getAccessType());
		
		jsonObject.put("hostname", databaseMeta.getHostname());
		jsonObject.put("databaseName", databaseMeta.getDatabaseName());
		jsonObject.put("username", databaseMeta.getUsername());
		jsonObject.put("password", Encr.decryptPasswordOptionallyEncrypted(databaseMeta.getPassword()));
		if(databaseMeta.isStreamingResults())
			jsonObject.put("streamingResults", databaseMeta.isStreamingResults());
		jsonObject.put("dataTablespace", databaseMeta.getDataTablespace());
		jsonObject.put("indexTablespace", databaseMeta.getIndexTablespace());
		if(databaseMeta.getSQLServerInstance() != null)
			jsonObject.put("sqlServerInstance", databaseMeta.getSQLServerInstance());
		if(databaseMeta.isUsingDoubleDecimalAsSchemaTableSeparator())
			jsonObject.put("usingDoubleDecimalAsSchemaTableSeparator", databaseMeta.isUsingDoubleDecimalAsSchemaTableSeparator());
		jsonObject.put(SAPR3DatabaseMeta.ATTRIBUTE_SAP_LANGUAGE, databaseMeta.getAttributes().getProperty( SAPR3DatabaseMeta.ATTRIBUTE_SAP_LANGUAGE ));
		jsonObject.put(SAPR3DatabaseMeta.ATTRIBUTE_SAP_SYSTEM_NUMBER, databaseMeta.getAttributes().getProperty( SAPR3DatabaseMeta.ATTRIBUTE_SAP_SYSTEM_NUMBER ));
		jsonObject.put(SAPR3DatabaseMeta.ATTRIBUTE_SAP_CLIENT, databaseMeta.getAttributes().getProperty( SAPR3DatabaseMeta.ATTRIBUTE_SAP_CLIENT ));
		
		jsonObject.put(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL, databaseMeta.getAttributes().getProperty( GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL ));
		jsonObject.put(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS, databaseMeta.getAttributes().getProperty( GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS ));
		jsonObject.put("servername", databaseMeta.getServername());
		
		Object v = databaseMeta.getAttributes().get(MSSQLServerNativeDatabaseMeta.ATTRIBUTE_USE_INTEGRATED_SECURITY);
		if (v != null && v instanceof String) {
			String useIntegratedSecurity = (String) v;
			jsonObject.put(MSSQLServerNativeDatabaseMeta.ATTRIBUTE_USE_INTEGRATED_SECURITY, Boolean.parseBoolean(useIntegratedSecurity));
		} else {
			jsonObject.put(MSSQLServerNativeDatabaseMeta.ATTRIBUTE_USE_INTEGRATED_SECURITY, false);
		}
		
		jsonObject.put("port", databaseMeta.getDatabasePortNumberString());
		
		// Option parameters:
		
		Map<String, String> extraOptions = databaseMeta.getExtraOptions();
		JSONArray options = new JSONArray();
		if (extraOptions != null) {
			Iterator<String> keys = extraOptions.keySet().iterator();
			String currentType = databaseMeta.getPluginId();
			while (keys.hasNext()) {

				String parameter = keys.next();
				String value = extraOptions.get(parameter);
				if (!StringUtils.hasText(value) || (value.equals(DatabaseMeta.EMPTY_OPTIONS_STRING))) {
					value = "";
				}

				int dotIndex = parameter.indexOf('.');
				if (dotIndex >= 0) {
					String parameterOption = parameter.substring(dotIndex + 1);
					String databaseType = parameter.substring(0, dotIndex);
					if (currentType != null && currentType.equals(databaseType)) {
						JSONObject jsonObject2 = new JSONObject();
						jsonObject2.put("name", parameterOption);
						jsonObject2.put("value", value);
						options.add(jsonObject2);
					}
				}
			}
		}
		jsonObject.put("extraOptions", options);
		
		// Advanced panel settings:
		jsonObject.put("supportBooleanDataType", databaseMeta.supportsBooleanDataType());
		jsonObject.put("supportTimestampDataType", databaseMeta.supportsTimestampDataType());
		jsonObject.put("quoteIdentifiersCheck", databaseMeta.isQuoteAllFields());
		jsonObject.put("lowerCaseIdentifiersCheck", databaseMeta.isForcingIdentifiersToLowerCase());
		jsonObject.put("upperCaseIdentifiersCheck", databaseMeta.isForcingIdentifiersToUpperCase());
		jsonObject.put("preserveReservedCaseCheck", databaseMeta.preserveReservedCase());
		jsonObject.put("preferredSchemaName", databaseMeta.getPreferredSchemaName());
		jsonObject.put("connectSQL", StringEscapeHelper.encode(databaseMeta.getConnectSQL()));
		
		// Cluster panel settings
		jsonObject.put("partitioned", databaseMeta.isPartitioned() ? "Y" : "N");
		
		JSONArray partitionInfo = new JSONArray();
		PartitionDatabaseMeta[] clusterInformation = databaseMeta.getPartitioningInformation();
		if(clusterInformation != null) {
			for ( int i = 0; i < clusterInformation.length; i++ ) {
		        PartitionDatabaseMeta meta = clusterInformation[i];
		        JSONObject jsonObject2 = new JSONObject();
				jsonObject2.put("partitionId", meta.getPartitionId());
				jsonObject2.put("hostname", meta.getHostname());
				jsonObject2.put("port", meta.getPort());
				jsonObject2.put("databaseName", meta.getDatabaseName());
				jsonObject2.put("username", meta.getUsername());
				jsonObject2.put("password", meta.getPassword());

				partitionInfo.add(jsonObject2);
		      }
		}
		jsonObject.put("partitionInfo", partitionInfo);
		
		// Pooling panel settings
		jsonObject.put("usingConnectionPool", databaseMeta.isUsingConnectionPool() ? "Y" : "N");
		jsonObject.put("initialPoolSize", databaseMeta.getInitialPoolSize());
		jsonObject.put("maximumPoolSize", databaseMeta.getMaximumPoolSize());
		Properties properties = databaseMeta.getConnectionPoolingProperties();
		JSONArray jsonArray2 = new JSONArray();
		for (DatabaseConnectionPoolParameter parameter : BaseDatabaseMeta.poolingParameters) {
			JSONObject jsonObject2 = new JSONObject();
			jsonObject2.put("enabled", properties.containsKey(parameter.getParameter()));
			jsonObject2.put("name", parameter.getParameter());
			jsonObject2.put("defValue", parameter.getDefaultValue());
			jsonObject2.put("description", StringEscapeHelper.encode(parameter.getDescription()));
			jsonArray2.add(jsonObject2);
		}
            
		jsonObject.put("pool_params", jsonArray2);
		jsonObject.put("read_only", databaseMeta.isReadOnly());
		
		return jsonObject;
	}
	
	public static DatabaseMeta decode(JSONObject jsonObject) throws KettleDatabaseException {

		DatabaseMeta databaseMeta = new DatabaseMeta();


		databaseMeta.setName(jsonObject.getString("name"));
		databaseMeta.setDisplayName(databaseMeta.getName());
		databaseMeta.setDatabaseType(jsonObject.getString("type"));
		databaseMeta.setAccessType(jsonObject.getInteger("access"));
		
		if(jsonObject.containsKey("hostname"))
			databaseMeta.setHostname(jsonObject.getString("hostname"));
		if(jsonObject.containsKey("databaseName"))
			databaseMeta.setDBName(jsonObject.getString("databaseName"));
		if(jsonObject.containsKey("username"))
			databaseMeta.setUsername(jsonObject.getString("username"));
		if(jsonObject.containsKey("password"))
			databaseMeta.setPassword(jsonObject.getString("password"));
		if(jsonObject.containsKey("streamingResults"))	// infobright-jndi
			databaseMeta.setStreamingResults(true);
		if(jsonObject.containsKey("dataTablespace"))	//oracle-jndi
			databaseMeta.setDataTablespace(jsonObject.getString("dataTablespace"));
		if(jsonObject.containsKey("indexTablespace"))	//oracle-jndi
			databaseMeta.setIndexTablespace(jsonObject.getString("indexTablespace"));
		if(jsonObject.containsKey("sqlServerInstance"))		//mssql-native
			databaseMeta.setSQLServerInstance(jsonObject.getString("sqlServerInstance"));
		if(jsonObject.containsKey("usingDoubleDecimalAsSchemaTableSeparator"))	//mssql-jndi
			databaseMeta.setUsingDoubleDecimalAsSchemaTableSeparator(jsonObject.getBooleanValue("usingDoubleDecimalAsSchemaTableSeparator"));
		
		// SAP Attributes...
	    if ( jsonObject.containsKey(SAPR3DatabaseMeta.ATTRIBUTE_SAP_LANGUAGE) ) {
	    	databaseMeta.getAttributes().put( SAPR3DatabaseMeta.ATTRIBUTE_SAP_LANGUAGE, jsonObject.getString(SAPR3DatabaseMeta.ATTRIBUTE_SAP_LANGUAGE) );
	    }
	    if ( jsonObject.containsKey(SAPR3DatabaseMeta.ATTRIBUTE_SAP_SYSTEM_NUMBER) ) {
	    	databaseMeta.getAttributes().put( SAPR3DatabaseMeta.ATTRIBUTE_SAP_SYSTEM_NUMBER, jsonObject.getString(SAPR3DatabaseMeta.ATTRIBUTE_SAP_SYSTEM_NUMBER) );
	    }
	    if ( jsonObject.containsKey(SAPR3DatabaseMeta.ATTRIBUTE_SAP_CLIENT) ) {
	    	databaseMeta.getAttributes().put( SAPR3DatabaseMeta.ATTRIBUTE_SAP_CLIENT, jsonObject.getString(SAPR3DatabaseMeta.ATTRIBUTE_SAP_CLIENT) );
	    }
	    
	    // Generic settings...
	    if ( jsonObject.containsKey(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL) ) {
	    	databaseMeta.getAttributes().put( GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL, jsonObject.getString(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_URL) );
	    }
	    if ( jsonObject.containsKey(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS) ) {
	    	databaseMeta.getAttributes().put( GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS, jsonObject.getString(GenericDatabaseMeta.ATRRIBUTE_CUSTOM_DRIVER_CLASS) );
	    }
	    
	    // Server Name: (Informix)
	    if ( jsonObject.containsKey("servername") ) {
	    	databaseMeta.setServername(jsonObject.getString("servername"));
	    }
		
	    // Microsoft SQL Server Use Integrated Security
	    if ( jsonObject.containsKey(MSSQLServerNativeDatabaseMeta.ATTRIBUTE_USE_INTEGRATED_SECURITY) ) {
	    	boolean flag = jsonObject.getBooleanValue(MSSQLServerNativeDatabaseMeta.ATTRIBUTE_USE_INTEGRATED_SECURITY);
	    	if(flag) databaseMeta.getAttributes().put(MSSQLServerNativeDatabaseMeta.ATTRIBUTE_USE_INTEGRATED_SECURITY, flag);
	    }
	    
	    if(jsonObject.containsKey("port"))
	    	databaseMeta.setDBPort(jsonObject.getString("port"));

	    // Option parameters:
	    
		JSONArray options = jsonObject.getJSONArray("extraOptions");
		if(options != null) {
			for(int i=0; i<options.size(); i++) {
				JSONObject jsonObject2 = options.getJSONObject(i);
				String parameter = jsonObject2.getString("name");
				String value = jsonObject2.getString("value");
				
				if (value == null) {
					value = "";
				}
				
				if ((parameter != null) && (parameter.trim().length() > 0)) {
					if (value.trim().length() <= 0) {
						value = DatabaseMeta.EMPTY_OPTIONS_STRING;
					}

					databaseMeta.addExtraOption(databaseMeta.getPluginId(), parameter, value);
				}
			}
		}
		
		// Advanced panel settings:

	    if ( jsonObject.containsKey("supportBooleanDataType") ) {
	    	databaseMeta.setSupportsBooleanDataType( jsonObject.getBooleanValue("supportBooleanDataType") );
	    }

	    if ( jsonObject.containsKey("supportTimestampDataType") ) {
	    	databaseMeta.setSupportsTimestampDataType( jsonObject.getBooleanValue("supportTimestampDataType") );
	    }

	    if ( jsonObject.containsKey("quoteIdentifiersCheck") ) {
	    	databaseMeta.setQuoteAllFields( jsonObject.getBooleanValue("quoteIdentifiersCheck") );
	    }

	    if ( jsonObject.containsKey("lowerCaseIdentifiersCheck") ) {
	    	databaseMeta.setForcingIdentifiersToLowerCase( jsonObject.getBooleanValue("lowerCaseIdentifiersCheck") );
	    }

	    if ( jsonObject.containsKey("upperCaseIdentifiersCheck") ) {
	    	databaseMeta.setForcingIdentifiersToUpperCase( jsonObject.getBooleanValue("upperCaseIdentifiersCheck") );
	    }

	    if ( jsonObject.containsKey("preserveReservedCaseCheck") ) {
	    	databaseMeta.setPreserveReservedCase( jsonObject.getBooleanValue("preserveReservedCaseCheck") );
	    }

	    if ( jsonObject.containsKey("preferredSchemaName") ) {
	    	databaseMeta.setPreferredSchemaName( jsonObject.getString("preferredSchemaName") );
	    }

	    if ( jsonObject.containsKey("connectSQL") ) {
	    	databaseMeta.setConnectSQL( StringEscapeHelper.decode(jsonObject.getString("connectSQL")) );
	    }
		
	    // Cluster panel settings
	    databaseMeta.setPartitioned("Y".equalsIgnoreCase(jsonObject.getString("partitioned")));
	    if ( "Y".equalsIgnoreCase(jsonObject.getString("partitioned")) ) {
			JSONArray partitionInfo = jsonObject.getJSONArray("partitionInfo");
			if(partitionInfo != null) {
				ArrayList<PartitionDatabaseMeta> list = new ArrayList<PartitionDatabaseMeta>();
				for (int i = 0; i < partitionInfo.size(); i++) {
					JSONObject jsonObject2 = partitionInfo.getJSONObject(i);
					PartitionDatabaseMeta meta = new PartitionDatabaseMeta();

					String partitionId = jsonObject2.getString("partitionId");
					if ((partitionId == null) || (partitionId.trim().length() <= 0)) {
						continue;
					}

					meta.setPartitionId(jsonObject2.getString("partitionId"));
					meta.setHostname(jsonObject2.getString("hostname"));
					meta.setPort(jsonObject2.getString("port"));
					meta.setDatabaseName(jsonObject2.getString("databaseName"));
					meta.setUsername(jsonObject2.getString("username"));
					meta.setPassword(jsonObject2.getString("password"));
					list.add(meta);
				}
				if (list.size() > 0)
					databaseMeta.setPartitioningInformation(list.toArray( new PartitionDatabaseMeta[list.size()] ));
			}
	    }

	    if("Y".equalsIgnoreCase(jsonObject.getString("usingConnectionPool"))) {
	    	databaseMeta.setUsingConnectionPool( true );
	    	
			try {
				databaseMeta.setInitialPoolSize(jsonObject.getInteger("initialPoolSize"));
			} catch (Exception e) {
			}

			try {
				databaseMeta.setMaximumPoolSize(jsonObject.getInteger("maximumPoolSize"));
			} catch (Exception e) {
			}
	    	
	    	JSONArray pool_params = jsonObject.getJSONArray("pool_params");
	    	if(pool_params != null) {
	    		Properties properties = new Properties();
	    		for(int i=0; i<pool_params.size(); i++) {
					JSONObject jsonObject2 = pool_params.getJSONObject(i);
					Boolean enabled = jsonObject2.getBooleanValue("enabled");
					String parameter = jsonObject2.getString("name");
					String value = jsonObject2.getString("defValue");

					if (!enabled) {
						continue;
					}
					
					if( StringUtils.hasText(parameter) && StringUtils.hasText(value) ) {
						properties.setProperty( parameter, value );
					}
				}
	    		databaseMeta.setConnectionPoolingProperties( properties );
	    	}
	    }

		databaseMeta.setReadOnly(jsonObject.getBooleanValue("read_only"));
		return databaseMeta;
	}

	public static DatabaseMeta decode(RepositoryDatabaseResponse databaseResponse) throws KettleDatabaseException {
		DatabaseMeta databaseMeta = null;
		if(databaseResponse !=null){
			databaseMeta = new DatabaseMeta();

			databaseMeta.setDatabaseInterface(DatabaseMeta.getDatabaseInterface(databaseResponse.getDatabaseType()));
			databaseMeta.setAttributes(new Properties()); // new attributes

			databaseMeta.setName( databaseResponse.getRepositoryConnectionName() );

			databaseMeta.setAccessType( databaseResponse.getDatabaseContype());


			databaseMeta.setHostname( Const.NVL(databaseResponse.getHostName(), ""));
			databaseMeta.setDBName(Const.NVL(databaseResponse.getDatabaseName(), ""));
			databaseMeta.setDBPort( Const.NVL(databaseResponse.getPort(), "") );
			databaseMeta.setUsername( Const.NVL(databaseResponse.getUsername(), "") );
			databaseMeta.setPassword( Encr.decryptPasswordOptionallyEncrypted( Const.NVL(databaseResponse.getPassword(), "") ) );
			databaseMeta.setServername( Const.NVL(databaseResponse.getServername(), ""));
			databaseMeta.setDataTablespace(Const.NVL(databaseResponse.getDataTbs(), "") );
			databaseMeta.setIndexTablespace( Const.NVL(databaseResponse.getIndexTbs(), "") );

			// Also, load all the properties we can find...
			List<RepositoryDatabaseAttributeResponse> attrs = databaseResponse.getAttrs();
			for (RepositoryDatabaseAttributeResponse row : attrs)
			{
				String code = Const.NVL(row.getCode(), "");
				String attribute = Const.NVL(row.getValueStr(), "");
				// System.out.println("Attributes: "+(getAttributes()!=null)+", code: "+(code!=null)+", attribute: "+(attribute!=null));
				databaseMeta.getAttributes().put(code, Const.NVL(attribute, ""));
			}
		}

		return databaseMeta;
	}

	public static RepositoryDatabaseRequest decode(DatabaseMeta databaseMeta) throws KettleDatabaseException {
		RepositoryDatabaseRequest databaseRequest = null;
		if(databaseMeta !=null){
			databaseRequest = new RepositoryDatabaseRequest();
			databaseRequest.setDatabaseType(databaseMeta.getDatabaseInterface().getPluginId());
			databaseRequest.setRepositoryConnectionName(databaseMeta.getName());
			databaseRequest.setDatabaseContype(databaseMeta.getAccessType());
			databaseRequest.setHostName(databaseMeta.getHostname());
			databaseRequest.setDatabaseName(databaseMeta.getDatabaseName());
			databaseRequest.setPort(databaseMeta.getDatabaseInterface().getDatabasePortNumberString());
			databaseRequest.setUsername(databaseMeta.getUsername() );
			databaseRequest.setPassword( Encr.encryptPasswordIfNotUsingVariables( databaseMeta.getPassword()) );
			databaseRequest.setServername( databaseMeta.getServername());
			databaseRequest.setDataTbs(databaseMeta.getDataTablespace() );
			databaseRequest.setIndexTbs( databaseMeta.getIndexTablespace() );

			List<RepositoryDatabaseAttributeRequest> attrs = Lists.newArrayList();
			Map<String, String> connectionExtraOptions = databaseMeta.getExtraOptions();
			for(String key : connectionExtraOptions.keySet()){
				attrs.add(new RepositoryDatabaseAttributeRequest(key,connectionExtraOptions.get(key)));
			}
			databaseRequest.setAttrs(attrs);

		}

		return databaseRequest;
	}


	public static DatabaseMeta checkParameters(JSONObject jsonObject) throws KettleDatabaseException {

		DatabaseMeta database = decode(jsonObject);

		if(database.isUsingConnectionPool()) {
			String parameters = "";
			JSONArray pool_params = jsonObject.getJSONArray("pool_params");
			if(pool_params != null) {
				for(int i=0; i<pool_params.size(); i++) {
					JSONObject jsonObject2 = pool_params.getJSONObject(i);
					Boolean enabled = jsonObject2.getBoolean("enabled");
					String parameter = jsonObject2.getString("name");
					String value = jsonObject2.getString("defValue");

					if (!enabled) {
						continue;
					}

					if(!StringUtils.hasText(value) ) {
						parameters = parameters.concat( parameter ).concat( System.getProperty( "line.separator" ) );
					}
				}

			}

			if(parameters.length() > 0) {
				String message = Messages.getString( "DataHandler.USER_INVALID_PARAMETERS" ).concat( parameters );
				throw new ApplicationException(KettleError.CHECK_DATABASE_FAILURE.getCode(),message);
			}
		}

		String[] remarks = database.checkParameters();
		String message = "";

		if ( remarks.length != 0 ) {
			for (int i = 0; i < remarks.length; i++) {
				message = message.concat("* ").concat(remarks[i]).concat(System.getProperty("line.separator"));
			}
			throw new ApplicationException(KettleError.CHECK_DATABASE_FAILURE.getCode(),message);
		}

		return database;
	}
}
