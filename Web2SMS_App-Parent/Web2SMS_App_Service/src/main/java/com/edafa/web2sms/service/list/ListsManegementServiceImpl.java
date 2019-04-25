package com.edafa.web2sms.service.list;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.bea.common.security.xacml.InvalidAttributeException;
import com.edafa.jee.apperr.AppError;
import com.edafa.jee.apperr.monitor.AppErrorManager;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.alarm.ErrorsSource;
import com.edafa.web2sms.dalayer.dao.interfaces.ListTypeDaoLocal;
import com.edafa.web2sms.dalayer.enums.ListTypeName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.exception.IntraListInquiryFailed;
import com.edafa.web2sms.dalayer.exception.InvalidCustomerForQuotaInquiry;
import com.edafa.web2sms.dalayer.model.ListType;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.service.campaign.exception.CampaignNotFoundException;
import com.edafa.web2sms.service.conversoin.ListConversionBean;
import com.edafa.web2sms.service.enums.ResponseStatus;
import com.edafa.web2sms.service.list.exception.ContactNotFoundException;
import com.edafa.web2sms.service.list.exception.DuplicateContactException;
import com.edafa.web2sms.service.list.exception.DuplicateListNameException;
import com.edafa.web2sms.service.list.exception.EmptyConatctListException;
import com.edafa.web2sms.service.list.exception.InvalidFileException;
import com.edafa.web2sms.service.list.exception.InvalidRequestException;
import com.edafa.web2sms.service.list.exception.ListNotFoundException;
import com.edafa.web2sms.service.list.exception.ListTypeExpansionException;
import com.edafa.web2sms.service.list.exception.ListTypeNotFoundException;
import com.edafa.web2sms.service.list.exception.LockedListException;
import com.edafa.web2sms.service.list.files.FileDetails;
import com.edafa.web2sms.service.list.interfaces.ListManegementBeanLocal;
import com.edafa.web2sms.service.list.interfaces.ListsManegementService;
import com.edafa.web2sms.service.list.model.ContactListInfoResultSet;
import com.edafa.web2sms.service.list.model.ContactListResultSet;
import com.edafa.web2sms.service.list.model.ContactResultSet;
import com.edafa.web2sms.service.list.model.FileResult;
import com.edafa.web2sms.service.model.ContactListInfoModel;
import com.edafa.web2sms.service.model.ContactListModel;
import com.edafa.web2sms.service.model.ContactModel;
import com.edafa.web2sms.service.model.FileTokenResult;
import com.edafa.web2sms.service.model.FileValidation;
import com.edafa.web2sms.service.model.ResultStatus;
import com.edafa.web2sms.service.model.UserTrxInfo;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

/**
 * Session Bean implementation class ListServiceBean
 */
@Stateless
@WebService(name = "ListsManegementService", serviceName = "ListsManegementService", targetNamespace = "http://www.edafa.com/web2sms/service/lists", endpointInterface = "com.edafa.web2sms.service.list.interfaces.ListsManegementService")
public class ListsManegementServiceImpl implements ListsManegementService {

	@EJB
	ListConversionBean listConversionBean;
	@EJB
	ListManegementBeanLocal listManegementBean;
	@EJB
	ListTypeDaoLocal ListTypeDao;        
	@EJB
	AppErrorManagerAdapter appErrorManagerAdapter;
        
	Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
	Logger listLogger = LogManager.getLogger(LoggersEnum.LIST_MNGMT.name());

	// Constructor---------------------------------------------------------

	/**
	 * Default constructor.
	 */
	public ListsManegementServiceImpl() {

	}

	public ResultStatus createNewList(UserTrxInfo userTrxInfo, ContactListModel contactsList) {

		ResultStatus result = new ResultStatus(ResponseStatus.SUCCESS);

		try {
			listManegementBean.createNewList(userTrxInfo, contactsList);
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage("IneligibleAccountException");
		} catch (DuplicateListNameException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Duplicate List Name");
			result.setStatus(ResponseStatus.DUPLICATE_LIST_NAME);
			result.setErrorMessage("DuplicateListNameException");
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
		} catch (EmptyConatctListException e) {
                        reportAppError(AppErrors.INVALID_REQUEST, "Empty Conatct List");
			result.setStatus(ResponseStatus.CONTACTS_NOT_FOUND);
			result.setErrorMessage(e.getMessage());
			listLogger.error(userTrxInfo.logId() + e.getMessage());
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(logMsg);

		}
		return result;
	}
	
	public ResultStatus createNewListWithFlag(UserTrxInfo userTrxInfo, ContactListModel contactsList) {

		ResultStatus result = new ResultStatus(ResponseStatus.SUCCESS);

		try {
			listManegementBean.createNewList(userTrxInfo, contactsList);
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage("IneligibleAccountException");
		} catch (DuplicateListNameException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Duplicate List Name");
			result.setStatus(ResponseStatus.DUPLICATE_LIST_NAME);
			result.setErrorMessage("DuplicateListNameException");
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
		} catch (EmptyConatctListException e) {
                        reportAppError(AppErrors.INVALID_REQUEST, "Empty Conatct List");
			result.setStatus(ResponseStatus.CONTACTS_NOT_FOUND);
			result.setErrorMessage(e.getMessage());
			listLogger.error(userTrxInfo.logId() + e.getMessage());
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(logMsg);

		}
		return result;
	}

	public ResultStatus handleVirtualList(UserTrxInfo userTrxInfo, List<ContactModel> contactsList) {

		ResultStatus result = new ResultStatus(ResponseStatus.SUCCESS);

		try {
			listManegementBean.handleVirtualList(userTrxInfo, contactsList);

		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage("IneligibleAccountException");
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());

		} catch (InvalidAttributeException e) {
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid Attribute");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());

		} catch (DuplicateContactException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Duplicate Contact");
			result.setStatus(ResponseStatus.DUPLICATE_CONTACT);
			result.setErrorMessage(e.getMessage());

		} catch (LockedListException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Locked List");
			result.setStatus(ResponseStatus.LOCKED_LIST);
			result.setErrorMessage(e.getMessage());

		} catch (ListNotFoundException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "List NotFound");
			result.setStatus(ResponseStatus.LIST_NOT_FOUND);
			result.setErrorMessage(e.getMessage());

		} catch (EmptyConatctListException e) {
                        reportAppError(AppErrors.INVALID_REQUEST, "Empty Conatct List");
			result.setStatus(ResponseStatus.CONTACTS_NOT_FOUND);
			result.setErrorMessage(e.getMessage());
			listLogger.error(userTrxInfo.logId() + e.getMessage());
		} catch (ListTypeNotFoundException e) {
                        reportAppError(AppErrors.INVALID_REQUEST, "List Type NotFound");
			//TODO: Should add new declarative response 
			result.setStatus(ResponseStatus.INVALID_REQUEST);
			result.setErrorMessage(e.getMessage());
		} catch (ListTypeExpansionException e) {
                        reportAppError(AppErrors.INVALID_REQUEST, "List Type Expansion");
			//TODO: Should add new declarative response 
			result.setStatus(ResponseStatus.INVALID_REQUEST);
			result.setErrorMessage(e.getMessage());
		}
		return result;
	}

	@Override
	public ResultStatus copyToNewList(UserTrxInfo userTrxInfo, ContactListInfoModel contactsList, int oldListId) {
		ResultStatus result = new ResultStatus(ResponseStatus.SUCCESS);

		try {
			listManegementBean.copyToNewList(userTrxInfo, contactsList, oldListId);
			return result;
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage("IneligibleAccountException");
			listLogger.error(userTrxInfo.logId() + e.getMessage());
			return result;
		} catch (DuplicateListNameException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Duplicate List Name");
			result.setStatus(ResponseStatus.DUPLICATE_LIST_NAME);
			result.setErrorMessage("DuplicateListNameException");
			return result;
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (InvalidRequestException e) {
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
			result.setStatus(ResponseStatus.CONTACTS_NOT_FOUND);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(logMsg);
			return result;
		}
	}
        
	@Override
	public ContactResultSet getContactList(UserTrxInfo userTrxInfo, int listId) {
		ContactResultSet result = new ContactResultSet();

		try {
			result.setContact(listManegementBean.getContactList(userTrxInfo, listId));
			result.setStatus(ResponseStatus.SUCCESS);
			return result;
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ContactResultSet getContactListWithPagination(UserTrxInfo userTrxInfo, int listId, int first, int max) {
		ContactResultSet result = new ContactResultSet();

		try {
			result.setContact(listManegementBean.getContactList(userTrxInfo, listId, first, max));
			result.setStatus(ResponseStatus.SUCCESS);
			return result;
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		}

	}

	@Override
	// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ContactListInfoResultSet getContactListsInfobyCampId(UserTrxInfo userTrxInfo, String campId) {

		ContactListInfoResultSet result = new ContactListInfoResultSet();

		try {
			List<ContactListInfoModel> list = listManegementBean.getContactListsInfo(userTrxInfo, campId);
			if (list != null) {
				result.setContactListInfoResultSet(list);
				result.setStatus(ResponseStatus.SUCCESS);
			}
			return result;
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (CampaignNotFoundException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Campaign NotFound");
			result.setStatus(ResponseStatus.CAMPAIGN_NOT_FOUND);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg, e);
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		}

	}

	@Override
	// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ContactListInfoResultSet getContactListsInfo(UserTrxInfo userTrxInfo, List<ListTypeName> listTypeNames) {

		ContactListInfoResultSet result = new ContactListInfoResultSet();

		try {
			result.setContactListInfoResultSet(listManegementBean.getContactListsInfo(userTrxInfo, listTypeNames));
			result.setStatus(ResponseStatus.SUCCESS);
			return result;
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (DBException e) {
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			return result;
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg, e);
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		}

	}
	
	@Override
	// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ContactListInfoResultSet getContactListsInfoWithFlag(UserTrxInfo userTrxInfo, List<ListTypeName> listTypeNames) {

		ContactListInfoResultSet result = new ContactListInfoResultSet();

		try {
			result.setContactListInfoResultSet(listManegementBean.getContactListsInfo(userTrxInfo, listTypeNames));
			result.setStatus(ResponseStatus.SUCCESS);
			return result;
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (DBException e) {
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			return result;
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg, e);
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		}

	}

	@Override
	// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ContactListInfoResultSet getContactListsInfoPagination(UserTrxInfo userTrxInfo, int first, int max, List<ListTypeName> listTypeNames) {

		ContactListInfoResultSet result = new ContactListInfoResultSet();

		try {
			result.setContactListInfoResultSet(listManegementBean.getContactListsInfo(userTrxInfo, first, max, listTypeNames));
			result.setStatus(ResponseStatus.SUCCESS);
			return result;
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg, e);
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		}
	}
	
	@Override
	// @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public ContactListInfoResultSet getContactListsInfoPaginationWithFlag(UserTrxInfo userTrxInfo, int first, int max, List<ListTypeName> listTypeNames) {

		ContactListInfoResultSet result = new ContactListInfoResultSet();

		try {
			result.setContactListInfoResultSet(listManegementBean.getContactListsInfo(userTrxInfo, first, max, listTypeNames));
			result.setStatus(ResponseStatus.SUCCESS);
			return result;
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg, e);
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		}
	}

	@Override
	public int countContactListsInfo(UserTrxInfo userTrxInfo, List<ListTypeName> listTypeNames) {
		try {
			return listManegementBean.countContactListsInfo(userTrxInfo, listTypeNames);
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ContactListResultSet searchLists(UserTrxInfo userTrxInfo, String listName, List<ListTypeName> listType) {

		ContactListResultSet result = new ContactListResultSet();
		try {

			result.setContactListInfoSet(listManegementBean.searchLists(userTrxInfo, listName, listType));
			result.setStatus(ResponseStatus.SUCCESS);
			return result;
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (ListNotFoundException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "List NotFound");
			result.setStatus(ResponseStatus.LIST_NOT_FOUND);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		}

	}

	@Override
	public FileResult createNewListFromFile(UserTrxInfo userTrxInfo, FileDetails fileDetails) {

		FileValidation validation;
		FileResult result = new FileResult();
		try {

			validation = listManegementBean.createNewList(userTrxInfo, fileDetails);
			result.setFileResult(validation);
			result.setStatus(ResponseStatus.SUCCESS);
			return result;

		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
			return result;

		} catch (DuplicateListNameException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Duplicate List Name");
			result.setStatus(ResponseStatus.DUPLICATE_LIST_NAME);
			result.setErrorMessage(e.getMessage());
			return result;

		} catch (IOException e) {
                        reportAppError(AppErrors.IO_ERROR, "IO Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (InvalidFileException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Invalid File");
			result.setStatus(ResponseStatus.INVALED_FILE);
			result.setErrorMessage("There is no valid records.");
			return result;
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		}
	}

	@Override
	public ContactResultSet expandContactList(UserTrxInfo userTrxInfo, ContactListModel contactsList) {
		ContactResultSet result = new ContactResultSet();

		try {
			listManegementBean.expandContactList(userTrxInfo, contactsList);
			result.setStatus(ResponseStatus.SUCCESS);
			listLogger.debug(userTrxInfo.logId() + "all contacts are vaild with no duplication");
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (DuplicateContactException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Duplicate Contact");
			if (e.isEntireListFlag()) {
				result.setStatus(ResponseStatus.DUPLICATE_CONTACT);
				result.setErrorMessage(e.getMessage());
			} else {
				result.setContact(listConversionBean.getContactsModel(e.getContacts()));
				result.setStatus(ResponseStatus.PARTIAL_UPDATE);
				result.setErrorMessage("Some contacts are duplicated.");

			}
		} catch (LockedListException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Locked List");
			result.setStatus(ResponseStatus.LOCKED_LIST);
			result.setErrorMessage(e.getMessage());

		} catch (ListNotFoundException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "List NotFound");
			result.setStatus(ResponseStatus.LIST_NOT_FOUND);
			result.setErrorMessage(e.getMessage());

		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());

		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());

		}

		return result;

	}

	@Override
	public ResultStatus deleteSubContactList(UserTrxInfo userTrxInfo, ContactListModel contactsList) {
		ResultStatus result = new ResultStatus();

		try {
			listManegementBean.deleteSubContactList(userTrxInfo, contactsList);
			result.setStatus(ResponseStatus.SUCCESS);
			return result;
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage("this account can't perform this action.");
			return result;
		} catch (LockedListException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Locked List");
			result.setStatus(ResponseStatus.LOCKED_LIST);
			result.setErrorMessage("this list is locked due to assotiation to Active Campagin.");
			return result;
		} catch (ListNotFoundException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "List NotFound");
			result.setStatus(ResponseStatus.LIST_NOT_FOUND);
			result.setErrorMessage(e.getListName() + "Not found in DataBase.");
			return result;
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		}

	}

	@Override
	public ResultStatus deleteContactList(UserTrxInfo userTrxInfo, ContactListInfoModel contactsInfoList) {
		ResultStatus result = new ResultStatus();

		try {
			listManegementBean.deleteContactList(userTrxInfo, contactsInfoList);
			result.setStatus(ResponseStatus.SUCCESS);
			return result;
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage("this account can't perform this action.");
			return result;
		} catch (LockedListException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Locked List");
			result.setStatus(ResponseStatus.LOCKED_LIST);
			result.setErrorMessage("this list is locked due to assotiation to Active Campagin.");
			return result;
		} catch (ListNotFoundException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "List NotFound");
			result.setStatus(ResponseStatus.LIST_NOT_FOUND);
			result.setErrorMessage(e.getListName() + "Not found in DataBase.");
			return result;
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			return result;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.edafa.web2sms.service.list.interfaces.ListsManegementService#
	 * exportListToCsvFile(com.edafa.web2sms.service.model.UserTrxInfo,
	 * java.lang.Integer) Inactive until now ...
	 */
	// @Override
	// public void exportListToFile(UserTrxInfo userTrxInfo, Integer listId) {
	//
	// try {
	// listManegementBean.exportListToFile(userTrxInfo, listId);
	// // result.setStatus(ResponseStatus.SUCCESS);
	// // return result;
	// } catch (IneligibleAccountException e) {
	//
	// // result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
	// // result.setErrorMessage("this account can't perform this action.");
	// // return result;
	// } catch (DBException e) {
	//
	// // result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
	// // result.setErrorMessage("this account can't perform this action.");
	// // return result;
	// }
	// }

	public FileTokenResult exportListToCsvFile(UserTrxInfo userTrxInfo, Integer listId) {
		FileTokenResult result = new FileTokenResult();
		try {
			String fileToken = listManegementBean.exportListToCsvFile(userTrxInfo, listId);
			result.setStatus(ResponseStatus.SUCCESS);
			result.setFileToken(fileToken);
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
			String logMsg = userTrxInfo.logInfo();
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage("Database error");
			String logMsg = userTrxInfo.logInfo();
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
		} catch (IOException e) {
                        reportAppError(AppErrors.IO_ERROR, "IO Failure");
			result.setStatus(ResponseStatus.FAIL);
			String logMsg = userTrxInfo.logInfo();
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
		} catch (Exception e) {
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
			String logMsg = userTrxInfo.logInfo();
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
		}
		return result;
	}

	@Override
	public ContactResultSet searchContacts(UserTrxInfo userTrxInfo, String contact, Integer listId) {
		ContactResultSet result = new ContactResultSet();
		List<ContactModel> resultList;

		try {

			resultList = listManegementBean.searchContacts(userTrxInfo, contact, listId);
			result.setContact(resultList);
			if (result.getContact() != null) {
				result.setStatus(ResponseStatus.SUCCESS);
			} else {
				result.setStatus(ResponseStatus.CONTACTS_NOT_FOUND);
			}
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);

		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.INVALID_REQUEST);
			result.setErrorMessage("DataBase Error");
		} catch (Exception e) {
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
		}
		return result;
	}

	@Override
	public ResultStatus editContactListName(UserTrxInfo userTrxInfo, Integer listId, String newListName) {
		ResultStatus result = new ResultStatus();

		try {
			listManegementBean.editContactListName(userTrxInfo, listId, newListName);
			result.setStatus(ResponseStatus.SUCCESS);
			return result;
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.INVALID_REQUEST);
			result.setErrorMessage("DataBase Error");
		} catch (ListNotFoundException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "List NotFound");
			result.setStatus(ResponseStatus.LIST_NOT_FOUND);
			result.setErrorMessage(e.getMessage());
			return result;
		} catch (LockedListException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Locked List");
			result.setStatus(ResponseStatus.LOCKED_LIST);
			result.setErrorMessage("this list is locked due to assotiation to Active Campagin.");
			return result;
		} catch (DuplicateListNameException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Duplicate List Name");
			result.setStatus(ResponseStatus.DUPLICATE_LIST_NAME);
			result.setErrorMessage("DuplicateListNameException");
			return result;
		}
		return result;
	}

	@Override
	public ResultStatus editContacts(UserTrxInfo userTrxInfo, ContactModel contact, String msisdn, Integer listId) {
		ResultStatus result = new ResultStatus();

		try {
			listManegementBean.editContacts(userTrxInfo, contact, msisdn, listId);
			result.setStatus(ResponseStatus.SUCCESS);
			return result;
		} catch (IneligibleAccountException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.INVALID_REQUEST);
			result.setErrorMessage("DataBase Error");
		} catch (ContactNotFoundException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Contact NotFound");
			result.setStatus(ResponseStatus.CONTACTS_NOT_FOUND);
			result.setErrorMessage("contact with MSISDN[" + msisdn + "] not found.");
		}
		return result;

	}

//	@Override
//	public ResultStatus createIntraList(UserTrxInfo userTrxInfo) {
//		ResultStatus result = new ResultStatus(ResponseStatus.SUCCESS);
//
//		try {
//			listManegementBean.fetchIntraList(userTrxInfo);
//		} catch (DBException e) {
//			result.setStatus(ResponseStatus.FAIL);
//			result.setErrorMessage(e.getMessage());
//		} catch (Exception e) {
//			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
//			appLogger.error(logMsg, e);
//			listLogger.error(logMsg + e.getMessage());
//			result.setStatus(ResponseStatus.FAIL);
//			result.setErrorMessage(logMsg);
//		}
//		return result;
//	}

	@Override
	public ResultStatus handleIntraList(UserTrxInfo userTrxInfo) {
		ResultStatus result = new ResultStatus();
		try {
			listManegementBean.handleIntraList(userTrxInfo);
			result.setStatus(ResponseStatus.SUCCESS);

		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.INVALID_REQUEST);
			result.setErrorMessage("DataBase Error");
			String logMsg = userTrxInfo.logInfo() + "DBException while handling this request";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
		} catch (InterruptedException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Operation Interrupted");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage("InterruptedException while removing intra list");
			String logMsg = userTrxInfo.logInfo() + "InterruptedException while removing intra list";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
		} catch (ExecutionException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Operation execution failed");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage("ExecutionException while removing intra list");
			String logMsg = userTrxInfo.logInfo() + "ExecutionException while removing intra list";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
		} catch (ListNotFoundException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "List NotFound");
			result.setStatus(ResponseStatus.LIST_NOT_FOUND);
			result.setErrorMessage("ListNotFoundException");
			String logMsg = userTrxInfo.logInfo() + "ListNotFoundException while removing intra list";
			appLogger.error(logMsg, e);
			listLogger.error(logMsg + e.getMessage());
		} catch (IntraListInquiryFailed e) {
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			String logMsg = userTrxInfo.logInfo() + e.getMessage();
			appLogger.error(logMsg, e);
		} catch (InvalidCustomerForQuotaInquiry e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Invalid Customer For Quota Inquiry");
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage("InvalidCustomerForQuotaInquiry while calling ODS");
			String logMsg = userTrxInfo.logInfo() + "InvalidCustomerForQuotaInquiry while calling ODS";
			appLogger.error(logMsg, e);
		}
		
		return result;
	}
        
        private void reportAppError(AppErrors error, String msg) {
		appErrorManagerAdapter.reportAppError(error, msg, ErrorsSource.LIST_MANAGEMENT);
	}

//	@Override
//	public ResultStatus createIntraList(UserTrxInfo userTrxInfo) {
//		ResultStatus result = new ResultStatus(ResponseStatus.SUCCESS);
//
//		try {
//			listManegementBean.fetchIntraList(userTrxInfo);
//		} catch (DBException e) {
//			result.setStatus(ResponseStatus.FAIL);
//			result.setErrorMessage(e.getMessage());
//		} catch (Exception e) {
//			String logMsg = userTrxInfo.logInfo() + "error while handling this request";
//			appLogger.error(logMsg, e);
//			listLogger.error(logMsg + e.getMessage());
//			result.setStatus(ResponseStatus.FAIL);
//			result.setErrorMessage(logMsg);
//		}
//		return result;
//	}

}
