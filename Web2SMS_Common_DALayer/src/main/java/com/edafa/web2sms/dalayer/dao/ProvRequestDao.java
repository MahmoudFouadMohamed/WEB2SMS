/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.ProvRequestDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.ProvRequestActive;
import com.edafa.web2sms.dalayer.model.ProvRequestStatus;
import com.edafa.web2sms.dalayer.model.ProvRequestType;

/**
 * 
 * @author yyaseen, akhalifah
 */
@Stateless
public class ProvRequestDao extends AbstractDao<ProvRequestActive> implements ProvRequestDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ProvRequestDao() {
		super(ProvRequestActive.class);
	}

	@Override
	public void updateStatus(String requestId, ProvRequestStatus status) throws DBException {
		try {

			em.createNamedQuery(ProvRequestActive.UPDATE_REQUEST_STATUS).setParameter(ProvRequestActive.STATUS, status)
					.setParameter(ProvRequestActive.REQUEST_ID, requestId).executeUpdate();

		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<ProvRequestActive> findByCompanyIdAndStatus(String companyId, List<ProvRequestStatus> statuses)
			throws DBException {
		List<ProvRequestActive> resultSet = null;
		try {
			TypedQuery<ProvRequestActive> q = em
					.createNamedQuery(ProvRequestActive.FIND_BY_COMP_ID_AND_STATUS, ProvRequestActive.class)
					.setParameter(ProvRequestActive.COMPANY_ID, companyId)
					.setParameter(ProvRequestActive.STATUES, statuses);
			resultSet = q.getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
		return resultSet;
	}

	@Override
	public List<ProvRequestActive> findByCompanyAdminAndStatus(String companyAdmin, List<ProvRequestStatus> statuses)
			throws DBException {
		List<ProvRequestActive> resultSet = null;
		try {
			TypedQuery<ProvRequestActive> q = em
					.createNamedQuery(ProvRequestActive.FIND_BY_COMP_ADMIN_AND_STATUSES, ProvRequestActive.class)
					.setParameter(ProvRequestActive.COMPANY_ADMIN, companyAdmin)
					.setParameter(ProvRequestActive.STATUES, statuses);
			resultSet = q.getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
		return resultSet;
	}

	@Override
	public int countByCompanyIdAndRequestTypeAndStatus(String companyId, ProvRequestType requestType,
			ProvRequestStatus status) throws DBException {
		try {
			Long count = em.createNamedQuery(ProvRequestActive.COUNT_BY_COMP_ID_AND_TYPE_AND_STATUS, Long.class)
					.setParameter(ProvRequestActive.COMPANY_ID, companyId)
					.setParameter(ProvRequestActive.TYPE, requestType).setParameter(ProvRequestActive.STATUS, status)
					.getSingleResult();

			if (count != null)
				return count.intValue();
			return 0;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public int countByCompanyIdRequestTypeStatusAndSender(String companyId, ProvRequestType requestType,
			ProvRequestStatus status, String sender) throws DBException {
		try {
			Long count = em.createNamedQuery(ProvRequestActive.COUNT_BY_COMP_ID_TYPE_STATUS_AND_SENDER, Long.class)
					.setParameter(ProvRequestActive.COMPANY_ID, companyId)
					.setParameter(ProvRequestActive.TYPE, requestType).setParameter(ProvRequestActive.STATUS, status)
					.setParameter(ProvRequestActive.SENDER_NAME, sender).getSingleResult();

			if (count != null)
				return count.intValue();
			return 0;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}
}
