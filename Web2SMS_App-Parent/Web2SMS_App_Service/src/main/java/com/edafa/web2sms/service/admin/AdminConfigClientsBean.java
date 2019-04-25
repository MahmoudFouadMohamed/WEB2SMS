package com.edafa.web2sms.service.admin;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.xml.ws.BindingProvider;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.service.admin.interfaces.AdminConfigClientsBeanLocal;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.ws.utils.configs.AccManagConfigsManagerService;
import com.edafa.ws.utils.configs.AccManagConfigsManagerService_Service;
import com.edafa.ws.utils.configs.AppConfigsManagerService;
import com.edafa.ws.utils.configs.AppConfigsManagerService_Service;
import com.edafa.ws.utils.configs.CoreConfigsManagerService;
import com.edafa.ws.utils.configs.CoreConfigsManagerService_Service;
import com.edafa.ws.utils.configs.ReportingConfigsManagerService;
import com.edafa.ws.utils.configs.ReportingConfigsManagerService_Service;
import com.edafa.ws.utils.configs.ProvConfigsManagerService;
import com.edafa.ws.utils.configs.ProvConfigsManagerService_Service;
import com.edafa.ws.utils.configs.SMSAPIConfigsManagerService;
import com.edafa.ws.utils.configs.SMSAPIConfigsManagerService_Service;
import com.edafa.ws.utils.configs.UIConfigsManagerService;
import com.edafa.ws.utils.configs.UIConfigsManagerService_Service;

/**
 *
 * @author loay
 */
@Singleton
@Startup
public class AdminConfigClientsBean implements AdminConfigClientsBeanLocal {

	Logger appLogger = LogManager.getLogger(LoggersEnum.ADMIN_MNGMT.name());

	CoreConfigsManagerService coreConfigsManagerService;
	AppConfigsManagerService appConfigsManagerService;
	UIConfigsManagerService uiConfigsManagerService;
	// SMSGWConfigsManagerService smsgwConfigsManagerService;
	SMSAPIConfigsManagerService smsApiConfigsManagerService;
	AccManagConfigsManagerService accManagConfigsManagerService;
	ReportingConfigsManagerService reportingConfigsManagerService;
	ProvConfigsManagerService provConfigsManagerService;

	@PostConstruct
	public void init() {
		try {
			appLogger.info("Initalizing ConfigsManagerService ports");
			readURLs();
			appLogger.info("ConfigsManagerService ports initalized successfully");
		} catch (Exception e) {
			appLogger.fatal("Error during initalizing ConfigsManagerService ports", e);
			throw new Error(e);
		}
	}

	@Override
	public void readURLs() {
		String url;
		appLogger.info("Initalizing CoreConfigsManagerService port");
		CoreConfigsManagerService_Service coreConfigsManagerService_service = new CoreConfigsManagerService_Service();
		coreConfigsManagerService = coreConfigsManagerService_service.getCoreConfigsManagerServicePort();
		url = (String) Configs.CORE_CONFIG_WS_URL.getValue();
		initBindingProvider((BindingProvider) coreConfigsManagerService, url);

		appLogger.info("Initalizing AppConfigsManagerService port");
		AppConfigsManagerService_Service appConfigsManagerService_service = new AppConfigsManagerService_Service();
		appConfigsManagerService = appConfigsManagerService_service.getAppConfigsManagerServicePort();
		url = (String) Configs.APP_CONFIG_WS_URL.getValue();
		initBindingProvider((BindingProvider) appConfigsManagerService, url);

		appLogger.info("Initalizing UIConfigsManagerService port");
		UIConfigsManagerService_Service uiConfigsManagerService_service = new UIConfigsManagerService_Service();
		uiConfigsManagerService = uiConfigsManagerService_service.getUIConfigsManagerServicePort();
		url = (String) Configs.UI_CONFIG_WS_URL.getValue();
		initBindingProvider((BindingProvider) uiConfigsManagerService, url);

		appLogger.info("Initalizing smsApiConfigsManagerService port");
		SMSAPIConfigsManagerService_Service smsApiConfigsManagerService_service = new SMSAPIConfigsManagerService_Service();
		smsApiConfigsManagerService = smsApiConfigsManagerService_service.getSMSAPIConfigsManagerServicePort();
		url = (String) Configs.SMSAPI_CONFIG_WS_URL.getValue();
		initBindingProvider((BindingProvider) smsApiConfigsManagerService, url);

		appLogger.info("Initalizing accManagConfigsManagerService port");
		AccManagConfigsManagerService_Service accManagConfigsManagerService_service = new AccManagConfigsManagerService_Service();
		accManagConfigsManagerService = accManagConfigsManagerService_service.getAccManagConfigsManagerServicePort();
		url = (String) Configs.ACCOUNT_MANAGEMENT_CONFIG_WS_URL.getValue();
		initBindingProvider((BindingProvider) accManagConfigsManagerService, url);

                
		appLogger.info("Initalizing provConfigsManagerService port");
		ProvConfigsManagerService_Service provConfigsManagerService_service = new ProvConfigsManagerService_Service();
		provConfigsManagerService = provConfigsManagerService_service.getProvConfigsManagerServicePort();
		url = (String) Configs.PROV_CONFIG_WS_URL.getValue();
		initBindingProvider((BindingProvider) provConfigsManagerService, url);

		// appLogger.info("Initalizing SMSGWConfigsManagerService port");
		// SMSGWConfigsManagerService_Service smsgwConfigsManagerService_service
		// = new SMSGWConfigsManagerService_Service();
		// smsgwConfigsManagerService =
		// smsgwConfigsManagerService_service.getSMSGWConfigsManagerServicePort();
		// url = (String) Configs.SMSGW_CONFIG_WS_URL.getValue();
		// initBindingProvider((BindingProvider) smsgwConfigsManagerService,
		// url);

		appLogger.info("Initalizing accManagConfigsManagerService port");
		ReportingConfigsManagerService_Service reportingConfigsManagerService_Service = new ReportingConfigsManagerService_Service();
		reportingConfigsManagerService = reportingConfigsManagerService_Service.getReportingConfigsManagerServicePort();
		url = (String) Configs.REPORTING_MANAGEMENT_CONFIG_WS_URL.getValue();
		initBindingProvider((BindingProvider) reportingConfigsManagerService, url);
	}

	private void initBindingProvider(BindingProvider bindingProvider, String url) {
		int requestTimeout = (int) Configs.WS_CLIENT_CONNECT_TIMEOUT.getValue();
		int connectTimeout = (int) Configs.WS_CLIENT_REQUEST_TIMEOUT.getValue();
		appLogger.debug("Setting binding properties for " + bindingProvider.getClass().getSimpleName() + " port");
		bindingProvider.getRequestContext().put("com.sun.xml.ws.request.timeout", requestTimeout);
		bindingProvider.getRequestContext().put("com.sun.xml.ws.connect.timeout", connectTimeout);
		bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
		appLogger.debug("Binding properties for " + bindingProvider + ": requestTimeout=" + requestTimeout
				+ ", connectTimeout=" + connectTimeout + ", endpointAddress=\"" + url + "\"");
	}

	@Override
	public CoreConfigsManagerService getCoreConfigsManagerService() {
		if (coreConfigsManagerService == null) {
			readURLs();
		}
		return coreConfigsManagerService;
	}

	@Override
	public AppConfigsManagerService getAppConfigsManagerService() {
		if (appConfigsManagerService == null) {
			readURLs();
		}
		return appConfigsManagerService;
	}

	@Override
	public UIConfigsManagerService getUiConfigsManagerService() {
		if (uiConfigsManagerService == null) {
			readURLs();
		}
		return uiConfigsManagerService;
	}

	@Override
	public SMSAPIConfigsManagerService getSmsApiConfigsManagerService() {
		if (smsApiConfigsManagerService == null) {
			readURLs();
		}
		return smsApiConfigsManagerService;
	}

	@Override
	public AccManagConfigsManagerService getAccountManagementConfigsManagerService() {
		if (accManagConfigsManagerService == null) {
			readURLs();
		}
		return accManagConfigsManagerService;
	}

	@Override
	public ReportingConfigsManagerService getReportingConfigsManagerService() {
		if (reportingConfigsManagerService == null) {
			readURLs();
		}
		return reportingConfigsManagerService;
	}   
	@Override
	public ProvConfigsManagerService getProvConfigsManagerService() {
		if (provConfigsManagerService == null) {
			readURLs();
		}
		return provConfigsManagerService;
	}
}
