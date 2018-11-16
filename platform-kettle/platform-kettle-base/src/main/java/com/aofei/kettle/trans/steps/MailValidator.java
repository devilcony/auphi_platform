package com.aofei.kettle.trans.steps;

import java.util.List;

import com.aofei.kettle.core.PropsUI;
import com.aofei.kettle.trans.step.AbstractStep;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.abort.AbortMeta;
import org.pentaho.di.trans.steps.mailvalidator.MailValidatorMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;

@Component("MailValidator")
@Scope("prototype")
public class MailValidator extends AbstractStep {

	@Override
	public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {
		MailValidatorMeta mailValidatorMeta = (MailValidatorMeta) stepMetaInterface;
		mailValidatorMeta.setEmailField(cell.getAttribute("emailfield"));
		mailValidatorMeta.setSMTPCheck("Y".equalsIgnoreCase(cell.getAttribute("smtpCheck")));
		mailValidatorMeta.setTimeOut(cell.getAttribute("timeout"));
		mailValidatorMeta.setEmailSender(cell.getAttribute("emailSender"));
		mailValidatorMeta.setDefaultSMTP(cell.getAttribute("defaultSMTP"));
		mailValidatorMeta.setDynamicDefaultSMTP("Y".equalsIgnoreCase(cell.getAttribute("isdynamicDefaultSMTP")));
		mailValidatorMeta.setDefaultSMTPField(cell.getAttribute("defaultSMTPField"));

		mailValidatorMeta.setResultFieldName(cell.getAttribute("resultfieldname"));
		mailValidatorMeta.setResultAsString("Y".equalsIgnoreCase(cell.getAttribute("ResultAsString")));
		mailValidatorMeta.setEmailValideMsg(cell.getAttribute("emailValideMsg"));
		mailValidatorMeta.setEmailNotValideMsg(cell.getAttribute("emailNotValideMsg"));
		mailValidatorMeta.setErrorsField(cell.getAttribute("errorsFieldName"));

	}

	@Override
	public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
		Document doc = mxUtils.createDocument();
		Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);
		MailValidatorMeta mailValidatorMeta = (MailValidatorMeta) stepMetaInterface;

		e.setAttribute("emailfield", mailValidatorMeta.getEmailField());
		e.setAttribute("smtpCheck", mailValidatorMeta.isSMTPCheck() ? "Y" : "N");
		e.setAttribute("timeout", mailValidatorMeta.getTimeOut());
		e.setAttribute("emailSender", mailValidatorMeta.getEmailSender());
		e.setAttribute("defaultSMTP", mailValidatorMeta.getDefaultSMTP());
		e.setAttribute("isdynamicDefaultSMTP", mailValidatorMeta.isDynamicDefaultSMTP() ? "Y" : "N");
		e.setAttribute("defaultSMTPField", mailValidatorMeta.getDefaultSMTPField());

		e.setAttribute("resultfieldname", mailValidatorMeta.getResultFieldName());
		e.setAttribute("ResultAsString", mailValidatorMeta.isResultAsString() ? "Y" : "N");
		e.setAttribute("emailValideMsg", mailValidatorMeta.getEmailValideMsg());
		e.setAttribute("emailNotValideMsg", mailValidatorMeta.getEmailNotValideMsg());
		e.setAttribute("errorsFieldName", mailValidatorMeta.getErrorsField());

		return e;
	}

}
