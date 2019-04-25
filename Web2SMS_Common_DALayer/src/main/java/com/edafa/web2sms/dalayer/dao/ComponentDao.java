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
import com.edafa.web2sms.dalayer.dao.interfaces.ComponentDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Component;

/**
 *
 * @author loay
 */
@Singleton
@Startup
public class ComponentDao extends AbstractDao<Component> implements ComponentDaoLocal {

    private final String LOG_CLASS_NAME = "ComponentDao: ";
    private final String LOG_ENTITY_NAME = "Component";

    @PersistenceContext(unitName = PersistenceUnits.ALARMS_DB)
    private EntityManager em;

    private Map<String, Component> cachedMap;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ComponentDao() {
        super(Component.class);
        cachedMap = new HashMap<String, Component>();
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
        List<Component> componentList = findAll();
        System.out.println(LOG_CLASS_NAME + "Retrieved " + LOG_ENTITY_NAME + " list from database, found: " + componentList.size() + " elements.");

        for (Component component : componentList) {
            cachedMap.put(component.getComponentName(), component);
        }
    }

    @Override
    public Component getCachedObjectById(Object id) {
        for (Component component : cachedMap.values()) {
            if (component.getComponentId().equals(id)) {
                return component;
            }
        }
        return null;
    }

    @Override
    public Component getCachedObjectByName(String name) {
        return cachedMap.get(name);
    }

}
