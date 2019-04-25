package com.edafa.web2sms.service.conversoin;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.edafa.web2sms.dalayer.dao.interfaces.CampaignDaoLocal;
import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.CampaignStatsReport;
import com.edafa.web2sms.dalayer.model.ContactList;
import com.edafa.web2sms.service.model.CampaignAggregationReport;
import com.edafa.web2sms.service.model.UserTrxInfo;

@Stateless
@LocalBean
public class ReportConversionBean {

	@EJB
	private CampaignDaoLocal campaignDao;

	public ReportConversionBean() {}

	public List<CampaignAggregationReport> getCampaignAggregationReport(List<CampaignStatsReport> campaignStatsReports,
			UserTrxInfo userTrxInfo) throws DBException {
		List<CampaignAggregationReport> aggregationReports = new ArrayList<CampaignAggregationReport>(
				campaignStatsReports.size());

		for (CampaignStatsReport stat : campaignStatsReports) {
			CampaignAggregationReport campaignAggregationReport = new CampaignAggregationReport();

			campaignAggregationReport.setAccountId(stat.getCampaignStatsReportPK().getAccountUserId());
			campaignAggregationReport.setCampaignId(stat.getCampaignStatsReportPK().getCampaignId());
			campaignAggregationReport.setCampaignStatus(CampaignStatusName.valueOf(stat.getCampaignStatus()));
			campaignAggregationReport.setSender(stat.getSenderName());
			campaignAggregationReport.setSmsText(stat.getSmsText());
			campaignAggregationReport.setListsCount(stat.getListsCount());
			campaignAggregationReport.setExecutionCount(stat.getExecutionCount());
			campaignAggregationReport.setCampaignName(stat.getCampaignName());
			campaignAggregationReport.setCreationTimestamp(stat.getCreationTimestamp());
			campaignAggregationReport.setStartTimestamp(stat.getStartTimestamp());
			campaignAggregationReport.setEndTimestamp(stat.getEndTimestamp());

			Campaign campaign = campaignDao.find(stat.getCampaignStatsReportPK().getCampaignId());

			campaignAggregationReport.setUsername(campaign.getAccountUser().getName());
			campaignAggregationReport
					.setFrequency(campaign.getCampaignScheduling().getScheduleFrequency().getScheduleFreqName().name());

			int recipientCount = 0;
			List<ContactList> contactLists = campaign.getContactLists();
			List<String> lists = new ArrayList<String>(contactLists.size());

			for (ContactList contactList : contactLists) {
				lists.add(contactList.getListName());
				recipientCount += contactList.getContactsCount();
			}

			campaignAggregationReport.setContactListName(lists);
			campaignAggregationReport.setRecipientCount(recipientCount);

			campaignAggregationReport.setSubmittedSMSCount(stat.getSubmittedSmsCount());
			campaignAggregationReport.setSubmittedSMSSegCount(stat.getSubmittedSmsSegCount());

			campaignAggregationReport
					.setFailedSMSCount(stat.getSmsFailed() + stat.getSmsFailedToSend() + stat.getSmsTimedOut());
			campaignAggregationReport.setFailedSMSSegCount(
					stat.getSegmentsFailed() + stat.getSegmentsFailedToSend() + stat.getSegmentsTimedOut());

			campaignAggregationReport.setDeliverdSMSCount(stat.getSmsDelivered());
			campaignAggregationReport.setDeliverdSMSSegCount(stat.getSegmentsDelivered());

			campaignAggregationReport.setUnDeliverdSMSCount(stat.getSmsNotDelivered());
			campaignAggregationReport.setUnDeliverdSMSSegCount(stat.getSegmentsNotDelivered());

			campaignAggregationReport.setPendingSMSCount(stat.getSmsSent() + stat.getSmsSubmitted());
			campaignAggregationReport.setPendingSMSSegCount(stat.getSegmentsSent() + stat.getSegmentsSubmitted());

			campaignAggregationReport.setSmsCount(stat.getSmsCount());
			campaignAggregationReport.setSmsSegCount(stat.getSmsSegCount());

			campaignAggregationReport.setSentSms(stat.getVfSentSms(), stat.getOgSentSms(), stat.getEtSentSms(),
					stat.getWeSentSms(), stat.getInterSentSms());
			campaignAggregationReport.setDeliveredSms(stat.getVfDeliveredSms(), stat.getOgDeliveredSms(),
					stat.getEtDeliveredSms(), stat.getWeDeliveredSms(), stat.getInterDeliveredSms());
			campaignAggregationReport.setNotDeliveredSms(stat.getVfNotDeliveredSms(), stat.getOgNotDeliveredSms(),
					stat.getEtNotDeliveredSms(), stat.getWeNotDeliveredSms(), stat.getInterNotDeliveredSms());

			campaignAggregationReport.setSentSegments(stat.getVfSentSeg(), stat.getOgSentSeg(), stat.getEtSentSeg(),
					stat.getWeSentSeg(), stat.getInterSentSeg());
			campaignAggregationReport.setDeliveredSegments(stat.getVfDeliveredSeg(), stat.getOgDeliveredSeg(),
					stat.getEtDeliveredSeg(), stat.getWeDeliveredSeg(), stat.getInterDeliveredSeg());
			campaignAggregationReport.setNotDeliveredSegments(stat.getVfNotDeliveredSeg(), stat.getOgNotDeliveredSeg(),
					stat.getEtNotDeliveredSeg(), stat.getWeNotDeliveredSeg(), stat.getInterNotDeliveredSeg());

			aggregationReports.add(campaignAggregationReport);
		}

		return aggregationReports;
	}

	public CampaignAggregationReport getCampaignAggregationReport(CampaignStatsReport stat, UserTrxInfo userTrxInfo)
			throws DBException {
		CampaignAggregationReport campaignAggregationReport = new CampaignAggregationReport();

		campaignAggregationReport.setAccountId(stat.getCampaignStatsReportPK().getAccountUserId());
		campaignAggregationReport.setCampaignId(stat.getCampaignStatsReportPK().getCampaignId());
		campaignAggregationReport.setCampaignStatus(CampaignStatusName.valueOf(stat.getCampaignStatus()));
		campaignAggregationReport.setSender(stat.getSenderName());
		campaignAggregationReport.setSmsText(stat.getSmsText());
		campaignAggregationReport.setListsCount(stat.getListsCount());
		campaignAggregationReport.setExecutionCount(stat.getExecutionCount());
		campaignAggregationReport.setCampaignName(stat.getCampaignName());
		campaignAggregationReport.setCreationTimestamp(stat.getCreationTimestamp());
		campaignAggregationReport.setStartTimestamp(stat.getStartTimestamp());
		campaignAggregationReport.setEndTimestamp(stat.getEndTimestamp());

		Campaign campaign = campaignDao.find(stat.getCampaignStatsReportPK().getCampaignId());

		campaignAggregationReport.setUsername(campaign.getAccountUser().getName());
		campaignAggregationReport
				.setFrequency(campaign.getCampaignScheduling().getScheduleFrequency().getScheduleFreqName().name());

		int recipientCount = 0;
		List<ContactList> contactLists = campaign.getContactLists();
		List<String> lists = new ArrayList<String>(contactLists.size());

		for (ContactList contactList : contactLists) {
			lists.add(contactList.getListName());
			recipientCount += contactList.getContactsCount();
		}

		campaignAggregationReport.setContactListName(lists);
		campaignAggregationReport.setRecipientCount(recipientCount);

		campaignAggregationReport.setSubmittedSMSCount(stat.getSmsSubmitted());
		campaignAggregationReport.setSubmittedSMSSegCount(stat.getSegmentsSubmitted());

		campaignAggregationReport
				.setFailedSMSCount(stat.getSmsFailed() + stat.getSmsFailedToSend() + stat.getSmsTimedOut());
		campaignAggregationReport.setFailedSMSSegCount(
				stat.getSegmentsFailed() + stat.getSegmentsFailedToSend() + stat.getSegmentsTimedOut());

		campaignAggregationReport.setDeliverdSMSCount(stat.getSmsDelivered());
		campaignAggregationReport.setDeliverdSMSSegCount(stat.getSegmentsDelivered());

		campaignAggregationReport.setUnDeliverdSMSCount(stat.getSmsNotDelivered());
		campaignAggregationReport.setUnDeliverdSMSSegCount(stat.getSegmentsNotDelivered());

		campaignAggregationReport.setPendingSMSCount(stat.getSmsSent() + stat.getSmsSubmitted());
		campaignAggregationReport.setPendingSMSSegCount(stat.getSegmentsSent() + stat.getSegmentsSubmitted());

		campaignAggregationReport.setSmsCount(stat.getSmsTotal());
		campaignAggregationReport.setSmsSegCount(stat.getSegmentsTotal());

		campaignAggregationReport.setSentSms(stat.getVfSentSms(), stat.getOgSentSms(), stat.getEtSentSms(),
				stat.getWeSentSms(), stat.getInterSentSms());
		campaignAggregationReport.setDeliveredSms(stat.getVfDeliveredSms(), stat.getOgDeliveredSms(),
				stat.getEtDeliveredSms(), stat.getWeDeliveredSms(), stat.getInterDeliveredSms());
		campaignAggregationReport.setNotDeliveredSms(stat.getVfNotDeliveredSms(), stat.getOgNotDeliveredSms(),
				stat.getEtNotDeliveredSms(), stat.getWeNotDeliveredSms(), stat.getInterNotDeliveredSms());

		campaignAggregationReport.setSentSegments(stat.getVfSentSeg(), stat.getOgSentSeg(), stat.getEtSentSeg(),
				stat.getWeSentSeg(), stat.getInterSentSeg());
		campaignAggregationReport.setDeliveredSegments(stat.getVfDeliveredSeg(), stat.getOgDeliveredSeg(),
				stat.getEtDeliveredSeg(), stat.getWeDeliveredSeg(), stat.getInterDeliveredSeg());
		campaignAggregationReport.setNotDeliveredSegments(stat.getVfNotDeliveredSeg(), stat.getOgNotDeliveredSeg(),
				stat.getEtNotDeliveredSeg(), stat.getWeNotDeliveredSeg(), stat.getInterNotDeliveredSeg());

		return campaignAggregationReport;
	}
}
