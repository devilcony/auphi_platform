package com.aofei.kettle.trans.steps;

import java.util.List;

import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import com.aofei.kettle.utils.JSONArray;
import com.aofei.kettle.utils.JSONObject;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.mail.MailMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

@Component("Mail")
@Scope("prototype")
public class Mail extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		MailMeta mailMeta = (MailMeta) stepMetaInterface;

		//address
		mailMeta.setDestination(cell.getAttribute("destination"));
		mailMeta.setDestinationCc(cell.getAttribute("destinationCc"));
		mailMeta.setDestinationBCc(cell.getAttribute("destinationBCc"));

		mailMeta.setReplyName(cell.getAttribute("replytoname"));
		mailMeta.setReplyAddress(cell.getAttribute("replyto"));

		mailMeta.setReplyToAddresses(cell.getAttribute("replyToAddresses"));
		mailMeta.setContactPerson(cell.getAttribute("contact_person"));
		mailMeta.setContactPhone(cell.getAttribute("contact_phone"));

		//mailserver
		mailMeta.setServer(cell.getAttribute("server"));
		mailMeta.setPort(cell.getAttribute("port"));

		mailMeta.setUsingAuthentication("Y".equalsIgnoreCase(cell.getAttribute("use_auth")));
		mailMeta.setAuthenticationUser(cell.getAttribute("auth_user"));
		mailMeta.setAuthenticationPassword(Encr.encryptPasswordIfNotUsingVariables(cell.getAttribute("auth_password")));
		mailMeta.setUsingSecureAuthentication("Y".equalsIgnoreCase(cell.getAttribute("use_secure_auth")));
		mailMeta.setSecureConnectionType(cell.getAttribute("secureconnectiontype"));

		//mail content
		mailMeta.setIncludeDate("Y".equalsIgnoreCase(cell.getAttribute("include_date")));
		mailMeta.setOnlySendComment("Y".equalsIgnoreCase(cell.getAttribute("only_comment")));
		mailMeta.setUseHTML("Y".equalsIgnoreCase(cell.getAttribute("use_HTML")));
		mailMeta.setEncoding(cell.getAttribute("encoding"));
		mailMeta.setUsePriority("Y".equalsIgnoreCase(cell.getAttribute("use_Priority")));
		mailMeta.setPriority(cell.getAttribute("priority"));
		mailMeta.setImportance(cell.getAttribute("importance"));
		mailMeta.setSensitivity(cell.getAttribute("sensitivity"));

		mailMeta.setSubject(cell.getAttribute("subject"));
		mailMeta.setComment(cell.getAttribute("comment"));

		//attachment
		mailMeta.setAttachContentFromField("Y".equalsIgnoreCase(cell.getAttribute("attachContentFromField")));
		mailMeta.setAttachContentField(cell.getAttribute("attachContentField"));
		mailMeta.setAttachContentFileNameField(cell.getAttribute("attachContentFileNameField"));

		mailMeta.setZipFilenameDynamic("Y".equalsIgnoreCase(cell.getAttribute("isFilenameDynamic")));
		mailMeta.setDynamicFieldname(cell.getAttribute("dynamicFieldname"));
		mailMeta.setDynamicWildcard(cell.getAttribute("dynamicWildcard"));
		mailMeta.setSourceFileFoldername(cell.getAttribute("sourcefilefoldername"));
		mailMeta.setIncludeSubFolders("Y".equalsIgnoreCase(cell.getAttribute("include_subfolders")));
		mailMeta.setSourceWildcard(cell.getAttribute("sourcewildcard"));

		mailMeta.setZipFiles("Y".equalsIgnoreCase(cell.getAttribute("zip_files")));
		mailMeta.setZipFilenameDynamic("Y".equalsIgnoreCase(cell.getAttribute("zipFilenameDynamic")));
		mailMeta.setDynamicZipFilenameField(cell.getAttribute("dynamicZipFilename"));
		mailMeta.setZipFilename(cell.getAttribute("zip_name"));
		mailMeta.setZipLimitSize(cell.getAttribute("zip_limit_size"));

		//images
		String embeddedimages = cell.getAttribute("embeddedimages");
		JSONArray jsonArray = JSONArray.fromObject(embeddedimages);
		mailMeta.allocate(jsonArray.size());

		String[] embeddedImages = new String[jsonArray.size()];
		String[] contentIds = new String[jsonArray.size()];
		for(int i=0; i<jsonArray.size(); i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			embeddedImages[i] = jsonObject.optString("image_name");
			contentIds[i] = jsonObject.optString("content_id");
		}
		mailMeta.setEmbeddedImages(embeddedImages);
		mailMeta.setContentIds(contentIds);

	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		MailMeta mailMeta = (MailMeta) stepMetaInterface;

		// address
		e.setAttribute("destination", mailMeta.getDestination());
		e.setAttribute("destinationCc", mailMeta.getDestinationCc());
		e.setAttribute("destinationBCc", mailMeta.getDestinationBCc());

		e.setAttribute("replytoname", mailMeta.getReplyName());
		e.setAttribute("replyto", mailMeta.getReplyAddress());

		e.setAttribute("replyToAddresses", mailMeta.getReplyToAddresses());
		e.setAttribute("contact_person", mailMeta.getContactPerson());
		e.setAttribute("contact_phone", mailMeta.getContactPhone());

		//mailserver
		e.setAttribute("server", mailMeta.getServer());
		e.setAttribute("port", mailMeta.getPort());

		e.setAttribute("use_auth", mailMeta.isUsingAuthentication() ? "Y" : "N");
		e.setAttribute("auth_user", mailMeta.getAuthenticationUser());
		e.setAttribute("auth_password", Encr.decryptPasswordOptionallyEncrypted(mailMeta.getAuthenticationPassword()));
		e.setAttribute("use_secure_auth", mailMeta.isUsingSecureAuthentication() ? "Y" : "N");
		e.setAttribute("secureconnectiontype", mailMeta.getSecureConnectionType());

		//mail content
		e.setAttribute("include_date", mailMeta.getIncludeDate() ? "Y" : "N");
		e.setAttribute("only_comment", mailMeta.isOnlySendComment() ? "Y" : "N");
		e.setAttribute("use_HTML", mailMeta.isUseHTML() ? "Y" : "N");
		e.setAttribute("encoding", mailMeta.getEncoding());
		e.setAttribute("use_Priority", mailMeta.isUsePriority() ? "Y" : "N");
		e.setAttribute("priority", mailMeta.getPriority());
		e.setAttribute("importance", mailMeta.getImportance());
		e.setAttribute("sensitivity", mailMeta.getSensitivity());

		e.setAttribute("subject", mailMeta.getSubject());
		e.setAttribute("comment", mailMeta.getComment());

		//attachment
		e.setAttribute("attachContentFromField", mailMeta.isAttachContentFromField() ? "Y" : "N");
		e.setAttribute("attachContentField", mailMeta.getAttachContentField());
		e.setAttribute("attachContentFileNameField", mailMeta.getAttachContentFileNameField());

		e.setAttribute("isFilenameDynamic", mailMeta.isDynamicFilename() ? "Y" : "N");
		e.setAttribute("dynamicFieldname", mailMeta.getDynamicFieldname());
		e.setAttribute("dynamicWildcard", mailMeta.getDynamicWildcard());
		e.setAttribute("sourcefilefoldername", mailMeta.getSourceFileFoldername());
		e.setAttribute("include_subfolders", mailMeta.isIncludeSubFolders() ? "Y" : "N");
		e.setAttribute("sourcewildcard", mailMeta.getSourceWildcard());

		e.setAttribute("zip_files", mailMeta.isZipFiles() ? "Y" : "N");
		e.setAttribute("zipFilenameDynamic", mailMeta.isZipFilenameDynamic() ? "Y" : "N");
		e.setAttribute("dynamicZipFilename", mailMeta.getDynamicZipFilenameField());
		e.setAttribute("zip_name", mailMeta.getZipFilename());
		e.setAttribute("zip_limit_size", mailMeta.getZipLimitSize());


		//images
		JSONArray jsonArray = new JSONArray();
		String[] embeddedImages = mailMeta.getEmbeddedImages();
		String[] contentIds = mailMeta.getContentIds();
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
