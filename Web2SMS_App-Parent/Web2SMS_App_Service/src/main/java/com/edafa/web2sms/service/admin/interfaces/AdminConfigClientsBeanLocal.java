package com.edafa.web2sms.service.admin.interfaces;

import javax.ejb.Local;

import com.edafa.ws.utils.configs.AccManagConfigsManagerService;
import com.edafa.ws.utils.configs.AppConfigsManagerService;
import com.edafa.ws.utils.configs.CoreConfigsManagerService;
import com.edafa.ws.utils.configs.ReportingConfigsManagerService;
import com.edafa.ws.utils.configs.ProvConfigsManagerService;
import com.edafa.ws.utils.configs.SMSAPIConfigsManagerService;
import com.edafa.ws.utils.configs.UIConfigsManagerService;

/**
 *
 * @author loay
 */
@Local
public interface AdminConfigClientsBeanLocal {

	public void readURLs();
	public CoreConfigsManagerService getCoreConfigsManagerService();
	public AppConfigsManagerService getAppConfigsManagerService();
	public UIConfigsManagerService getUiConfigsManagerService();
	public SMSAPIConfigsManagerService getSmsApiConfigsManagerService();
	public AccManagConfigsManagerService getAccountManagementConfigsManagerService();
	public ReportingConfigsManagerService getReportingConfigsManagerService();
	public ProvConfigsManagerService getProvConfigsManagerService();
}
