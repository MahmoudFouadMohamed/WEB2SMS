package com.edafa.web2sms.report.web.conf;

import java.util.Set;

import javax.ws.rs.core.Application;

import com.edafa.web2sms.report.web.rest.service.ReportingRestWebServiceBean;

@javax.ws.rs.ApplicationPath("")
public class ApplicationConfig extends Application {

	@Override
	public Set<Class<?>> getClasses() {
		return getRestClasses();
	}

	private Set<Class<?>> getRestClasses() {
		Set<Class<?>> resources = new java.util.HashSet<Class<?>>();

		resources.add(ReportingRestWebServiceBean.class);

		return resources;
	}

}
