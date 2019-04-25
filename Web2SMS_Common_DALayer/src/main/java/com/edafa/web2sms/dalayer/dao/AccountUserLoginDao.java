/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.dao;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountUserLoginDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AccountUserLogin;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mahmoud
 */
@Stateless
public class AccountUserLoginDao extends AbstractDao<AccountUserLogin> implements AccountUserLoginDaoLocal {

    @PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AccountUserLoginDao(EntityManager em, Class<AccountUserLogin> entityClass) {
        super(entityClass);
        this.em = em;
    }

    public AccountUserLoginDao() {
        super(AccountUserLogin.class);
    }

}
