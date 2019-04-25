package com.edafa.web2sms.service.tier;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.dao.interfaces.TierDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Tier;
import com.edafa.web2sms.acc_manag.service.account.exception.TierNotFoundException;
import com.edafa.web2sms.service.admin.interfaces.AdminTierManagementBeanLocal;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.tier.exceptions.EmptyListException;
import com.edafa.web2sms.service.tier.exceptions.InvalidTierException;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

/**
 * Session Bean implementation class TierManagementBean
 */
@Stateless
@LocalBean
public class TierManagementBean implements AdminTierManagementBeanLocal {

	Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
	Logger tierLogger = LogManager.getLogger(LoggersEnum.ADMIN_MNGMT.name());

	@EJB
	private TierDaoLocal tierDao;

	@Override
	public void createTier(AdminTrxInfo adminTrxInfo, Tier tier) throws DBException, InvalidTierException {

		tierLogger.info(adminTrxInfo.logInfo());

		try {

			if (tier.getTierName() != null || tier.getRatePlan() != null || tier.getTierId() != null || tier.getTierType() != null) {
				tierLogger.debug(adminTrxInfo.logId() + "creating new tier");
				tierDao.create(tier);
			} else {
				throw new InvalidTierException(tier);
			}

		} catch (DBException e) {
			throw e;

		}

	}

	@Override
	public void updateTier(AdminTrxInfo adminTrxInfo, Tier tier) throws DBException, InvalidTierException {
		tierLogger.info(adminTrxInfo.logInfo());

		try {

			tierLogger.debug(adminTrxInfo.logId() + "creating new tier");

			if (tier.getTierName() != null || tier.getRatePlan() != null) {
				tierDao.edit(tier);
			} else {
				throw new InvalidTierException(tier);
			}

		} catch (DBException e) {
			throw e;

		}

	}

	@Override
	public void deleteTier(AdminTrxInfo adminTrxInfo, Integer tierId) throws TierNotFoundException, DBException {

		tierLogger.info(adminTrxInfo.logInfo());
		try {

			Tier tmp = tierDao.findByTierId(tierId);

			if (tmp != null) {
				tierDao.remove(tmp);
			} else {
				throw new TierNotFoundException(tierId);
			}
		} catch (DBException e) {
			throw e;

		}

	}

	@Override
	public Tier viewTier(AdminTrxInfo adminTrxInfo, Integer tierId) throws TierNotFoundException, DBException {
		tierLogger.info(adminTrxInfo.logInfo());

		Tier result;
		try {
			result = tierDao.findByTierId(tierId);
			if (result == null) {
				throw new TierNotFoundException(tierId);
			}
		} catch (DBException e) {
			throw e;

		}
		return result;
	}

	@Override
	public List<Tier> viewTier(AdminTrxInfo adminTrxInfo, int first, int max) throws EmptyListException, DBException {
		tierLogger.info(adminTrxInfo.logInfo());

		List<Tier> result = new ArrayList<>();
		try {
			result = tierDao.findAll(first, max);
			if (result == null) {
				throw new EmptyListException(first);
			}

		} catch (DBException e) {
			throw e;
		}
		return result;

	}

	@Override
	public int count(AdminTrxInfo adminTrxInfo) throws DBException {
		tierLogger.info(adminTrxInfo.logInfo());
		int result;
		try {
			result = tierDao.count();
		} catch (DBException e) {
			throw e;
		}
		return result;
	}

}
