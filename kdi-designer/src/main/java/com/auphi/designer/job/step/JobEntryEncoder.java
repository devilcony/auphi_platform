package com.auphi.designer.job.step;

import org.pentaho.di.job.entry.JobEntryCopy;
import org.w3c.dom.Element;

public interface JobEntryEncoder {

	public Element encodeStep(JobEntryCopy jobEntry) throws Exception;
	
}
