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
import com.edafa.web2sms.dalayer.dao.interfaces.FtpServerDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.FtpServer;

/**
 *
 * @author loay
 */
@Singleton
@Startup
public class FtpServerDao extends AbstractDao<FtpServer> implements FtpServerDaoLocal {

    private final String LOG_CLASS_NAME = "FtpServerDao: ";
    private final String LOG_ENTITY_NAME = "FtpServer";

    @PersistenceContext(unitName = PersistenceUnits.ALARMS_DB)
    private EntityManager em;

    private Map<Integer, FtpServer> cachedMap;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FtpServerDao() {
        super(FtpServer.class);
        cachedMap = new HashMap<Integer, FtpServer>();
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
        List<FtpServer> ftpServerList = findAll();
        System.out.println(LOG_CLASS_NAME + "Retrieved " + LOG_ENTITY_NAME + " list from database, found: " + ftpServerList.size() + " elements.");

        for (FtpServer ftpServer : ftpServerList) {
            cachedMap.put(ftpServer.getServerId(), ftpServer);
        }
    }

    @Override
    public FtpServer getCachedObjectById(Object serverId) {
        return cachedMap.get(serverId);
    }

    @Override
    public FtpServer getCachedObjectByName(Integer serverId) {
        return cachedMap.get(serverId);
    }

}
