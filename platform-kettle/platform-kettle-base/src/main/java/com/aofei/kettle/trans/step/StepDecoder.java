package com.aofei.kettle.trans.step;

import java.util.List;

import com.aofei.base.model.response.CurrentUserResponse;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.metastore.api.IMetaStore;

import com.mxgraph.model.mxCell;

public interface StepDecoder {

	public StepMeta decodeStep(mxCell cell, List<DatabaseMeta> databases, IMetaStore metaStore) throws Exception;

}
