package com.aofei.kettle;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class PluginFactory implements ApplicationContextAware {
	private static ApplicationContext context = null;

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		context = ctx;
	}

	public static Object getBean(String beanId) {
		return context.getBean(beanId);
	}

	public static boolean containBean(String beanId) {
		return context.containsBean(beanId);
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}

}
