package com.edafa.web2sms.ui.util;

import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.xml.ws.BindingProvider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.service.report.ReportManagementService;
import com.edafa.web2sms.service.report.ReportManagementService_Service;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.configs.enums.ModulesEnum;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsListener;
import com.edafa.web2sms.utils.configs.interfaces.ConfigsManagerBeanLocal;

@Singleton
@LocalBean
@Startup
public class WSClients implements ConfigsListener {

	Logger appLogger = LogManager.getLogger(LoggersEnum.ADMIN_UI.name());

	@EJB
	ConfigsManagerBeanLocal configsManagerBean;

	ReportManagementService_Service reportManagementService;
	// ReportManagementService reportServicePort;

	ThreadLocal<ReportManagementService> reportManagementServicePorts;

	private void initBindingProvider(BindingProvider bindingProvider, String url) {
		int requestTimeout = (int) Configs.WS_REQUEST_TIMEOUT.getValue();
		int connectTimeout = (int) Configs.WS_CONNECT_TIMEOUT.getValue();
		appLogger.debug("Setting binding properties for " + bindingProvider.getClass().getSimpleName() + " port");
		bindingProvider.getRequestContext().put("com.sun.xml.ws.request.timeout", requestTimeout);
		bindingProvider.getRequestContext().put("com.sun.xml.ws.connect.timeout", connectTimeout);
		bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
		appLogger.debug("Binding properties for " + bindingProvider.getClass().getName() + ": requestTimeout="
				+ requestTimeout + ", connectTimeout=" + connectTimeout + ", endpointAddress=\"" + url + "\"");
	}

	public WSClients() {

	}

	@PostConstruct
	public void init() {
		loadURLs();
		configsManagerBean.registerConfigsListener(ModulesEnum.AdminManagement, this);

	}

	public ReportManagementService getReportServicePort() {
		return reportManagementServicePorts.get();
	}

	@Override
	public void configurationRefreshed(ModulesEnum module) {
		appLogger.info("Configuration refreshed for module " + module + ", will reinitalize web service clients");
		loadURLs();
		appLogger.info("Web service clients reinitalized");
	}

	@Override
	public void configurationRefreshed() {
		appLogger.info("Configuration refreshed, will reinitalize web service clients");
		loadURLs();
		appLogger.info("Web service clients reinitalized");
	}

	@Lock(LockType.WRITE)
	private void loadURLs() {
		try {
			reportManagementServicePorts = new ThreadLocal<ReportManagementService>() {

				AtomicInteger instCount = new AtomicInteger();

				@Override
				protected ReportManagementService initialValue() {
					String reportServiceURL = (String) Configs.REPORT_WEBSERVICE_URL.getValue();
					if (reportManagementService == null) {
						appLogger.info("Initializing ReportManagementService client");
						reportManagementService = new ReportManagementService_Service();
					}
					appLogger.info("Getting ReportManagementService port");
					ReportManagementService reportServicePort = reportManagementService
							.getReportManagementServicePort();
					initBindingProvider((BindingProvider) reportServicePort, reportServiceURL);
					appLogger.info("ReportManagementService port is intialized with url " + reportServiceURL
							+ ", instances count=" + instCount.incrementAndGet());

					return reportServicePort;
				}

				@Override
				public void remove() {
					super.remove();
					appLogger.trace("ReportManagementService port instance count=" + instCount.decrementAndGet());
				};
			};
		} catch (Exception e) {
			appLogger.error("Error during refresh reportManagementServicePorts : " + e.getMessage(), e);
		}
	}
}
