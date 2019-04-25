package com.edafa.web2sms.prov.tibco;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.adapters.cloud.exception.FailedToCallBackCloud;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountSenderDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountUserDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ProvRequestDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ProvRequestStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ProvRequestTypeDaoLocal;
import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.dalayer.enums.ProvReqStatusName;
import com.edafa.web2sms.dalayer.enums.ProvRequestTypeName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountStatus;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.ProvRequestStatus;
import com.edafa.web2sms.dalayer.model.ProvRequestType;
import com.edafa.web2sms.prov.tibco.interfaces.Web2SMSTIBCOProvisioning;
import com.edafa.web2sms.prov.tibco.types.AccountInfo;
import com.edafa.web2sms.prov.tibco.types.ActivateServiceRequest;
import com.edafa.web2sms.prov.tibco.types.ActivateServiceResponse;
import com.edafa.web2sms.prov.tibco.types.CancelProvRequest;
import com.edafa.web2sms.prov.tibco.types.CancelProvRequestResponse;
import com.edafa.web2sms.prov.tibco.types.ChangeSenderNameRequest;
import com.edafa.web2sms.prov.tibco.types.ChangeSenderNameResponse;
import com.edafa.web2sms.prov.tibco.types.ConfirmSenderNameAddRequest;
import com.edafa.web2sms.prov.tibco.types.ConfirmSenderNameAddResponse;
import com.edafa.web2sms.prov.tibco.types.ConfirmSenderNameChangeRequest;
import com.edafa.web2sms.prov.tibco.types.ConfirmSenderNameChangeResponse;
import com.edafa.web2sms.prov.tibco.types.DeactivateServiceRequest;
import com.edafa.web2sms.prov.tibco.types.DeactivateServiceResponse;
import com.edafa.web2sms.prov.tibco.types.DeleteSenderNameRequest;
import com.edafa.web2sms.prov.tibco.types.DeleteSenderNameResponse;
import com.edafa.web2sms.prov.tibco.types.DowngradeServiceRequest;
import com.edafa.web2sms.prov.tibco.types.DowngradeServiceResponse;
import com.edafa.web2sms.prov.tibco.types.GetAccountInfoRequest;
import com.edafa.web2sms.prov.tibco.types.GetAccountInfoResponse;
import com.edafa.web2sms.prov.tibco.types.PendingProvRequest;
import com.edafa.web2sms.prov.tibco.types.ProvisioningStatus;
import com.edafa.web2sms.prov.tibco.types.UpgradeServiceRequest;
import com.edafa.web2sms.prov.tibco.types.UpgradeServiceResponse;
import com.edafa.web2sms.prov.utils.XMLGregorianCalendarConverter;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountAlreadyActiveException;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.DuplicateProvioniongRequest;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidAccountException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidAccountStateException;
import com.edafa.web2sms.acc_manag.service.account.exception.ProvRequestNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameAlreadyAttached;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameNotAttached;
import com.edafa.web2sms.acc_manag.service.account.exception.TierNotFoundException;
import com.edafa.web2sms.service.enums.ProvResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.AccountModel;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.service.model.ProvTrxInfo;
import com.edafa.web2sms.service.model.ProvisioningRequest;
import com.edafa.web2sms.acc_manag.service.model.TierModel;
import com.edafa.web2sms.service.model.TrxInfo;
import com.edafa.web2sms.service.prov.exception.InvalidProvRequestException;
import com.edafa.web2sms.service.prov.interfaces.ServiceProvisioningLocal;
import com.edafa.web2sms.utils.TrxId;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.sms.MsisdnFormat;
import com.edafa.web2sms.utils.sms.SMSUtils;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidSMSSender;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountManegementFacingLocal;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.UserManagementFacingLocal;
import com.edafa.web2sms.utils.sms.exception.InvalidSMSSender;

/**
 * This class was generated by the JAX-WS RI. JAX-WS RI 2.2.8-b13937 Generated
 * source version: 2.2
 * 
 */
@Stateless
@LocalBean
@WebService(serviceName = "Web2SMSTIBCOProvisioingService", targetNamespace = "http://www.edafa.com/web2sms/prov/TIBCO", endpointInterface = "com.edafa.web2sms.prov.tibco.interfaces.Web2SMSTIBCOProvisioning")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
public class Web2SMSTIBCOProvisioingImpl implements Web2SMSTIBCOProvisioning {

	private Logger appLogger = LogManager.getLogger(LoggersEnum.PROV_UTILS.name());
	private Logger provLogger = LogManager.getLogger(LoggersEnum.PROV.name());

	private final String TRX_ID_PREFIX = "2";

	public Web2SMSTIBCOProvisioingImpl() {
	}

	@EJB
	ServiceProvisioningLocal serviceProvisioning;

	@EJB
	AccountManegementFacingLocal accountManegement;

	@EJB
	UserManagementFacingLocal userManegement;

	@EJB
	AccountSenderDaoLocal accountSenderDao;

	@EJB
	AccountStatusDaoLocal accountStatusDao;

	@EJB
	ProvRequestStatusDaoLocal provRequestStatusDao;

	@EJB
	ProvRequestTypeDaoLocal provRequestTypeDao;

	@EJB
	ProvRequestDaoLocal provRequestDao;

	@EJB
	AccountUserDaoLocal accountUserDao;

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ActivateServiceResponse activateService(ActivateServiceRequest request) {
		ProvisioningRequest provRequest = null;
		String logMsg;
		ActivateServiceResponse resp = new ActivateServiceResponse();
		ProvisioningStatus provStatus = new ProvisioningStatus();
		resp.setReturnStatus(provStatus);
		provStatus.setStatus(ProvResponseStatus.SUCCESS);
		ProvTrxInfo provTrxInfo = new ProvTrxInfo(TrxId.getTrxId(TRX_ID_PREFIX));
		provTrxInfo.setProvReqType(ProvRequestTypeName.ACTIVATE_ACCOUNT);
		provLogger.info(provTrxInfo.logInfo() + request.logInfo());

		provLogger.debug(provTrxInfo.logId() + "Validating the request");

		if (!request.isValid()) {
			provLogger.error(provTrxInfo.logId() + "The provisioning request is not valid");
			provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);

		} else {
			try {
				provLogger.debug(provTrxInfo.logId() + "Valid request");
				provLogger.info(provTrxInfo.logId() + "Retrieve provisioning request from database");
				provRequest = serviceProvisioning.findProvisioningRequest(provTrxInfo, request.getRequestId());
				provLogger.debug(provTrxInfo.logId() + "Provisioning request retrieved");
				provRequest.setMSISDN(request.getBillingMSISDN());
				provRequest.setRatePlan(request.getRatePlan());
				provRequest.setSenderName(request.getSenderName());

			} catch (ProvRequestNotFoundException e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
				provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
				provStatus.setErrorMessage(e.getMessage());
			} catch (DBException e) {
				appLogger.error(provTrxInfo.logId() + "database error", e);
				provLogger.error(provTrxInfo.logId() + "database error" + e);
				provStatus.setStatus(ProvResponseStatus.FAIL);
			}

			if (provRequest != null) {

				if (!provRequest.getRequestType().equals(ProvRequestTypeName.ACTIVATE_ACCOUNT)) {
					logMsg = "Porvisioning request type mismatch, expected type: "
							+ ProvRequestTypeName.ACTIVATE_ACCOUNT.toString() + ", found type: "
							+ provRequest.getRequestType().toString();
					provLogger.error(provTrxInfo.logId() + logMsg);
					provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
					provStatus.setErrorMessage(logMsg);

				} else {
					try {

						serviceProvisioning.activateService(provTrxInfo, provRequest);

					} catch (DBException e) {
						logMsg = "database error ";
						appLogger.error(provTrxInfo.logId() + logMsg, e);
						provLogger.error(provTrxInfo.logId() + logMsg + e.getCause().getMessage());
						provStatus.setStatus(ProvResponseStatus.FAIL);
					} catch (InvalidAccountException | InvalidProvRequestException e) {
						provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
						provStatus.setErrorMessage(e.getMessage());
					} catch (AccountAlreadyActiveException e) {
						provStatus.setStatus(ProvResponseStatus.ACCOUNT_ALREADY_ACTIVE);
						provStatus.setErrorMessage(e.getMessage());
					} catch (TierNotFoundException e) {
						provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
						provStatus.setErrorMessage(e.getMessage());
					} catch (SenderNameAlreadyAttached e) {
						provStatus.setStatus(ProvResponseStatus.SENDER_NAME_ALREADY_ATTACHED);
						provStatus.setErrorMessage(e.getMessage());
					} catch (FailedToCallBackCloud e) {
						provStatus.setStatus(ProvResponseStatus.CLOUD_CALL_BACK_FAILED);
						provStatus.setErrorMessage(e.getMessage());
					} catch (ProvRequestNotFoundException e) {
						provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
						provStatus.setErrorMessage(e.getMessage());
					} catch (InvalidSMSSender e) {
						provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
						provStatus.setErrorMessage(e.getMessage());
					} catch (Exception e) {
						logMsg = "failed to activate the account ";
						appLogger.error(provTrxInfo.logId() + logMsg, e);
						provLogger.error(provTrxInfo.logId() + logMsg, e);
						provStatus.setStatus(ProvResponseStatus.FAIL);

					}
				}
			}
		}

		provLogger.info(provTrxInfo.logId() + "final status: " + provStatus.getStatus());
		return resp;
	}

	@Override
	public DowngradeServiceResponse downgradeService(DowngradeServiceRequest request) {
		ProvisioningRequest provRequest = null;
		String logMsg;
		DowngradeServiceResponse resp = new DowngradeServiceResponse();
		ProvisioningStatus provStatus = new ProvisioningStatus();
		resp.setReturnStatus(provStatus);
		provStatus.setStatus(ProvResponseStatus.SUCCESS);
		ProvTrxInfo provTrxInfo = new ProvTrxInfo(TrxId.getTrxId(TRX_ID_PREFIX));
		provTrxInfo.setProvReqType(ProvRequestTypeName.DOWNGRADE_ACCOUNT);
		provLogger.info(provTrxInfo.logInfo() + request.logInfo());

		provLogger.debug(provTrxInfo.logId() + "Validating the request");

		if (!request.isValid()) {
			provLogger.error(provTrxInfo.logId() + "The provisioning request is not valid");
			provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);

		} else {
			try {
				try {
					provLogger.debug(provTrxInfo.logId() + "Valid request");
					provLogger.info(provTrxInfo.logId() + "Retrieve provisioning request from database");
					provRequest = serviceProvisioning.findProvisioningRequest(provTrxInfo, request.getRequestId());
					provLogger.debug(provTrxInfo.logId() + "Provisioning request retrieved");
					provRequest.setRatePlan(request.getNewRatePlan());

				} catch (ProvRequestNotFoundException e) {
					provLogger.error(provTrxInfo.logId() + "No prov request with id(" + request.getRequestId() + ")");
					provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
					provStatus.setErrorMessage(e.getMessage());
				} catch (DBException e) {
					appLogger.error(provTrxInfo.logId() + "Database error", e);
					provLogger.error(provTrxInfo.logId() + "Database error" + e);
					provStatus.setStatus(ProvResponseStatus.FAIL);
				}

				if (provRequest != null) {

					if (!provRequest.getRequestType().equals(ProvRequestTypeName.DOWNGRADE_ACCOUNT)) {
						logMsg = "Porvisioning request type mismatch, expected type: "
								+ ProvRequestTypeName.DOWNGRADE_ACCOUNT + ", found type: "
								+ provRequest.getRequestType();
						provLogger.error(provTrxInfo.logId() + logMsg);
						provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
						provStatus.setErrorMessage(logMsg);
					} else {

						try {

							serviceProvisioning.migrateService(provTrxInfo, provRequest);

						} catch (DBException e) {
							logMsg = "database error ";
							appLogger.error(provTrxInfo.logId() + logMsg, e);
							provLogger.error(provTrxInfo.logId() + logMsg + e.getCause().getMessage());
							provStatus.setStatus(ProvResponseStatus.FAIL);
						} catch (TierNotFoundException e) {
							provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
							provStatus.setErrorMessage(e.getMessage());
						} catch (FailedToCallBackCloud e) {
							provStatus.setStatus(ProvResponseStatus.CLOUD_CALL_BACK_FAILED);
							provStatus.setErrorMessage(e.getMessage());
						} catch (InvalidAccountStateException e) {
							provStatus.setStatus(ProvResponseStatus.INVALID_ACCOUNT_STATE);
							provStatus.setErrorMessage(e.getMessage());
						} catch (AccountNotFoundException e) {
							provStatus.setStatus(ProvResponseStatus.ACCOUNT_NOT_FOUND);
							provStatus.setErrorMessage(e.getMessage());
						} catch (InvalidProvRequestException e) {
							provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
							provStatus.setErrorMessage(e.getMessage());
						} catch (Exception e) {
							logMsg = "failed to migrate the account ";
							appLogger.error(provTrxInfo.logId() + logMsg, e);
							provLogger.error(provTrxInfo.logId() + logMsg + e.getMessage());
							provStatus.setStatus(ProvResponseStatus.FAIL);
							provStatus.setErrorMessage(e.getMessage());
						}

					}
				}
			} catch (Exception e) {
				logMsg = "Failed to migrate the account ";
				appLogger.error(provTrxInfo.logId() + logMsg, e);
				provLogger.error(provTrxInfo.logId() + logMsg + e.getMessage());
				provStatus.setStatus(ProvResponseStatus.CLOUD_CALL_BACK_FAILED);

			}
		}

		provLogger.info(provTrxInfo.logId() + "final status: " + provStatus.getStatus());
		return resp;
	}

	@Override
	public UpgradeServiceResponse upgradeService(UpgradeServiceRequest request) {
		ProvisioningRequest provRequest = null;
		String logMsg;
		UpgradeServiceResponse resp = new UpgradeServiceResponse();
		ProvisioningStatus provStatus = new ProvisioningStatus();
		resp.setReturnStatus(provStatus);
		provStatus.setStatus(ProvResponseStatus.SUCCESS);
		ProvTrxInfo provTrxInfo = new ProvTrxInfo(TrxId.getTrxId(TRX_ID_PREFIX));
		provTrxInfo.setProvReqType(ProvRequestTypeName.UPGRADE_ACCOUNT);
		provLogger.info(provTrxInfo.logInfo() + request.logInfo());

		provLogger.debug(provTrxInfo.logId() + "Validating the request");

		if (!request.isValid()) {
			provLogger.error(provTrxInfo.logId() + "The provisioning request is not valid");
			provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);

		} else {

			try {
				try {
					provLogger.debug(provTrxInfo.logId() + "Valid request");
					provLogger.info(provTrxInfo.logId() + "Retrieve provisioning request from database");
					provRequest = serviceProvisioning.findProvisioningRequest(provTrxInfo, request.getRequestId());
					provLogger.debug(provTrxInfo.logId() + "Provisioning request retrieved");
					provRequest.setRatePlan(request.getNewRatePlan());

				} catch (ProvRequestNotFoundException e) {
					provLogger.error(provTrxInfo.logId() + "No prov request with id(" + request.getRequestId() + ")");
					provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
					provStatus.setErrorMessage(e.getMessage());
				} catch (DBException e) {
					appLogger.error(provTrxInfo.logId() + "database error", e);
					provLogger.error(provTrxInfo.logId() + "database error", e);
					provStatus.setStatus(ProvResponseStatus.FAIL);
				}

				if (provRequest != null) {

					if (!provRequest.getRequestType().equals(provTrxInfo.getProvReqType())) {
						logMsg = "Porvisioning request type mismatch, expected type: " + provTrxInfo.getProvReqType()
								+ ", found type: " + provRequest.getRequestType();
						provLogger.error(provTrxInfo.logId() + logMsg);
						provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
						provStatus.setErrorMessage(logMsg);
					} else {
						try {

							serviceProvisioning.migrateService(provTrxInfo, provRequest);

						} catch (DBException e) {
							logMsg = "database error ";
							appLogger.error(provTrxInfo.logId() + logMsg, e);
							provLogger.error(provTrxInfo.logId() + logMsg, e);
							provStatus.setStatus(ProvResponseStatus.FAIL);
						} catch (TierNotFoundException e) {
							provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
						} catch (FailedToCallBackCloud e) {
							provStatus.setStatus(ProvResponseStatus.CLOUD_CALL_BACK_FAILED);
							provStatus.setErrorMessage(e.getMessage());
						} catch (InvalidAccountStateException e) {
							provStatus.setStatus(ProvResponseStatus.INVALID_ACCOUNT_STATE);
							provStatus.setErrorMessage(e.getMessage());
							;
						} catch (AccountNotFoundException e) {
							provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
							provStatus.setErrorMessage(e.getMessage());
						} catch (InvalidProvRequestException e) {
							provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
							provStatus.setErrorMessage(e.getMessage());
						}
					}
				}

			} catch (Exception e) {
				logMsg = "failed to migrate the account ";
				appLogger.error(provTrxInfo.logId() + logMsg, e);
				provLogger.error(provTrxInfo.logId() + logMsg, e);
				provStatus.setStatus(ProvResponseStatus.FAIL);
			}
		}

		provLogger.info(provTrxInfo.logId() + "Final status: " + provStatus.getStatus());
		return resp;
	}

	@Override
	public DeactivateServiceResponse deactivateService(DeactivateServiceRequest request) {
		DeactivateServiceResponse resp = new DeactivateServiceResponse();
		ProvisioningStatus status = new ProvisioningStatus();
		resp.setReturnStatus(status);
		status.setStatus(ProvResponseStatus.FAIL);

		return resp;
	}

	@Override
	public GetAccountInfoResponse getAccountInfo(GetAccountInfoRequest request) {
		AccountNotFoundException accountNotFound = null;
		GetAccountInfoResponse resp = new GetAccountInfoResponse();
		AccountInfo accountInfo = new AccountInfo();
		String accountHolderId = request.getAccountHolderId();
		String msisdn = request.getMSISDN();

		TrxInfo trxInfo = new TrxInfo();
		trxInfo.setTrxId(TrxId.getTrxId());

		provLogger.info(trxInfo.logId() + "Validating received " + request);

		if (!request.isValid()) {
			provLogger.error(trxInfo.logId() + "Invalid request" + request);
			accountInfo.setStatus(ProvResponseStatus.INVALID_REQUEST);
			accountInfo.setErrorMessage("Invalid Request.");
			resp.setAccountInfo(accountInfo);
			provLogger.info(trxInfo.logId() + "Final status: " + accountInfo.getStatus());
			return resp;
		}
		try {
			try {
				AccountModelFullInfo account;
				if (accountHolderId == null && msisdn != null) {
					provLogger.info(trxInfo.logId() + "formatting billing MSISDN: " + request.getMSISDN());
					msisdn = SMSUtils.formatAddress(msisdn, MsisdnFormat.INTER_CC_LOCAL);
					provLogger.info(trxInfo.logId() + "Getting account info by formatted billing MSISDN: " + msisdn);
					account = serviceProvisioning.findAccountByMSISDNFullInfo(trxInfo, msisdn);
				} else if (accountHolderId != null) {
					accountHolderId = request.getAccountHolderId().toLowerCase();
					provLogger.info(trxInfo.logId() + "Getting account info by account admin: " + accountHolderId);
					String[] acctHolder = accountHolderId.split("@");
					if (acctHolder.length < 1) {
						accountNotFound = new AccountNotFoundException();
					}
					account = serviceProvisioning.findAccountByCoAdminFullInfo(trxInfo,
							acctHolder.length == 2 ? acctHolder[1] : null);
				} else {
					account = null;
				}
				accountInfo = getAccountInfo(account);
			} catch (AccountNotFoundException e) {
				provLogger.warn(trxInfo.logId() + "Account not found in active state");
				accountNotFound = e;
			}
			List<ProvisioningRequest> activeProvRequests = new ArrayList<>();
			if (request.getAccountHolderId() == null && accountNotFound == null) {
				List<AccountStatus> statuses = new ArrayList<>();
				statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
				AccountUser user = accountUserDao.findAccountAdminUser(accountInfo.getAccountId(), statuses);
				if (user != null) {
					String acctAdmin = user.getUsername() + "@" + user.getAccount().getCompanyName();
					request.setAccountHolderId(acctAdmin.toLowerCase());
				}
			}
			if (request.getAccountHolderId() != null) {
				provLogger.info(trxInfo.logId() + "Getting pending provisioning requests by account admin: "
						+ accountHolderId);
				activeProvRequests = serviceProvisioning.findActiveProvisioningRequests(trxInfo, accountHolderId);
				for (ProvisioningRequest provisioningRequest : activeProvRequests) {
					PendingProvRequest pendingProvReq = getPendingProvReq(provisioningRequest);
					accountInfo.getPendingRequests().add(pendingProvReq);
				}
			}
			if (accountNotFound != null && activeProvRequests.isEmpty()) {
				accountInfo.setStatus(ProvResponseStatus.ACCOUNT_NOT_FOUND);
				accountInfo.setErrorMessage(accountNotFound.getMessage());
			} else {
				accountInfo.setStatus(ProvResponseStatus.SUCCESS);
			}
		} catch (DBException e) {
			provLogger.error(trxInfo.logId() + "Failed to retrieve account info from datebase ");
			appLogger.error(trxInfo.logId(), e);
			accountInfo.setStatus(ProvResponseStatus.FAIL);
			accountInfo.setErrorMessage("");

		} catch (Exception e) {
			String logMsg = trxInfo.logId() + "Failed to get account info ";
			provLogger.error(logMsg);
			appLogger.error(trxInfo.logId(), e);
			accountInfo.setStatus(ProvResponseStatus.FAIL);
		}

		resp.setAccountInfo(accountInfo);
		provLogger.info(trxInfo.logId() + "Final status: " + accountInfo.getStatus());
		return resp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.edafa.web2sms.prov.tibco.interfaces.Web2SMSTIBCOProvisioning#
	 * changeSenderName
	 * (com.edafa.web2sms.prov.tibco.types.ChangeSenderNameRequest) This method
	 * used when tibco want to change sender name, The initiation from tibco
	 * 
	 * @Override public ConfirmSenderNameAddResponse
	 * addSenderName(ConfirmSenderNameAddRequest request) {
	 * ConfirmSenderNameAddResponse resp = new ConfirmSenderNameAddResponse();
	 * 
	 * ProvisioningStatus provStatus = new ProvisioningStatus(); ProvTrxInfo
	 * provTrxInfo = new ProvTrxInfo(); resp.setReturnStatus(provStatus);
	 * provTrxInfo.setProvReqType(ProvRequestTypeName.ADD_SENDER_NAME); if
	 * (!request.isValid()) { provLogger.error("Invalid request " + request);
	 * provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST); return resp; }
	 * 
	 * provTrxInfo.setTrxId(TrxId.getTrxId(TRX_ID_PREFIX));
	 * provLogger.info(provTrxInfo.logInfo() + "Request to add sender name : " +
	 * request.getSenderName());
	 * 
	 * provLogger.debug(provTrxInfo.logId() +
	 * "Retrieving account from database"); AccountModel acct; try {
	 * provLogger.debug(provTrxInfo.logId() + "Check prov. request existance");
	 * checkProvRequestExistence(provTrxInfo,
	 * ProvRequestTypeName.ADD_SENDER_NAME, ProvReqStatusName.PENDING,
	 * request.getSenderName());
	 * 
	 * acct = accountManegement.findAccountByCoAdmin(provTrxInfo,
	 * request.getAccountHolderId
	 * ().substring(request.getAccountHolderId().indexOf("@")));
	 * 
	 * provLogger.debug(provTrxInfo.logId() +
	 * "Account retrived from database with id " + acct.logId());
	 * 
	 * provTrxInfo.setAccountId(acct.getAccountId());
	 * 
	 * provLogger.debug(provTrxInfo.logId() +
	 * "Initializing provisioning request"); ProvisioningRequest provRequest =
	 * new ProvisioningRequest(); String reqId = UUID.randomUUID().toString();
	 * provLogger.trace(provTrxInfo.logId() +
	 * "Generated provisioning request id=" + reqId);
	 * provRequest.setRequestId(reqId);
	 * provRequest.setStatus(ProvReqStatusName.PENDING);
	 * 
	 * List<AccountStatus> statuses = new ArrayList<>();
	 * statuses.add(accountStatusDao
	 * .getCachedObjectByName(AccountStatusName.ACTIVE)); AccountUser user =
	 * accountUserDao.findAccountAdminUser(acct.getAccountId(), statuses);
	 * String acctAdmin = user.getUsername() + "@" +
	 * user.getAccount().getCompanyName();
	 * 
	 * provRequest.setAccountAdmin(acctAdmin);
	 * 
	 * provRequest.setCompanyId(acct.getAccountId());
	 * provRequest.setCompanyName(acct.getCompanyName());
	 * provRequest.setEntryDate(new Date());
	 * provRequest.setRequestType(ProvRequestTypeName.ADD_SENDER_NAME);
	 * provRequest.setSenderName(request.getSenderName());
	 * provRequest.setMSISDN(acct.getBillingMsisdn());
	 * provLogger.info(provTrxInfo.logId() +
	 * "Change sender provisioning request: " + provRequest.logRequest());
	 * 
	 * try { serviceProvisioning.createProvisioningRequest(provTrxInfo,
	 * provRequest, false); } catch (DuplicateProvioniongRequest e) { // Should
	 * never happen provLogger.error(provTrxInfo.logId() +
	 * "Failed to create provisioning request", e); } catch
	 * (TierNotFoundException e) { // Should never happen
	 * provLogger.error(provTrxInfo.logId() +
	 * "Failed to create provisioning request", e); }
	 * 
	 * provLogger.debug(provTrxInfo.logId() + "Changing sender name");
	 * 
	 * accountManegement.addAccountSenderName(provTrxInfo,
	 * request.getSenderName());
	 * 
	 * provStatus.setStatus(ProvResponseStatus.SUCCESS);
	 * provLogger.info(provTrxInfo.logId() +
	 * "Account sender name changed successfully, delete the active prov request"
	 * ); serviceProvisioning.archiveProvisioningRequest(provTrxInfo, reqId,
	 * ProvReqStatusName.SUCCESS);
	 * 
	 * } catch (DBException e) { provLogger.error(provTrxInfo.logId() +
	 * "Database error ", e); provStatus.setStatus(ProvResponseStatus.FAIL); }
	 * catch (AccountNotFoundException e) { provLogger.error(provTrxInfo.logId()
	 * + e.getMessage());
	 * provStatus.setStatus(ProvResponseStatus.ACCOUNT_NOT_FOUND);
	 * provStatus.setErrorMessage(e.getMessage()); } catch
	 * (SenderNameAlreadyAttached e) { provLogger.error(provTrxInfo.logId() +
	 * e.getMessage());
	 * provStatus.setStatus(ProvResponseStatus.SENDER_NAME_ALREADY_ATTACHED);
	 * provStatus.setErrorMessage(e.getMessage()); } catch (InvalidSMSSender e)
	 * { provLogger.error(provTrxInfo.logId() + e.getMessage());
	 * provStatus.setStatus(ProvResponseStatus.FAIL);
	 * provStatus.setErrorMessage(e.getMessage()); } catch (Exception e) {
	 * provLogger.error(provTrxInfo.logId() +
	 * "Failed to change the sender name ", e);
	 * provStatus.setStatus(ProvResponseStatus.FAIL); }
	 * provLogger.info(provTrxInfo.logId() + "Final status: " +
	 * provStatus.getStatus()); return resp; }
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.edafa.web2sms.prov.tibco.interfaces.Web2SMSTIBCOProvisioning#
	 * changeSenderName
	 * (com.edafa.web2sms.prov.tibco.types.ChangeSenderNameRequest) This method
	 * used when tibco want to change sender name, The initiation from tibco
	 */
	@Override
	public ConfirmSenderNameAddResponse confirmSenderNameAdd(ConfirmSenderNameAddRequest request) {
		/*
		 * ConfirmSenderNameAddResponse resp = new
		 * ConfirmSenderNameAddResponse();
		 * 
		 * ProvisioningStatus provStatus = new ProvisioningStatus(); ProvTrxInfo
		 * provTrxInfo = new ProvTrxInfo(); resp.setReturnStatus(provStatus);
		 * provTrxInfo.setProvReqType(ProvRequestTypeName.ADD_SENDER_NAME); if
		 * (!request.isValid()) { provLogger.error("Invalid request " +
		 * request); provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
		 * return resp; }
		 * 
		 * provTrxInfo.setTrxId(TrxId.getTrxId(TRX_ID_PREFIX));
		 * provLogger.info(provTrxInfo.logInfo() +
		 * "Request to add sender name : " + request.getSenderName());
		 * 
		 * provLogger.debug(provTrxInfo.logId() +
		 * "Retrieving account from database"); AccountModel acct; try {
		 * provLogger.debug(provTrxInfo.logId() +
		 * "Check prov. request existance");
		 * checkProvRequestExistence(provTrxInfo,
		 * ProvRequestTypeName.ADD_SENDER_NAME, ProvReqStatusName.PENDING,
		 * request.getSenderName());
		 * 
		 * acct = accountManegement.findAccountByCoAdmin(provTrxInfo,
		 * request.getAccountHolderId
		 * ().substring(request.getAccountHolderId().indexOf("@")));
		 * 
		 * provLogger.debug(provTrxInfo.logId() +
		 * "Account retrived from database with id " + acct.logId());
		 * 
		 * provTrxInfo.setAccountId(acct.getAccountId());
		 * 
		 * provLogger.debug(provTrxInfo.logId() +
		 * "Initializing provisioning request"); ProvisioningRequest provRequest
		 * = new ProvisioningRequest(); String reqId =
		 * UUID.randomUUID().toString(); provLogger.trace(provTrxInfo.logId() +
		 * "Generated provisioning request id=" + reqId);
		 * provRequest.setRequestId(reqId);
		 * provRequest.setStatus(ProvReqStatusName.PENDING);
		 * 
		 * List<AccountStatus> statuses = new ArrayList<>();
		 * statuses.add(accountStatusDao
		 * .getCachedObjectByName(AccountStatusName.ACTIVE)); AccountUser user =
		 * accountUserDao.findAccountAdminUser(acct.getAccountId(), statuses);
		 * String acctAdmin = user.getUsername() + "@" +
		 * user.getAccount().getCompanyName();
		 * 
		 * provRequest.setAccountAdmin(acctAdmin);
		 * 
		 * provRequest.setCompanyId(acct.getAccountId());
		 * provRequest.setCompanyName(acct.getCompanyName());
		 * provRequest.setEntryDate(new Date());
		 * provRequest.setRequestType(ProvRequestTypeName.ADD_SENDER_NAME);
		 * provRequest.setSenderName(request.getSenderName());
		 * provRequest.setMSISDN(acct.getBillingMsisdn());
		 * provLogger.info(provTrxInfo.logId() +
		 * "Change sender provisioning request: " + provRequest.logRequest());
		 * 
		 * try { serviceProvisioning.createProvisioningRequest(provTrxInfo,
		 * provRequest, false); } catch (DuplicateProvioniongRequest e) { //
		 * Should never happen provLogger.error(provTrxInfo.logId() +
		 * "Failed to create provisioning request", e); } catch
		 * (TierNotFoundException e) { // Should never happen
		 * provLogger.error(provTrxInfo.logId() +
		 * "Failed to create provisioning request", e); }
		 * 
		 * provLogger.debug(provTrxInfo.logId() + "Changing sender name");
		 * 
		 * accountManegement.addAccountSenderName(provTrxInfo,
		 * request.getSenderName());
		 * 
		 * provStatus.setStatus(ProvResponseStatus.SUCCESS);
		 * provLogger.info(provTrxInfo.logId() +
		 * "Account sender name changed successfully, delete the active prov request"
		 * ); serviceProvisioning.archiveProvisioningRequest(provTrxInfo, reqId,
		 * ProvReqStatusName.SUCCESS);
		 * 
		 * } catch (DBException e) { provLogger.error(provTrxInfo.logId() +
		 * "Database error ", e); provStatus.setStatus(ProvResponseStatus.FAIL);
		 * } catch (AccountNotFoundException e) {
		 * provLogger.error(provTrxInfo.logId() + e.getMessage());
		 * provStatus.setStatus(ProvResponseStatus.ACCOUNT_NOT_FOUND);
		 * provStatus.setErrorMessage(e.getMessage()); } catch
		 * (SenderNameAlreadyAttached e) { provLogger.error(provTrxInfo.logId()
		 * + e.getMessage());
		 * provStatus.setStatus(ProvResponseStatus.SENDER_NAME_ALREADY_ATTACHED
		 * ); provStatus.setErrorMessage(e.getMessage()); } catch
		 * (InvalidSMSSender e) { provLogger.error(provTrxInfo.logId() +
		 * e.getMessage()); provStatus.setStatus(ProvResponseStatus.FAIL);
		 * provStatus.setErrorMessage(e.getMessage()); } catch (Exception e) {
		 * provLogger.error(provTrxInfo.logId() +
		 * "Failed to change the sender name ", e);
		 * provStatus.setStatus(ProvResponseStatus.FAIL); }
		 * provLogger.info(provTrxInfo.logId() + "Final status: " +
		 * provStatus.getStatus()); return resp;
		 */

		ProvisioningRequest provRequest = null;
		String logMsg;
		ConfirmSenderNameAddResponse resp = new ConfirmSenderNameAddResponse();
		ProvisioningStatus provStatus = new ProvisioningStatus();
		resp.setReturnStatus(provStatus);
		provStatus.setStatus(ProvResponseStatus.SUCCESS);
		ProvTrxInfo provTrxInfo = new ProvTrxInfo(TrxId.getTrxId(TRX_ID_PREFIX));
		provTrxInfo.setProvReqType(ProvRequestTypeName.ADD_SENDER_NAME);
		provLogger.info(provTrxInfo.logInfo() + request);

		provLogger.debug(provTrxInfo.logId() + "Validating the request");

		if (!request.isValid()) {
			provLogger.error(provTrxInfo.logId() + "The provisioning request is not valid");
			provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);

		} else {

			try {
				try {
					provLogger.debug(provTrxInfo.logId() + "Valid request");
					provLogger.info(provTrxInfo.logId() + "Retrieve provisioning request from database");
					provRequest = serviceProvisioning.findProvisioningRequest(provTrxInfo, request.getRequestId());
					provLogger.debug(provTrxInfo.logId() + "Provisioning request retrieved");

				} catch (ProvRequestNotFoundException e) {
					provLogger.error(provTrxInfo.logId() + "No prov request with id(" + request.getRequestId() + ")");
					provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
					provStatus.setErrorMessage(e.getMessage());
				} catch (DBException e) {
					appLogger.error(provTrxInfo.logId() + "database error", e);
					provLogger.error(provTrxInfo.logId() + "database error", e);
					provStatus.setStatus(ProvResponseStatus.FAIL);
				}

				if (provRequest != null) {

					if (!provRequest.getRequestType().equals(provTrxInfo.getProvReqType())) {
						logMsg = "Porvisioning request type mismatch, expected type: " + provTrxInfo.getProvReqType()
								+ ", found type: " + provRequest.getRequestType();
						provLogger.error(provTrxInfo.logId() + logMsg);
						provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
						provStatus.setErrorMessage(logMsg);
					} else {
						provLogger.debug(provTrxInfo.logId() + "Retrieving account from database");
						AccountModel acct;
						try {
							String[] accountHolder = provRequest.getAccountAdmin().split("@");

							if (accountHolder.length < 2) {
								throw new AccountNotFoundException();
							}
							acct = accountManegement.findAccountByCoAdmin(provTrxInfo.getAccountProvTrxInfo(), accountHolder[1]);

							provLogger.debug(provTrxInfo.logId() + "Account retrived from database with id "
									+ acct.logId());

							provTrxInfo.setAccountId(acct.getAccountId());
							provLogger.debug(provTrxInfo.logId() + "Adding sender name");
							accountManegement.addAccountSenderName(provTrxInfo.getAccountProvTrxInfo(), provRequest.getSenderName());
							provLogger.debug(provTrxInfo.logId() + "Archiving the request with status "
									+ ProvReqStatusName.SUCCESS);
							serviceProvisioning.archiveProvisioningRequest(provTrxInfo, provRequest.getRequestId(),
									ProvReqStatusName.SUCCESS);
						} catch (DBException e) {
							logMsg = "database error ";
							appLogger.error(provTrxInfo.logId() + logMsg, e);
							provLogger.error(provTrxInfo.logId() + logMsg, e);
							provStatus.setStatus(ProvResponseStatus.FAIL);

						} catch (SenderNameAlreadyAttached e) {
							// Should not happen
							provLogger.error(provTrxInfo.logId() + e.getMessage());
							provStatus.setStatus(ProvResponseStatus.SENDER_NAME_ALREADY_ATTACHED);
							provStatus.setErrorMessage(e.getMessage());
						} catch (InvalidSMSSender e) {
							provLogger.error(provTrxInfo.logId() + e.getMessage());
							provStatus.setStatus(ProvResponseStatus.INVALID_SENDER);
							provStatus.setErrorMessage(e.getMessage());
						}
					}
				}

			} catch (Exception e) {
				logMsg = "failed to add sender name ";
				appLogger.error(provTrxInfo.logId() + logMsg, e);
				provLogger.error(provTrxInfo.logId() + logMsg, e);
				provStatus.setStatus(ProvResponseStatus.FAIL);
			}
		}

		provLogger.info(provTrxInfo.logId() + "Final status: " + provStatus.getStatus());
		return resp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.edafa.web2sms.prov.tibco.interfaces.Web2SMSTIBCOProvisioning#
	 * changeSenderName
	 * (com.edafa.web2sms.prov.tibco.types.ChangeSenderNameRequest) This method
	 * used when tibco want to change sender name, The initiation from tibco
	 */
	@Override
	public ChangeSenderNameResponse changeSenderName(ChangeSenderNameRequest request) {
		ChangeSenderNameResponse resp = new ChangeSenderNameResponse();

		ProvisioningStatus provStatus = new ProvisioningStatus();
		ProvTrxInfo provTrxInfo = new ProvTrxInfo();
		resp.setReturnStatus(provStatus);
		provTrxInfo.setProvReqType(ProvRequestTypeName.CHANGE_SENDER_NAME);
		if (!request.isValid()) {
			provLogger.error("Invalid request " + request);
			provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
			return resp;
		}

		provTrxInfo.setTrxId(TrxId.getTrxId(TRX_ID_PREFIX));
		provLogger.info(provTrxInfo.logInfo() + "Request to change sender name to: " + request.getNewSenderName());

		provLogger.debug(provTrxInfo.logId() + "Retrieving account from database");
		AccountModel acct;
		try {
			provLogger.debug(provTrxInfo.logId() + "Check prov. request existance");
			checkProvRequestExistence(provTrxInfo, ProvRequestTypeName.CHANGE_SENDER_NAME, ProvReqStatusName.PENDING);

			String[] accountHolder = request.getAccountHolderId().split("@");

			if (accountHolder.length < 2) {
				throw new AccountNotFoundException();
			}
			acct = accountManegement.findAccountByCoAdmin(provTrxInfo.getAccountProvTrxInfo(), accountHolder[1]);

			provLogger.debug(provTrxInfo.logId() + "Account retrived from database with id " + acct.logId());

			provTrxInfo.setAccountId(acct.getAccountId());

			provLogger.debug(provTrxInfo.logId() + "Initializing provisioning request");
			ProvisioningRequest provRequest = new ProvisioningRequest();
			String reqId = UUID.randomUUID().toString();
			provLogger.trace(provTrxInfo.logId() + "Generated provisioning request id=" + reqId);
			provRequest.setRequestId(reqId);
			provRequest.setStatus(ProvReqStatusName.PENDING);

			List<AccountStatus> statuses = new ArrayList<>();
			statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
			AccountUser user = accountUserDao.findAccountAdminUser(acct.getAccountId(), statuses);
			String acctAdmin = user.getUsername() + "@" + user.getAccount().getCompanyName();

			provRequest.setAccountAdmin(acctAdmin);

			provRequest.setCompanyId(acct.getAccountId());
			provRequest.setCompanyName(acct.getCompanyName());
			provRequest.setEntryDate(new Date());
			provRequest.setRequestType(ProvRequestTypeName.CHANGE_SENDER_NAME);
			provRequest.setSenderName(request.getOldSenderName());
			provRequest.setNewSenderName(request.getNewSenderName());
			provRequest.setMSISDN(acct.getBillingMsisdn());
			provLogger.info(provTrxInfo.logId() + "Change sender provisioning request: " + provRequest.logRequest());

			try {
				serviceProvisioning.createProvisioningRequest(provTrxInfo, provRequest, false);
			} catch (DuplicateProvioniongRequest e) {
				// Should never happen
				provLogger.error(provTrxInfo.logId() + "Failed to create provisioning request", e);
			} catch (TierNotFoundException e) {
				// Should never happen
				provLogger.error(provTrxInfo.logId() + "Failed to create provisioning request", e);
			}

			provLogger.debug(provTrxInfo.logId() + "Changing sender name");

			accountManegement.changeAccountSenderName(provTrxInfo.getAccountProvTrxInfo(), request.getOldSenderName(),
					request.getNewSenderName());

			provStatus.setStatus(ProvResponseStatus.SUCCESS);
			provLogger.info(provTrxInfo.logId()
					+ "Account sender name changed successfully, delete the active prov request");
			serviceProvisioning.archiveProvisioningRequest(provTrxInfo, reqId, ProvReqStatusName.SUCCESS);

		} catch (DBException e) {
			provLogger.error(provTrxInfo.logId() + "Database error ", e);
			provStatus.setStatus(ProvResponseStatus.FAIL);
		} catch (AccountNotFoundException e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
			provStatus.setStatus(ProvResponseStatus.ACCOUNT_NOT_FOUND);
			provStatus.setErrorMessage(e.getMessage());
		} catch (SenderNameAlreadyAttached e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
			provStatus.setStatus(ProvResponseStatus.SENDER_NAME_ALREADY_ATTACHED);
			provStatus.setErrorMessage(e.getMessage());
		} catch (SenderNameNotAttached e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
			// TODO: add SENDER_NAME_Not_ATTACHED to ProvResponseStatus
			provStatus.setStatus(ProvResponseStatus.FAIL);
			provStatus.setErrorMessage(e.getMessage());
		} catch (AccountManagInvalidSMSSender e) {
			provLogger.error(provTrxInfo.logId() + e.getMessage());
			provStatus.setStatus(ProvResponseStatus.FAIL);
			provStatus.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			provLogger.error(provTrxInfo.logId() + "Failed to change the sender name ", e);
			provStatus.setStatus(ProvResponseStatus.FAIL);
		}
		provLogger.info(provTrxInfo.logId() + "Final status: " + provStatus.getStatus());
		return resp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.edafa.web2sms.prov.tibco.interfaces.Web2SMSTIBCOProvisioning#
	 * confirmSenderNameChange
	 * (com.edafa.web2sms.prov.tibco.types.ConfirmSenderNameChangeRequest) This
	 * method used when tibco receive change sender name request from UI, The
	 * initiation from customer
	 */
	@Override
	public ConfirmSenderNameChangeResponse confirmSenderNameChange(ConfirmSenderNameChangeRequest request) {
		ProvisioningRequest provRequest = null;
		String logMsg;
		ConfirmSenderNameChangeResponse resp = new ConfirmSenderNameChangeResponse();
		ProvisioningStatus provStatus = new ProvisioningStatus();
		resp.setReturnStatus(provStatus);
		provStatus.setStatus(ProvResponseStatus.SUCCESS);
		ProvTrxInfo provTrxInfo = new ProvTrxInfo(TrxId.getTrxId(TRX_ID_PREFIX));
		provTrxInfo.setProvReqType(ProvRequestTypeName.CHANGE_SENDER_NAME);
		provLogger.info(provTrxInfo.logInfo() + request);

		provLogger.debug(provTrxInfo.logId() + "Validating the request");

		if (!request.isValid()) {
			provLogger.error(provTrxInfo.logId() + "The provisioning request is not valid");
			provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);

		} else {

			try {
				try {
					provLogger.debug(provTrxInfo.logId() + "Valid request");
					provLogger.info(provTrxInfo.logId() + "Retrieve provisioning request from database");
					provRequest = serviceProvisioning.findProvisioningRequest(provTrxInfo, request.getRequestId());
					provLogger.debug(provTrxInfo.logId() + "Provisioning request retrieved");

				} catch (ProvRequestNotFoundException e) {
					provLogger.error(provTrxInfo.logId() + "No prov request with id(" + request.getRequestId() + ")");
					provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
					provStatus.setErrorMessage(e.getMessage());
				} catch (DBException e) {
					appLogger.error(provTrxInfo.logId() + "database error", e);
					provLogger.error(provTrxInfo.logId() + "database error", e);
					provStatus.setStatus(ProvResponseStatus.FAIL);
				}

				if (provRequest != null) {

					if (!provRequest.getRequestType().equals(provTrxInfo.getProvReqType())) {
						logMsg = "Porvisioning request type mismatch, expected type: " + provTrxInfo.getProvReqType()
								+ ", found type: " + provRequest.getRequestType();
						provLogger.error(provTrxInfo.logId() + logMsg);
						provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
						provStatus.setErrorMessage(logMsg);
					} else {
						provLogger.debug(provTrxInfo.logId() + "Retrieving account from database");
						AccountModel acct;
						try {
							String[] accountHolder = provRequest.getAccountAdmin().split("@");

							if (accountHolder.length < 2) {
								throw new AccountNotFoundException();
							}
							acct = accountManegement.findAccountByCoAdmin(provTrxInfo.getAccountProvTrxInfo(), accountHolder[1]);
							provLogger.debug(provTrxInfo.logId() + "Account retrived from database with id "
									+ acct.logId());

							provTrxInfo.setAccountId(acct.getAccountId());
							provLogger.debug(provTrxInfo.logId() + "Changing sender name");
							accountManegement.changeAccountSenderName(provTrxInfo.getAccountProvTrxInfo(), provRequest.getSenderName(),
									provRequest.getNewSenderName());
							provLogger.debug(provTrxInfo.logId() + "Archiving the request with status "
									+ ProvReqStatusName.SUCCESS);
							serviceProvisioning.archiveProvisioningRequest(provTrxInfo, provRequest.getRequestId(),
									ProvReqStatusName.SUCCESS);
						} catch (DBException e) {
							logMsg = "database error ";
							appLogger.error(provTrxInfo.logId() + logMsg, e);
							provLogger.error(provTrxInfo.logId() + logMsg, e);
							provStatus.setStatus(ProvResponseStatus.FAIL);

						} catch (SenderNameAlreadyAttached e) {
							// Should not happen
							provLogger.error(provTrxInfo.logId() + e.getMessage());
							provStatus.setStatus(ProvResponseStatus.SENDER_NAME_ALREADY_ATTACHED);
							provStatus.setErrorMessage(e.getMessage());
						} catch (AccountManagInvalidSMSSender e) {
							provLogger.error(provTrxInfo.logId() + e.getMessage());
							provStatus.setStatus(ProvResponseStatus.INVALID_SENDER);
							provStatus.setErrorMessage(e.getMessage());
						}
					}
				}

			} catch (Exception e) {
				logMsg = "failed to migrate the account ";
				appLogger.error(provTrxInfo.logId() + logMsg, e);
				provLogger.error(provTrxInfo.logId() + logMsg, e);
				provStatus.setStatus(ProvResponseStatus.FAIL);
			}
		}

		provLogger.info(provTrxInfo.logId() + "Final status: " + provStatus.getStatus());
		return resp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.edafa.web2sms.prov.tibco.interfaces.Web2SMSTIBCOProvisioning#
	 * deleteSenderName
	 * (com.edafa.web2sms.prov.tibco.types.DeleteSenderNameRequest)
	 */
	@Override
	public DeleteSenderNameResponse deleteSenderName(DeleteSenderNameRequest request) {
		DeleteSenderNameResponse resp = new DeleteSenderNameResponse();

		ProvisioningStatus provStatus = new ProvisioningStatus();
		ProvTrxInfo provTrxInfo = new ProvTrxInfo();
		resp.setReturnStatus(provStatus);
		provTrxInfo.setProvReqType(ProvRequestTypeName.DELETE_SENDER_NAME);
		if (!request.isValid()) {
			provLogger.error("Invalid request " + request);
			provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
			return resp;
		}

		provTrxInfo.setTrxId(TrxId.getTrxId(TRX_ID_PREFIX));
		provLogger.info(provTrxInfo.logInfo() + "Request to delete sender name : " + request.getSenderName());

		provLogger.debug(provTrxInfo.logId() + "Retrieving account from database");
		String reqId = UUID.randomUUID().toString();
		AccountModel acct;
		try {
			String[] accountHolder = request.getAccountHolderId().split("@");

			if (accountHolder.length < 2) {
				throw new AccountNotFoundException();
			}
			acct = accountManegement.findAccountByCoAdmin(provTrxInfo.getAccountProvTrxInfo(), accountHolder[1]);
			provTrxInfo.setAccountId(acct.getAccountId());

			provLogger.debug(provTrxInfo.logId() + "Check prov. request existance");
			checkProvRequestExistence(provTrxInfo, ProvRequestTypeName.DELETE_SENDER_NAME, ProvReqStatusName.PENDING,
					request.getSenderName());

			provLogger.debug(provTrxInfo.logId() + "Account retrived from database with id " + acct.logId());

			provTrxInfo.setAccountId(acct.getAccountId());

			provLogger.debug(provTrxInfo.logId() + "Initializing provisioning request");
			ProvisioningRequest provRequest = new ProvisioningRequest();
			provLogger.trace(provTrxInfo.logId() + "Generated provisioning request id=" + reqId);
			provRequest.setRequestId(reqId);
			provRequest.setStatus(ProvReqStatusName.PENDING);

			List<AccountStatus> statuses = new ArrayList<>();
			statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
			AccountUser user = accountUserDao.findAccountAdminUser(acct.getAccountId(), statuses);

			if (user != null) {
				String acctAdmin = user.getUsername() + "@" + user.getAccount().getCompanyName();

				provRequest.setAccountAdmin(acctAdmin);

				provRequest.setCompanyId(acct.getAccountId());
				provRequest.setCompanyName(acct.getCompanyName());
				provRequest.setEntryDate(new Date());
				provRequest.setRequestType(ProvRequestTypeName.DELETE_SENDER_NAME);
				provRequest.setSenderName(request.getSenderName());
				provRequest.setMSISDN(acct.getBillingMsisdn());
				provLogger
						.info(provTrxInfo.logId() + "Delete sender provisioning request: " + provRequest.logRequest());

				try {
					serviceProvisioning.createProvisioningRequest(provTrxInfo, provRequest, false);
				} catch (DuplicateProvioniongRequest e) {
					// Should never happen
					provLogger.error(provTrxInfo.logId() + "Failed to create provisioning request", e);
				} catch (TierNotFoundException e) {
					// Should never happen
					provLogger.error(provTrxInfo.logId() + "Failed to create provisioning request", e);
				}

				provLogger.debug(provTrxInfo.logId() + "Changing sender name");

				accountManegement.deleteAccountSenderName(provTrxInfo.getAccountProvTrxInfo(), request.getSenderName());

				provStatus.setStatus(ProvResponseStatus.SUCCESS);
				provLogger.info(provTrxInfo.logId()
						+ "Account sender name deleted successfully, delete the active prov request");
				serviceProvisioning.archiveProvisioningRequest(provTrxInfo, reqId, ProvReqStatusName.SUCCESS);
			}
		} catch (DBException e) {
			provLogger.error(provTrxInfo.logId() + "Database error ", e);
			provStatus.setStatus(ProvResponseStatus.FAIL);
			provLogger.info(provTrxInfo.logId() + "Account sender name failed to delete");
			try {
				serviceProvisioning.archiveProvisioningRequest(provTrxInfo, reqId, ProvReqStatusName.FAIL);
			} catch (DBException | ProvRequestNotFoundException e1) {
				provLogger.error(provTrxInfo.logId() + "failed to archive request with fail status", e);
			}
		} catch (AccountNotFoundException e) { // no need to archive already request doesn't persist
			provLogger.error(provTrxInfo.logId() + e.getMessage());
			provStatus.setStatus(ProvResponseStatus.ACCOUNT_NOT_FOUND);
			provStatus.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			provLogger.error(provTrxInfo.logId() + "Failed to Delete the sender name ", e);
			provStatus.setStatus(ProvResponseStatus.FAIL);
			try {
				provLogger.error(provTrxInfo.logId() + "try to archive request with fail status if found.");
				serviceProvisioning.archiveProvisioningRequest(provTrxInfo, reqId, ProvReqStatusName.FAIL);
			} catch (DBException | ProvRequestNotFoundException e1) {
				provLogger.error(provTrxInfo.logId() + "failed to archive request with fail status", e);
			}
		}
		provLogger.info(provTrxInfo.logId() + "Final status: " + provStatus.getStatus());
		return resp;
	}

	@Override
	public CancelProvRequestResponse cancelProvRequest(CancelProvRequest request) {
		CancelProvRequestResponse resp = new CancelProvRequestResponse();
		ProvisioningStatus provStatus = new ProvisioningStatus();
		resp.setReturnStatus(provStatus);
		provStatus.setStatus(ProvResponseStatus.SUCCESS);
		ProvTrxInfo provTrxInfo = new ProvTrxInfo(TrxId.getTrxId(TRX_ID_PREFIX));
		provLogger
				.info(provTrxInfo.logInfo() + "Received request to cancel provisioning request: " + request.logInfo());

		provLogger.debug(provTrxInfo.logId() + "Validating the request");

		if (!request.isValid()) {
			provLogger.error(provTrxInfo.logId() + "The request is invalid");
			provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
		}

		ProvisioningRequest provRequest = null;
		try {
			provLogger.debug(provTrxInfo.logId() + "Valid request");
			provLogger.info(provTrxInfo.logId() + "Retrieve provisioning request from database");
			provRequest = serviceProvisioning.findProvisioningRequest(provTrxInfo, request.getRequestId());
			provLogger.debug(provTrxInfo.logId() + "Provisioning request retrieved");

		} catch (ProvRequestNotFoundException e) {
			provLogger.error(provTrxInfo.logId() + "No prov request with id(" + request.getRequestId() + ")");
			provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
			provStatus.setErrorMessage(e.getMessage());
		} catch (DBException e) {
			appLogger.error(provTrxInfo.logId() + "Database error", e);
			provLogger.error(provTrxInfo.logId() + "Database error", e);
			provStatus.setStatus(ProvResponseStatus.FAIL);
		}

		if (provRequest != null) {
			try {
				serviceProvisioning.cancelProvisioningRequest(provTrxInfo, provRequest);
			} catch (DBException e) {
				provLogger.error(provTrxInfo.logId() + "Failed to cancel request, database error: " + e.getMessage());
				provStatus.setStatus(ProvResponseStatus.FAIL);
			} catch (ProvRequestNotFoundException e) {
				provLogger.error(provTrxInfo.logId() + e.getMessage());
				provStatus.setStatus(ProvResponseStatus.INVALID_REQUEST);
				provStatus.setErrorMessage(e.getMessage());
			} catch (FailedToCallBackCloud e) {
				provStatus.setStatus(ProvResponseStatus.CLOUD_CALL_BACK_FAILED);
				provStatus.setErrorMessage(e.getMessage());
			}
		}
		provLogger.info(provTrxInfo.logId() + "Final status: " + provStatus.getStatus());
		return resp;
	}

	// Helper mothods
	private AccountInfo getAccountInfo(AccountModelFullInfo account) {
		if (account == null) {
			return null;
		}
		AccountInfo acctInfo = new AccountInfo();
		acctInfo.setAccountId(account.getAccountId());
		acctInfo.setAccountStatus(account.getStatus());

		List<AccountStatus> statuses = new ArrayList<>();
		statuses.add(accountStatusDao.getCachedObjectByName(AccountStatusName.ACTIVE));
		AccountUser user = accountUserDao.findAccountAdminUser(account.getAccountId(), statuses);
		if (user != null) {
			String acctAdmin = user.getUsername() + "@" + user.getAccount().getCompanyName();

			acctInfo.setAccountAdmin(acctAdmin);
		}
		acctInfo.setCompanyName(account.getCompanyName());
		acctInfo.setBillingMsisdn(account.getBillingMsisdn());
		acctInfo.setCompanyName(account.getCompanyName());
		acctInfo.setTier(account.getTier());

		List<String> senders = account.getSenders();
		if (senders != null && senders.size() > 0)
			acctInfo.setSenderName(account.getSenders());
		return acctInfo;
	}

	private PendingProvRequest getPendingProvReq(ProvisioningRequest provisioningRequest) {
		PendingProvRequest pendingProvReq = new PendingProvRequest();
		pendingProvReq.setRequestId(provisioningRequest.getRequestId());
		pendingProvReq.setCompanyAdmin(provisioningRequest.getAccountAdmin());
		pendingProvReq.setCompanyId(provisioningRequest.getCompanyId());
		pendingProvReq.setCompanyName(provisioningRequest.getCompanyName());
		pendingProvReq.setRequstType(provisioningRequest.getRequestType());
		pendingProvReq.setSenderName(provisioningRequest.getSenderName());
		pendingProvReq.setRequestTimestamp(XMLGregorianCalendarConverter.asXMLGregorianCalendar(provisioningRequest
				.getEntryDate()));
		TierModel tier = provisioningRequest.getTier();
		if (tier != null) {
			pendingProvReq.setRateplan(provisioningRequest.getTier().getRateplan());
		}
		if (provisioningRequest.getNewSenderName() != null) {
			pendingProvReq.setNewSenderName(provisioningRequest.getNewSenderName());
		}
		return pendingProvReq;
	}

	private void checkProvRequestExistence(ProvTrxInfo trxInfo, ProvRequestTypeName provRequestType,
			ProvReqStatusName statusName) throws DBException, DuplicateProvioniongRequest {

		ProvRequestStatus status = provRequestStatusDao.getCachedObjectByName(statusName);
		ProvRequestType type = provRequestTypeDao.getCachedObjectByName(provRequestType);
		if (provRequestDao.countByCompanyIdAndRequestTypeAndStatus(trxInfo.getAccountId(), type, status) > 0) {
			DuplicateProvioniongRequest e = new DuplicateProvioniongRequest(provRequestType);
			throw e;
		}
	}

	private void checkProvRequestExistence(ProvTrxInfo trxInfo, ProvRequestTypeName provRequestType,
			ProvReqStatusName statusName, String sender) throws DBException, DuplicateProvioniongRequest,
			InvalidProvRequestException {

		ProvRequestStatus status = provRequestStatusDao.getCachedObjectByName(statusName);
		ProvRequestType type = provRequestTypeDao.getCachedObjectByName(provRequestType);

		switch (provRequestType) {

		case ADD_SENDER_NAME: {
			if (provRequestDao.countByCompanyIdRequestTypeStatusAndSender(trxInfo.getAccountId(), type, status, sender) > 0) {
				DuplicateProvioniongRequest e = new DuplicateProvioniongRequest(provRequestType);
				throw e;
			}
			break;
		}
		case CHANGE_SENDER_NAME: {
			// TODO: what will happens if more than one change request for same
			// sender

			break;
		}
		case DELETE_SENDER_NAME: {
			if (provRequestDao.countByCompanyIdRequestTypeStatusAndSender(trxInfo.getAccountId(), type, status, sender) > 0) {
				DuplicateProvioniongRequest e = new DuplicateProvioniongRequest(provRequestType);
				throw e;
			}

			// Can't delete all sender... Account should has at least one sender
			int provDelRequests = provRequestDao.countByCompanyIdAndRequestTypeAndStatus(trxInfo.getAccountId(), type,
					status);
			int acctSenders = accountSenderDao.CountByAccountId(trxInfo.getAccountId());
			if (acctSenders - provDelRequests < 2) {
				InvalidProvRequestException e = new InvalidProvRequestException(
						"Account Should has at least one sender");
				throw e;
			}
			break;
		}
		default:
			break;

		}

		/*
		 * if (provRequestDao.countByCompanyIdAndRequestTypeAndStatus(trxInfo.
		 * getAccountId(), type, status) > 0 ) { DuplicateProvioniongRequest e =
		 * new DuplicateProvioniongRequest(provRequestType); throw e; }
		 */
	}

}
