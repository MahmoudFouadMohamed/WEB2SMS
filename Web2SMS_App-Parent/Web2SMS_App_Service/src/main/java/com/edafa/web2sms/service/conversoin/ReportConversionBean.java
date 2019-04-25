package com.edafa.web2sms.service.conversoin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.edafa.web2sms.dalayer.dao.interfaces.CampaignDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignListsDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ListTypeDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSStatusDaoLocal;
import com.edafa.web2sms.dalayer.enums.ListTypeName;
import com.edafa.web2sms.dalayer.enums.SMSStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.CampaignAggregationView;
import com.edafa.web2sms.dalayer.model.ListType;
import com.edafa.web2sms.dalayer.model.SMSAPIView;
import com.edafa.web2sms.dalayer.pojo.CampSMSStats;
import com.edafa.web2sms.dalayer.pojo.CampaignAssociatedList;
import com.edafa.web2sms.dalayer.pojo.CampaignFrequency;
import com.edafa.web2sms.service.model.CampaignAggregationReport;
import com.edafa.web2sms.service.model.CampaignModel;
import com.edafa.web2sms.service.model.SMSReport;

/**
 * Session Bean implementation class ReportConversionBean
 */
@Stateless
@LocalBean
public class ReportConversionBean {

	/**
	 * Default constructor.
	 */
	public ReportConversionBean() {
		// TODO Auto-generated constructor stub
	}

	@EJB
	CampaignDaoLocal campaignDao;

	@EJB
	CampaignListsDaoLocal campaignListsDao;

	@EJB
	ListTypeDaoLocal listTypeDao;

	@EJB
	SMSStatusDaoLocal smsStatusDao;

	// public CampaignAggregationReport
	// getCampaignAggregationReport(CampaignAggregationView caView) {
	//
	// CampaignAggregationReport report = new CampaignAggregationReport();
	// report.setAccountId(caView.getAccountId());
	// report.setCampaignId(caView.getCampaignId());
	// report.setCampaignName(caView.getCampaignName());
	// report.setCampaignStatus(caView.getCampaignStatus());
	// report.setCreationTimestamp(caView.getCreationTimestamp());
	// report.setDeliverdSMSCount(caView.getDeliverdSMSCount());
	// report.setDeliverdSMSSegCount(caView.getDeliverdSMSSegCount());
	// report.setEndTimestamp(caView.getEndTimestamp());
	// report.setFailedSMSCount(caView.getFailedSMSCount());
	// report.setFailedSMSSegCount(caView.getFailedSMSSegCount());
	// report.setListsCount(caView.getListsCount());
	// report.setPendingSMSCount(caView.getPendingSMSCount());
	// report.setPendingSMSSegCount(caView.getPendingSMSSegCount());
	// report.setRecipientCount(caView.getRecipientCount());
	// report.setSender(caView.getSender());
	// report.setSmsCount(caView.getSmsCount());
	// report.setSmsSegCount(caView.getSmsSegCount());
	// report.setStartTimestamp(caView.getStartTimestamp());
	// report.setSubmittedSMSCount(caView.getSubmittedSMSCount());
	// report.setSubmittedSMSSegCount(caView.getSubmittedSMSSegCount());
	// report.setExecutionCount(caView.getExecutionCount());
	// report.setUnDeliverdSMSCount(caView.getUnDeliverdSMSCount());
	// report.setUnDeliverdSMSSegCount(caView.getUnDeliverdSMSSegCount());
	// return report;
	//
	// }

	// public List<CampaignAggregationReport>
	// getCampaignAggregationReport(List<CampaignAggregationView> caView) {
	//
	// List<CampaignAggregationReport> result = new LinkedList<>();
	//
	// for (CampaignAggregationView tmp : caView) {
	// result.add(getCampaignAggregationReport(tmp));
	// }
	// return result;
	// }

	public List<SMSReport> getSMSReport(List<SMSAPIView> apiLogList) {
		Map<String, SMSReport> reportMap = new HashMap<>();
		for (SMSAPIView smsApiLog : apiLogList) {
			String sender = smsApiLog.getSender();
			SMSReport report = reportMap.get(sender);
			if (report == null) {
				report = new SMSReport(sender);
			}

			SMSStatusName smsStatus = smsApiLog.getSMSStatus().getName();
			switch (smsStatus) {
			case DELIVERED:
				report.incDeliveredSMS();
				report.setDeliveredSMSSegment(report.getDeliveredSMSSegment() + smsApiLog.getSegmentsCount());
				break;

			case NOT_DELIVERED:
				report.incUnDeliveredSMS();
				report.setUnDeliveredSMSSegment(report.getUnDeliveredSMSSegment() + smsApiLog.getSegmentsCount());
				break;
			case FAILED:
			case FAILED_TO_SEND:
			case TIMED_OUT:
				report.incFailedSMS();
				report.setFailedSMSSegment(report.getFailedSMSSegment() + smsApiLog.getSegmentsCount());
				break;
			case SENT:
			case SUBMITTED:
				report.incPendingSMS();
				report.setPendingSMSSegment(report.getFailedSMSSegment() + smsApiLog.getSegmentsCount());
				break;
			case RECEIVED:
			case REJECTED:
			case UNKNOWN:
			default:
				break;
			}
		}
		List<SMSReport> reportResult = new ArrayList<>(reportMap.values());
		Collections.sort(reportResult);
		return reportResult;
	}

	public List<CampaignAggregationReport> getCampaignAggregationReport(List<CampaignAggregationView> campAggViewList,
			List<Campaign> campList) {

		// map to join each campaign Id with its CampaignAggregationReport
		HashMap<String, CampaignAggregationReport> campViewMap = new HashMap<String, CampaignAggregationReport>();
		Map<String, Campaign> campMap = new HashMap<>();

		for (Campaign campaign : campList) {
			campMap.put(campaign.getCampaignId(), campaign);
		}
		for (CampaignAggregationView campAggView : campAggViewList) {
			CampaignAggregationReport campAggViewReport;

			campAggViewReport = campViewMap.get(campAggView.getCampaignId());
			if (campAggViewReport == null) {
				campAggViewReport = new CampaignAggregationReport(campAggView, campMap.get(campAggView.getCampaignId()));
				campViewMap.put(campAggView.getCampaignId(), campAggViewReport);
			}
			// else {
			// if (campAggView.isUpdatedCamp() &&
			// !campAggViewReport.isUpdatedCamp()) {
			// campAggViewReport.resetCounters();
			// campAggViewReport.setUpdatedCamp(true);
			// }
			// }

			SMSStatusName campSmsStatus = smsStatusDao.getCachedObjectById(campAggView.getSMSStatus()).getName();
			switch (campSmsStatus) {
			case DELIVERED: {
				if (campAggView.isUpdatedCamp()
						|| (!campAggView.isUpdatedCamp() && campAggViewReport.getDeliverdSMSCount() == 0)) {
					campAggViewReport.setDeliverdSMSCount(campAggView.getSmsStatusCount());
					campAggViewReport.setDeliverdSMSSegCount(campAggView.getSmsStatusSegCount());
				}
				break;
			}
			case NOT_DELIVERED: {
				if (campAggView.isUpdatedCamp()
						|| (!campAggView.isUpdatedCamp() && campAggViewReport.getUnDeliverdSMSCount() == 0)) {
					campAggViewReport.setUnDeliverdSMSCount(campAggView.getSmsStatusCount());
					campAggViewReport.setUnDeliverdSMSSegCount(campAggView.getSmsStatusSegCount());
				}
				break;
			}
			case FAILED:
			case FAILED_TO_SEND:
			case TIMED_OUT: {
				if (campAggView.isUpdatedCamp()
						|| (!campAggView.isUpdatedCamp() && campAggViewReport.getFailedSMSCount() == 0)) {
					campAggViewReport.setFailedSMSCount(campAggView.getSmsStatusCount());
					campAggViewReport.setFailedSMSSegCount(campAggView.getSmsStatusSegCount());
				}
				break;
			}
			case SENT:
			case SUBMITTED: {
				if (campAggView.isUpdatedCamp()
						|| (!campAggView.isUpdatedCamp() && campAggViewReport.getPendingSMSCount() == 0)) {
					campAggViewReport.setPendingSMSCount(campAggView.getSmsStatusCount());
					campAggViewReport.setPendingSMSSegCount(campAggView.getSmsStatusSegCount());
				}
				break;
			}
			case RECEIVED:
			case REJECTED:
			case UNKNOWN:
			default:
				break;
			}
		}

		List<String> campaignIdList = new ArrayList<>(campViewMap.keySet());
		List<ListType> listTypes = new ArrayList<>();
		listTypes.add(listTypeDao.getCachedObjectByName(ListTypeName.NORMAL_LIST));
		List<CampaignAssociatedList> campListNamelist;
		try {
			List<CampaignFrequency> campFreqList = campaignDao.findCampFreq(campaignIdList);
			for (CampaignFrequency campaignFrequency : campFreqList) {
				CampaignAggregationReport campAggRep = campViewMap.get(campaignFrequency.getCampaignId());
				campAggRep.setFrequency(campaignFrequency.getFreqName());
			}
			campListNamelist = campaignListsDao.findListNameByCampId(campaignIdList, listTypes);
			for (CampaignAssociatedList campaignAssociatedList : campListNamelist) {
				CampaignAggregationReport campAggRep = campViewMap.get(campaignAssociatedList.getCampaignId());
				campAggRep.addToContactListName(campaignAssociatedList.getListName());
			}
		} catch (DBException e) {
			e.printStackTrace();
		}

		List<CampaignAggregationReport> result = new ArrayList<>(campViewMap.values());

		Collections.sort(result, Collections.reverseOrder());
		return result;
	}

	public CampaignAggregationReport getCampaignAggregationReport(CampaignAggregationView campAggView, String campList) {

		CampaignAggregationReport campAggViewReport = new CampaignAggregationReport();
//		System.out.println("campAggView : " + campAggView);
		SMSStatusName campSmsStatus = smsStatusDao.getCachedObjectById(campAggView.getSMSStatus()).getName();
//		System.out.println("campSmsStatus : " + campSmsStatus);
		campAggViewReport.setAccountId(campAggView.getAccountId().getAccountId());
		campAggViewReport.setCampaignId(campAggView.getCampaignId());
		campAggViewReport.setCampaignName(campAggView.getCampaignName());
		campAggViewReport.setCampaignStatus(campAggView.getCampaignStatus());
		campAggViewReport.setCreationTimestamp(campAggView.getCreationTimestamp());
		campAggViewReport.setEndTimestamp(campAggView.getEndTimestamp());
		campAggViewReport.setExecutionCount(campAggView.getExecutionCount());
		campAggViewReport.setSmsCount(campAggView.getSmsCount());
		campAggViewReport.setSmsSegCount(campAggView.getSmsSegCount());
		campAggViewReport.setSubmittedSMSCount(campAggView.getSubmittedSMSCount());
		campAggViewReport.setSubmittedSMSSegCount(campAggView.getSubmittedSMSSegCount());
		campAggViewReport.setStartTimestamp(campAggView.getStartTimestamp());
		campAggViewReport.setSender(campAggView.getSender());
		campAggViewReport.setSmsText(campAggView.getSmsText());
		switch (campSmsStatus) {
		case DELIVERED: {
			if (campAggView.isUpdatedCamp()
					|| (!campAggView.isUpdatedCamp() && campAggViewReport.getDeliverdSMSCount() == 0)) {

//				System.out.println(campAggView.isUpdatedCamp()
//						|| (!campAggView.isUpdatedCamp() && campAggViewReport.getDeliverdSMSCount() == 0));

				campAggViewReport.setDeliverdSMSCount(campAggView.getSmsStatusCount());
				campAggViewReport.setDeliverdSMSSegCount(campAggView.getSmsStatusSegCount());
			}
			break;
		}
		case NOT_DELIVERED: {
			if (campAggView.isUpdatedCamp()
					|| (!campAggView.isUpdatedCamp() && campAggViewReport.getUnDeliverdSMSCount() == 0)) {
				campAggViewReport.setUnDeliverdSMSCount(campAggView.getSmsStatusCount());
				campAggViewReport.setUnDeliverdSMSSegCount(campAggView.getSmsStatusSegCount());
			}
			break;
		}
		case FAILED:
		case FAILED_TO_SEND:
		case TIMED_OUT: {
			if (campAggView.isUpdatedCamp()
					|| (!campAggView.isUpdatedCamp() && campAggViewReport.getFailedSMSCount() == 0)) {
				campAggViewReport.setFailedSMSCount(campAggView.getSmsStatusCount());
				campAggViewReport.setFailedSMSSegCount(campAggView.getSmsStatusSegCount());
			}
			break;
		}
		case SENT:
		case SUBMITTED: {
			if (campAggView.isUpdatedCamp()
					|| (!campAggView.isUpdatedCamp() && campAggViewReport.getPendingSMSCount() == 0)) {
				campAggViewReport.setPendingSMSCount(campAggView.getSmsStatusCount());
				campAggViewReport.setPendingSMSSegCount(campAggView.getSmsStatusSegCount());

//				System.out.println(campAggViewReport.getPendingSMSCount());
//				System.out.println("condition: " + campAggView.isUpdatedCamp()
//						+ (!campAggView.isUpdatedCamp() && campAggViewReport.getPendingSMSCount() == 0));
			}
			break;
		}
		case RECEIVED:
		case REJECTED:
		case UNKNOWN:
		default:
			break;
		}
		return campAggViewReport;
	}

	public void getActiveCampaignStats(List<CampSMSStats> campSMSStatList, List<CampaignModel> campaigns) {

		HashMap<String, CampaignModel> campStatMap = new HashMap<String, CampaignModel>();

		for (CampaignModel campaignModel : campaigns) {
			campStatMap.put(campaignModel.getCampaignId(), campaignModel);
		}
		for (CampSMSStats campSMSStat : campSMSStatList) {
			CampaignModel campaignModel;
			campaignModel = campStatMap.get(campSMSStat.getCampaignId());
			if (campaignModel == null) {
				// Shouldn't happend
			}
			SMSStatusName campSmsStatus = campSMSStat.getStatusId();
			switch (campSmsStatus) {
			case DELIVERED:
				campaignModel.setDeliveredSMSCount(campSMSStat.getSmsCount());
				campaignModel.setDeliveredSMSSegCount(campSMSStat.getSmsSegCount());
				break;
			case NOT_DELIVERED:
				campaignModel.setUnDeliveredSMSCount(campSMSStat.getSmsCount());
				campaignModel.setUnDeliveredSMSSegCount(campSMSStat.getSmsSegCount());
				break;
			case FAILED:
			case FAILED_TO_SEND:
			case TIMED_OUT:
				campaignModel.setFailedSMSCount(campSMSStat.getSmsCount());
				campaignModel.setFailedSMSSegCount(campSMSStat.getSmsSegCount());
				break;
			case SENT:
			case SUBMITTED:
				campaignModel.setPendingSMSCount(campSMSStat.getSmsCount());
				campaignModel.setPendingSMSSegCount(campSMSStat.getSmsSegCount());
				break;
			case RECEIVED:
			case REJECTED:
			case UNKNOWN:
			default:
				break;
			}
		}
	}

}
