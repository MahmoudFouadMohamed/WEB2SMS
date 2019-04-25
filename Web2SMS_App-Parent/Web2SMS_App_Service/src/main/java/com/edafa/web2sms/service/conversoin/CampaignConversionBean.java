package com.edafa.web2sms.service.conversoin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.edafa.web2sms.dalayer.dao.interfaces.CampaignTypeDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.LanguageDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ScheduleFrequencyDaoLocal;
import com.edafa.web2sms.dalayer.enums.ScheduleFrequencyName;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.CampaignAction;
import com.edafa.web2sms.dalayer.model.CampaignExecution;
import com.edafa.web2sms.dalayer.model.CampaignSMSDetails;
import com.edafa.web2sms.dalayer.model.CampaignScheduling;
import com.edafa.web2sms.dalayer.model.ContactList;
import com.edafa.web2sms.dalayer.model.ScheduleFrequency;
import com.edafa.web2sms.service.model.CampaignModel;
import com.edafa.web2sms.service.model.SubmittedCampaignModel;
import com.edafa.web2sms.utils.configs.enums.Configs;

@Stateless
public class CampaignConversionBean {

	@EJB
	ScheduleFrequencyDaoLocal scheduleFrequencyDao;

	@EJB
	ListConversionBean listConversionBean;

	@EJB
	LanguageDaoLocal languageDao;
	
	@EJB
	CampaignTypeDaoLocal campaignTypeDao;

	public Campaign getCampaign(SubmittedCampaignModel submittedCampaign) {
		Campaign campaign = new Campaign();
		campaign.setCampaignExecution(new CampaignExecution(campaign));
		campaign.setCampaignScheduling(new CampaignScheduling(campaign));
		
		campaign.setSmsDetails(new CampaignSMSDetails(campaign));
		campaign.setType(campaignTypeDao.getCachedObjectByName(submittedCampaign.getCampaignType()));
		mergeCampaign(campaign, submittedCampaign);
		return campaign;
	}

	public CampaignModel getCampaignModel(Campaign campaign) {
		CampaignModel campaignModel = new CampaignModel();

		CampaignExecution campExecution = campaign.getCampaignExecution();
		CampaignScheduling campScheduling = campaign.getCampaignScheduling();
		CampaignSMSDetails campSmsDetails = campaign.getSmsDetails();

		campaignModel.setStatus(campaign.getStatus().getCampaignStatusName());

		if (campExecution.getAction() != null) {
			CampaignAction action = campExecution.getAction();
			campaignModel.setAction(action.getCampaignActionName());
		}

		campaignModel.setCampaignId(campaign.getCampaignId());
		campaignModel.setCampaignName(campaign.getName());
		campaignModel.setCampaignType(campaign.getType().getCampaignTypeName());
		campaignModel.setResendFailedFlag(campaign.isResendFailedFlag());
		
		int smsCount = campSmsDetails.getSMSCount();
		int smsSegCount = campSmsDetails.getSMSSegCount();
		int submittedSmsCount = campExecution.getSubmittedSmsCount();
		int submittedSmsSegCount = campExecution.getSubmittedSmsSegCount();

		campaignModel.setSmsCount(smsCount);
		campaignModel.setSmsSegCount(smsSegCount);
		campaignModel.setSmsText(campSmsDetails.getSMSText());
		campaignModel.setLanguage(campSmsDetails.getLanguage().getLanguageName());
		campaignModel.setRegisteredDelivery(campSmsDetails.getRegisteredDelivery());
		campaignModel.setSenderName(campSmsDetails.getSenderName());

		campaignModel.setCreationTimestamp(campaign.getCreationTimestamp());

		campaignModel.setSubmittedSMSCount(submittedSmsCount);
		campaignModel.setSubmittedSMSSegCount(submittedSmsSegCount);
		Integer expectedExeCount = campScheduling.getExpectedExecutionCount();
		if (expectedExeCount != null && expectedExeCount != 0) {
			campaignModel.setRecipientCount(smsCount / expectedExeCount != 0 ? smsCount / expectedExeCount
					: expectedExeCount);
		}

		// view status bar for each campaign
		campaignModel.setSubmittedSMSRatio(((((double) submittedSmsCount / (double) smsCount)) * 100) / 2);
		campaignModel.setStartTimestamp(campExecution.getStartTimestamp());
		campaignModel.setEndTimestamp(campExecution.getEndTimestamp());
		campaignModel.setExecusionComments(campExecution.getComments());
		campaignModel.setExecutionCount(campExecution.getExecutionCount() != null ? campExecution.getExecutionCount()
				: 0);

		campaignModel.setScheduleStartTimestamp(campScheduling.getScheduleStartTimestamp());
		campaignModel.setScheduleEndTimestamp(campScheduling.getScheduleEndDate());
		campaignModel.setScheduleStopTime(campScheduling.getScheduleStopTime());
		campaignModel.setScheduleFrequency(campScheduling.getScheduleFrequency().getScheduleFreqName());
		campaignModel.setScheduledFlag(campScheduling.getScheduledFlag());
		return campaignModel;
	}
	
	public CampaignModel getResendCampaignModel(Campaign campaign) {
		CampaignModel campaignModel = new CampaignModel();

		CampaignSMSDetails campSmsDetails = campaign.getSmsDetails();

		campaignModel.setStatus(campaign.getStatus().getCampaignStatusName());
		String campNameSuffex = (String) Configs.RESEND_CAMPAIGN_SUFFIX_NAME.getValue();


//		campaignModel.setCampaignId(campaign.getCampaignId());
		campaignModel.setCampaignName(campaign.getName()+campNameSuffex);
		campaignModel.setCampaignType(campaign.getType().getCampaignTypeName());
		campaignModel.setResendFailedFlag(campaign.isResendFailedFlag());

		campaignModel.setSmsText(campSmsDetails.getSMSText());
		campaignModel.setLanguage(campSmsDetails.getLanguage().getLanguageName());
		campaignModel.setRegisteredDelivery(campSmsDetails.getRegisteredDelivery());
		campaignModel.setSenderName(campSmsDetails.getSenderName());

		campaignModel.setCreationTimestamp(campaign.getCreationTimestamp());

		campaignModel.setScheduleStartTimestamp(new Date());
		campaignModel.setScheduleStopTime(null);
		campaignModel.setScheduleEndTimestamp(null);
		campaignModel.setScheduledFlag(false);
		campaignModel.setScheduleFrequency(ScheduleFrequencyName.ONCE);
		campaignModel.setContactLists(new ArrayList<Integer>());

		return campaignModel;
	}

	public void mergeCampaign(Campaign campaign, SubmittedCampaignModel submittedCampaign) {
		CampaignSMSDetails smsDetails = campaign.getSmsDetails();
		if (smsDetails == null)
			smsDetails = new CampaignSMSDetails(campaign);

		CampaignScheduling campScheduling = campaign.getCampaignScheduling();
		if (campScheduling == null)
			campScheduling = new CampaignScheduling(campaign);

		CampaignExecution campExecution = campaign.getCampaignExecution();
		if (campExecution == null)
			campExecution = new CampaignExecution(campaign);

		campaign.setCampaignId(submittedCampaign.getCampaignId());
		campaign.setName(submittedCampaign.getCampaignName());
		campaign.setSmsDetails(smsDetails);
		campaign.setCampaignScheduling(campScheduling);
		campaign.setCampaignExecution(campExecution);

		smsDetails.setCampaign(campaign);
		
		String smsText = submittedCampaign.getSmsText() ;
		
		smsText = smsText.replace("٠", "0");
		smsText = smsText.replace("١", "1");
		smsText = smsText.replace("٢", "2");
		smsText = smsText.replace("٣", "3");
		smsText = smsText.replace("٤", "4");
		smsText = smsText.replace("٥", "5");
		smsText = smsText.replace("٦", "6");
		smsText = smsText.replace("٧", "7");
		smsText = smsText.replace("٨", "8");
		smsText = smsText.replace("٩", "9");
		smsDetails.setSMSText(smsText);
		smsDetails.setSenderName(submittedCampaign.getSenderName());
		smsDetails.setLanguage(languageDao.getCachedObjectByName(submittedCampaign.getLanguage()));
		smsDetails.setRegisteredDelivery(submittedCampaign.isRegisteredDelivery());

		campScheduling.setCampaign(campaign);
		campScheduling.setScheduleStartTimestamp(submittedCampaign.getScheduleStartTimestamp());

		campScheduling.setScheduleStopTime(submittedCampaign.getScheduleStopTime());

		if (submittedCampaign.getScheduleEndDate() != null) {
			Calendar schedStartTimestamp = Calendar.getInstance();
			Calendar schedEndDate = Calendar.getInstance();
			schedStartTimestamp.setTime(campScheduling.getScheduleStartTimestamp());
			schedEndDate.setTime(submittedCampaign.getScheduleEndDate());
			schedEndDate.set(Calendar.HOUR, schedStartTimestamp.get(Calendar.HOUR_OF_DAY));
			schedEndDate.set(Calendar.MINUTE, schedStartTimestamp.get(Calendar.MINUTE));
			schedEndDate.set(Calendar.SECOND, schedStartTimestamp.get(Calendar.SECOND));
			schedEndDate.set(Calendar.MILLISECOND, schedStartTimestamp.get(Calendar.MILLISECOND));
			campScheduling.setScheduleEndDate(schedEndDate.getTime());
		}
		ScheduleFrequency frequency = scheduleFrequencyDao.getCachedObjectByName(submittedCampaign
				.getScheduleFrequency());
		campScheduling.setScheduleFrequency(frequency);
		campScheduling.setScheduledFlag(submittedCampaign.getScheduledFlag());

		campExecution.setCampaign(campaign);
		
		if (submittedCampaign.getContactLists() != null) {
			List<ContactList> newLists = listConversionBean.getContactLists(submittedCampaign.getContactLists());

			List<ContactList> currentLists = campaign.getContactLists();
			if (currentLists != null) {

				// Adding new lists
				for (ContactList newList : newLists) {
					if (!currentLists.contains(newList)) {
						currentLists.add(newList);
					}
				}

				// Remove the removed lists
				for (Iterator it = currentLists.iterator(); it.hasNext();) {
					ContactList currentList = (ContactList) it.next();
					if (!newLists.contains(currentList)) {
						it.remove();
					}
				}
				// campaign.setContactLists(currentLists);
			} else {
				campaign.setContactLists(newLists);
			}
		} else {
			campaign.setContactLists(new ArrayList<ContactList>());
		}

	}
}
