package com.edafa.web2sms.service.api.sms;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import com.edafa.web2sms.dalayer.dao.interfaces.AccountDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountIPDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountSMSAPIDaoLocal;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountIP;
import com.edafa.web2sms.dalayer.model.AccountSMSAPI;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.SMSLog;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.service.api.sms.exceptions.InvalidAccountSMSApiPasswordException;
import com.edafa.web2sms.service.api.sms.exceptions.InvalidIpsListException;
import com.edafa.web2sms.service.api.sms.interfaces.SMSAPIManagementBeanLocal;
import com.edafa.web2sms.service.api.sms.model.ActivateSMSAPIRequest;
import com.edafa.web2sms.service.api.sms.utils.interfaces.SecureKeyLocal;
import com.edafa.web2sms.service.campaign.exception.CampaignNotFoundException;
import com.edafa.web2sms.service.exception.NoLogsForCampaignException;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.model.UserTrxInfo;
import com.edafa.web2sms.utils.FileNameUtils;
import com.edafa.web2sms.utils.configs.enums.AppSettings;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.security.AESLocal;
import com.edafa.web2sms.utils.security.interfaces.HashUtilsLocal;

@Stateless
public class SMSAPIManagementBean implements SMSAPIManagementBeanLocal {

	@EJB
	AccountDaoLocal accountDao;

	@EJB
	AccountSMSAPIDaoLocal accountSMSAPIDao;

	@EJB
	AccountIPDaoLocal accountIPDao;

	@EJB
	SecureKeyLocal secureKey;

	@EJB
	AESLocal aes;

	@EJB
	HashUtilsLocal hu;

	private Logger smsLogger = LogManager.getLogger(LoggersEnum.SMS_API_MNGT.name());
	private Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());

	int minPasswordLength = (int) Configs.MIN_PASSWORD_LENGTH.getValue();

	public void activateSMSAPI(AdminTrxInfo trxInfo, ActivateSMSAPIRequest request) throws Exception {

		smsLogger.info(trxInfo.logInfo() + request);

		activateValidation(trxInfo, request);
		AccountSMSAPI acctSMS = new AccountSMSAPI();
		Account acct = accountDao.findByAccountId(request.getAccountId());
		if (acct != null)
			acctSMS.setAccount(acct);
		else {
			smsLogger.debug(trxInfo.logId() + "account not found ");
			throw new AccountNotFoundException(request.getAccountId());
		}
		String password;
		password = hu.hashWord(request.getPassword());
		acctSMS.setPassword(password);
		String acctKey = secureKey.generateSecureKey();
		acctKey = aes.encrypt(acctKey);
		acctSMS.setSecureKey(acctKey);

		acctSMS.setAccountIPs(new ArrayList<AccountIP>());

		for (String ip : request.getIPs()) {
			AccountIP accountIP = new AccountIP();
			accountIP.setAcctSmsApiId(acctSMS);
			accountIP.setClientIp(ip);

			acctSMS.getAccountIPs().add(accountIP);
		}// end for

		accountSMSAPIDao.create(acctSMS);

		smsLogger.info(trxInfo.logInfo() + "Activation success");

	}// end of activate method

	public void deactivateSMSAPI(AdminTrxInfo trxInfo, String accountId) throws DBException {

		smsLogger.info(trxInfo.logInfo() + "Deactivate SMS API service for account (" + accountId
				+ "), getting account info.");
		Account acct = accountDao.findByAccountId(accountId);
		smsLogger.debug(trxInfo.logId() + acct + " found.");
		AccountSMSAPI acctSMS = acct.getAccountSmsApi();

		if (acctSMS != null) {
			smsLogger.debug(trxInfo.logId() + "Deactivating SMS API service...");
			accountSMSAPIDao.remove(acctSMS);

			smsLogger.info(trxInfo.logId() + "SMS API service deactivated successfully.");
		} else {
			smsLogger.error("Account Already doesn't activate SMS API service.");
		}
	}// end of deactivate method

	private void activateValidation(AdminTrxInfo trxInfo, ActivateSMSAPIRequest request) throws Exception {
		smsLogger.info(trxInfo.logInfo() + "activation Request validation for account id" + request.getAccountId());

		if (accountDao.count(request.getAccountId()) <= 0) {
			smsLogger.debug(trxInfo.logId() + "account not found");
			throw new AccountNotFoundException(request.getAccountId());
		}// end if

		if (request.getPassword().length() < minPasswordLength) {
			smsLogger.debug(trxInfo.logId() + "invalid password");
			throw new InvalidAccountSMSApiPasswordException(request.getPassword(), minPasswordLength);
		}// end if

		if (request.getIPs() == null || request.getIPs().isEmpty()) {
			smsLogger.debug(trxInfo.logId() + "empty list of IPs");
			throw new InvalidIpsListException();
		}// end if

		smsLogger.info(trxInfo.logInfo() + "activation Request validation for account id" + request.getAccountId()
				+ "is finished");
	}

	@Override
	public List<Account> getAccountListPaginated(AdminTrxInfo trxInfo, int first, int max) {
		smsLogger.info(trxInfo.logInfo() + "getAccountListPaginated from " + first + " to : " + max);
		// Should be changed to accountDao
		return accountSMSAPIDao.getAccountListPaginated(first, max);
	}

	@Override
	public int getAccountsCount(AdminTrxInfo trxInfo) throws DBException {
		smsLogger.info(trxInfo.logInfo() + "getAccountsCount ");
		return accountDao.count();
	}

	@Override
	public void editSmsApiInfo(AdminTrxInfo trxInfo, AccountSMSAPI accountSMSAPI) throws Exception {

		if (accountSMSAPI.getPassword().length() <= minPasswordLength) {
			smsLogger.debug(trxInfo.logId() + "invalid password");
			throw new InvalidAccountSMSApiPasswordException(accountSMSAPI.getPassword(), minPasswordLength);
		}
		if (accountSMSAPI.getAccountIPs() == null) {
			smsLogger.debug(trxInfo.logId() + "empty list of IPs");
			throw new InvalidIpsListException();
		}

		smsLogger.info(trxInfo.logInfo() + " start editing SMS API account ");

		accountSMSAPIDao.editSmsApiInfo(accountSMSAPI);

		smsLogger.info(trxInfo.logInfo() + " editing SMS API account is done ");

	}

	@Override
	public String regenerateSecureKey(AdminTrxInfo trxInfo, String accountId) throws Exception {
		smsLogger.info(trxInfo.logInfo() + "regenerate Secure Key for account id" + accountId);

		AccountSMSAPI smsAccount = accountSMSAPIDao.findByAccountId(accountId);
		if (smsAccount != null) {
			try {
				String acctKey = secureKey.generateSecureKey();
				String encyrptedAccKey;
				smsLogger.debug(trxInfo.logId() + "acctKey is generated");

				encyrptedAccKey = aes.encrypt(acctKey);
				smsAccount.setSecureKey(encyrptedAccKey);
				accountSMSAPIDao.editSmsApiInfo(smsAccount);
				return acctKey;
			} catch (Exception e) {
//				e.printStackTrace();
				appLogger.error(trxInfo.logId() + "cann't encyrpt secure key" , e);

				smsLogger.debug(trxInfo.logId() + "cann't encyrpt secure key");

			}
		} else {

			smsLogger.debug(trxInfo.logId() + "account with id " + accountId + "not found");
			throw new AccountNotFoundException();
		}
		return null;
	}

	@Override
	public void resetPassword(AdminTrxInfo trxInfo, String accountId, String newPassword) throws Exception {
		smsLogger.info(trxInfo.logInfo() + "reset password for account id + " + accountId);
		AccountSMSAPI acctSMS = new AccountSMSAPI();
		Account acct = accountDao.findByAccountId(accountId);
		if (acct != null) {
			acctSMS.setAccount(acct);
			acctSMS = acct.getAccountSmsApi();
		} else {
			smsLogger.debug(trxInfo.logId() + "account not found ");
			throw new AccountNotFoundException(accountId);
		}

		if (newPassword == null || newPassword.length() < minPasswordLength)
			throw new InvalidAccountSMSApiPasswordException(newPassword, minPasswordLength);

		acctSMS.setPassword(hu.hashWord(newPassword));
		accountSMSAPIDao.editSmsApiInfo(acctSMS);
		smsLogger.info(trxInfo.logInfo() + "resetting password for account id + " + accountId + "is done..");
	}

	public void EditIpsList(List<String> ipList, String accountId, AdminTrxInfo trxInfo) throws Exception {
		smsLogger.info(trxInfo.logInfo() + "Edit list of Ips for account id:  " + accountId);
		AccountSMSAPI acctSMS = new AccountSMSAPI();
		Account acct = accountDao.findByAccountId(accountId);
		if (acct != null) {
			acctSMS = acct.getAccountSmsApi();
			acctSMS.setAccount(acct);
		}

		else {
			smsLogger.debug(trxInfo.logId() + "account not found ");
			throw new AccountNotFoundException(accountId);
		}

		if (ipList == null || ipList.size() == 0)
			throw new InvalidIpsListException();

		for (int i = 0; i < acctSMS.getAccountIPs().size(); i++) {
			accountIPDao.remove(acctSMS.getAccountIPs().get(i));
		}
		acctSMS.setAccountIPs(new Vector<AccountIP>());

		accountSMSAPIDao.editSmsApiInfo(acctSMS);
		// accountDao.edit(acct);

		Vector<AccountIP> newIPs = new Vector<AccountIP>();
		for (int i = 0; i < ipList.size(); i++) {
			AccountIP accountIP = new AccountIP();
			accountIP.setAcctSmsApiId(acctSMS);
			accountIP.setClientIp(ipList.get(i));
			newIPs.add(accountIP);
		}
		acctSMS.setAccountIPs(newIPs);

		accountSMSAPIDao.editSmsApiInfo(acctSMS); // throws db exception
		smsLogger.info(trxInfo.logInfo() + "editing list of IPs for account id: " + accountId + "is done..");

	}

	@Override
	public String getCurrentSecureKey(AdminTrxInfo trxInfo, String accountId) throws Exception {
		smsLogger.info(trxInfo.logInfo() + "get current  Secure Key for account id" + accountId);

		AccountSMSAPI smsAccount = accountSMSAPIDao.findByAccountId(accountId);
		if (smsAccount != null) {
			try {
				String acctKey = smsAccount.getSecureKey();
				String decyrptedAccKey;
				decyrptedAccKey = aes.decrypt(acctKey);
				return decyrptedAccKey;
			} catch (Exception e) {
//				e.printStackTrace();
				appLogger.error(trxInfo.logId() + "cann't decyrpt secure key" , e);

				smsLogger.debug(trxInfo.logId() + "cann't decyrpt secure key");

			}
		} else {

			smsLogger.debug(trxInfo.logId() + "account with id " + accountId + "not found");
			throw new AccountNotFoundException();
		}
		return null;
	}

}
