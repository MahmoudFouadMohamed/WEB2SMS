package com.edafa.web2sms.dalayer.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.dao.interfaces.ServiceDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Service;

/**
 *
 * @author loay
 */
@Singleton
@Startup
public class ServiceDao extends AbstractDao<Service> implements ServiceDaoLocal {

    private final String LOG_CLASS_NAME = "ServiceDao: ";
    private final String LOG_ENTITY_NAME = "Service";

    @PersistenceContext(unitName = PersistenceUnits.ALARMS_DB)
    private EntityManager em;

    private Map<String, Service> cachedMap;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ServiceDao() {
        super(Service.class);
        cachedMap = new HashMap<String, Service>();
    }

    @PostConstruct
    private void init() {
        try {
            refreshCachedValues();
        } catch (Exception e) {
            throw new Error("Cannot initialize the cached " + LOG_ENTITY_NAME + " Entity Map", e);
        }
    }

    @Override
    public void refreshCachedValues() throws DBException {
        System.out.println(LOG_CLASS_NAME + "Retrieve " + LOG_ENTITY_NAME + " list from database");
        List<Service> serviceList = findAll();
        System.out.println(LOG_CLASS_NAME + "Retrieved " + LOG_ENTITY_NAME + " list from database, found: " + serviceList.size() + " elements.");

        for (Service service : serviceList) {
            cachedMap.put(service.getServiceName(), service);
        }
    }

    @Override
    public Service getCachedObjectById(Object id) {
        for (Service service : cachedMap.values()) {
            if (service.getServiceId().equals(id)) {
                return service;
            }
        }
        return null;
    }

    @Override
    public Service getCachedObjectByName(String name) {
        return cachedMap.get(name);
    }

}
