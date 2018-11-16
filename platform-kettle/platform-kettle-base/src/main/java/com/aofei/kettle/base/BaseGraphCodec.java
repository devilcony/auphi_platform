package com.aofei.kettle.base;

import java.util.HashSet;
import java.util.Set;

import com.aofei.kettle.App;
import com.aofei.kettle.base.GraphCodec;
import com.aofei.kettle.cluster.SlaveServerCodec;
import com.aofei.kettle.core.NotePadCodec;
import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import com.aofei.kettle.utils.StringEscapeHelper;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.NotePadMeta;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.gui.Point;
import org.pentaho.di.core.logging.ChannelLogTable;
import org.pentaho.di.core.logging.LogTableField;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectory;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.filerep.KettleFileRepository;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

public abstract class BaseGraphCodec implements GraphCodec {

	public Element encodeCommRootAttr(AbstractMeta meta, Document doc) throws UnknownParamException {
		Element e = doc.createElement("Info");
		e.setAttribute("name", meta.getName());
		e.setAttribute("fileName", meta.getFilename());
		e.setAttribute("description", meta.getDescription());
		e.setAttribute("extended_description", meta.getExtendedDescription());

		RepositoryDirectoryInterface directory = meta.getRepositoryDirectory();
		e.setAttribute("directory", directory != null ? directory.getPath() : RepositoryDirectory.DIRECTORY_SEPARATOR);

		e.setAttribute("shared_objects_file", meta.getSharedObjectsFile());

		e.setAttribute("created_user", meta.getCreatedUser());
	    e.setAttribute("created_date", XMLHandler.date2string( meta.getCreatedDate() ));
	    e.setAttribute("modified_user", meta.getModifiedUser());
	    e.setAttribute("modified_date", XMLHandler.date2string( meta.getModifiedDate() ));

	    String[] parameters = meta.listParameters();
	    JSONArray jsonArray = new JSONArray();
	    for ( int idx = 0; idx < parameters.length; idx++ ) {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", parameters[idx]);
			jsonObject.put("default_value", meta.getParameterDefault( parameters[idx] ));
			jsonObject.put("description", meta.getParameterDescription( parameters[idx] ));
			jsonArray.add(jsonObject);
	    }
	    e.setAttribute("parameters", jsonArray.toString());

//	    ChannelLogTable channelLogTable = meta.getChannelLogTable();
//	    JSONObject jsonObject = new JSONObject();
//	    jsonObject.put( "connection", channelLogTable.getConnectionName() );
//	    jsonObject.put( "schema", channelLogTable.getSchemaName() );
//	    jsonObject.put( "table", channelLogTable.getTableName() );
//	    jsonObject.put( "timeout_days", channelLogTable.getTimeoutInDays() );
//	    JSONArray fields = new JSONArray();
//	    for ( LogTableField field : channelLogTable.getFields() ) {
//	    	JSONObject jsonField = new JSONObject();
//	    	jsonField.put("id", field.getId());
//	    	jsonField.put("enabled", field.isEnabled());
//	    	jsonField.put("name", field.getFieldName());
//	    	jsonField.put("description", StringEscapeHelper.encode(field.getDescription()));
//	    	fields.add(jsonField);
//	    }
//	    jsonObject.put("fields", fields);
//	    e.setAttribute("channelLogTable", jsonObject.toString());

		return e;
	}

	public void encodeDatabases(Element e, AbstractMeta meta) {
		Props props = null;
		if (Props.isInitialized()) {
			props = Props.getInstance();
		}

		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < meta.nrDatabases(); i++) {
			DatabaseMeta dbMeta = meta.getDatabase(i);
			if (props != null && props.areOnlyUsedConnectionsSavedToXML()) {
				if (isDatabaseConnectionUsed(meta, dbMeta)) {
					jsonArray.add(dbMeta.getName());

					try {
						Repository repository = App.getInstance().getRepository();
						ObjectId id_database = repository.getDatabaseID(dbMeta.getName());
						if(id_database == null) {
							repository.save(dbMeta, "add private database", null);
						}
					} catch (KettleException e1) {
						e1.printStackTrace();
					}
				}
			} else {
				jsonArray.add(dbMeta.getName());
			}
		}

		e.setAttribute("databases", jsonArray.toString());
	}

	public void encodeSlaveServers(Element e, AbstractMeta meta) {
		JSONArray jsonArray = new JSONArray();
		for (int i = 0; i < meta.getSlaveServers().size(); i++) {
			SlaveServer slaveServer = meta.getSlaveServers().get(i);
			jsonArray.add(SlaveServerCodec.encode(slaveServer));
		}
		e.setAttribute("slaveServers", jsonArray.toString());
	}

	public void encodeNote(Document doc, mxGraph graph, AbstractMeta meta) {
		if (meta.getNotes() != null) {
			for (NotePadMeta ni : meta.getNotes()) {
				Point location = ni.getLocation();

				Element note = doc.createElement(PropsUI.NOTEPAD);
				note.setAttribute("label", StringEscapeHelper.encode(ni.getNote()));
				String style = NotePadCodec.encodeStyle(ni);

				graph.insertVertex(graph.getDefaultParent(), null, note, location.x, location.y, ni.width + Const.NOTE_MARGIN * 2, ni.height + Const.NOTE_MARGIN * 2, style);
			}
		}
	}

	public void decodeCommRootAttr(mxCell root, AbstractMeta meta) throws Exception {
		Repository repository = App.getInstance().getRepository();
		meta.setRepository(repository);
		meta.setMetaStore(App.getInstance().getMetaStore());

		meta.setName(root.getAttribute("name"));
		if(repository == null) {
			meta.setFilename(root.getAttribute("fileName"));
		} else {
			String directory = root.getAttribute("directory");
			RepositoryDirectoryInterface path = repository.findDirectory(directory);
			if(path == null)
				path = new RepositoryDirectory();
			meta.setRepositoryDirectory(path);

			if(repository instanceof KettleFileRepository) {
				KettleFileRepository ktr = (KettleFileRepository) repository;
				ObjectId fileId = ktr.getTransformationID(root.getAttribute("name"), path);
				if(fileId == null)
					fileId = ktr.getJobId(root.getAttribute("name"), path);
				String realPath = ktr.calcFilename(fileId);
				meta.setFilename(realPath);
			}
		}
		meta.setDescription(root.getAttribute("description"));
		meta.setExtendedDescription(root.getAttribute("extended_description"));
		meta.setSharedObjectsFile(root.getAttribute("shared_objects_file"));

		// Read the named parameters.
		JSONArray namedParameters = JSONArray.fromObject(root.getAttribute("parameters"));
		for (int i = 0; i < namedParameters.size(); i++) {
			JSONObject jsonObject = namedParameters.getJSONObject(i);

			String paramName = jsonObject.optString("name");
			String defaultValue = jsonObject.optString("default_value");
			String descr = jsonObject.optString("description");

			meta.addParameterDefinition(paramName, defaultValue, descr);
		}

		meta.setCreatedUser( root.getAttribute( "created_user" ));
		meta.setCreatedDate(XMLHandler.stringToDate( root.getAttribute( "created_date" ) ));
		meta.setModifiedUser(root.getAttribute( "modified_user" ));
		meta.setModifiedDate(XMLHandler.stringToDate( root.getAttribute( "modified_date" ) ));

		JSONObject jsonObject = JSONObject.fromObject(root.getAttribute("channelLogTable"));
		ChannelLogTable channelLogTable = meta.getChannelLogTable();
		channelLogTable.setConnectionName(jsonObject.optString("connection"));
		channelLogTable.setSchemaName(jsonObject.optString("schema"));
		channelLogTable.setTableName(jsonObject.optString("table"));
		channelLogTable.setTimeoutInDays(jsonObject.optString("timeout_days"));
		JSONArray jsonArray = jsonObject.optJSONArray("fields");
		if(jsonArray != null) {
			for ( int i = 0; i < jsonArray.size(); i++ ) {
		    	JSONObject fieldJson = jsonArray.getJSONObject(i);
		    	String id = fieldJson.optString("id");
		    	LogTableField field = channelLogTable.findField( id );
		    	if ( field == null && i<channelLogTable.getFields().size()) {
		    		field = channelLogTable.getFields().get(i);
		    	}
				if (field != null) {
					field.setFieldName(fieldJson.optString("name"));
					field.setEnabled(fieldJson.optBoolean("enabled"));
				}
			}
		}
	}

	public void decodeNote(mxGraph graph, AbstractMeta meta) {
		int count = graph.getModel().getChildCount(graph.getDefaultParent());
		for(int i=0; i<count; i++) {
			mxCell cell = (mxCell) graph.getModel().getChildAt(graph.getDefaultParent(), i);
			if(cell.isVertex()) {
				Element e = (Element) cell.getValue();
				if(PropsUI.NOTEPAD.equals(e.getTagName())) {
					meta.getNotes().add(NotePadCodec.decode(graph, cell));
				}
			}
		}
	}

	public void decodeDatabases(mxCell root, AbstractMeta meta) throws Exception {
		JSONArray jsonArray = JSONArray.fromObject(root.getAttribute("databases"));
		Set<String> privateTransformationDatabases = new HashSet<String>(jsonArray.size());
		Repository repository = App.getInstance().getRepository();
		for (int i = 0; i < jsonArray.size(); i++) {
			String name = jsonArray.getString(i);
			ObjectId id_database = repository.getDatabaseID(name);
			DatabaseMeta dbcon = repository.loadDatabaseMeta(id_database, null);

//			JSONObject jsonObject = jsonArray.getJSONObject(i);
//			DatabaseMeta dbcon =  DatabaseCodec.decode(jsonObject);
//
			dbcon.shareVariablesWith(meta);
			if (!dbcon.isShared()) {
				privateTransformationDatabases.add(dbcon.getName());
			}

			DatabaseMeta exist = meta.findDatabase(dbcon.getName());
			if (exist == null) {
				meta.addDatabase(dbcon);
			} else {
				if (!exist.isShared()) {
					int idx = meta.indexOfDatabase(exist);
					meta.removeDatabase(idx);
					meta.addDatabase(idx, dbcon);
				}
			}
		}
		meta.setPrivateDatabases(privateTransformationDatabases);
	}

	public void decodeSlaveServers(mxCell root, AbstractMeta meta) throws Exception {
		JSONArray jsonArray = JSONArray.fromObject(root.getAttribute("slaveServers"));
		for (int i = 0; i < jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			SlaveServer slaveServer = SlaveServerCodec.decode(jsonObject);
			slaveServer.shareVariablesWith(meta);

			SlaveServer check = meta.findSlaveServer(slaveServer.getName());
			if (check != null) {
				if (!check.isShared()) {
					meta.addOrReplaceSlaveServer(slaveServer);
				}
			} else {
				meta.getSlaveServers().add(slaveServer);
			}
		}
	}

	public abstract boolean isDatabaseConnectionUsed(AbstractMeta meta, DatabaseMeta databaseMeta);

}
