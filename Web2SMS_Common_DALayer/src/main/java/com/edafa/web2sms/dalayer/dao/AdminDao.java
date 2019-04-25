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

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.AdminDaoLocal;
import com.edafa.web2sms.dalayer.model.Admin;

/**
 *
 * @author yyaseen
 */
@Stateless
public class AdminDao extends AbstractDao<Admin> implements AdminDaoLocal {
	@PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public AdminDao() {
		super(Admin.class);
	}

	public Admin find(String username) {
		List<Admin> admins = em.createNamedQuery(Admin.FIND_BY_USER_NAME, Admin.class).setParameter(Admin.USERNAME, username).getResultList();
		em.clear();
		if (admins != null)
			if (!admins.isEmpty())
				return admins.get(0);

		return null;
	}

}
