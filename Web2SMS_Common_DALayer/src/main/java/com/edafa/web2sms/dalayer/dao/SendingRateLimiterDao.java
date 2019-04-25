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
import com.edafa.web2sms.dalayer.dao.interfaces.SendingRateLimiterDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SendingRateLimiter;
import com.edafa.web2sms.dalayer.model.constants.SendingRateLimiterConstant;

/**
 *
 * @author mahmoud
 */
@Stateless
public class SendingRateLimiterDao extends AbstractDao<SendingRateLimiter>
		implements SendingRateLimiterDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	public SendingRateLimiterDao() {
		super(SendingRateLimiter.class);
	}

	@Override
	public void create(List<SendingRateLimiter> limiters) throws DBException {
		for (SendingRateLimiter limiter : limiters) {
			try {
				create(limiter);
			} catch (DBException e) {
				throw new DBException("Failed to create entity in DB ["
						+ limiter + "].", e);
			}
		}
	}

	@Override
	public List<SendingRateLimiter> findAll() throws DBException {
		List<SendingRateLimiter> limiters = new ArrayList<SendingRateLimiter>();
		limiters = em.createNamedQuery(SendingRateLimiterConstant.FIND_ALL)
				.getResultList();

//		System.out.println("Dao limit: " + limiters.size());
		return limiters;

	}

	@Override
	public void edit(List<SendingRateLimiter> limiters) throws DBException {
		for (SendingRateLimiter limiter : limiters) {
			try {
				edit(limiter);
			} catch (DBException e) {
				throw new DBException("Failed to edit entity in DB [" + limiter
						+ "].", e);
			}
		}
	}
	
	
	@Override
	public void edit(SendingRateLimiter limiter) throws DBException {
			try {
		int result =	em.createNamedQuery(SendingRateLimiterConstant.UPDATE_LIMITER)
					.setParameter(SendingRateLimiterConstant.CAMP_ENABLED,
							limiter.getCampEnabled())
					.setParameter(SendingRateLimiterConstant.LIMITER_ID,
							limiter.getLimiterId())
					.setParameter(SendingRateLimiterConstant.MAX_PERMITS,
							limiter.getMaxPermits())
					.setParameter(SendingRateLimiterConstant.SMS_API_ENABLED,
							limiter.getSmsapiEnabled()).executeUpdate();
		} catch (Exception e) {
			throw new DBException("Failed to edit entity in DB [" + limiter
					+ "].", e);

		}
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

}
