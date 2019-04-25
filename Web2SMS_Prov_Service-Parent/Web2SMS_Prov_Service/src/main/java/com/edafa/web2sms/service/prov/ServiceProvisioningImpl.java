package com.edafa.web2sms.service.prov;

import com.edafa.jee.apperr.AppError;
import com.edafa.jee.apperr.monitor.AppErrorManager;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.adapters.tibco.exception.SRCreationFailed;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.alarm.ErrorsSource;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.DuplicateProvioniongRequest;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidSenderType;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameAlreadyAttached;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameNotAttached;
import com.edafa.web2sms.service.enums.ProvResponseStatus;
import com.edafa.web2sms.service.model.ProvResultStatus;
import com.edafa.web2sms.service.model.ProvTrxInfo;
import com.edafa.web2sms.service.prov.exception.InvalidProvRequestException;
import com.edafa.web2sms.service.prov.interfaces.ServiceProvisioningInterface;
import com.edafa.web2sms.service.prov.interfaces.ServiceProvisioningLocal;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.sms.exception.InvalidSMSSender;

/**
 * Session Bean implementation class ServiceProvisioningImpl
 */
@Stateless
@WebService(name = "ServiceProvisioning", portName = "ServiceProvisioning", targetNamespace = "http://www.edafa.com/web2sms/service/prov/", endpointInterface = "com.edafa.web2sms.service.prov.interfaces.ServiceProvisioningInterface")
public class ServiceProvisioningImpl implements ServiceProvisioningInterface {
	Logger appLogger = LogManager.getLogger(LoggersEnum.PROV_UTILS.name());
	Logger provLogger = LogManager.getLogger(LoggersEnum.PROV.name());

	@EJB
	ServiceProvisioningLocal serviceProvisioning;
        @EJB
	AppErrorManagerAdapter appErrorManagerAdapter;

	public ProvResultStatus requestChangeSender(ProvTrxInfo provTrxInfo, String oldSender, String newSender) {
		ProvResultStatus status = new ProvResultStatus(ProvResponseStatus.SUCCESS);
		if (!provTrxInfo.isValid() || oldSender == null || oldSender.isEmpty() || newSender == null
				|| newSender.isEmpty()) {
			provLogger.info("Received invalid change sender request: provTrxInfo=" + provTrxInfo + ", oldSender="
					+ oldSender + ", newSender=" + newSender);
			status.setStatus(ProvResponseStatus.INVALID_REQUEST);
			return status;
		}

		try {
			serviceProvisioning.requestChangeSender(provTrxInfo, oldSender, newSender);
		} catch (DBException e) {
			appLogger.error(provTrxInfo.logId() + "Datebase error: ", e);
			provLogger.error(provTrxInfo.logId() + "Datebase error: ", e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
                    status.setStatus(ProvResponseStatus.FAIL);
		} catch (AccountNotFoundException e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
			status.setStatus(ProvResponseStatus.ACCOUNT_NOT_FOUND);
			status.setErrorMessage(e.getMessage());
		} catch (SenderNameAlreadyAttached e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INVALID_OPERATION, "Sender Name Already Attached");
			status.setStatus(ProvResponseStatus.SENDER_NAME_ALREADY_ATTACHED);
			status.setErrorMessage(e.getMessage());
		} catch (InvalidSMSSender e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid SMSSender");
			status.setStatus(ProvResponseStatus.INVALID_SENDER);
			status.setErrorMessage(e.getMessage());
		} catch (SRCreationFailed e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INVALID_OPERATION, "SR Creation Failed");
			status.setStatus(ProvResponseStatus.SR_CREATION_FAILED);
			status.setErrorMessage(e.getMessage());
		} catch (DuplicateProvioniongRequest e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INVALID_OPERATION, "Duplicate Provioniong Request");
			status.setStatus(ProvResponseStatus.DUPLICATE_REQUEST);
			status.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			provLogger.error(provTrxInfo.logId() + "Failed to handle request", e);
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			status.setStatus(ProvResponseStatus.FAIL);
		}
		provLogger.info(provTrxInfo.logId() + "Final returned status: " + status.getStatus());
		return status;
	}

	@Override
	public ProvResultStatus requestAddSender(ProvTrxInfo provTrxInfo, String newSender) {
		ProvResultStatus status = new ProvResultStatus(ProvResponseStatus.SUCCESS);
		if (!provTrxInfo.isValid() || newSender == null || newSender.isEmpty() ) {
			provLogger.info("Received invalid Delete sender request: provTrxInfo=" + provTrxInfo + ", Sender="
					+ newSender );
			status.setStatus(ProvResponseStatus.INVALID_REQUEST);
			return status;
		}
			try {
				serviceProvisioning.requestAddSender(provTrxInfo, newSender);
			} catch (DBException e) {
				appLogger.error(provTrxInfo.logId() + "Datebase error: ", e);
				provLogger.error(provTrxInfo.logId() + "Datebase error: ", e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
                    status.setStatus(ProvResponseStatus.FAIL);
			} catch (AccountNotFoundException e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
                                reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
				status.setStatus(ProvResponseStatus.ACCOUNT_NOT_FOUND);
				status.setErrorMessage(e.getMessage());
			} catch (SenderNameAlreadyAttached e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
                                reportAppError(AppErrors.INVALID_OPERATION, "Sender Name Already Attached");
				status.setStatus(ProvResponseStatus.SENDER_NAME_ALREADY_ATTACHED);
				status.setErrorMessage(e.getMessage());
			} catch (InvalidSMSSender e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
                                reportAppError(AppErrors.INVALID_REQUEST, "Invalid SMS Sender");
				status.setStatus(ProvResponseStatus.INVALID_SENDER);
				status.setErrorMessage(e.getMessage());
			} catch (SRCreationFailed e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
                                reportAppError(AppErrors.INVALID_OPERATION, "SR Creation Failed");
				status.setStatus(ProvResponseStatus.SR_CREATION_FAILED);
				status.setErrorMessage(e.getMessage());
			} catch (DuplicateProvioniongRequest e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
                                reportAppError(AppErrors.INVALID_OPERATION, "Duplicate Provioniong Request");
				status.setStatus(ProvResponseStatus.DUPLICATE_REQUEST);
				status.setErrorMessage(e.getMessage());
			} catch (InvalidSenderType e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
                                reportAppError(AppErrors.INVALID_REQUEST, "Invalid Sender Type");
				status.setStatus(ProvResponseStatus.INVALID_SENDER);
				status.setErrorMessage(e.getMessage());
			} catch (SenderNameNotAttached e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
                                reportAppError(AppErrors.INVALID_OPERATION, "Sender Name NotAttached");
				status.setStatus(ProvResponseStatus.SENDER_NAME_ALREADY_ATTACHED);
				status.setErrorMessage(e.getMessage());
			} catch (InvalidProvRequestException e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
                                reportAppError(AppErrors.INVALID_REQUEST, "Invalid Prov Request");
				status.setStatus(ProvResponseStatus.INVALID_REQUEST);
				status.setErrorMessage(e.getMessage());
			} catch (Exception e) {
				provLogger.error(provTrxInfo.logId() + "Failed to handle request", e);
                                reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
				status.setStatus(ProvResponseStatus.FAIL);
			}
		provLogger.info(provTrxInfo.logId() + "Final returned status: " + status.getStatus());
		return status;
	}

	@Override
	public ProvResultStatus requestDeleteSender(ProvTrxInfo provTrxInfo, String sender) {
		ProvResultStatus status = new ProvResultStatus(ProvResponseStatus.SUCCESS);
		if (!provTrxInfo.isValid() || sender == null || sender.isEmpty() ) {
			provLogger.info("Received invalid Delete sender request: provTrxInfo=" + provTrxInfo + ", Sender="
					+ sender );
			status.setStatus(ProvResponseStatus.INVALID_REQUEST);
			return status;
		}
			try {
				serviceProvisioning.requestDeleteSender(provTrxInfo, sender);
			} catch (DBException e) {
				appLogger.error(provTrxInfo.logId() + "Datebase error: ", e);
				provLogger.error(provTrxInfo.logId() + "Datebase error: ", e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
                   	status.setStatus(ProvResponseStatus.FAIL);
			} catch (AccountNotFoundException e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
                                reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
				status.setStatus(ProvResponseStatus.ACCOUNT_NOT_FOUND);
				status.setErrorMessage(e.getMessage());
			} catch (SenderNameAlreadyAttached e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
                                reportAppError(AppErrors.INVALID_OPERATION, "Sender Name Already Attached");
				status.setStatus(ProvResponseStatus.SENDER_NAME_ALREADY_ATTACHED);
				status.setErrorMessage(e.getMessage());
			} catch (InvalidSMSSender e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
                                reportAppError(AppErrors.INVALID_REQUEST, "Invalid SMS Sender");
				status.setStatus(ProvResponseStatus.INVALID_SENDER);
				status.setErrorMessage(e.getMessage());
			} catch (SRCreationFailed e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
                                reportAppError(AppErrors.INVALID_OPERATION, "SR Creation Failed");
				status.setStatus(ProvResponseStatus.SR_CREATION_FAILED);
				status.setErrorMessage(e.getMessage());
			} catch (DuplicateProvioniongRequest e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
                                reportAppError(AppErrors.INVALID_OPERATION, "Duplicate Provioniong Request");
				status.setStatus(ProvResponseStatus.DUPLICATE_REQUEST);
				status.setErrorMessage(e.getMessage());
			} catch (InvalidSenderType e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
                                reportAppError(AppErrors.INVALID_REQUEST, "Invalid Sender Type");
				status.setStatus(ProvResponseStatus.INVALID_SENDER);
				status.setErrorMessage(e.getMessage());
			} catch (SenderNameNotAttached e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
                                reportAppError(AppErrors.INVALID_OPERATION, "Sender Name Not Attached");
				status.setStatus(ProvResponseStatus.SENDER_NAME_ALREADY_ATTACHED);
				status.setErrorMessage(e.getMessage());
			} catch (InvalidProvRequestException e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
                                reportAppError(AppErrors.INVALID_REQUEST, "Invalid Prov Request");
				status.setStatus(ProvResponseStatus.INVALID_REQUEST);
				status.setErrorMessage(e.getMessage());
			} catch (Exception e) {
				provLogger.error(provTrxInfo.logId() + "Failed to handle request", e);
                                reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
				status.setStatus(ProvResponseStatus.FAIL);
			}
		provLogger.info(provTrxInfo.logId() + "Final returned status: " + status.getStatus());
		return status;
	}
        
        private void reportAppError(AppErrors error, String msg) {
		appErrorManagerAdapter.reportAppError(error, msg, ErrorsSource.APP_PROV);
	}

}
