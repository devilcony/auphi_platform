package com.aofei.kettle.repository.controller;

import java.util.List;
import java.util.Locale;

import com.aofei.kettle.App;
import com.aofei.kettle.repository.beans.DirectoryVO;
import com.aofei.kettle.repository.beans.RepositoryObjectVO;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleSecurityException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.i18n.LanguageChoice;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.ui.core.PropsUI;
import org.springframework.util.StringUtils;

public class Main {

	public static void main(String[] args) throws KettlePluginException, KettleSecurityException, KettleException {
//		LanguageChoice.getInstance().setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
//		KettleLogStore.init(5000, 720);
//		KettleEnvironment.init();
//		com.aofei.kettle.core.PropsUI.getInstance();
//
//		RepositoriesMeta input = new RepositoriesMeta();
//		if (input.readData()) {
//			RepositoryMeta repositoryMeta = input.searchRepository( "wkloc" );
//			if(repositoryMeta != null) {
//				Repository repository = PluginRegistry.getInstance().loadClass(RepositoryPluginType.class, repositoryMeta.getId(), Repository.class );
//			    repository.init( repositoryMeta );
//			    repository.connect( "admin", "admin" );
//
//
//
//			    Props.getInstance().setLastRepository( repositoryMeta.getName() );
//			    Props.getInstance().setLastRepositoryLogin( "admin" );
//			    Props.getInstance().setProperty( PropsUI.STRING_START_SHOW_REPOSITORIES, "N");
//
//			    Props.getInstance().saveProps();
//
//			    RepositoryDirectoryInterface dir = repository.getUserHomeDirectory();
//
//				List<RepositoryDirectoryInterface> directorys = dir.getChildren();
//				for(RepositoryDirectoryInterface child : directorys) {
//					DirectoryVO directory = new DirectoryVO(child);
//				}
//
//				String transPath = dir.getPath();
//				List<RepositoryElementMetaInterface> elements = repository.getTransformationObjects(dir.getObjectId(), false);
//				if(elements != null) {
//					for(RepositoryElementMetaInterface e : elements) {
//						TransMeta transMeta = repository.loadTransformation(e.getName(), dir, null, true, null);
//						RepositoryObjectVO ro = new RepositoryObjectVO(e, transMeta);
//					}
//				}
//
//				elements = repository.getJobObjects(dir.getObjectId(), false);
//				if(elements != null) {
//					for(RepositoryElementMetaInterface e : elements) {
//						JobMeta jobMeta = repository.loadJob(e.getName(), dir, null, null);
//						RepositoryObjectVO ro = new RepositoryObjectVO(e, jobMeta);
//					}
//				}
//
//			    repository.disconnect();
//			}
//		}
	}

}
