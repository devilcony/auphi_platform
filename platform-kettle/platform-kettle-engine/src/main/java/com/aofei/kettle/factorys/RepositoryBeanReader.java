package com.aofei.kettle.factorys;

import com.aofei.kettle.App;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.i18n.LanguageChoice;
import org.pentaho.di.repository.Repository;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

@Component
public class RepositoryBeanReader implements ApplicationContextAware {


	public void setDefaultRepository(Repository repository) {
		App.getInstance().selectRepository(repository);
	}


	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		Map<String, Repository> repositories = context.getBeansOfType(Repository.class);



		if(repositories != null) {

			for(Repository repository : repositories.values()) {
				App.getInstance().addRepository(repository);
			}

			if(repositories.size() == 1) {
				App.getInstance().selectRepository(repositories.values().iterator().next());
			}
		}


	}

	static {
		try {
			LanguageChoice.getInstance().setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
			KettleLogStore.init( 5000, 720 );
			KettleEnvironment.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
