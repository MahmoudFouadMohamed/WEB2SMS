package com.edafa.web2sms.sms;

import com.edafa.web2sms.sms.caching.SMSapiUtils;
import java.util.Arrays;
import java.util.List;

import com.edafa.web2sms.sms.enums.ResultStatus;
import com.edafa.web2sms.sms.enums.SMSResponseStatus;
import com.edafa.web2sms.sms.model.EnquireCampaignByIdRequest;
import com.edafa.web2sms.sms.model.EnquireCampaignByIdResponse;
import com.edafa.web2sms.sms.model.EnquireSMSByIdRequest;
import com.edafa.web2sms.sms.model.EnquireSMSByIdResponse;
import com.edafa.web2sms.sms.model.EnquireSMSByIdResponseList;
import com.edafa.web2sms.sms.model.EnquireSMSsByDatesAndMSISDNResponse;
import com.edafa.web2sms.sms.model.EnquireSMSsByDatesAndMSISDNResponseList;
import com.edafa.web2sms.sms.model.EnquireSMSsByDatesRequest;
import com.edafa.web2sms.sms.model.EnquireSMSsByDatesResponse;
import com.edafa.web2sms.sms.model.SMSDetails;
import com.edafa.web2sms.sms.model.SMSResponseBySmsId;
import com.edafa.web2sms.sms.model.SubmitCampaignResponse;
import com.edafa.web2sms.sms.model.SubmitDetailedCampaignRequest;
import com.edafa.web2sms.sms.model.SubmitDetailedSMSRequest;
import com.edafa.web2sms.sms.model.SubmitSMSResponse;
import com.edafa.web2sms.sms.model.SubmitSMSResponseBySmsId;

public class SmsApiResponseGenerator {

	public static SubmitSMSResponse generateSubmitSMSFakeResponse(SubmitDetailedSMSRequest request, List<SMSResponseStatus> smsStatus) {
            if (smsStatus == null) {
                return generateSubmitSMSFakeResponse(request);
            }
            
		List<SMSDetails> smsList = request.getSMSs();
		SMSResponseStatus[] smsResponseStatusList = new SMSResponseStatus[smsList.size()];

		for (int i = 0; i < smsResponseStatusList.length; i++) {
                    smsResponseStatusList[i] = (i >= smsStatus.size() || SMSapiUtils.checkSMSFailed(smsStatus.get(i))) ?
                            SMSResponseStatus.SUBMITTED : smsStatus.get(i);
		}

		return generateSubmitSMSResponse(ResultStatus.SUCCESS, null, smsResponseStatusList);
	}

	public static SubmitSMSResponse generateSubmitSMSFakeResponse(SubmitDetailedSMSRequest request) {
		List<SMSDetails> smsList = request.getSMSs();
		SMSResponseStatus[] smsResponseStatusList = new SMSResponseStatus[smsList.size()];

		for (int i = 0; i < smsResponseStatusList.length; i++) {
			smsResponseStatusList[i] = SMSResponseStatus.SUBMITTED;
		}

		return generateSubmitSMSResponse(ResultStatus.SUCCESS, null, smsResponseStatusList);
	}

	public static SubmitSMSResponse generateSubmitSMSResponse(ResultStatus resultStatus, String description,
			SMSResponseStatus... smsResponseStatus) {
		SubmitSMSResponse response = new SubmitSMSResponse();

		response.setResultStatus(resultStatus);
		response.setDescription(description);
		response.setSmsStatus(Arrays.asList(smsResponseStatus));

		return response;
	}

	public static SubmitCampaignResponse generateSubmitCampaignFakeResponse(SubmitDetailedCampaignRequest request,
			String campaignId) {
		return generateSubmitCampaignResponse(campaignId, null, ResultStatus.SUCCESS);
	}

	public static SubmitCampaignResponse generateSubmitCampaignResponse(String campaignId, String description,
			ResultStatus resultStatus) {
		SubmitCampaignResponse response = new SubmitCampaignResponse();

		response.setCampaignId(campaignId);
		response.setDescription(description);
		response.setResultStatus(resultStatus);

		return response;
	}

	public static EnquireCampaignByIdResponse generateEnquireCampaignByIdFakeResponse(
			EnquireCampaignByIdRequest request) {
		return generateEnquireCampaignByIdResponse("Unable to inquire about the campaigns", ResultStatus.GENERIC_ERROR, null, 0, 0, 0);
	}

	public static EnquireCampaignByIdResponse generateEnquireCampaignByIdResponse(String description,
			ResultStatus resultStatus, String campaignStatus, int numOfDeliveredSeg, int numOfUnDeliveredSeg,
			double submittedRatio) {
		EnquireCampaignByIdResponse response = new EnquireCampaignByIdResponse();

		response.setDescription(description);
		response.setCampaignStatus(campaignStatus);
		response.setNumOfDeliveredSeg(numOfDeliveredSeg);
		response.setNumOfUnDeliveredSeg(numOfUnDeliveredSeg);
		response.setResultStatus(resultStatus);
		response.setSubmittedRatio(submittedRatio);

		return response;
	}

	public static SubmitSMSResponseBySmsId generateSubmitSMSBySmsIdFakeResponse(SubmitDetailedSMSRequest request, List<SMSResponseBySmsId> smsResponseList) {
                if (smsResponseList == null) {
                    return generateSubmitSMSBySmsIdFakeResponse(request);
                }
		List<SMSDetails> smsList = request.getSMSs();
		SMSResponseBySmsId[] smsResponseBySmsIdList = new SMSResponseBySmsId[smsList.size()];
                    
		for (int i = 0; i < smsResponseBySmsIdList.length; i++) {
			SMSDetails smsDetails = smsList.get(i);
			
                        SMSResponseBySmsId smsResponse;
                        if (i >= smsResponseList.size() || SMSapiUtils.checkSMSFailed(smsResponseList.get(i).getSmsStatus())) {
                            smsResponse = new SMSResponseBySmsId(smsDetails.getReceiverMSISDN(),
                                smsDetails.getSMSId(), SMSResponseStatus.SUBMITTED);
                        } else {
                            smsResponse = new SMSResponseBySmsId(smsDetails.getReceiverMSISDN(),
                                smsDetails.getSMSId(), smsResponseList.get(i).getSmsStatus());
                        }
                            
			smsResponseBySmsIdList[i] = smsResponse;
		}

		return generateSubmitSMSBySmsIdResponse(null, ResultStatus.SUCCESS, smsResponseBySmsIdList);
	}

	public static SubmitSMSResponseBySmsId generateSubmitSMSBySmsIdFakeResponse(SubmitDetailedSMSRequest request) {
		List<SMSDetails> smsList = request.getSMSs();
		SMSResponseBySmsId[] smsResponseBySmsIdList = new SMSResponseBySmsId[smsList.size()];

		for (int i = 0; i < smsResponseBySmsIdList.length; i++) {
			SMSDetails smsDetails = smsList.get(i);
			SMSResponseBySmsId smsResponse = new SMSResponseBySmsId(smsDetails.getReceiverMSISDN(),
					smsDetails.getSMSId(), SMSResponseStatus.SUBMITTED);
			smsResponseBySmsIdList[i] = smsResponse;
		}

		return generateSubmitSMSBySmsIdResponse(null, ResultStatus.SUCCESS, smsResponseBySmsIdList);
	}

	public static SubmitSMSResponseBySmsId generateSubmitSMSBySmsIdResponse(String description,
			ResultStatus resultStatus, SMSResponseBySmsId... smsResponseList) {
		SubmitSMSResponseBySmsId response = new SubmitSMSResponseBySmsId();

		response.setDescription(description);
		response.setResultStatus(resultStatus);
		response.setsmsResponseList(Arrays.asList(smsResponseList));

		return response;
	}

	public static EnquireSMSByIdResponse generateEnquireSMSByIdFakeResponse(EnquireSMSByIdRequest request) {
		EnquireSMSByIdResponseList enquireSMSByIdResponse = null;

//		enquireSMSByIdResponse.setDeliveryDate(null);
//		enquireSMSByIdResponse.setRecievedDate(null);
//		enquireSMSByIdResponse.setReciever(null);
//		enquireSMSByIdResponse.setSendDate(null);
//		enquireSMSByIdResponse.setSmsId(request.getSmsId());
//		enquireSMSByIdResponse.setSmsStatus(null);

		return generateEnquireSMSByIdResponse("Unable to inquire about the SMSs", ResultStatus.GENERIC_ERROR, enquireSMSByIdResponse);
	}

	public static EnquireSMSByIdResponse generateEnquireSMSByIdResponse(String description, ResultStatus resultStatus,
			EnquireSMSByIdResponseList... smsStatusWithSmsId) {
		EnquireSMSByIdResponse response = new EnquireSMSByIdResponse();

		response.setDescription(description);
		response.setResultStatus(resultStatus);
                if (smsStatusWithSmsId != null) {
                    response.setSmsStatusWithSmsId(Arrays.asList(smsStatusWithSmsId));
                } else {
                    response.setSmsStatusWithSmsId(null);
                }

		return response;
	}

	public static EnquireSMSsByDatesAndMSISDNResponse generateEnquireSMSsByDatesAndMSISDNFakeResponse(
			EnquireSMSsByDatesRequest request) {
		return generateEnquireSMSsByDatesAndMSISDNResponse("Unable to inquire about the SMSs by date and reciever", ResultStatus.GENERIC_ERROR, null);
	}

	public static EnquireSMSsByDatesAndMSISDNResponse generateEnquireSMSsByDatesAndMSISDNResponse(String description,
			ResultStatus resultStatus, EnquireSMSsByDatesAndMSISDNResponseList responseList) {
		EnquireSMSsByDatesAndMSISDNResponse response = new EnquireSMSsByDatesAndMSISDNResponse();

		response.setDescription(description);
		response.setResultStatus(resultStatus);
		response.setResponseList(responseList);

		return response;
	}

	public static EnquireSMSsByDatesResponse generateEnquireSMSsByDatesFakeResponse(EnquireSMSsByDatesRequest request) {
		return generateEnquireSMSsByDatesResponse("Unable to inquire about the SMSs by date", ResultStatus.GENERIC_ERROR, 0, 0, 0, 0);
	}

	public static EnquireSMSsByDatesResponse generateEnquireSMSsByDatesResponse(String description,
			ResultStatus resultStatus, int smsApiDelivered, int smsApiUnDelivered, int apiCampDelivered,
			int apiCampUnDelivered) {
		EnquireSMSsByDatesResponse response = new EnquireSMSsByDatesResponse();

		response.setApiCampDelivered(apiCampDelivered);
		response.setApiCampUnDelivered(apiCampUnDelivered);
		response.setDescription(description);
		response.setResultStatus(resultStatus);
		response.setSmsApiDelivered(smsApiDelivered);
		response.setSmsApiUnDelivered(smsApiUnDelivered);

		return response;
	}
}
