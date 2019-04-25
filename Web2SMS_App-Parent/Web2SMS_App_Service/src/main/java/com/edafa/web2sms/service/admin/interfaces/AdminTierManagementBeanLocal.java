package com.edafa.web2sms.service.admin.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Tier;
import com.edafa.web2sms.acc_manag.service.account.exception.TierNotFoundException;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.tier.exceptions.EmptyListException;
import com.edafa.web2sms.service.tier.exceptions.InvalidTierException;

@Local
public interface AdminTierManagementBeanLocal {

	void createTier(AdminTrxInfo adminTrxInfo, Tier tier) throws DBException, InvalidTierException;

	void updateTier(AdminTrxInfo adminTrxInfo, Tier tier) throws DBException, InvalidTierException;

	void deleteTier(AdminTrxInfo adminTrxInfo, Integer tierId) throws DBException, TierNotFoundException;
	
	Tier viewTier (AdminTrxInfo adminTrxInfo, Integer tierId) throws DBException, TierNotFoundException;
	
	List<Tier> viewTier (AdminTrxInfo adminTrxInfo, int first, int range) throws DBException, EmptyListException;
	
	int count(AdminTrxInfo adminTrxInfo) throws DBException;

}
