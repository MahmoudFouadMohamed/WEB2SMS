package com.edafa.web2sms.acc_manag.service.account.group;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.acc_manag.alarm.AccManagAppErrorManagerAdapter;
import com.edafa.web2sms.acc_manag.alarm.AppErrors;
import com.edafa.web2sms.acc_manag.alarm.ErrorsSource;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.GroupAlreadyExistException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.GroupCreationException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.SetGroupAdminException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.GroupNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.LastAdminNotRemovableException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.NotEditableException;
import com.edafa.web2sms.acc_manag.service.account.group.exception.NotRemovableException;
import com.edafa.web2sms.acc_manag.service.account.group.interfaces.GroupManagementBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.group.interfaces.GroupManagementService;
import com.edafa.web2sms.acc_manag.service.account.group.model.AccountGroupResultSet;
import com.edafa.web2sms.acc_manag.service.account.group.model.AccountGroupsDetailedResultSet;
import com.edafa.web2sms.acc_manag.service.account.group.model.AccountGroupsResultSet;
import com.edafa.web2sms.acc_manag.service.account.group.user.interfaces.GroupUserManagementBeanLocal;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.CountResult;
import com.edafa.web2sms.acc_manag.service.model.GroupBasicInfo;
import com.edafa.web2sms.acc_manag.service.model.GroupModel;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;
import com.edafa.web2sms.acc_manag.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.dalayer.exception.DBException;

/**
 * Session Bean implementation class GroupManagementServiceImpl
 * @author memad
 *
 */

@Stateless

@WebService(name = "GroupManagementService", serviceName = "GroupManagementService", targetNamespace = "http://www.edafa.com/web2sms/service/acc_manag/account/group", endpointInterface = "com.edafa.web2sms.acc_manag.service.account.group.interfaces.GroupManagementService")
//@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
public class GroupManagementServiceImpl implements GroupManagementService {

	Logger appLogger;
	Logger acctLogger;

	@EJB
	GroupManagementBeanLocal groupManagementBean;

	@EJB
	GroupUserManagementBeanLocal groupUserManagementBean;

	@EJB
	AccManagAppErrorManagerAdapter appErrorManagerAdapter;

	/**
	 * Default constructor.
	 */
	public GroupManagementServiceImpl() {
		// zero argument constructor
	}

	@PostConstruct
	public void initLoggers() {
		appLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT_UTILS.name());
		acctLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT.name());
	}

	@Override
	public CountResult countAccountGroups(AccountUserTrxInfo userTrxInfo, String groupName) {

		acctLogger.info(userTrxInfo.logInfo() + "Count groups with search group name("
				+ (groupName == null ? "NULL" : groupName) + ")");

		CountResult countResult = new CountResult(ResponseStatus.SUCCESS);

		try {
			int groupsCount = groupManagementBean.countAccountGroups(userTrxInfo, groupName);
			countResult.setCount(groupsCount);
		} catch (DBException e) {
			logAndHandleDBException(userTrxInfo, countResult, e);
		} catch (IneligibleAccountException e) {
			logAndHandleWarning(userTrxInfo, countResult, ResponseStatus.INELIGIBLE_ACCOUNT, e,
					AppErrors.INELIGIBLE_ACCOUNT, e.getMessage());
		} catch (Exception e) {
			logAndHandleError(userTrxInfo, countResult, ResponseStatus.FAIL, e, AppErrors.GENERAL_ERROR,
					"Unknown Error");
		}

		return countResult;
	}

	@Override
	public AccountGroupsResultSet getAccountGroups(AccountUserTrxInfo userTrxInfo, String groupName, int first,
			int max) {

		acctLogger.info(userTrxInfo.logInfo() + "Get account groups with search group name("
				+ (groupName == null ? "NULL" : groupName) + ") and first(" + first + ") and max(" + max + ")");

		AccountGroupsResultSet result = new AccountGroupsResultSet(ResponseStatus.SUCCESS);
		try {
			List<GroupBasicInfo> groups = groupManagementBean.getAccountGroups(userTrxInfo, groupName, first, max);
			result.setGroupModels(groups);
		} catch (DBException e) {
			logAndHandleDBException(userTrxInfo, result, e);
		} catch (IneligibleAccountException e) {
			logAndHandleWarning(userTrxInfo, result, ResponseStatus.INELIGIBLE_ACCOUNT, e, AppErrors.INELIGIBLE_ACCOUNT,
					e.getMessage());
		} catch (Exception e) {
			logAndHandleError(userTrxInfo, result, ResponseStatus.FAIL, e, AppErrors.GENERAL_ERROR, "Unknown Error");
		}

		return result;
	}

	@Override
	public AccountGroupResultSet getAccountGroupFullInfo(AccountUserTrxInfo userTrxInfo,
			GroupBasicInfo groupBasicInfo) {

		String groupId = groupBasicInfo.getGroupId();
		String groupName = groupBasicInfo.getGroupName();

		acctLogger.info(userTrxInfo.logInfo() + "Get account group full info for group name("
				+ (groupName == null ? "NULL" : groupName) + ") and groupId(" + groupId + ")");

		AccountGroupResultSet result = new AccountGroupResultSet(ResponseStatus.SUCCESS);

		try {
			GroupModel groupModel = groupManagementBean.getAccountGroupFullInfo(userTrxInfo, groupBasicInfo);
			result.setGroupModel(groupModel);
		} catch (DBException e) {
			logAndHandleDBException(userTrxInfo, result, e);
		} catch (IneligibleAccountException e) {
			logAndHandleWarning(userTrxInfo, result, ResponseStatus.INELIGIBLE_ACCOUNT, e, AppErrors.INELIGIBLE_ACCOUNT,
					e.getMessage());
		} catch (GroupNotFoundException e) {
			logAndHandleWarning(userTrxInfo, result, ResponseStatus.GROUP_NOT_EXIST, e, AppErrors.INVALID_OPERATION,
					e.getMessage());
		} catch (Exception e) {
			logAndHandleError(userTrxInfo, result, ResponseStatus.FAIL, e, AppErrors.GENERAL_ERROR, "Unknown Error");
		}

		return result;
	}

	@Override
	public AccountGroupsDetailedResultSet getAccountGroupsDetailed(AccountUserTrxInfo userTrxInfo, String groupName,
			int first, int max) {

		acctLogger.info(userTrxInfo.logInfo() + "Get account groups Detailed with search group name("
				+ (groupName == null ? "NULL" : groupName) + ") and first(" + first + ") and max(" + max + ")");

		AccountGroupsDetailedResultSet result = new AccountGroupsDetailedResultSet(ResponseStatus.SUCCESS);

		try {
			List<GroupModel> groups = groupManagementBean.getAccountGroupsDetailed(userTrxInfo, groupName, first, max);
			result.setGroupModels(groups);
		} catch (DBException e) {
			logAndHandleDBException(userTrxInfo, result, e);
		} catch (IneligibleAccountException e) {

			logAndHandleWarning(userTrxInfo, result, ResponseStatus.INELIGIBLE_ACCOUNT, e, AppErrors.INELIGIBLE_ACCOUNT,
					e.getMessage());
		} catch (Exception e) {
			logAndHandleError(userTrxInfo, result, ResponseStatus.FAIL, e, AppErrors.GENERAL_ERROR, "Unknown Error");
		}

		return result;
	}

	@Override
	public AccountGroupResultSet createGroup(AccountUserTrxInfo userTrxInfo, GroupModel group) {

		acctLogger.info(userTrxInfo.logInfo() + "Create Group with groupData={" + group + "}");

		AccountGroupResultSet resultStatus = new AccountGroupResultSet(ResponseStatus.SUCCESS);

		try {
			GroupModel createdGroup = groupManagementBean.createGroup(userTrxInfo, group);
			resultStatus.setGroupModel(createdGroup);
		} catch (DBException e) {
			logAndHandleDBException(userTrxInfo, resultStatus, e);
		} catch (IneligibleAccountException e) {
			logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.INELIGIBLE_ACCOUNT, e,
					AppErrors.INELIGIBLE_ACCOUNT, e.getMessage());
		} catch (GroupAlreadyExistException e) {
			logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.GROUP_ALREADY_EXIST, e,
					AppErrors.INVALID_OPERATION, e.getMessage());
		} catch (UserNotFoundException e) {
			logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.USER_NOT_EXIST, e,
					AppErrors.INVALID_OPERATION, e.getMessage());
		} catch (GroupCreationException e) {
			logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.INVALID_REQUEST, e,
					AppErrors.INVALID_OPERATION, e.getMessage());
		} catch (LastAdminNotRemovableException e) {
			logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.LAST_ADMIN_NOT_REMOVABLE, e,
					AppErrors.INVALID_OPERATION, e.getMessage());
		} catch (Exception e) {
			logAndHandleError(userTrxInfo, resultStatus, ResponseStatus.FAIL, e, AppErrors.GENERAL_ERROR,
					"Unknown Error");
		}

		return resultStatus;
	}

	@Override
	public ResultStatus updateGroup(AccountUserTrxInfo userTrxInfo, GroupModel group) {

		acctLogger.info(userTrxInfo.logInfo() + "Update Group with id[" + group.getGroupId() + "] with groupData={"
				+ group + "}");

		ResultStatus resultStatus = new ResultStatus(ResponseStatus.SUCCESS);

		try {
			groupManagementBean.updateGroup(userTrxInfo, group);
		} catch (DBException e) {
			logAndHandleDBException(userTrxInfo, resultStatus, e);
		} catch (IneligibleAccountException e) {

			logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.INELIGIBLE_ACCOUNT, e,
					AppErrors.INELIGIBLE_ACCOUNT, e.getMessage());
		} catch (GroupNotFoundException e) {
			logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.GROUP_NOT_EXIST, e,
					AppErrors.INVALID_OPERATION, e.getMessage());
		} catch (NotEditableException e) {
			logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.GROUP_NOT_EDITABLE, e,
					AppErrors.INVALID_OPERATION, e.getMessage());
		} catch (UserNotFoundException e) {
			logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.USER_NOT_EXIST, e,
					AppErrors.INVALID_OPERATION, e.getMessage());
		} catch (LastAdminNotRemovableException e) {
			logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.LAST_ADMIN_NOT_REMOVABLE, e,
					AppErrors.INVALID_OPERATION, e.getMessage());
                } catch (SetGroupAdminException e) {
                    logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.GROUP_NOT_EDITABLE, e,
                            AppErrors.INVALID_OPERATION, e.getMessage());
                } catch (Exception e) {
			logAndHandleError(userTrxInfo, resultStatus, ResponseStatus.FAIL, e, AppErrors.GENERAL_ERROR,
					"Unknown Error");
		}

		return resultStatus;
	}

	@Override
	public ResultStatus deleteGroup(AccountUserTrxInfo userTrxInfo, GroupBasicInfo group) {

		acctLogger.info(userTrxInfo.logInfo() + "Delete Group with id[" + group.getGroupId() + "]");

		ResultStatus resultStatus = new ResultStatus(ResponseStatus.SUCCESS);

		try {
			groupManagementBean.deleteGroup(userTrxInfo, group);
		} catch (DBException e) {
			logAndHandleDBException(userTrxInfo, resultStatus, e);
		} catch (IneligibleAccountException e) {

			logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.INELIGIBLE_ACCOUNT, e,
					AppErrors.INELIGIBLE_ACCOUNT, e.getMessage());
		} catch (GroupNotFoundException e) {
			logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.GROUP_NOT_EXIST, e,
					AppErrors.INVALID_OPERATION, e.getMessage());
		} catch (NotRemovableException e) {

			logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.GROUP_NOT_EDITABLE, e,
					AppErrors.INVALID_OPERATION, e.getMessage());
		} catch (Exception e) {
			logAndHandleError(userTrxInfo, resultStatus, ResponseStatus.FAIL, e, AppErrors.GENERAL_ERROR,
					"Unknown Error");
		}

		return resultStatus;
	}

	@Override
	public ResultStatus setUserAsGroupAdmin(AccountUserTrxInfo userTrxInfo, String userId, String groupId) {

		acctLogger.info(userTrxInfo.logInfo() + "Set user with id(" + userId + ") as group admin of group with id("
				+ groupId + ")");

		ResultStatus resultStatus = new ResultStatus(ResponseStatus.SUCCESS);
		try {
			groupUserManagementBean.setUserAsGroupAdmin(userTrxInfo, userId, groupId);
		} catch (DBException e) {
			logAndHandleDBException(userTrxInfo, resultStatus, e);
		} catch (IneligibleAccountException e) {
			logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.INELIGIBLE_ACCOUNT, e,
					AppErrors.INELIGIBLE_ACCOUNT, e.getMessage());
		} catch (UserNotFoundException e) {
			logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.USER_NOT_EXIST, e,
					AppErrors.INVALID_OPERATION, e.getMessage());
		} catch (GroupNotFoundException e) {
			logAndHandleWarning(userTrxInfo, resultStatus, ResponseStatus.GROUP_NOT_EXIST, e,
					AppErrors.INVALID_OPERATION, e.getMessage());
		} catch (Exception e) {
			logAndHandleError(userTrxInfo, resultStatus, ResponseStatus.FAIL, e, AppErrors.GENERAL_ERROR,
					"Unknown Error");
		}

		return resultStatus;
	}

	private void logAndHandleDBException(AccountUserTrxInfo userTrxInfo, ResultStatus resultStatus, DBException e) {

		boolean isDBTimeout = (e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
				|| (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")));

		if (isDBTimeout) {
			logAndHandleError(userTrxInfo, resultStatus, ResponseStatus.FAIL, e, AppErrors.DATABASE_TIMEOUT,
					"DB Timeout");
		} else {
			logAndHandleError(userTrxInfo, resultStatus, ResponseStatus.FAIL, e, AppErrors.DATABASE_ERROR, "DB error");
		}

	}

	private void logAndHandleError(AccountUserTrxInfo userTrxInfo, ResultStatus result, ResponseStatus responseStatus,
			Exception e, AppErrors error, String errorDesc) {

		String logMsg = userTrxInfo.logInfo() + errorDesc;
		acctLogger.error(logMsg + "," + e.getMessage());
		appLogger.error(logMsg, e);

		handleException(result, responseStatus, error, errorDesc);

	}

	private void logAndHandleWarning(AccountUserTrxInfo userTrxInfo, ResultStatus result, ResponseStatus responseStatus,
			Exception e, AppErrors error, String errorDesc) {

		String logMsg = userTrxInfo.logInfo() + errorDesc;
		acctLogger.warn(logMsg + "," + e.getMessage());

		handleException(result, responseStatus, error, errorDesc);

	}

	private void handleException(ResultStatus result, ResponseStatus responseStatus, AppErrors error,
			String errorDesc) {
		if (result != null) {
			result.setStatus(responseStatus);
			result.setErrorMessage(errorDesc);
		}

		reportAppError(error, errorDesc);
	}

	private void reportAppError(AppErrors error, String msg) {
		appErrorManagerAdapter.reportAppError(error, msg, ErrorsSource.ACCOUNT_MANAGEMENT);
	}

}
