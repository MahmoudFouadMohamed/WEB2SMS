/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.ProvRequestsArchDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.ProvRequestArch;
import com.edafa.web2sms.dalayer.model.ProvRequestStatus;
import com.edafa.web2sms.dalayer.pojo.ProvisioningEvent;

/**
 * 
 * @author yyaseen
 */
@Stateless
public class ProvRequestsArchDao extends AbstractDao<ProvRequestArch> implements ProvRequestsArchDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ProvRequestsArchDao() {
		super(ProvRequestArch.class);
	}

	@Override
	public List<ProvisioningEvent> findProvRequestByDateAndStatus(Date startDate, Date endDate, ProvRequestStatus status)
			throws DBException {
		try {
			List<ProvisioningEvent> result = em
					.createNamedQuery(ProvRequestArch.FIND_BY_UPDATEDATE_AND_STATUS, ProvisioningEvent.class)
					.setParameter(ProvRequestArch.START_DATE, startDate)
					.setParameter(ProvRequestArch.END_DATE, endDate).setParameter(ProvRequestArch.STATUS, status)
					.getResultList();

			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<ProvRequestArch> findProvRequestByAccountId(String accountId) throws DBException {
		try {
			List<ProvRequestArch> result = em
					.createNamedQuery(ProvRequestArch.FIND_BY_ACCOUNT_ID, ProvRequestArch.class)
					.setParameter(ProvRequestArch.COMPANY_ID, accountId).getResultList();

			return result;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}
}
