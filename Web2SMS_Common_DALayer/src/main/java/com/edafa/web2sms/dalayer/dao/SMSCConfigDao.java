package com.edafa.web2sms.dalayer.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSCConfigDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SMSCConfig;

/**
 * 
 * @author akhalifah
 */
@Stateless
public class SMSCConfigDao extends AbstractDao<SMSCConfig> implements
		SMSCConfigDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public SMSCConfigDao() {
		super(SMSCConfig.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.edafa.smsgw.dalayer.dao.interfaces.SMSCConfigDaoLocal#findAllCount()
	 */
	@Override
	public int findAllCount() throws DBException {
		try {
			return em.createNamedQuery(SMSCConfig.FIND_ALL_COUNT, Long.class)
					.getSingleResult().intValue();

		}// end try
		catch (Exception e) {
			throw new DBException(e);
		}// end catch
	}// end of method findAllCount

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.edafa.smsgw.dalayer.dao.interfaces.SMSCConfigDaoLocal#findAll(int,
	 * int, java.lang.String)
	 */
	@Override
	public List<SMSCConfig> findAll(int max, int first) throws DBException {
		List<SMSCConfig> smscList = em
				.createNamedQuery(SMSCConfig.FIND_ALL, SMSCConfig.class)
				.setFirstResult(first).setMaxResults(max).getResultList();
		return smscList;
	}

	@Override
	public List<SMSCConfig> findActive() throws DBException {
		try {
			List<SMSCConfig> smscList = em.createNamedQuery(
					SMSCConfig.FIND_ACTIVE, SMSCConfig.class).getResultList();
			return smscList;
		} catch (Exception e) {
			throw new DBException(e);
		}
	}
}
