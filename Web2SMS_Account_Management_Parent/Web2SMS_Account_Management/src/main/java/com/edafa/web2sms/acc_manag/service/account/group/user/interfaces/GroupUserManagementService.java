package com.edafa.web2sms.acc_manag.service.account.group.user.interfaces;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;

import com.edafa.web2sms.acc_manag.service.account.group.user.model.GroupUsersResultSet;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.GroupModel;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;

/**
 * @author memad for Future use
 *
	@WebService(name = "GroupUserManagementService", portName = "GroupUserManagementServicePort", targetNamespace = "http://www.edafa.com/web2sms/acc_manag/service/account/group/user")
	@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
 */

public interface GroupUserManagementService {

	@WebMethod(operationName = "getGroupUsers", exclude = true)
	@WebResult(name = "GroupUsersResultSet", partName = "groupsList")
	public GroupUsersResultSet getGroupUsers(
			@WebParam(name = "AccountUserTrxInfo", partName = "userTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "group", partName = "group") GroupModel group);

	@WebMethod(operationName = "assignUserToGroup", exclude = true)
	@WebResult(name = "ResultStatus")
	public ResultStatus assignUserToGroup(@WebParam(name = "AccountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "Group") GroupModel group, @WebParam(name = "UserName") String userName);

	@WebMethod(operationName = "removeUserFromGroup", exclude = true)
	@WebResult(name = "ResultStatus")
	public ResultStatus removeUserFromGroup(@WebParam(name = "AccountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "Group") GroupModel group, @WebParam(name = "UserName") String userName);

	@WebMethod(operationName = "setUserAsGroupAdmin", exclude = true)
	@WebResult(name = "AccountResult", partName = "accountResult")
	public ResultStatus setUserAsGroupAdmin(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "UserId", partName = "userId") String userId,
			@WebParam(name = "GroupId", partName = "groupId") String groupId);

}
