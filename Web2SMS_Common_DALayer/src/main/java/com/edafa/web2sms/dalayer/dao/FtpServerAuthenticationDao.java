package com.edafa.web2sms.dalayer.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.constants.StoredProcedurs;
import com.edafa.web2sms.dalayer.dao.interfaces.FtpServerAuthenticationDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;

/**
 *
 * @author loay
 */
@Singleton
@Startup
public class FtpServerAuthenticationDao implements FtpServerAuthenticationDaoLocal {

    private final String LOG_CLASS_NAME = "FtpServerAuthenticationDao: ";
    private final String LOG_NAME = "FtpServerAuthentication";

    @PersistenceContext(unitName = PersistenceUnits.ALARMS_DB)
    private EntityManager em;

    private String username;
    private String password;

    protected EntityManager getEntityManager() {
        return em;
    }

    @PostConstruct
    private void init() {
        try {
            refreshCachedValues();
        } catch (Exception e) {
            throw new Error("Cannot initialize the cached " + LOG_NAME + " Values", e);
        }
    }

    @Override
    public void refreshCachedValues() throws DBException {
        System.out.println(LOG_CLASS_NAME + "Retrieve " + LOG_NAME + " from database");
        try {
            Connection con = (Connection) getEntityManager().unwrap(Connection.class);

            CallableStatement callableStatement = con.prepareCall("{ call " + StoredProcedurs.DEC_FTP_CON_AUTH + "(?,?) }");

            callableStatement.registerOutParameter(1, Types.VARCHAR);
            callableStatement.registerOutParameter(2, Types.VARCHAR);

            callableStatement.executeUpdate();

            username = callableStatement.getString(1);
            password = callableStatement.getString(2);
            System.out.println(LOG_CLASS_NAME + "Retrieved " + LOG_NAME + " from database");
        } catch (Exception exception) {
            throw new DBException(exception);
        }
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getCachedObjectById(Object id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getCachedObjectByName(String propertyName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
