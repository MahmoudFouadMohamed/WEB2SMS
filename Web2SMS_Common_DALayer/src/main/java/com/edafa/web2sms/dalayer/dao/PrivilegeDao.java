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
import com.edafa.web2sms.dalayer.dao.interfaces.PrivilegeDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Privilege;

/**
 *
 * @author mahmoud
 */
@Stateless
public class PrivilegeDao extends AbstractDao<Privilege> implements PrivilegeDaoLocal {

    @PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
    private EntityManager em;

    public PrivilegeDao() {
        super(Privilege.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Privilege findByPrivilegeName(String privilegeName) throws DBException {
        try {
            TypedQuery<Privilege> q = em.createNamedQuery(Privilege.FIND_BY_PRIVILEGE_NAME, Privilege.class).setParameter(
                    Privilege.PRIVILEGE_NAME, privilegeName);
            List<Privilege> list = q.getResultList();
            if (list != null && list.size() > 0) {
                return list.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new DBException(e);
        }
    }

    @Override
    public List<Privilege> findByHiddenFlag(boolean hiddenFlag) throws DBException {
        try {
            TypedQuery<Privilege> q = em.createNamedQuery(Privilege.FIND_BY_HIDDEN_FLAG, Privilege.class).setParameter(
                    Privilege.HIDDEN_FLAG, hiddenFlag);
            List<Privilege> list = q.getResultList();
            if (list != null && list.size() > 0) {
                return list;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new DBException(e);
        }
    }   

}
