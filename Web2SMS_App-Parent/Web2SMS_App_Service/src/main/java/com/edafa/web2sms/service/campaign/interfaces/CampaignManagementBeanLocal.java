package com.edafa.web2sms.service.campaign.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.CampaignActionName;
import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.CampaignStatus;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.AccountQuotaNotFoundException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.InsufficientQuotaException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.NotPrePaidAccountException;
import com.edafa.web2sms.service.campaign.exception.CampaignNotFoundException;
import com.edafa.web2sms.service.campaign.exception.CampaignTypeNotDefinedException;
import com.edafa.web2sms.service.campaign.exception.InvalidCampListException;
import com.edafa.web2sms.service.campaign.exception.InvalidCampaignException;
import com.edafa.web2sms.service.campaign.exception.InvalidCampaignStateException;
import com.edafa.web2sms.service.model.CampaignDetails;
import com.edafa.web2sms.service.model.CampaignModel;
import com.edafa.web2sms.service.model.SubmittedCampaignModel;
import com.edafa.web2sms.service.model.TrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import com.edafa.web2sms.service.campaign.exception.InvalidCampaignActionException;
import com.edafa.web2sms.service.model.UserModel;
import com.edafa.web2sms.service.model.UserTrxInfo;

@Local
public interface CampaignManagementBeanLocal {

    void createCampaign(UserTrxInfo userTrxInfo, SubmittedCampaignModel campaign) throws DBException,
            IneligibleAccountException, InvalidCampaignException, UserNotFoundException, InvalidCampListException, InsufficientQuotaException, NotPrePaidAccountException, AccountQuotaNotFoundException, CampaignTypeNotDefinedException;

    void createCampaign(UserTrxInfo userTrxInfo, SubmittedCampaignModel campaign, CampaignStatusName campaignStatusName) throws DBException,
            IneligibleAccountException, InvalidCampaignException, UserNotFoundException, InvalidCampListException, InsufficientQuotaException, NotPrePaidAccountException, AccountQuotaNotFoundException, CampaignTypeNotDefinedException;

    void updateCampaign(UserTrxInfo userTrxInfo, SubmittedCampaignModel updatedCamp) throws DBException,
            IneligibleAccountException, InvalidCampaignException, CampaignNotFoundException,
            InvalidCampaignStateException, UserNotFoundException, InvalidCampListException, InsufficientQuotaException, NotPrePaidAccountException,
            AccountQuotaNotFoundException, CampaignTypeNotDefinedException;

    public List<CampaignModel> getActiveCampaigns(UserTrxInfo userTrxInfo, int firstIndex, int count)
            throws DBException, IneligibleAccountException;

    public List<CampaignModel> searchCampaigns(UserTrxInfo userTrxInfo, String campaignName, int firstIndex, int count, List<CampaignStatusName> statuses)
            throws DBException, IneligibleAccountException;

    public int countSearchCampaigns(UserTrxInfo userTrxInfo, String campaignName, List<CampaignStatusName> statuses) throws DBException, IneligibleAccountException;

    List<CampaignModel> getCampaignsHistory(UserTrxInfo userTrxInfo, int firstIndex, int count) throws DBException,
            IneligibleAccountException;

    CampaignDetails getCampaignDetails(UserTrxInfo userTrxInfo, String campId) throws IneligibleAccountException, DBException;

    CampaignStatus getCampaignStatus(CampaignStatusName statusName);

    void updateCampaignAction(UserTrxInfo userTrxInfo, String campaignId, CampaignActionName action)
            throws IneligibleAccountException, DBException;

    int getActiveCampaignsCount(UserModel user) throws DBException;

    int getArchiveCampaignsCount(UserModel user) throws DBException;

    void deleteCampaigns(UserTrxInfo userTrxInfo, List<String> campaignIds) throws IneligibleAccountException,
            DBException;

    List<CampaignStatus> getActiveCampaignStatusList();

    List<CampaignStatus> getArchiveCampaignStatusList();

    List<CampaignStatus> getPendingCampaignStatusList();
    
    List<CampaignStatus> getAllCampaignStatusList();

    int updateCampaignsAction(TrxInfo trxInfo, String accountId, CampaignActionName action,
            List<CampaignStatus> statusList) throws DBException;

//    int holdRunningCampaigns(TrxInfo trxInfo, String accountId) throws DBException;

    SubmittedCampaignModel resendFailedFromCampaign(UserTrxInfo userTrxInfo, String campaignId)
            throws IneligibleAccountException, DBException, InvalidCampaignException, UserNotFoundException, InvalidCampListException, InsufficientQuotaException, NotPrePaidAccountException, AccountQuotaNotFoundException, CampaignTypeNotDefinedException;

    void updateCampaignStatus(UserTrxInfo userTrxInfo, String campaignId, CampaignActionName actionToUpdateStatus)
            throws IneligibleAccountException, CampaignNotFoundException, InvalidCampaignActionException, DBException;

}
