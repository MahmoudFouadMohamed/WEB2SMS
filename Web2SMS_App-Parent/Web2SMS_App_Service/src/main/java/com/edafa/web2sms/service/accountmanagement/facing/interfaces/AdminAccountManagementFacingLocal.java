/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.accountmanagement.facing.interfaces;

import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.model.AccountAdminTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountTrxInfo;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidAddressFormattingException;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidMSISDNFormatException;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.exception.InvalidCustomerForQuotaInquiry;
import com.edafa.web2sms.dalayer.exception.QuotaInquiryFailed;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.pojo.QuotaInfo;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author mahmoud
 */
@Local
public interface AdminAccountManagementFacingLocal {
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
