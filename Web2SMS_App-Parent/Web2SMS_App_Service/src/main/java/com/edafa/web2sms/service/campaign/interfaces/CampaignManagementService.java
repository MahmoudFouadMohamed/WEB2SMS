package com.edafa.web2sms.service.campaign.interfaces;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.edafa.web2sms.dalayer.enums.CampaignActionName;
import com.edafa.web2sms.service.campaign.model.CampaignDetailsResult;
import com.edafa.web2sms.service.campaign.model.CampaignResult;
import com.edafa.web2sms.service.campaign.model.CampaignResultSet;
import com.edafa.web2sms.service.model.ResultStatus;
import com.edafa.web2sms.service.model.SubmittedCampaignModel;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.model.CampaignStatus;
import com.edafa.web2sms.service.model.CountResult;
import com.edafa.web2sms.service.model.UserModel;
import com.edafa.web2sms.service.model.UserTrxInfo;

@WebService(name = "CampaignManagementService", portName = "CampaignManagementServicePort", targetNamespace = "http://www.edafa.com/web2sms/service/campaign")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
// @XmlSeeAlso({ ObjectFactory.class })
public interface CampaignManagementService {

	@WebMethod(operationName = "getActiveCampaigns")
	@WebResult(name = "CampaignResultSet", partName = "campaignList")
	public CampaignResultSet getActiveCampaigns(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "firstIndex", partName = "firstIndex") int firstIndex,
			@WebParam(name = "count", partName = "count") int count);
        
        @WebMethod(operationName = "searchCampaigns")
	@WebResult(name = "CampaignResultSet", partName = "campaignList")
	public CampaignResultSet searchCampaigns(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
                	@WebParam(name = "CampaignName", partName = "campaignName") String campaignName,
                        @WebParam(name = "firstIndex", partName = "firstIndex") int firstIndex,
			@WebParam(name = "count", partName = "count") int count,
                        @WebParam(name = "statuses", partName = "statuses") List<CampaignStatusName> statuses);

        @WebMethod(operationName = "countSearchCampaigns")
	@WebResult(name = "CountResult", partName = "countResult")
	public CountResult countSearchCampaigns(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
                	@WebParam(name = "CampaignName", partName = "campaignName") String campaignName,
                        @WebParam(name = "statuses", partName = "statuses") List<CampaignStatusName> statuses);

        
	@WebMethod(operationName = "getCampaignDetailes")
	@WebResult(name = "CampaignDetailsResult", partName = "campaignDetailsResult")
	public CampaignDetailsResult getCampaignDetails(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "CampaignId", partName = "campaignId") String campId);

	@WebMethod(operationName = "getHistoryCampaigns")
	@WebResult(name = "CampaignResultSet", partName = "campaignList")
	public CampaignResultSet getHistoryCampaigns(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "firstIndex", partName = "firstIndex") int firstIndex,
			@WebParam(name = "count", partName = "count") int count);

	@WebMethod(operationName = "createCampaign")
	@WebResult(name = "ResultStatus", partName = "status")
	public CampaignResult createCampaign(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "Campaign", partName = "campaign") SubmittedCampaignModel campaign);

	@WebMethod(operationName = "createResentCampaign")
	@WebResult(name = "ResultStatus", partName = "status")
	CampaignResult createResentCampaign(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "CampaignId", partName = "campaignId") String campaignId);

	@WebMethod(operationName = "updateCampaign")
	@WebResult(name = "ResultStatus", partName = "status")
	public CampaignResult updateCampaign(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "Campaign", partName = "updatedCampaign") SubmittedCampaignModel updatedCampaign);

	@WebMethod(operationName = "updateCampaignAction")
	@WebResult(name = "ResultStatus", partName = "status")
	public ResultStatus updateCampaignAction(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "CampaignId", partName = "campaignId") String campaignId,
			@WebParam(name = "Action", partName = "action") CampaignActionName action);

	// @WebMethod(operationName = "getActiveCampaignStats")
	// @WebResult(name = "ActiveCampaignStatsResult", partName =
	// "activeCampaignStatsResult")
	// public ActiveCampaignStatsResult getActiveCampaignStats(
	// @WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo
	// userTrxInfo,
	// @WebParam(name = "CampaignId", partName = "campaignId") List<String>
	// campaignId);

	@WebMethod(operationName = "getActiveCampaignCount")
	@WebResult(name = "Count", partName = "count")
	public int getActiveCampaignCount(@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserModel user);

	@WebMethod(operationName = "getCampaignHistoryCount")
	@WebResult(name = "Count", partName = "count")
	public int getCampaignHistoryCount(@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserModel user);

	@WebMethod(operationName = "deleteHistory")
	@WebResult(name = "ResultStatus", partName = "status")
	public ResultStatus deleteCampaignHistory(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "CampaignHistoryList", partName = "campaignHistoryList") List<String> campaignIds);

	@WebMethod(operationName = "updateCampaignStatus")
	@WebResult(name = "ResultStatus", partName = "status")
	public ResultStatus updateCampaignStatus(
			@WebParam(name = "UserTrxInfo", partName = "userTrxInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "CampaignId", partName = "campaignId") String campaignId,
			@WebParam(name = "Action", partName = "actionToUpdateStatus") CampaignActionName actionToUpdateStatus);

}
