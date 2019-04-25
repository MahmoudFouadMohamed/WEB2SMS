/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.SystemRolesDaoLocal;
import com.edafa.web2sms.dalayer.model.SystemRole;

/**
 * 
 * @author yyaseen
 */
@Stateless
public class RoleDao extends AbstractDao<SystemRole> implements SystemRolesDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public RoleDao() {
		super(SystemRole.class);
	}

	@Override
	public SystemRole findByRoleId(Integer roleId) {
		SystemRole result = em.createNamedQuery(SystemRole.FIND_BY_ROLE_ID, SystemRole.class)
				.setParameter(SystemRole.ROLE_ID, roleId).getSingleResult();
		em.clear();
		return result;
	}

}
