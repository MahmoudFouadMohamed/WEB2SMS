package com.edafa.web2sms.service.campaign;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jws.WebService;
import javax.xml.ws.BindingType;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.jee.apperr.AppError;
import com.edafa.jee.apperr.monitor.AppErrorManager;
import com.edafa.web2sms.alarm.AppErrorManagerAdapter;
import com.edafa.web2sms.alarm.AppErrors;
import com.edafa.web2sms.alarm.ErrorsSource;
import com.edafa.web2sms.dalayer.enums.CampaignActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.AccountQuotaNotFoundException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.InsufficientQuotaException;
import com.edafa.web2sms.service.model.CountResult;
import com.edafa.web2sms.service.campaign.exception.CampaignNotFoundException;
import com.edafa.web2sms.service.campaign.exception.InvalidCampListException;
import com.edafa.web2sms.service.campaign.exception.InvalidCampaignException;
import com.edafa.web2sms.service.campaign.exception.InvalidCampaignStateException;
import com.edafa.web2sms.service.campaign.interfaces.CampaignManagementBeanLocal;
import com.edafa.web2sms.service.campaign.interfaces.CampaignManagementService;
import com.edafa.web2sms.service.campaign.model.CampaignDetailsResult;
import com.edafa.web2sms.service.campaign.model.CampaignResult;
import com.edafa.web2sms.service.campaign.model.CampaignResultSet;
import com.edafa.web2sms.service.enums.ResponseStatus;
import com.edafa.web2sms.service.model.CampaignDetails;
import com.edafa.web2sms.service.model.CampaignModel;
import com.edafa.web2sms.service.model.ResultStatus;
import com.edafa.web2sms.service.model.SubmittedCampaignModel;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.service.campaign.exception.InvalidCampaignActionException;
import com.edafa.web2sms.service.model.UserModel;
import com.edafa.web2sms.service.model.UserTrxInfo;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

/**
 * Session Bean implementation class CampaignManagementBean
 */
@Stateless
@LocalBean
@WebService(name = "CampaignManagementService", serviceName = "CampaignManagementService", targetNamespace = "http://www.edafa.com/web2sms/service/campaign", endpointInterface = "com.edafa.web2sms.service.campaign.interfaces.CampaignManagementService")
@BindingType("http://schemas.xmlsoap.org/wsdl/soap/http")
public class CampaignManagementServiceImpl implements CampaignManagementService {

	private Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
	private Logger campLogger = LogManager.getLogger(LoggersEnum.CAMP_MNGMT.name());

	@EJB
	CampaignManagementBeanLocal campaignManagementBean;

	@EJB
	AppErrorManagerAdapter appErrorManagerAdapter;

	/**
	 * Default constructor.
	 */
	public CampaignManagementServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public CampaignResult createCampaign(UserTrxInfo userTrxInfo, SubmittedCampaignModel campaign) {
		if (!userTrxInfo.isValid() || !campaign.isValid()) {
			campLogger.error("Received invalid request to create campaign: " + userTrxInfo + " " + campaign
					+ ", return status: " + ResponseStatus.INVALID_REQUEST);
			return new CampaignResult(ResponseStatus.INVALID_REQUEST);
		}

		CampaignResult result = new CampaignResult(ResponseStatus.SUCCESS);
		try {
			campaignManagementBean.createCampaign(userTrxInfo, campaign);
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logId() + e);
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (DBException e) {
			String logMsg = userTrxInfo.logInfo() + "Database error";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);			
		} catch (InvalidCampaignException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INVALID_OPERATION, "Invalid Campaign");
			result.setStatus(ResponseStatus.INVALID_CAMPAIGN);
			result.setErrorMessage(e.getMessage());
			result.setValidationStatusList(e.getValidationStatusList());
		} catch (InvalidCampListException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			appLogger.error(userTrxInfo.logId(), e);
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid CampList");
			result.setStatus(ResponseStatus.INVALID_CAMPAIGN_LIST);
			result.setErrorMessage(e.getMessage());
		} 
		catch(InsufficientQuotaException e)
		{
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			appLogger.error(userTrxInfo.logId(), e);
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Insufficient Quota");
			result.setStatus(ResponseStatus.ACCOUNT_QUOTA_EXCEEDED);
			result.setErrorMessage(e.getMessage());
		}// end catch
		catch(AccountQuotaNotFoundException e)
		{
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			appLogger.error(userTrxInfo.logId(), e);
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account Quota NotFound");
			result.setStatus(ResponseStatus.ACCOUNT_QUOTA_NOT_FOUND);
			result.setErrorMessage(e.getMessage());
		}// end catch
		catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "Failed to handle this request";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
		}
		return result;
	}
	
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public CampaignResult createResentCampaign(UserTrxInfo userTrxInfo, String campaignId) {

		CampaignResult result = new CampaignResult(ResponseStatus.SUCCESS);
		try {
			campaignManagementBean.resendFailedFromCampaign(userTrxInfo, campaignId);
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logId() + e);
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (DBException e) {
			String logMsg = userTrxInfo.logInfo() + "Database error";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
		} catch (InvalidCampaignException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INVALID_OPERATION, "Invalid Campaign");
			result.setStatus(ResponseStatus.INVALID_CAMPAIGN);
			result.setErrorMessage(e.getMessage());
			result.setValidationStatusList(e.getValidationStatusList());
		} catch (InvalidCampListException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			appLogger.error(userTrxInfo.logId(), e);
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid CampList");
			result.setStatus(ResponseStatus.INVALID_CAMPAIGN_LIST);
			result.setErrorMessage(e.getMessage());
		} 
		catch(InsufficientQuotaException e)
		{
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			appLogger.error(userTrxInfo.logId(), e);
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Insufficient Quota");
			result.setStatus(ResponseStatus.ACCOUNT_QUOTA_EXCEEDED);
			result.setErrorMessage(e.getMessage());
		}// end catch
		catch(AccountQuotaNotFoundException e)
		{
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			appLogger.error(userTrxInfo.logId(), e);
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account Quota NotFound");
			result.setStatus(ResponseStatus.ACCOUNT_QUOTA_NOT_FOUND);
			result.setErrorMessage(e.getMessage());
		}// end catch
		catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "Failed to handle this request";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
		}
		return result;
	}

	@Override
	public CampaignResult updateCampaign(UserTrxInfo userTrxInfo, SubmittedCampaignModel updatedCampaign) {
		if (!userTrxInfo.isValid() || !updatedCampaign.isValid()) {
			campLogger.error("Received invalid request to update campaign: " + userTrxInfo + " " + updatedCampaign);
			campLogger.error("Return status: " + ResponseStatus.INVALID_REQUEST);
			return new CampaignResult(ResponseStatus.INVALID_REQUEST);
		}

		CampaignResult result = new CampaignResult(ResponseStatus.SUCCESS);
		try {
			campaignManagementBean.updateCampaign(userTrxInfo, updatedCampaign);
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logId() + e);
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (DBException e) {
			String logMsg = userTrxInfo.logInfo() + "Database error";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
		} catch (InvalidCampaignException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INVALID_OPERATION, "Invalid Campaign");
			result.setStatus(ResponseStatus.INVALID_CAMPAIGN);
			result.setErrorMessage(e.getMessage());
			result.setValidationStatusList(e.getValidationStatusList());
		} catch (CampaignNotFoundException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INVALID_OPERATION, "Campaign NotFound");
			result.setStatus(ResponseStatus.CAMPAIGN_NOT_FOUND);
			result.setErrorMessage(e.getMessage());
		} catch (InvalidCampListException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			appLogger.error(userTrxInfo.logId(), e);
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid CampList");
			result.setStatus(ResponseStatus.INVALID_CAMPAIGN_LIST);
			result.setErrorMessage(e.getMessage());
		} catch (InvalidCampaignStateException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INVALID_OPERATION, "Invalid Campaign State");
			result.setStatus(ResponseStatus.INVALID_CAMPAIGN_STATE);
			result.setErrorMessage(e.getMessage());
		}
		catch(InsufficientQuotaException e)
		{
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			appLogger.error(userTrxInfo.logId(), e);
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Insufficient Quota");
			result.setStatus(ResponseStatus.ACCOUNT_QUOTA_EXCEEDED);
			result.setErrorMessage(e.getMessage());
		}// end catch
		catch(AccountQuotaNotFoundException e)
		{
			campLogger.error(userTrxInfo.logId() + e.getMessage());
			appLogger.error(userTrxInfo.logId(), e);
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Account Quota NotFound");
			result.setStatus(ResponseStatus.ACCOUNT_QUOTA_NOT_FOUND);
			result.setErrorMessage(e.getMessage());
		}// end catch 
		catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "Failed to handle this request";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
			result.setStatus(ResponseStatus.FAIL);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
		}
		return result;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public CampaignResultSet getActiveCampaigns(UserTrxInfo userTrxInfo, int firstIndex, int count) {
		if (!userTrxInfo.isValid()) {
			campLogger.error("Received invalid request to get active campaigns: " + userTrxInfo);
			campLogger.error("Return status: " + ResponseStatus.INVALID_REQUEST);
			return new CampaignResultSet(ResponseStatus.INVALID_REQUEST);
		}

		CampaignResultSet campaignResultSet = new CampaignResultSet(ResponseStatus.SUCCESS);
		List<CampaignModel> activeCampaigns;
		try {
			activeCampaigns = campaignManagementBean.getActiveCampaigns(userTrxInfo, firstIndex, count);
			campaignResultSet.setCampaigns(activeCampaigns);
		} catch (DBException e) {
			String logMsg = userTrxInfo.logInfo() + "Database error";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			campaignResultSet.setStatus(ResponseStatus.FAIL);
			campaignResultSet.setErrorMessage(e.getMessage());			
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logInfo() + e.getMessage());
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			campaignResultSet.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			campaignResultSet.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "Failed to handle this request";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			campaignResultSet.setStatus(ResponseStatus.FAIL);
			campaignResultSet.setErrorMessage(e.getMessage());			
		}
		return campaignResultSet;
	}
        
        @Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public CampaignResultSet searchCampaigns(UserTrxInfo userTrxInfo, String campaignName, int firstIndex, int count, List<CampaignStatusName> statuses) {
		if (!userTrxInfo.isValid()) {
			campLogger.error("Received invalid request to get campaigns: " + userTrxInfo + " with statuses: " + statuses + ", and campaign name(" + campaignName +")");
			campLogger.error("Return status: " + ResponseStatus.INVALID_REQUEST);
			return new CampaignResultSet(ResponseStatus.INVALID_REQUEST);
		}

		CampaignResultSet campaignResultSet = new CampaignResultSet(ResponseStatus.SUCCESS);
		List<CampaignModel> campaigns;
		try {
			campaigns = campaignManagementBean.searchCampaigns(userTrxInfo, campaignName, firstIndex, count, statuses);
			campaignResultSet.setCampaigns(campaigns);
		} catch (DBException e) {
			String logMsg = userTrxInfo.logInfo() + "Database error";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			campaignResultSet.setStatus(ResponseStatus.FAIL);
			campaignResultSet.setErrorMessage(e.getMessage());			
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logInfo() + e.getMessage());
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			campaignResultSet.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			campaignResultSet.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "Failed to handle this request";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			campaignResultSet.setStatus(ResponseStatus.FAIL);
			campaignResultSet.setErrorMessage(e.getMessage());			
		}
		return campaignResultSet;
	}

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public CountResult countSearchCampaigns(UserTrxInfo userTrxInfo, String campaignName, List<CampaignStatusName> statuses) {
        if (!userTrxInfo.isValid()) {
            campLogger.error("Received invalid request to count Search campaigns: " + userTrxInfo + " with statuses: " + statuses + ", campaign name(" + campaignName + ")");
            campLogger.error("Return status: " + ResponseStatus.INVALID_REQUEST);
            return new CountResult(ResponseStatus.INVALID_REQUEST);
        }

        CountResult campaignCount = new CountResult(ResponseStatus.SUCCESS);
        try {
            int campCount = campaignManagementBean.countSearchCampaigns(userTrxInfo, campaignName, statuses);
            campaignCount.setCount(campCount);
        } catch (DBException e) {
            String logMsg = userTrxInfo.logInfo() + "Database error";
            appLogger.error(logMsg, e);
            campLogger.error(logMsg, e);
            if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                    || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
            } else {
                reportAppError(AppErrors.DATABASE_ERROR, "DB error");
            }
            campaignCount.setStatus(ResponseStatus.FAIL);
            campaignCount.setErrorMessage(e.getMessage());
        } catch (IneligibleAccountException e) {
            campLogger.error(userTrxInfo.logInfo() + e.getMessage());
            reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
            campaignCount.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
            campaignCount.setErrorMessage(e.getMessage());
        } catch (Exception e) {
            String logMsg = userTrxInfo.logInfo() + "Failed to handle this request";
            appLogger.error(logMsg, e);
            campLogger.error(logMsg, e);
            reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
            campaignCount.setStatus(ResponseStatus.FAIL);
            campaignCount.setErrorMessage(e.getMessage());
        }
        return campaignCount;
    }

        
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public CampaignResultSet getHistoryCampaigns(UserTrxInfo userTrxInfo, int firstIndex, int count) {
		if (!userTrxInfo.isValid()) {
			campLogger.error("Received invalid request to get campaigns history: " + userTrxInfo);
			campLogger.error("Return status: " + ResponseStatus.INVALID_REQUEST);
			return new CampaignResultSet(ResponseStatus.INVALID_REQUEST);
		}

		CampaignResultSet campaignResultSet = new CampaignResultSet(ResponseStatus.SUCCESS);
		List<CampaignModel> campaigns;

		try {
			campaigns = campaignManagementBean.getCampaignsHistory(userTrxInfo, firstIndex, count);
			campaignResultSet.setCampaigns(campaigns);
		} catch (DBException e) {
			String logMsg = userTrxInfo.logInfo() + "Database error";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			campaignResultSet.setStatus(ResponseStatus.FAIL);			
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logInfo() + e.getMessage());
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			campaignResultSet.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			campaignResultSet.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "Failed to handle this request";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			campaignResultSet.setStatus(ResponseStatus.FAIL);
		}

		return campaignResultSet;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ResultStatus updateCampaignAction(UserTrxInfo userTrxInfo, String campaignId, CampaignActionName action) {

		if (!userTrxInfo.isValid() || campaignId == null || campaignId.isEmpty() || action == null) {
			return new ResultStatus(ResponseStatus.INVALID_REQUEST);
		}

		ResultStatus result = new ResultStatus(ResponseStatus.SUCCESS);
		try {
			campaignManagementBean.updateCampaignAction(userTrxInfo, campaignId, action);
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (DBException e) {
			String logMsg = userTrxInfo.logInfo() + "Database error";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());			
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "Failed to handle this request";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);			
		}

		return result;
	}

	@Override
	public int getActiveCampaignCount(UserModel user) {
		try {
			return campaignManagementBean.getActiveCampaignsCount(user);
		} catch (Exception e) {
			String logMsg = "Unhandled exception cought in " + this.getClass().getSimpleName()
					+ ".deleteCampaignHistory";
                    //    campLogger.error(logMsg, e);
                    //    appLogger.error(logMsg, e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			return 0;
		}
	}

	@Override
	public int getCampaignHistoryCount(UserModel user) {
		try {
			return campaignManagementBean.getArchiveCampaignsCount(user);
		} catch (Exception e) {
			String logMsg = "Unhandled exception cought in " + this.getClass().getSimpleName()
					+ ".deleteCampaignHistory";
                    //    campLogger.error(logMsg, e);
                    //    appLogger.error(logMsg, e);
			reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			return 0;
		}
	}

	@Override
	public ResultStatus deleteCampaignHistory(UserTrxInfo userTrxInfo, List<String> campaignIds) {
		if (!userTrxInfo.isValid() || campaignIds == null || campaignIds.isEmpty()) {
			return new ResultStatus(ResponseStatus.INVALID_REQUEST);
		}

		ResultStatus result = new ResultStatus(ResponseStatus.SUCCESS);
		try {
			campaignManagementBean.deleteCampaigns(userTrxInfo, campaignIds);
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (DBException e) {
			String logMsg = userTrxInfo.logInfo() + "Database error while retreiving CampaignDetails";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "Unhandled exception cought in " + this.getClass().getSimpleName()
					+ ".deleteCampaignHistory";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
		}
		return result;
	}

	@Override
	public CampaignDetailsResult getCampaignDetails(UserTrxInfo userTrxInfo, String campId) {
		if (!userTrxInfo.isValid() || campId == null || campId.isEmpty()) {
			return new CampaignDetailsResult(ResponseStatus.INVALID_REQUEST);
		}

		CampaignDetailsResult result = new CampaignDetailsResult(ResponseStatus.SUCCESS);
		try {
			CampaignDetails campaignDetails = campaignManagementBean.getCampaignDetails(userTrxInfo, campId);
			result.setCampaignDetails(campaignDetails);
		} catch (IneligibleAccountException e) {
			campLogger.error(userTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (DBException e) {
			String logMsg = userTrxInfo.logInfo() + "Database error while retrieving campaign details";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "Failed to retrieve campaign details";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);
		}
		return result;
	}

        
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public ResultStatus updateCampaignStatus(UserTrxInfo userTrxInfo, String campaignId, CampaignActionName actionToUpdateStatus) {

		if (!userTrxInfo.isValid() || campaignId == null || campaignId.isEmpty() || actionToUpdateStatus == null) {
			return new ResultStatus(ResponseStatus.INVALID_REQUEST);
		}

		ResultStatus result = new ResultStatus(ResponseStatus.SUCCESS);
		try {
			campaignManagementBean.updateCampaignStatus(userTrxInfo, campaignId, actionToUpdateStatus);
		} catch (IneligibleAccountException e) {
			campLogger.warn(userTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INELIGIBLE_ACCOUNT, "Ineligible Account");
			result.setStatus(ResponseStatus.INELIGIBLE_ACCOUNT);
			result.setErrorMessage(e.getMessage());
		} catch (CampaignNotFoundException e) {
			campLogger.warn(userTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INVALID_REQUEST, "Campaign not found");
			result.setStatus(ResponseStatus.INVALID_REQUEST);
			result.setErrorMessage(e.getMessage());
		} catch (InvalidCampaignActionException e) {
			campLogger.warn(userTrxInfo.logId() + e.getMessage());
                        reportAppError(AppErrors.INVALID_REQUEST, "Invalid action for current campaign status");
			result.setStatus(ResponseStatus.INVALID_REQUEST);
			result.setErrorMessage(e.getMessage());
		} catch (DBException e) {
			String logMsg = userTrxInfo.logInfo() + "Database error";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
                    if ((e.getCause() != null) && (e.getCause().getClass().getName().contains("TimedOut")
                            || (e.getCause().getMessage() != null && e.getCause().getMessage().contains("TimedOut")))) {
                        reportAppError(AppErrors.DATABASE_TIMEOUT, "DB Timeout");
                    } else {
                        reportAppError(AppErrors.DATABASE_ERROR, "DB error");
                    }
			result.setStatus(ResponseStatus.FAIL);
			result.setErrorMessage(e.getMessage());			
		} catch (Exception e) {
			String logMsg = userTrxInfo.logInfo() + "Failed to handle this request";
			appLogger.error(logMsg, e);
			campLogger.error(logMsg, e);
                        reportAppError(AppErrors.GENERAL_ERROR, "Generic Failure");
			result.setStatus(ResponseStatus.FAIL);			
		}

		return result;
	}

	private void reportAppError(AppErrors error, String msg) {
		appErrorManagerAdapter.reportAppError(error, msg, ErrorsSource.CAMPAIGN_MANAGEMENT);
	}
	
	// Called implicitly from get Active camp

//	@Override
//	public ActiveCampaignStatsResult getActiveCampaignStats(UserTrxInfo userTrxInfo, List<String> campaignId) {
//		ActiveCampaignStatsResult result = new ActiveCampaignStatsResult();
//		List<ActiveCampaignStats> activeCampStats;
//		if (!userTrxInfo.isValid() || campaignId == null || campaignId.isEmpty()) {
//			result.setStatus(ResponseStatus.INVALID_REQUEST);
//			return result;
//		}
//
//		result.setStatus(ResponseStatus.SUCCESS);
//		try {
//			activeCampStats = campaignManagementBean.getCampSMSStats(userTrxInfo, campaignId);
//			result.setActiveCampaignStats(activeCampStats);
//		} catch (DBException e) {
//			String logMsg = userTrxInfo.logInfo() + "Database error";
//			appLogger.error(logMsg, e);
//			campLogger.error(logMsg, e);
//			result.setStatus(ResponseStatus.FAIL);
//			result.setErrorMessage(e.getMessage());
//			reportAppError(AppErrors.DATABASE_ERROR, logMsg);
//		} catch (Exception e) {
//			String logMsg = userTrxInfo.logInfo() + "Failed to handle this request";
//			appLogger.error(logMsg, e);
//			campLogger.error(logMsg, e);
//			result.setStatus(ResponseStatus.FAIL);
//			reportAppError(AppErrors.GENERAL_ERROR, logMsg);
//		}
//
//		return result;
//	}

}
