package com.edafa.web2sms.dalayer.dao;

import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSCBindTypeDaoLocal;
import com.edafa.web2sms.dalayer.enums.BindTypeEnum;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SMSCBindType;

/**
 * Session Bean implementation class SMSCBindTypeEnum
 */
@Stateless
@LocalBean
public class SMSCBindTypeDao extends AbstractDao<SMSCBindType> implements
		SMSCBindTypeDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public SMSCBindTypeDao() {
		super(SMSCBindType.class);
	}

	@Override
	public SMSCBindType findById(short id) throws DBException {
		try {
			return em.createNamedQuery(SMSCBindType.FIND_BY_ID, SMSCBindType.class)
					.setParameter(SMSCBindType.ID, id).getSingleResult();
		} catch (EJBException e) {
			throw new DBException(e.getCause());
		}
	}

	@Override
	public SMSCBindType findByType(BindTypeEnum type) throws DBException {
		try {
			return em.createNamedQuery(SMSCBindType.FIND_BY_TYPE, SMSCBindType.class)
					.setParameter(SMSCBindType.TYPE, type).getSingleResult();
		} catch (EJBException e) {
			throw new DBException(e.getCause());
		}
	}
}
