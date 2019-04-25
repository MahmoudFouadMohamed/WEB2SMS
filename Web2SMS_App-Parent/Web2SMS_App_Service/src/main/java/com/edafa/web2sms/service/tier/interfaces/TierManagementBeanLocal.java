package com.edafa.web2sms.service.tier.interfaces;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.TierModel;

public interface TierManagementBeanLocal {
	
	
	void createTier(AdminTrxInfo adminTrxInfo, TierModel tierModel) throws DBException, IneligibleAccountException ;
	
	void editTier(AdminTrxInfo adminTrxInfo, TierModel tierModel) throws IneligibleAccountException, DBException;
	
	void deleteTier(AdminTrxInfo adminTrxInfo, Integer tierId) throws IneligibleAccountException, DBException;

}
