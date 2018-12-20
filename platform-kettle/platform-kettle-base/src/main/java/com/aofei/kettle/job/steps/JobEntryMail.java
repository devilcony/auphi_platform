package com.aofei.kettle.job.steps;

import java.util.List;

import org.pentaho.di.core.ResultFile;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.job.step.AbstractJobEntry;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

@Component("MAIL")
@Scope("prototype")
public class JobEntryMail extends AbstractJobEntry {

	@Override
	public void decode(JobEntryInterface jobEntry, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		org.pentaho.di.job.entries.mail.JobEntryMail jem = (org.pentaho.di.job.entries.mail.JobEntryMail) jobEntry;
		
		//address
		jem.setDestination(cell.getAttribute("destination"));
		jem.setDestinationCc(cell.getAttribute("destinationCc"));
		jem.setDestinationBCc(cell.getAttribute("destinationBCc"));
		
		jem.setReplyName(cell.getAttribute("replytoname"));
		jem.setReplyAddress(cell.getAttribute("replyto"));
		
		jem.setReplyToAddresses(cell.getAttribute("replyToAddresses"));
		jem.setContactPerson(cell.getAttribute("contact_person"));
		jem.setContactPhone(cell.getAttribute("contact_phone"));
		
		//mailserver
		jem.setServer(cell.getAttribute("server"));
		jem.setPort(cell.getAttribute("port"));
		
		jem.setUsingAuthentication("Y".equalsIgnoreCase(cell.getAttribute("use_auth")));
		jem.setAuthenticationUser(cell.getAttribute("auth_user"));
		jem.setAuthenticationPassword(Encr.encryptPasswordIfNotUsingVariables(cell.getAttribute("auth_password")));
		jem.setUsingSecureAuthentication("Y".equalsIgnoreCase(cell.getAttribute("use_secure_auth")));
		jem.setSecureConnectionType(cell.getAttribute("secureconnectiontype"));
		
		//mail content
		jem.setIncludeDate("Y".equalsIgnoreCase(cell.getAttribute("include_date")));
		jem.setOnlySendComment("Y".equalsIgnoreCase(cell.getAttribute("only_comment")));
		jem.setUseHTML("Y".equalsIgnoreCase(cell.getAttribute("use_HTML")));
		jem.setEncoding(cell.getAttribute("encoding"));
		jem.setUsePriority("Y".equalsIgnoreCase(cell.getAttribute("use_Priority")));
		jem.setPriority(cell.getAttribute("priority"));
		jem.setImportance(cell.getAttribute("importance"));
		jem.setSensitivity(cell.getAttribute("sensitivity"));
		
		jem.setSubject(cell.getAttribute("subject"));
		jem.setComment(cell.getAttribute("comment"));
		
		//attachment
		jem.setIncludingFiles("Y".equalsIgnoreCase(cell.getAttribute("include_files")));
		JSONArray jsonArray = JSONArray.fromObject(cell.getAttribute("filetypes"));
		int[] fileType = new int[jsonArray.size()];
		for(int i=0; i<jsonArray.size(); i++) {
			fileType[i] = ResultFile.getType(jsonArray.getString(i));
		}
		jem.setFileType(fileType);
		jem.setZipFiles("Y".equalsIgnoreCase(cell.getAttribute("zip_files")));
		jem.setZipFilename(cell.getAttribute("zip_name"));
		
		
		//images
		String embeddedimages = cell.getAttribute("embeddedimages");
		jsonArray = JSONArray.fromObject(embeddedimages);
		
		String[] embeddedImages = new String[jsonArray.size()];
		String[] contentIds = new String[jsonArray.size()];
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			embeddedImages[i] = jsonObject.optString("image_name");
			contentIds[i] = jsonObject.optString("content_id");
		}
		jem.embeddedimages = embeddedImages;
		jem.contentids = contentIds;
	}

	@Override
	public Element encode(JobEntryInterface jobEntry) throws Exception {
		org.pentaho.di.job.entries.mail.JobEntryMail jem = (org.pentaho.di.job.entries.mail.JobEntryMail) jobEntry;
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.JOB_JOBENTRY_NAME);
		
		// address
		e.setAttribute("destination", jem.getDestination());
		e.setAttribute("destinationCc", jem.getDestinationCc());
		e.setAttribute("destinationBCc", jem.getDestinationBCc());
		
		e.setAttribute("replytoname", jem.getReplyName());
		e.setAttribute("replyto", jem.getReplyAddress());
		
		e.setAttribute("replyToAddresses", jem.getReplyToAddresses());
		e.setAttribute("contact_person", jem.getContactPerson());
		e.setAttribute("contact_phone", jem.getContactPhone());
		
		//mailserver
		e.setAttribute("server", jem.getServer());
		e.setAttribute("port", jem.getPort());
		
		e.setAttribute("use_auth", jem.isUsingAuthentication() ? "Y" : "N");
		e.setAttribute("auth_user", jem.getAuthenticationUser());
		e.setAttribute("auth_password", Encr.decryptPasswordOptionallyEncrypted(jem.getAuthenticationPassword()));
		e.setAttribute("use_secure_auth", jem.isUsingSecureAuthentication() ? "Y" : "N");
		e.setAttribute("secureconnectiontype", jem.getSecureConnectionType());
		
		//mail message
		e.setAttribute("include_date", jem.getIncludeDate() ? "Y" : "N");
		e.setAttribute("only_comment", jem.isOnlySendComment() ? "Y" : "N");
		e.setAttribute("use_HTML", jem.isUseHTML() ? "Y" : "N");
		e.setAttribute("encoding", jem.getEncoding());
		e.setAttribute("use_Priority", jem.isUsePriority() ? "Y" : "N");
		e.setAttribute("priority", jem.getPriority());
		e.setAttribute("importance", jem.getImportance());
		e.setAttribute("sensitivity", jem.getSensitivity());
		
		e.setAttribute("subject", jem.getSubject());
		e.setAttribute("comment", jem.getComment());
		
		//attachment
		e.setAttribute("include_files", jem.isIncludingFiles() ? "Y" : "N");
		
		JSONArray jsonArray = new JSONArray();
		int[] fileTypes = jem.getFileType();
		for(int fileType : fileTypes)
			jsonArray.add(ResultFile.getTypeCode(fileType));
		e.setAttribute("filetypes", jsonArray.toString());
		e.setAttribute("zip_files", jem.isZipFiles() ? "Y" : "N");
		e.setAttribute("zip_name", jem.getZipFilename());
		
		jsonArray = new JSONArray();
		String[] embeddedImages = jem.embeddedimages;
		String[] contentIds = jem.contentids;
		if(embeddedImages != null) {
			for(int j=0; j<embeddedImages.length; j++) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("image_name", embeddedImages[j]);
				jsonObject.put("content_id", contentIds[j]);
				jsonArray.add(jsonObject);
			}
		}
		e.setAttribute("embeddedimages", jsonArray.toString());
		
		return e;
	}

}
