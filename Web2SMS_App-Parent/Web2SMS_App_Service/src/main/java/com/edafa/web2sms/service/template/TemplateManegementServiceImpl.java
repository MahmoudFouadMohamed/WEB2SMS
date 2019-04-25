package com.edafa.web2sms.service.template;

import com.edafa.jee.apperr.AppError;
import com.edafa.jee.apperr.monitor.AppErrorManager;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.alarm.ErrorsSource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.service.enums.ResponseStatus;
import com.edafa.web2sms.service.model.ResultStatus;
import com.edafa.web2sms.service.model.TemplateModel;
import com.edafa.web2sms.service.model.UserTrxInfo;
import com.edafa.web2sms.service.template.exception.InvalidTemplateException;
import com.edafa.web2sms.service.template.exception.TemplatesNotFoundException;
import com.edafa.web2sms.service.template.interfaces.TemplateManegementBeanLocal;
import com.edafa.web2sms.service.template.interfaces.TemplateManegementService;
import com.edafa.web2sms.service.template.model.TemplatesResultSet;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

/**
 * Session Bean implementation class TemplateManegementServiceImpl
 */
@Stateless
@WebService(name = "TemplateManegementService", serviceName = "TemplateManegementService", targetNamespace = "http://www.edafa.com/web2sms/service/template", endpointInterface = "com.edafa.web2sms.service.template.interfaces.TemplateManegementService")
public class TemplateManegementServiceImpl implements TemplateManegementService {

	@EJB
	TemplateManegementBeanLocal templateBean;
	@EJB
	AppErrorManagerAdapter appErrorManagerAdapter;
        
	Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
	Logger templateLogger = LogManager.getLogger(LoggersEnum.TEMPLATE_MNGMT.name());

	/**
	 * Default constructor.
	 */
	public TemplateManegementServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public TemplatesResultSet getUserTemplates(UserTrxInfo userTrxInfo) {

		TemplatesResultSet result = new TemplatesResultSet();
		if (userTrxInfo.isValid()) {
			try {
				result.setTemplateList(templateBean.getUserTemplates(userTrxInfo));
				result.setStatus(ResponseStatus.SUCCESS);
			} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
 				result.setStatus(ResponseStatus.FAIL);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + "DataBase error ", e);
				appLogger.error(userTrxInfo.logId() + "failed to handle DBException.", e);
			} catch (IneligibleAccountException e) {
                                reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
				result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + "Account status: " + userTrxInfo.getUser(), e);
				appLogger.error(userTrxInfo.logInfo()+"Account has no permission to templates", e);
			} catch (TemplatesNotFoundException e) {
                                reportAppError(AppErrors.INVALID_OPERATION, "Templates NotFound");
				result.setStatus(ResponseStatus.TEMPLATES_NOT_FOUND);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + e.getMessage(), e);
				appLogger.error(userTrxInfo.logInfo()+"Failed to find template", e);
			}
		} else{
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
			result.setStatus(ResponseStatus.INVALID_REQUEST);
			result.setErrorMessage("INVALID_REQUEST_INFO");			
		}
		templateLogger.info(userTrxInfo.logInfo()+"return status is: "+result.getStatus().name());
		return result;
	}
	
	
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public TemplatesResultSet getUserAndAdminTemplates(UserTrxInfo userTrxInfo) {

		TemplatesResultSet result = new TemplatesResultSet();
		if (userTrxInfo.isValid()) {
			try {
				result.setTemplateList(templateBean.getUserAndAdminTemplates(userTrxInfo));
				result.setStatus(ResponseStatus.SUCCESS);
			} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
 				result.setStatus(ResponseStatus.FAIL);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + "DataBase error ", e);
				appLogger.error(userTrxInfo.logId() + "failed to handle DBException.", e);
			} catch (IneligibleAccountException e) {
                                reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
				result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + "Account status: " + userTrxInfo.getUser(), e);
				appLogger.error(userTrxInfo.logInfo()+"Account has no permission to templates", e);
			} catch (TemplatesNotFoundException e) {
                                reportAppError(AppErrors.INVALID_OPERATION, "Templates NotFound");
				result.setStatus(ResponseStatus.TEMPLATES_NOT_FOUND);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + e.getMessage(), e);
				appLogger.error(userTrxInfo.logInfo()+"Failed to find template", e);
			}
		} else{
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
			result.setStatus(ResponseStatus.INVALID_REQUEST);
			result.setErrorMessage("INVALID_REQUEST_INFO");			
		}
		templateLogger.info(userTrxInfo.logInfo()+"return status is: "+result.getStatus().name());
		return result;
	}
	

	@Override
	public ResultStatus createTemplate(UserTrxInfo userTrxInfo, TemplateModel templateModel) {
		ResultStatus result = new ResultStatus();
		if (userTrxInfo.isValid()) {
			try {
				templateBean.createTemplate(userTrxInfo, templateModel);
				result.setStatus(ResponseStatus.SUCCESS);
			} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
 				result.setStatus(ResponseStatus.FAIL);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + "DataBase error ", e);
				appLogger.error(userTrxInfo.logId() + "failed to handle DBException.", e);
			} catch (IneligibleAccountException e) {
                                reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
				result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + "Account status: " + userTrxInfo.getUser(), e);
				appLogger.error(userTrxInfo.logInfo(), e);
			} catch (InvalidTemplateException e) {
                                reportAppError(AppErrors.INVALID_REQUEST, "Invalid Template");
				result.setStatus(ResponseStatus.INVALID_REQUEST);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + e.getMessage(), e);
				appLogger.error(userTrxInfo.logId() + "Invalid template parameter", e);
				
			}
		} else{
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
			result.setStatus(ResponseStatus.INVALID_REQUEST);
			result.setErrorMessage("INVALID_REQUEST_INFO");			
		}
		templateLogger.info(userTrxInfo.logInfo()+"return status is: "+result.getStatus().name());
		return result;

	}

	@Override
	public ResultStatus deleteTemplate(UserTrxInfo userTrxInfo, Integer templateId) {
		ResultStatus result = new ResultStatus();
		if (userTrxInfo.isValid()) {
			try {
				templateBean.deleteTemplate(userTrxInfo, templateId);
				result.setStatus(ResponseStatus.SUCCESS);
			} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
 				result.setStatus(ResponseStatus.FAIL);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + "DataBase error ", e);
				appLogger.error(userTrxInfo.logId() + "failed to handle DBException.", e);
			} catch (IneligibleAccountException e) {
                                reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
				result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + "Account status: " + userTrxInfo.getUser(), e);
				appLogger.error(userTrxInfo.logInfo(), e);
			} catch (TemplatesNotFoundException e) {
                                reportAppError(AppErrors.INVALID_OPERATION, "Templates NotFound");
				result.setStatus(ResponseStatus.TEMPLATES_NOT_FOUND);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + e.getMessage(), e);
				appLogger.error(userTrxInfo.logInfo()+"Failed to find template", e);
			}
		} else{
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
			result.setStatus(ResponseStatus.INVALID_REQUEST);
			result.setErrorMessage("INVALID_REQUEST_INFO");			
		}
		templateLogger.info(userTrxInfo.logInfo()+"return status is: "+result.getStatus().name());
		return result;

	}

	@Override
	public ResultStatus updateTemplate(UserTrxInfo userTrxInfo, TemplateModel templateModel) {
		ResultStatus result = new ResultStatus();
		if (userTrxInfo.isValid()) {
			try {
				templateBean.updateTemplate(userTrxInfo, templateModel);
				result.setStatus(ResponseStatus.SUCCESS);
			} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
 				result.setStatus(ResponseStatus.FAIL);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + "DataBase error ", e);
				appLogger.error(userTrxInfo.logId() + "failed to handle DBException.", e);
			} catch (IneligibleAccountException e) {
                                reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
				result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + "Account status: " + userTrxInfo.getUser(), e);
				appLogger.error(userTrxInfo.logInfo(), e);
			} catch (TemplatesNotFoundException e) {
                                reportAppError(AppErrors.INVALID_OPERATION, "Templates NotFound");
				result.setStatus(ResponseStatus.TEMPLATES_NOT_FOUND);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + e.getMessage(), e);
				appLogger.error(userTrxInfo.logInfo()+"Failed to find template", e);
			} catch (InvalidTemplateException e) {
                                reportAppError(AppErrors.INVALID_REQUEST, "Invalid Template");
				result.setStatus(ResponseStatus.INVALID_REQUEST);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + e.getMessage(), e);
				appLogger.error(userTrxInfo.logId() + "Invalid template parameter", e);
			}
		} else{
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
			result.setStatus(ResponseStatus.INVALID_REQUEST);
			result.setErrorMessage("INVALID_REQUEST_INFO");			
		}
		templateLogger.info(userTrxInfo.logInfo()+"return status is: "+result.getStatus().name());
		return result;

	}

	@Override
	public TemplatesResultSet getAdminTemplates(UserTrxInfo userTrxInfo) {
		TemplatesResultSet result = new TemplatesResultSet();
		if (userTrxInfo.isValid()) {
			try {
				result.setTemplateList(templateBean.getAdminTemplates(userTrxInfo));
				result.setStatus(ResponseStatus.SUCCESS);
			} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
 				result.setStatus(ResponseStatus.FAIL);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + "DataBase error ", e);
				appLogger.error(userTrxInfo.logId() + "failed to handle DBException.", e);
			} catch (IneligibleAccountException e) {
                                reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
				result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + "Account status: " + userTrxInfo.getUser(), e);
				appLogger.error(userTrxInfo.logInfo(), e);
			} catch (TemplatesNotFoundException e) {
                                reportAppError(AppErrors.INVALID_OPERATION, "Templates NotFound");
				result.setStatus(ResponseStatus.TEMPLATES_NOT_FOUND);
				result.setErrorMessage(e.getMessage());
				templateLogger.error(userTrxInfo.logId() + e.getMessage(), e);
				appLogger.error(userTrxInfo.logInfo()+"Failed to find template", e);
			}
		} else{
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
			result.setStatus(ResponseStatus.INVALID_REQUEST);
			result.setErrorMessage("INVALID_REQUEST_INFO: "+ userTrxInfo.logInfo());			
		}
		templateLogger.info(userTrxInfo.logInfo()+"return status is: "+result.getStatus().name());
		return result;
	}
        

        private void reportAppError(AppErrors error, String msg) {
		appErrorManagerAdapter.reportAppError(error, msg, ErrorsSource.TEMPLATE_MANAGEMENT);
	}

}
