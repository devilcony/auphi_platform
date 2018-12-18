package com.aofei.kettle.trans.step;

import com.aofei.base.model.response.CurrentUserResponse;
import org.pentaho.di.trans.step.StepMeta;
import org.w3c.dom.Element;

public interface StepEncoder {

	public Element encodeStep(StepMeta stepMeta, CurrentUserResponse user) throws Exception;

}
