/**
 * 
 */
package com.edafa.web2sms.acc_manag.service.account.user.interfaces;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.edafa.web2sms.acc_manag.service.account.user.model.AccountUsersResultSet;
import com.edafa.web2sms.acc_manag.service.account.user.model.UserResult;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.CountResult;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;
import com.edafa.web2sms.acc_manag.service.model.UserDetailsModel;

/**
 * @author khalid
 *
 */
@WebService(name = "UserManegementService", portName = "UserManegementServicePort", targetNamespace = "http://www.edafa.com/web2sms/service/acc_manag/user")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface UserManagementService {

	@WebMethod(operationName = "findUserByCompanyName")
	@WebResult(name = "AccountResult", partName = "accountResult")
	public UserResult findUserByCoName(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "CompanyName", partName = "companyName") String companyName);

	@WebMethod(operationName = "setUserAsGroupAdmin")
	@WebResult(name = "AccountResult", partName = "accountResult")
	public ResultStatus setUserAsGroupAdmin(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "UserId", partName = "userId") String userId,
			@WebParam(name = "GroupId", partName = "groupId") String groupId);

	@WebMethod(operationName = "countAccountUsers")
	@WebResult(name = "AccountUsersCount")
	public CountResult countAccountUsers(
                @WebParam(name = "AccountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
                @WebParam(name = "Search") String userName);

	@WebMethod(operationName = "getAccountUsers")
	@WebResult(name = "AccountGroupsResultSet")
	public AccountUsersResultSet getAccountUsers(@WebParam(name = "AccountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
             @WebParam(name = "Search") String userName,
             @WebParam(name = "First") int first,
             @WebParam(name = "Max") int max);

	@WebMethod(operationName = "countDefaultGroupUsers")
	@WebResult(name = "AccountUsersCount")
	public CountResult countDefaultGroupUsers(
                @WebParam(name = "AccountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
                @WebParam(name = "Search") String userName);

	@WebMethod(operationName = "getDefaultGroupUsers")
	@WebResult(name = "AccountGroupsResultSet")
	public AccountUsersResultSet getDefaultGroupUsers(
			@WebParam(name = "AccountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
		     @WebParam(name = "Search") String groupName,
             @WebParam(name = "First") int first,
             @WebParam(name = "Max") int max);

	@WebMethod(operationName = "updateUserData")
	@WebResult(name = "ResultStatus")
	public ResultStatus updateUserData(@WebParam(name = "AccountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "User") UserDetailsModel user);

        @WebMethod(operationName = "changeUserPassword")
	@WebResult(name = "ResultStatus")
	public ResultStatus changeUserPassword(
                @WebParam(name = "accountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
                @WebParam(name = "oldPassword") String oldPassword,
                @WebParam(name = "newPassword") String newPassword);
        
}
