package com.edafa.web2sms.service.campaign;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.dao.interfaces.CampaignStatusDaoLocal;
import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.model.CampaignStatus;
import com.edafa.web2sms.service.campaign.interfaces.CampaignManagementBeanLocal;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

@Stateless
public class CampaignManagementBean implements CampaignManagementBeanLocal {

	private Logger campLogger = LogManager.getLogger(LoggersEnum.CAMP_MNGMT.name());

	@EJB
	private CampaignStatusDaoLocal campaignStatusDao;

	public CampaignManagementBean() {}

	public CampaignStatus getCampaignStatus(CampaignStatusName statusName) {
		return campaignStatusDao.getCachedObjectByName(statusName);
	}

	@Override
	public List<CampaignStatus> getAllCampaignStatusList() {
		List<CampaignStatus> listOfStatus = new ArrayList<CampaignStatus>();
		listOfStatus.add(getCampaignStatus(CampaignStatusName.NEW));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.PAUSED));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.RUNNING));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.ON_HOLD));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.PARTIAL_RUN));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.APPROVED));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.CANCELLED));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.FAILED));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.FINISHED));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.OBSOLETE));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.APPROVAL_OBSOLETE));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.SEND_OBSOLETE));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.REJECTED));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.WAITING_APPROVAL));

		return listOfStatus;
	}

	@Override
	public List<CampaignStatus> getActiveCampaignStatusList() {
		List<CampaignStatus> listOfStatus = new ArrayList<CampaignStatus>();
		listOfStatus.add(getCampaignStatus(CampaignStatusName.NEW));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.PAUSED));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.RUNNING));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.ON_HOLD));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.PARTIAL_RUN));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.APPROVED));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.WAITING_APPROVAL));
		return listOfStatus;
	}

	@Override
	public List<CampaignStatus> getArchiveCampaignStatusList() {
		List<CampaignStatus> listOfStatus = new ArrayList<CampaignStatus>();
		listOfStatus.add(getCampaignStatus(CampaignStatusName.CANCELLED));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.FAILED));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.FINISHED));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.OBSOLETE));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.APPROVAL_OBSOLETE));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.SEND_OBSOLETE));
		listOfStatus.add(getCampaignStatus(CampaignStatusName.REJECTED));
		return listOfStatus;
	}

	@Override
	public List<CampaignStatus> getPendingCampaignStatusList() {
		List<CampaignStatus> listOfStatus = new ArrayList<CampaignStatus>();
		listOfStatus.add(getCampaignStatus(CampaignStatusName.WAITING_APPROVAL));
		return listOfStatus;
	}

	private List<CampaignStatus> getCampaignStatusList(List<CampaignStatusName> statuses) {
		List<CampaignStatus> listOfStatus = new ArrayList<CampaignStatus>();
		for (CampaignStatusName campaignStatusName : statuses) {
			listOfStatus.add(getCampaignStatus(campaignStatusName));
		}
		return listOfStatus;
	}

}
