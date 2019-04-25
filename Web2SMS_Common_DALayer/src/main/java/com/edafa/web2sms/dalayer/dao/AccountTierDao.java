package com.edafa.web2sms.dalayer.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountTierDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountTier;
import com.edafa.web2sms.dalayer.model.TierType;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mayahmed
 */

@Stateless
public class AccountTierDao extends AbstractDao<AccountTier> implements
		AccountTierDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AccountTierDao() {
		super(AccountTier.class);
	}
	
	
	

	@Override
	public AccountTier findByBillingMSISDN(String msisdn) throws DBException {
		try {

			List<AccountTier> list = em
					.createNamedQuery(AccountTier.FIND_BY_MSISDN,
							AccountTier.class)
					.setParameter(AccountTier.MSISDN, msisdn).getResultList();
			if (list != null && list.size() > 0) {
				return list.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new DBException(e);
		}

	}
	
	@Override
	public AccountTier findByAccountId(String accountId) throws DBException {
		try {

			List<AccountTier> list = em
					.createNamedQuery(AccountTier.FIND_BY_ACCT_ID,
							AccountTier.class)
					.setParameter(AccountTier.ACCOUNT_ID, accountId).getResultList();
			if (list != null && list.size() > 0) {
				return list.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			throw new DBException(e);
		}

	}

	@Override
	public TierType getTierTypeForBillingMSISDN(String msisdn)
			throws DBException {
		TierType tierType = null;
		AccountTier accountTier = findByBillingMSISDN(msisdn);
		if(accountTier!=null)
			tierType = accountTier.getTier().getTierType();
		return tierType;	
	}
	
	@Override
	public TierType getTierTypeForAccountId(String acctId)
			throws DBException {
		TierType tierType = null;
		AccountTier accountTier = findByAccountId(acctId);
		if(accountTier!=null)
			tierType = accountTier.getTier().getTierType();
		return tierType;	
	}

}
