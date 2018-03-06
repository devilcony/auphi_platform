package com.auphi.designer.trans.steps;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxUtils;
import com.auphi.designer.core.PropsUI;
import com.auphi.designer.trans.step.AbstractStep;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.excelinput.ExcelInputMeta;
import org.pentaho.metastore.api.IMetaStore;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Created by Tony on 16/7/11.
 */
@Component("ExcelInput")
@Scope("prototype")
public class ExcelInput extends AbstractStep {


    @Override
    public void decode(StepMetaInterface stepMetaInterface, mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception {

        ExcelInputMeta excelInputMeta = (ExcelInputMeta)stepMetaInterface;


    }

    @Override
    public Element encode(StepMetaInterface stepMetaInterface) throws Exception {
        int i;
        ExcelInputMeta meta = (ExcelInputMeta)stepMetaInterface;
        Document doc = mxUtils.createDocument();
        Element e = doc.createElement(PropsUI.TRANS_STEP_NAME);

        for(i = 0; i < meta.getFileName().length; ++i) {

        }

        return e;
    }
}
