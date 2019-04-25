package com.edafa.web2sms.dalayer.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountIPDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountIP;

@Stateless
public class AccountIPDao extends AbstractDao<AccountIP> implements AccountIPDaoLocal {

	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * Default constructor.
	 */
	public AccountIPDao() {
		super(AccountIP.class);
	}

	
	public void remove (AccountIP entity) throws DBException
	{
		
		em.remove(entity);
	}

}
