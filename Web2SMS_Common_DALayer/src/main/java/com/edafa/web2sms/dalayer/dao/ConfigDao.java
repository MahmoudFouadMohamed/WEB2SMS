/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.dao;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.ConfigDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ModuleDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Configuration;

/**
 * 
 * @author akhalifah
 */
@Stateless
public class ConfigDao extends AbstractDao<Configuration> implements ConfigDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@EJB
	ModuleDaoLocal moduleDao;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ConfigDao() {
		super(Configuration.class);
	}

	@Override
	public List<Configuration> findByKey(String key) throws DBException {
		try {
			return em.createNamedQuery(Configuration.FIND_BY_Key, Configuration.class)
					.setParameter(Configuration.KEY, key).getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<Configuration> findByValue(String value) throws DBException {
		try {
			return em.createNamedQuery(Configuration.FIND_BY_VALUE, Configuration.class)
					.setParameter(Configuration.VALUE, value).getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<Configuration> findByModuleName(String moduleName) throws DBException {
		try {
			return em.createNamedQuery(Configuration.FIND_BY_MODULE_NAME, Configuration.class)
					.setParameter(Configuration.MODULE_NAME, moduleName).getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public Configuration findBykey(String key, String moduleName) throws DBException {
		try {
			List<Configuration> resultSet = em
					.createNamedQuery(Configuration.FIND_BY_KEY_AND_MODULE_NAME, Configuration.class)
					.setParameter(Configuration.KEY, key).setParameter(Configuration.MODULE_NAME, moduleName)
					.getResultList();

			if (resultSet != null && !resultSet.isEmpty()) {
				return resultSet.get(0);
			}
		} catch (Exception e) {
			throw new DBException(e);
		}
		return null;
	}

	@Override
	public List<Configuration> findAllEditable() throws DBException {
		try {
			return em.createNamedQuery(Configuration.FIND_ALL_EDITABLE, Configuration.class).getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<Configuration> findAllEditableRange(int max, int first) throws DBException {
		try {
			return em.createNamedQuery(Configuration.FIND_ALL_EDITABLE, Configuration.class).setMaxResults(max)
					.setFirstResult(first).getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public int findAllEditableCount() throws DBException {
		try {
			return (em.createNamedQuery(Configuration.COUNT_EDITABLE_CONFIGS, Long.class).getSingleResult()).intValue();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	public List<Configuration> findEditableByModuleName(String moduleName) throws DBException {
		try {
			return em.createNamedQuery(Configuration.FIND_EDITABLE_BY_MODULE_NAME, Configuration.class)
					.setParameter(Configuration.MODULE_NAME, moduleName).getResultList();
		} catch (Exception e) {
			throw new DBException(e);
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void edit(List<Configuration> newConfigs) throws DBException {
		try {
			for (Configuration configuration : newConfigs) {
				edit(configuration);
			}
		} catch (Exception e) {
			throw new DBException(e);
		}
	}
}
