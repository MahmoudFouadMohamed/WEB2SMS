package com.edafa.web2sms.service.api.sms.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountSMSAPI;
import com.edafa.web2sms.service.api.sms.model.ActivateSMSAPIRequest;
import com.edafa.web2sms.service.model.AdminTrxInfo;

@Local
public interface SMSAPIManagementBeanLocal {
	void activateSMSAPI(AdminTrxInfo trxInfo, ActivateSMSAPIRequest request)
			throws Exception;

	void deactivateSMSAPI(AdminTrxInfo trxInfo, String accountId)
			throws DBException;

	List<Account> getAccountListPaginated(AdminTrxInfo trxInfo, int first,
			int max);

	int getAccountsCount(AdminTrxInfo trxInfo) throws DBException;

	void editSmsApiInfo(AdminTrxInfo trxInfo, AccountSMSAPI accountSMSAPI)
			throws Exception;

	String regenerateSecureKey(AdminTrxInfo trxInfo, String accountId) throws Exception;

	void resetPassword(AdminTrxInfo trxInfo, String accountId,
			String newPassword) throws Exception;

	void EditIpsList(List<String> IPs, String accountId, AdminTrxInfo trxInfo)
			throws Exception;
	
	 String getCurrentSecureKey(AdminTrxInfo trxInfo,
			String accountId) throws Exception;

}
