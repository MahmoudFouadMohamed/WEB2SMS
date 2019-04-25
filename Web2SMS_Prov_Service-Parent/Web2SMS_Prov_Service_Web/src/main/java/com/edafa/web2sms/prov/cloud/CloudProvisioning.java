package com.edafa.web2sms.prov.cloud;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.adapters.tibco.exception.SRCreationFailed;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.alarm.ErrorsSource;
import com.edafa.web2sms.dalayer.enums.ProvReqStatusName;
import com.edafa.web2sms.dalayer.enums.ProvRequestTypeName;
import com.edafa.web2sms.dalayer.enums.TierTypesEnum;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.prov.cloud.model.CompanySubscriptionProvisionFullInfoType;
import com.edafa.web2sms.prov.cloud.model.CompanyUserProvisionFullInfoType;
import com.edafa.web2sms.prov.cloud.model.GenericProductProvisionRequest;
import com.edafa.web2sms.prov.cloud.model.ProvisioningBaseType;
import com.edafa.web2sms.prov.cloud.model.UserProvisionBaseType;
import com.edafa.web2sms.prov.model.ProvResultStatus;
import com.edafa.web2sms.acc_manag.service.account.exception.DuplicateProvioniongRequest;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidTierTypeException;
import com.edafa.web2sms.acc_manag.service.account.exception.TierNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.service.enums.ProvResponseStatus;
import com.edafa.web2sms.service.model.ProvTrxInfo;
import com.edafa.web2sms.service.model.ProvisioningRequest;
import com.edafa.web2sms.acc_manag.service.model.TierModel;
import com.edafa.web2sms.service.prov.interfaces.ServiceProvisioningLocal;
import com.edafa.web2sms.utils.TrxId;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.sms.exception.InvalidSMSSender;
import com.sun.jersey.api.client.ClientResponse.Status;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountManegementFacingLocal;


@Stateless
@LocalBean
@Path("/cloud")
public class CloudProvisioning {
	Logger appLogger = LogManager.getLogger(LoggersEnum.PROV_UTILS.name());
	Logger provLogger = LogManager.getLogger(LoggersEnum.PROV.name());

	private final String PROV_TRX_ID_PREFIX = "2";

	@EJB
	ServiceProvisioningLocal serviceProvisioning;

	@EJB
	AccountManegementFacingLocal accountManagement;

	public CloudProvisioning() {
	}
        
        @EJB
	AppErrorManagerAdapter appErrorManagerAdapter;

	@POST
	@Consumes("application/xml")
	public Response handleProvisioningRequest(GenericProductProvisionRequest provRequest) {

		ProvisioningBaseType req;
		boolean valid = true;
		ProvResultStatus result = null;

		try {
			if ((req = provRequest.getGenericProductProvision()) != null) {
				req.setTypeName("GenericProductProvision");
				provLogger.info(req);
				valid = req.isValid();
				if (valid) {
					result = handleGenericProductProvisionReq((CompanySubscriptionProvisionFullInfoType) req);
				}
			} else if ((req = provRequest.getGenericProductUpgrade()) != null) {
				req.setTypeName("GenericProductUpgrade");
				provLogger.info(req);
				valid = req.isValid();
				if (valid) {
					result = handleGenericProductUpgrade((CompanySubscriptionProvisionFullInfoType) req);
				}
			} else if ((req = provRequest.getGenericProductDowngrade()) != null) {
				req.setTypeName("GenericProductDowngrade");
				provLogger.info(req);
				valid = req.isValid();
				if (valid) {
					result = handleGenericProductDowngrade((CompanySubscriptionProvisionFullInfoType) req);
				}
			} else if ((req = provRequest.getGenericProductSuspension()) != null) {
				req.setTypeName("GenericProductSuspension");
				provLogger.info(req);
				valid = req.isValid();
				if (valid) {
					result = handleGenericProductSuspension(req);
				}
			} else if ((req = provRequest.getGenericProductActivation()) != null) {
				req.setTypeName("GenericProductActivation");
				provLogger.info(req);
				valid = req.isValid();
				if (valid)
					result = handleGenericProductActivation(req);
			} else if ((req = provRequest.getGenericProductDelete()) != null) {
				req.setTypeName("GenericProductDelete");
				provLogger.info(req);
				valid = req.isValid();
				if (valid) {
					result = handleGenericProductDelete(req);
				}
			} else if ((req = provRequest.getGenericProductUserCreate()) != null) {
				req.setTypeName("GenericProductUserCreate");
				provLogger.info(req);
				valid = req.isValid();
				if (valid) {
					result = handleGenericProductUserCreate((CompanyUserProvisionFullInfoType) req);
				}
			} else if ((req = provRequest.getGenericProductUserDelete()) != null) {
				req.setTypeName("GenericProductUserDelete");
				provLogger.info(req);
				valid = req.isValid();
				if (valid) {
					result = handleGenericProductUserDelete((UserProvisionBaseType) req);
				}
			}
			//
			if (valid) {
				if (req != null) {
					if (result != null) {
						provLogger.info(req.logId() + "return status: " + result.getStatus());
						switch (result.getStatus()) {
						case SUCCESS:
							return Response.ok().build();
						case INVALID_REQUEST:
                                                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
							return Response.status(Status.BAD_REQUEST).build();
						case FAIL:
						default:
                                                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
							return Response.serverError().build();
						}
					} else
						return Response.serverError().build();
				} else
					return Response.ok().build();

			} else {
                                reportAppError(AppErrors.INVALID_REQUEST, "Invalid Request");
				provLogger.info(req.logId() + "is not valid request: " + req);
				return Response.status(Status.BAD_REQUEST).build();
			}

		} catch (Exception e) {
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			provLogger.error("Unhandled exception while processing cloud prov. request ", e);
			return Response.serverError().build();

		}
	}

	public ProvResultStatus handleGenericProductProvisionReq(CompanySubscriptionProvisionFullInfoType req) {
		ProvisioningRequest provReq = getProvisioningRequest(req);
		provReq.setRequestType(ProvRequestTypeName.ACTIVATE_ACCOUNT);
		TierModel tier = new TierModel();
		tier.setTierId(Integer.valueOf(req.getOther().getProductTier()));
		provReq.setTier(tier);
		ProvResultStatus result = handleCloudProvRequest(provReq);
		return result;
	}

	public ProvResultStatus handleGenericProductUpgrade(CompanySubscriptionProvisionFullInfoType req) {
		ProvisioningRequest provReq = getProvisioningRequest(req);
		provReq.setRequestType(ProvRequestTypeName.UPGRADE_ACCOUNT);
		TierModel tier = new TierModel();
		tier.setTierId(Integer.valueOf(req.getOther().getProductTier()));
		provReq.setTier(tier);
		ProvResultStatus result = handleCloudProvRequest(provReq);
		return result;
	}

	public ProvResultStatus handleGenericProductDowngrade(CompanySubscriptionProvisionFullInfoType req) {
		ProvisioningRequest provReq = getProvisioningRequest(req);
		provReq.setRequestType(ProvRequestTypeName.DOWNGRADE_ACCOUNT);
		TierModel tier = new TierModel();
		tier.setTierId(Integer.valueOf(req.getOther().getProductTier()));
		provReq.setTier(tier);
		ProvResultStatus result = handleCloudProvRequest(provReq);
		return result;
	}

	public ProvResultStatus handleGenericProductUserCreate(CompanyUserProvisionFullInfoType req) {
		ProvisioningRequest provReq = getProvisioningRequest(req);
		provReq.setRequestType(ProvRequestTypeName.USER_CREATE);
		provReq.setUserId(req.getUserID());
		ProvResultStatus result = handleCloudProvRequest(provReq);
		return result;
	}

	public ProvResultStatus handleGenericProductUserDelete(UserProvisionBaseType req) {
		ProvisioningRequest provReq = getProvisioningRequest(req);
		provReq.setRequestType(ProvRequestTypeName.USER_DELETE);
		provReq.setUserId(req.getUserID());
		ProvResultStatus result = handleCloudProvRequest(provReq);
		return result;
	}

	public ProvResultStatus handleGenericProductSuspension(ProvisioningBaseType req) {
		ProvisioningRequest provReq = getProvisioningRequest(req);
		provReq.setRequestType(ProvRequestTypeName.SUSPEND_ACCOUNT);
		ProvResultStatus result = handleCloudProvRequest(provReq);
		return result;
	}

	public ProvResultStatus handleGenericProductActivation(ProvisioningBaseType req) {
		ProvisioningRequest provReq = getProvisioningRequest(req);
		provReq.setRequestType(ProvRequestTypeName.REACTIVATE_ACCT_AFTER_SUSPENSION);
		ProvResultStatus result = handleCloudProvRequest(provReq);
		return result;
	}

	public ProvResultStatus handleGenericProductDelete(ProvisioningBaseType req) {
		ProvisioningRequest provReq = getProvisioningRequest(req);
		provReq.setRequestType(ProvRequestTypeName.DEACTIVATE_ACCOUNT);
		ProvResultStatus result = handleCloudProvRequest(provReq);
		return result;
	}

	public ProvResultStatus handleCloudProvRequest(ProvisioningRequest provRequest) {
		ProvResultStatus status = new ProvResultStatus();
		status.setStatus(ProvResponseStatus.SUCCESS);
		ProvTrxInfo trxInfo = new ProvTrxInfo(TrxId.getTrxId(PROV_TRX_ID_PREFIX));
		if (provRequest == null) {
			provLogger.info(trxInfo.logInfo() + "received null ");
			status.setStatus(ProvResponseStatus.INVALID_REQUEST);
			status.setErrorMessage("The request is null");
			return status;
		}

		provLogger.info(trxInfo.logInfo() + "received cloud request " + provRequest.logRequest());
		provRequest.setStatus(ProvReqStatusName.PENDING);

		// Mapping the companyId to accountId: they are the same
		String acctId = provRequest.getCompanyId();
		provLogger.debug(trxInfo.logId() + "setting account id with company id =" + acctId);
		trxInfo.setAccountId(acctId);

		try {
			switch (provRequest.getRequestType()) {
			case ACTIVATE_ACCOUNT:
				if (provRequest.getTier() == null || !provRequest.getTier().isValid()) {
					provLogger.error(trxInfo.logId() + "the tier is invalid (" + provRequest.getTier() + ")");
					throw new TierNotFoundException("The tier is not found or it is invalid ");
				}

				provLogger.debug(trxInfo.logId() + "Create provisioning request with SR");
				serviceProvisioning.handleCloudProvRequest(trxInfo, provRequest, true);
				break;

			case UPGRADE_ACCOUNT:
			case DOWNGRADE_ACCOUNT:
				provLogger.debug("recieving " + provRequest.getRequestType().name());
				provLogger.debug("finding account with id : " + provRequest.getCompanyId());

				Account account = accountManagement.findAccountById(trxInfo.getAccountProvTrxInfo(), provRequest.getCompanyId());
				provLogger.debug("found account " + account + "with tier type :"
						+ account.getTier().getTierType().getTierTypeName());

				if (provRequest.getTier() == null || !provRequest.getTier().isValid()) {
					provLogger.error(trxInfo.logId() + "the tier is invalid (" + provRequest.getTier() + ")");
					throw new TierNotFoundException("The tier is not found or it is invalid ");
				}
				if (account != null)
					if (account.getTier().getTierType().getTierTypeName().equals(TierTypesEnum.PREPAID)) {
						provLogger.error(trxInfo.logId()
								+ "account with one_off bundle can't request upgarde or downgrade quota.");
						throw new InvalidTierTypeException("Account Ineligible to ("
								+ provRequest.getRequestType().name() + ") with tier type: ("
								+ account.getTier().getTierType().getTierTypeName().name() + ")");
					}
				provLogger.debug("Getting Tier type for tier id : (" + provRequest.getTier().getTierId()+ ")");
			TierTypesEnum tierType = accountManagement.getTierTypeNameByTierId(trxInfo.getAccountProvTrxInfo(), provRequest.getTier().getTierId());
				if (tierType == TierTypesEnum.PREPAID) {
					provLogger.error(trxInfo.logId()
							+ "Can't "+ provRequest.getRequestType() +" to one_off bundle.");
					throw new InvalidTierTypeException("Can't "+ provRequest.getRequestType() +" to one_off bundle.");
				}
				provLogger.debug(trxInfo.logId() + "Create provisioning request with SR");
				serviceProvisioning.handleCloudProvRequest(trxInfo, provRequest, true);
				break;

			case SUSPEND_ACCOUNT:
			case REACTIVATE_ACCT_AFTER_SUSPENSION:
			case DEACTIVATE_ACCOUNT:
			case USER_CREATE:
			case USER_DELETE:
				provLogger.debug(trxInfo.logId() + "Create provisioning request without SR");
				serviceProvisioning.handleCloudProvRequest(trxInfo, provRequest, false);
				provLogger.info(trxInfo.logId() + "The request will by handled as asynchronous request");
				serviceProvisioning.handleAsyncCloudProvRequest(trxInfo, provRequest);
				break;
			default:
				status.setStatus(ProvResponseStatus.INVALID_REQUEST);
			}
		} catch (DBException e) {
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			provLogger.error(trxInfo.logId() + "Database error " + e);
			status.setStatus(ProvResponseStatus.FAIL);
			status.setErrorMessage(e.getMessage());
			provRequest.setStatus(ProvReqStatusName.FAIL);
		} catch (TierNotFoundException e) {
                        reportAppError(AppErrors.INVALID_REQUEST, "Tier NotFound");
			provLogger.error(trxInfo.logId() + e.getMessage());
			status.setStatus(ProvResponseStatus.INVALID_REQUEST);
			status.setErrorMessage(e.getMessage());
		} catch (InvalidSMSSender e) {
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid SMS Sender");
			provLogger.error(trxInfo.logId() + e.getMessage());
			status.setStatus(ProvResponseStatus.INVALID_REQUEST);
			status.setErrorMessage(e.getMessage());
		} catch (DuplicateProvioniongRequest e) {
                        reportAppError(AppErrors.INVALID_REQUEST, "Duplicate Provioniong Request");
			provLogger.error(trxInfo.logId() + e.getMessage());
			status.setStatus(ProvResponseStatus.INVALID_REQUEST);
			status.setErrorMessage(e.getMessage());
		} catch (SRCreationFailed e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "SR Creation Failed");
			provLogger.error(trxInfo.logId() + "Failed to create provisioning SR on TIBCO: " + e.getMessage());
			status.setStatus(ProvResponseStatus.SR_CREATION_FAILED);
			status.setErrorMessage(e.getMessage());
		} catch (InvalidTierTypeException e) {
                        reportAppError(AppErrors.INVALID_OPERATION, "Invalid Tier Type");
			provLogger.error(trxInfo.logId() + "InvalidTierType, " + e.getMessage());
			status.setStatus(ProvResponseStatus.FAIL);
			status.setErrorMessage(e.getMessage());
		} catch (AccountNotFoundException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account NotFound");
			provLogger.error(trxInfo.logId() + "Account not fount: " + e.getMessage());
			status.setStatus(ProvResponseStatus.ACCOUNT_NOT_FOUND);
			status.setErrorMessage(e.getMessage());
		} catch (UserNotFoundException e) {
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "User NotFound");
			provLogger.error(trxInfo.logId() + "User not found, " + e.getMessage());
			status.setStatus(ProvResponseStatus.USER_NOT_FOUND);
			status.setErrorMessage(e.getMessage());
		}
		catch (Exception e) {
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Fialure");
			provLogger.error(trxInfo.logId() + "Unhandled exception", e);
			status.setStatus(ProvResponseStatus.FAIL);
			status.setErrorMessage(e.getMessage());
		}
		provLogger.info(trxInfo.logId() + "Final status: " + status.getStatus());
		return status;

	}

	// Private helper functions
	private ProvisioningRequest getProvisioningRequest(ProvisioningBaseType req) {
		ProvisioningRequest provReq = new ProvisioningRequest();
		provReq.setCompanyId(req.getCompanyID());
		provReq.setCompanyDomain(req.getCompanyDomain());
		provReq.setCompanyName(getCompanyName(req.getAccountHolderID()));
		provReq.setAccountAdmin(req.getAccountHolderID());
		provReq.setEntryDate(new Date());
		provReq.setRequestId(req.getProvisioningID());
		provReq.setCallbackUrl(req.getCallbackURL());
		provReq.setMSISDN(req.getMSISDN());
		return provReq;
	}

	private String getCompanyName(String accountHolderID) {
		String[] arr = accountHolderID.split("@");
		return arr.length > 1 ? arr[1] : null;
	}
        
        private void reportAppError(AppErrors error, String msg) {
		appErrorManagerAdapter.reportAppError(error, msg, ErrorsSource.APP_PROV);
	}
}