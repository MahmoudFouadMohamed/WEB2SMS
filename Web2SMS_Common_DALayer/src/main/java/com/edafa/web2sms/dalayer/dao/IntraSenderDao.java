package com.edafa.web2sms.dalayer.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.IntraSenderDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.IntraSender;

/**
 * Session Bean implementation class IntraSenderDao
 */
@Stateless
public class IntraSenderDao extends AbstractDao<IntraSender> implements IntraSenderDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	/**
	 * Default constructor.
	 */
	public IntraSenderDao() {
		super(IntraSender.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	@Override
	public List<IntraSender> findSystemIntraSendersList() throws DBException {
		try {
			List<IntraSender> intraSenderList = em.createNamedQuery(IntraSender.FIND_SYSTEM_SENDER,
					IntraSender.class).getResultList();
			return intraSenderList;
		} catch (Exception ex) {
			throw new DBException(ex);
		}
	}

	@Override
	public IntraSender findIntraSenderByName(String senderName) throws DBException {
		try {
			List<IntraSender> intraSenderList = em
					.createNamedQuery(IntraSender.FIND_BY_SENDER_NAME, IntraSender.class)
					.setParameter(IntraSender.SENDER_NAME, senderName).getResultList();
			return (intraSenderList != null ? intraSenderList.get(0) : null);
		} catch (Exception ex) {
			throw new DBException(ex);
		}
	}

	@Override
	public int countIntraSender(String intraSender) throws DBException {
		try {
			Long intraSenderCount = 0L;
			intraSenderCount = em.createNamedQuery(IntraSender.COUNT_BY_SENDER_NAME, Long.class).setParameter(IntraSender.SENDER_NAME, intraSender).getSingleResult();
			return intraSenderCount.intValue();
		} catch (Exception ex) {
			throw new DBException(ex);
		}
	}
}
