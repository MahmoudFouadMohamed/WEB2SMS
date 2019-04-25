package com.edafa.web2sms.acc_manag.service.account.interfaces;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.edafa.web2sms.acc_manag.service.account.model.AccountResult;
import com.edafa.web2sms.acc_manag.service.account.model.AccountResultFullInfo;
import com.edafa.web2sms.acc_manag.service.account.model.PrivilegesResult;
import com.edafa.web2sms.acc_manag.service.account.model.QuotaHistoryResult;
import com.edafa.web2sms.acc_manag.service.account.model.QuotaInquiryResult;
import com.edafa.web2sms.acc_manag.service.model.AccountTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;

@WebService(name = "AccountManegementService", portName = "AccountManegementServicePort", targetNamespace = "http://www.edafa.com/web2sms/service/acc_manag/account")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface AccountManegementService {

//	@WebMethod(operationName = "findAccountByCompanyName")
//	@WebResult(name = "AccountResult", partName = "accountResult")
//	public AccountResult findAccountByCoName(
//			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") AccountUserTrxInfo userTrxInfo,
//			@WebParam(name = "CompanyName", partName = "companyName") String companyName);
//
	@WebMethod(operationName = "findAccountByCompanyNameFullInfo")
	@WebResult(name = "AccountResultFullInfo", partName = "accountResultFullInfo")
	public AccountResultFullInfo findAccountByCoNameFullInfo(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "CompanyName", partName = "companyName") String companyName);

	@WebMethod(operationName = "getQuotaInfoByMSISDN")
	@WebResult(name = "QuotaInquiryResult", partName = "quotaInquiryResult")
	public QuotaInquiryResult getQuotaInfoByMSISDN(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "BillingMSISDN", partName = "billingMSISDN") String billingMSISDN);
	
	@WebMethod(operationName = "getQuotaHistory")
	@WebResult(name = "QuotaHistoryResult", partName = "quotaHistoryResult")
	public QuotaHistoryResult getQuotaHistory(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "AccountId", partName = "accountId") String accountId);

        @WebMethod(operationName = "getAllSystemPrivileges")
	@WebResult(name = "PrivilegesResult", partName = "privilegesResult")
	public PrivilegesResult getAllSystemPrivileges( 
			@WebParam(name = "AccountTrxInfo", partName = "accountTrxInfo") AccountTrxInfo accountTrxInfo);

}
