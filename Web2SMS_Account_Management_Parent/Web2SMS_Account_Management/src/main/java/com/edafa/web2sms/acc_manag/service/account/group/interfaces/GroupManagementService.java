package com.edafa.web2sms.acc_manag.service.account.group.interfaces;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.edafa.web2sms.acc_manag.service.account.group.model.AccountGroupResultSet;
import com.edafa.web2sms.acc_manag.service.account.group.model.AccountGroupsDetailedResultSet;
import com.edafa.web2sms.acc_manag.service.account.group.model.AccountGroupsResultSet;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.CountResult;
import com.edafa.web2sms.acc_manag.service.model.GroupBasicInfo;
import com.edafa.web2sms.acc_manag.service.model.GroupModel;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;

/**
 * @author memad
 *
 */

@WebService(name = "GroupManagementService", portName = "GroupManagementServicePort",targetNamespace = "http://www.edafa.com/web2sms/service/acc_manag/account/group")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface GroupManagementService {

	@WebMethod(operationName = "createGroup")
	@WebResult(name = "ResultStatus")
	public AccountGroupResultSet createGroup(@WebParam(name = "AccountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "Group") GroupModel group);

	@WebMethod(operationName = "updateGroup")
	@WebResult(name = "ResultStatus")
	public ResultStatus updateGroup(@WebParam(name = "AccountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "Group") GroupModel group);

	@WebMethod(operationName = "deleteGroup")
	@WebResult(name = "ResultStatus")
	public ResultStatus deleteGroup(@WebParam(name = "AccountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "Group") GroupBasicInfo group);

	@WebMethod(operationName = "countAccountGroups")
	@WebResult(name = "AccountGroupsCount")
	public CountResult countAccountGroups(@WebParam(name = "AccountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "Search") String groupName);

	@WebMethod(operationName = "getAccountGroups")
	@WebResult(name = "AccountGroupsResultSet")
	public AccountGroupsResultSet getAccountGroups(
			@WebParam(name = "AccountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "Search") String groupName, @WebParam(name = "First") int first,
			@WebParam(name = "Max") int max);

	@WebMethod(operationName = "getAccountGroupFullInfo")
	@WebResult(name = "AccountGroupResultSet")
	public AccountGroupResultSet getAccountGroupFullInfo(
			@WebParam(name = "AccountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "GroupBasicInfo") GroupBasicInfo groupBasicInfo);

	@WebMethod(operationName = "getAccountGroupsDetailed")
	@WebResult(name = "AccountGroupsDetailedResultSet")
	public AccountGroupsDetailedResultSet getAccountGroupsDetailed(
			@WebParam(name = "AccountUserTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "Search") String groupName, @WebParam(name = "First") int first,
			@WebParam(name = "Max") int max);

	@WebMethod(operationName = "setUserAsGroupAdmin")
	@WebResult(name = "AccountResult", partName = "accountResult")
	public ResultStatus setUserAsGroupAdmin(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") AccountUserTrxInfo userTrxInfo,
			@WebParam(name = "UserId", partName = "userId") String userId,
			@WebParam(name = "GroupId", partName = "groupId") String groupId);
}
