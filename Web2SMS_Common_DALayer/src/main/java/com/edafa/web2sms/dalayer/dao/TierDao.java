/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.TierDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Tier;
import com.edafa.web2sms.dalayer.model.TierType;

/**
 * 
 * @author yyaseen
 */
@Stateless
public class TierDao extends AbstractDao<Tier> implements TierDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public TierDao() {
		super(Tier.class);
	}

	@Override
	public Tier findByRatePlan(String ratePlan) throws DBException {
		try {
			List<Tier> resultSet = em.createNamedQuery(Tier.FIND_BY_RATE_PLAN, Tier.class)
					.setParameter(Tier.RATE_PLAN, ratePlan).getResultList();
			if (resultSet != null && !resultSet.isEmpty()) {
				return resultSet.get(0);
			}
		} catch (Exception e) {
			throw new DBException(e);
		}
		return null;
	}

	@Override
	public Tier findByTierId(Integer tierId) throws DBException {

		Tier result = em.createNamedQuery(Tier.FIND_BY_TIER_ID, Tier.class).setParameter(Tier.TIER_ID, tierId)
				.getSingleResult();
		em.clear();
		return result;
	}
	
	@Override
	public long findTotalQuotaByTierId(Integer tierId) throws DBException {

		long result = em.createNamedQuery(Tier.FIND_TOTAL_QUOTA_BY_TIER_ID, Long.class).setParameter(Tier.TIER_ID, tierId)
				.getSingleResult();
		em.clear();
		return result;
	}

	@Override
	public List<Tier> findByTierType(TierType tierType) throws DBException {
		try {
			List<Tier> result = em.createNamedQuery(Tier.FIND_BY_TIER_ID, Tier.class)
					.setParameter(Tier.TIER_Type, tierType).getResultList();
			em.clear();
			if (result != null && !result.isEmpty()) {

				return result;
			}
		} catch (Exception e) {
			throw new DBException(e);
		}
		return null;
	}

	public List<Tier> findAll(int first, int max) {
		List<Tier> result = new ArrayList<>();

		result = em.createNamedQuery(Tier.FIND_ALL, Tier.class).setFirstResult(first).setMaxResults(max)
				.getResultList();
		em.flush();
		em.clear();
		return result;
	}

	@Override
	public void remove(Tier entity) throws DBException {
		try {
			em.createNamedQuery(Tier.REMOVE_BY_TIER_ID).setParameter(Tier.TIER_ID, entity.getTierId()).executeUpdate();
			em.flush();
			em.clear();
		}// end try
		catch (Exception e) {
			throw new DBException(e);
		}// end catch
	}// end of method remove
}
