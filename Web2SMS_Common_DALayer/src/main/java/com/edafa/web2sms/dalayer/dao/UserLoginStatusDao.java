/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.dao;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.UserLoginStatusDaoLocal;
import com.edafa.web2sms.dalayer.enums.UserLoginStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.UserLoginStatus;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author mahmoud
 */
@Singleton
@Startup
public class UserLoginStatusDao extends AbstractDao<UserLoginStatus> implements UserLoginStatusDaoLocal {

    @PersistenceContext(unitName = PersistenceUnits.WEB2SMS_DATA_SOURCE)
    private EntityManager em;

    private Map<UserLoginStatusName, UserLoginStatus> cachedMap;

    public UserLoginStatusDao() {
        super(UserLoginStatus.class);
        cachedMap = new HashMap<UserLoginStatusName, UserLoginStatus>();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @PostConstruct
    void init() {
        try {
            refreshCachedValues();
        } catch (Exception e) {
            throw new Error("Cannot initialize the cached AccountStatus Map", e);
        }
    }

    @Override
    public void refreshCachedValues() throws DBException {
        List<UserLoginStatus> allStatus = findAll();
        for (UserLoginStatus status : allStatus) {
            cachedMap.put(status.getName(), status);
        }
    }

    @Override
    public UserLoginStatus getCachedObjectById(Object id) {
        for (UserLoginStatus status : cachedMap.values()) {
            if (status.getName().equals(id)) {
                return status;
            }
        }
        return null;
    }

    @Override
    public UserLoginStatus getCachedObjectByName(UserLoginStatusName name) {
        return cachedMap.get(name);
    }
}
