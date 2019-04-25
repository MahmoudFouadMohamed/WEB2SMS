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
import com.edafa.web2sms.dalayer.dao.interfaces.ProcessEntityDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Component;
import com.edafa.web2sms.dalayer.model.Node;
import com.edafa.web2sms.dalayer.model.ProcessEntity;
import com.edafa.web2sms.dalayer.model.Service;

/**
 *
 * @author loay
 */
@Singleton
@Startup
public class ProcessEntityDao extends AbstractDao<ProcessEntity> implements ProcessEntityDaoLocal {

    private final String LOG_CLASS_NAME = "ProcessEntityDao: ";
    private final String LOG_ENTITY_NAME = "ProcessEntity";

    @PersistenceContext(unitName = PersistenceUnits.ALARMS_DB)
    private EntityManager em;

    private Map<String, ProcessEntity> cachedMap;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProcessEntityDao() {
        super(ProcessEntity.class);
        cachedMap = new HashMap<String, ProcessEntity>();
    }

    @PostConstruct
    private void init() {
        try {
            refreshCachedValues();
        } catch (Exception e) {
            throw new Error("Cannot initialize the cached " + LOG_ENTITY_NAME + " Map", e);
        }
    }

    @Override
    public void refreshCachedValues() throws DBException {
        System.out.println(LOG_CLASS_NAME + "Retrieve " + LOG_ENTITY_NAME + " list from database");
        List<ProcessEntity> processEntityList = findAll();
        System.out.println(LOG_CLASS_NAME + "Retrieved " + LOG_ENTITY_NAME + " list from database, found: " + processEntityList.size() + " elements.");

        for (ProcessEntity processEntity : processEntityList) {
            cachedMap.put(processEntity.getProcessName(), processEntity);
        }
    }

    @Override
    public ProcessEntity getCachedObjectById(Object id) {
        for (ProcessEntity processEntity : cachedMap.values()) {
            if (processEntity.getProcessId().equals(id)) {
                return processEntity;
            }
        }
        return null;
    }

    @Override
    public ProcessEntity getCachedObjectByComponentId(Component component) {
        for (ProcessEntity processEntity : cachedMap.values()) {
            if (processEntity.getComponentId().equals(component)) {
                return processEntity;
            }
        }
        return null;
    }

    @Override
    public ProcessEntity getCachedObjectByServiceId(Service service) {
        for (ProcessEntity processEntity : cachedMap.values()) {
            if (processEntity.getServiceId().equals(service)) {
                return processEntity;
            }
        }
        return null;
    }

    @Override
    public ProcessEntity getCachedObjectByNodeId(Node node) {
        for (ProcessEntity processEntity : cachedMap.values()) {
            if (processEntity.getNodeId().equals(node)) {
                return processEntity;
            }
        }
        return null;
    }

    @Override
    public ProcessEntity getCachedObjectByName(String processName) {
        return cachedMap.get(processName);
    }
}
