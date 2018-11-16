package com.aofei.kettle.job.step;

import org.pentaho.di.job.entry.JobEntryCopy;
import org.w3c.dom.Element;

public interface JobEntryEncoder {

	public Element encodeStep(JobEntryCopy jobEntry) throws Exception;

}
