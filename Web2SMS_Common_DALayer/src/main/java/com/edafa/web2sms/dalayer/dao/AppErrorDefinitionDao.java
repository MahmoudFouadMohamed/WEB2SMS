/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.dao;

import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.ErrorDefinitionDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.ErrorDefinitionEntity;
import com.edafa.web2sms.dalayer.model.Module;

/**
 * 
 * @author akhalifah
 */
@Stateless
public class AppErrorDefinitionDao extends AbstractDao<ErrorDefinitionEntity> implements ErrorDefinitionDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AppErrorDefinitionDao() {
		super(ErrorDefinitionEntity.class);
	}

	@Override
	public List<ErrorDefinitionEntity> findByModule(Module module) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ErrorDefinitionEntity> findByModules(List<Module> modules) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, List<ErrorDefinitionEntity>> findModulesErrorDefinition() throws DBException {
		// TODO Auto-generated method stub
		return null;
	}
}
