package com.edafa.web2sms.acc_manag.service.account.interfaces.Remote;

import java.util.List;

import javax.ejb.Remote;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.exception.InvalidCustomerForQuotaInquiry;
import com.edafa.web2sms.dalayer.exception.QuotaInquiryFailed;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.pojo.QuotaInfo;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.model.AccountAdminTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountTrxInfo;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidAddressFormattingException;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidMSISDNFormatException;


@Remote
public interface AdminAccountManagementBeanRemote
{

	List<Account> findAllAccounts(AccountAdminTrxInfo trxInfo, int first, int count) throws DBException;

	QuotaInfo getQuotaInfoByMSISDN(AccountTrxInfo trxInfo, String billingMSISDN) throws DBException, AccountNotFoundException,
			InvalidCustomerForQuotaInquiry, QuotaInquiryFailed, AccountManagInvalidMSISDNFormatException,
			AccountManagInvalidAddressFormattingException;

	int countAccounts(AccountTrxInfo trxInfo) throws DBException;

	List<Account> searchAccount(AccountAdminTrxInfo trxInfo, String accountID, String companyName,
			String billingMSISDN, int first, int max) throws DBException;

	long countSearchAccount(AccountAdminTrxInfo trxInfo, String accountID, String companyName,
			String billingMSISDN) throws DBException;

}