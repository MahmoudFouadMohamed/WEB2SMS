package com.edafa.web2sms.service.admin.interfaces;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.model.CampaignSearchParam;

@Local
public interface AdminCampaignManagementBeanLocal {

	List<Campaign> adminGetCampaign(AdminTrxInfo adminTrxInfo, CampaignSearchParam param);

	List<Campaign> adminGetCampaign(AdminTrxInfo adminTrxInfo, CampaignSearchParam param, int first, int max);
	
	Long adminCountCampaign(AdminTrxInfo adminTrxInfo, CampaignSearchParam param);


}
