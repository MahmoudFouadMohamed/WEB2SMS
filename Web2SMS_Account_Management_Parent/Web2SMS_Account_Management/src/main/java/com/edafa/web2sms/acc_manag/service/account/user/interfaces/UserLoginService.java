/**
 * 
 */
package com.edafa.web2sms.acc_manag.service.account.user.interfaces;

import com.edafa.web2sms.acc_manag.service.account.model.AccountResultFullInfo;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;


import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;

/**
 * @author mahmoud
 *
 */
@WebService(name = "UserLoginService", portName = "UserLoginServicePort", targetNamespace = "http://www.edafa.com/web2sms/service/acc_manag/user")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface UserLoginService {

        @WebMethod(operationName = "checkUserForLogin")
	@WebResult(name = "ResultStatus")
	public ResultStatus checkUserForLogin(
                @WebParam(name = "accountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
                @WebParam(name = "companyName") String companyName,
                @WebParam(name = "userLang") String userLang);
        
    	@WebMethod(operationName = "userLogin")
	@WebResult(name = "AccountResultFullInfo")
	public AccountResultFullInfo userLogin(
                @WebParam(name = "accountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
                @WebParam(name = "companyName") String companyName,
                @WebParam(name = "password") String password,
                @WebParam(name = "userLang") String userLang);

        @WebMethod(operationName = "directUserLogin")
	@WebResult(name = "AccountResultFullInfo")
	public AccountResultFullInfo directUserLogin(
                @WebParam(name = "accountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
                @WebParam(name = "companyName") String companyName);
       
        
        @WebMethod(operationName = "userForgetPassword")
	@WebResult(name = "ResultStatus")
	public ResultStatus userForgetPassword(
                @WebParam(name = "accountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
                @WebParam(name = "companyName") String companyName,
                @WebParam(name = "userLang") String userLang);
       
        @WebMethod(operationName = "resendTempPassword")
	@WebResult(name = "ResultStatus")
	public ResultStatus resendTempPassword(
                @WebParam(name = "accountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
                @WebParam(name = "companyName") String companyName,
                @WebParam(name = "userLang") String userLang);                   
}
