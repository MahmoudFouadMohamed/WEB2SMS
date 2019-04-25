/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.account.interfaces;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.exception.InvalidCustomerForQuotaInquiry;
import com.edafa.web2sms.dalayer.exception.QuotaInquiryFailed;
import com.edafa.web2sms.dalayer.pojo.QuotaInfo;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.model.AccountAdminTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountModel;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.acc_manag.service.model.QuotaHistoryModel;
import com.edafa.web2sms.acc_manag.service.model.AccountTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.PrivilegeModel;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidAddressFormattingException;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidMSISDNFormatException;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author mahmoud
 */
@Local
public interface AccountManegementServiceBeanLocal {

    AccountModelFullInfo findAccountByCoNameFullInfo(AccountUserTrxInfo trxInfo, String companyName) throws DBException,
            AccountNotFoundException;

    public AccountModel findAccountByCoName(AccountTrxInfo trxInfo, String companyName) throws DBException,
            AccountNotFoundException;

    QuotaInfo getQuotaInfoByMSISDN(AccountUserTrxInfo  trxInfo, String billingMSISDN) throws DBException, AccountNotFoundException,
            InvalidCustomerForQuotaInquiry, QuotaInquiryFailed, AccountManagInvalidMSISDNFormatException,
            AccountManagInvalidAddressFormattingException,IneligibleAccountException;

    QuotaHistoryModel getQuotaHistory(AccountUserTrxInfo trxInfo, String accountId) throws DBException, AccountNotFoundException, IneligibleAccountException;

    List<PrivilegeModel> getAllSystemPrivileges(AccountTrxInfo trxInfo) throws DBException;

}
