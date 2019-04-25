package com.edafa.web2sms.acc_manag.service.account.group.user;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.GroupNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.group.user.interfaces.GroupUserManagementService;
import com.edafa.web2sms.acc_manag.service.account.group.user.model.GroupUsersResultSet;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.GroupModel;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import com.edafa.web2sms.acc_manag.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.dalayer.exception.DBException;

/**
 * Session Bean implementation class TemplateManegementServiceImpl
 * @author memad
 *
	@WebService(name = "GroupUserManagementService", serviceName = "GroupUserManagementService", targetNamespace = "http://www.edafa.com/web2sms/service/acc_manag/account/group/user", endpointInterface = "com.edafa.web2sms.acc_manag.service.account.group.user.interfaces.GroupUserManagementService")
 */

@Stateless

//@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
public class GroupUserManegementServiceImpl implements GroupUserManagementService {

	Logger appLogger;
	Logger acctLogger;

	@EJB
	GroupUserManagementBean userManagement;

	/**
	 * Default constructor.
	 */
	public GroupUserManegementServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@PostConstruct
	public void initLoggers() {
		appLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT_UTILS.name());
		acctLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT.name());
	}

	@Override
	public GroupUsersResultSet getGroupUsers(AccountUserTrxInfo userTrxInfo, GroupModel group) {

		GroupUsersResultSet result = new GroupUsersResultSet(ResponseStatus.SUCCESS);

		try {
			List<AccManagUserModel> userModels = userManagement.getGroupUsers(userTrxInfo, group);
			result.setUserModels(userModels);

		} catch (DBException e) {
			logAndHandleException(userTrxInfo, result, e, "Database error");
		} catch (IneligibleAccountException e) {
			logAndHandleException(userTrxInfo, result, e, "Ineligible Account");
		}

		return result;
	}

	@Override
	public ResultStatus assignUserToGroup(AccountUserTrxInfo userTrxInfo, GroupModel group, String userName) {

		GroupUsersResultSet result = new GroupUsersResultSet(ResponseStatus.SUCCESS);

		try {
			userManagement.assignUserToGroup(userTrxInfo, group, userName);

		} catch (DBException e) {
			logAndHandleException(userTrxInfo, result, e, "Database error");
		} catch (IneligibleAccountException e) {
			logAndHandleException(userTrxInfo, result, e, "Ineligible Account");
		} catch (GroupNotFoundException e) {
			logAndHandleException(userTrxInfo, result, e, "Group" + group.getGroupName() + " not found");
		}

		return result;
	}

	@Override
	public ResultStatus setUserAsGroupAdmin(AccountUserTrxInfo userTrxInfo, String userId, String groupId) {

		GroupUsersResultSet result = new GroupUsersResultSet(ResponseStatus.SUCCESS);

		try {
			userManagement.setUserAsGroupAdmin(userTrxInfo, userId, groupId);

		} catch (DBException e) {
			logAndHandleException(userTrxInfo, result, e, "Database error");
		} catch (IneligibleAccountException e) {
			logAndHandleException(userTrxInfo, result, e, "Ineligible Account");
		} catch (UserNotFoundException e) {
			logAndHandleException(userTrxInfo, result, e, "User with ID " + userId + " not found");
		} catch (GroupNotFoundException e) {
			logAndHandleException(userTrxInfo, result, e, "Group with ID " + groupId + " not found");
		}

		return result;
	}

	@Override
	public ResultStatus removeUserFromGroup(AccountUserTrxInfo userTrxInfo, GroupModel group, String userName) {
		// TODO Auto-generated method stub
		return null;
	}

	private void logAndHandleException(AccountUserTrxInfo userTrxInfo, ResultStatus result, Exception e, String error) {
		String logMsg = userTrxInfo.logInfo() + error;
		appLogger.error(logMsg, e);
		acctLogger.error(logMsg, e);
		result.setStatus(ResponseStatus.FAIL);
		result.setErrorMessage(error);
	}
}
