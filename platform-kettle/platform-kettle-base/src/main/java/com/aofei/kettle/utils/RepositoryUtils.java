package com.aofei.kettle.utils;

import com.aofei.kettle.App;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.TransMeta;

public class RepositoryUtils {

	public static TransMeta readTrans(String name) throws KettleException {
		String dir = name.substring(0, name.lastIndexOf("/"));

		String transname = name.substring(name.lastIndexOf("/") + 1);
		if(transname.endsWith(".ktr"))
			transname = transname.substring(0, transname.length() - 4);

		Repository repository = App.getInstance().getRepository();
		RepositoryDirectoryInterface directory = repository.findDirectory(dir);
		if(directory == null)
			directory = repository.getUserHomeDirectory();

		return App.getInstance().getRepository().loadTransformation(transname, directory, null, true, null);
	}

	public static JobMeta readJob(String name) throws KettleException {
		String dir = name.substring(0, name.lastIndexOf("/"));

		String jobname = name.substring(name.lastIndexOf("/") + 1);
		if(jobname.endsWith(".kjb"))
			jobname = jobname.substring(0, jobname.length() - 4);

		Repository repository = App.getInstance().getRepository();
		RepositoryDirectoryInterface directory = repository.findDirectory(dir);
		if(directory == null)
			directory = repository.getUserHomeDirectory();

		return App.getInstance().getRepository().loadJob(jobname, directory, null, null);
	}

}
