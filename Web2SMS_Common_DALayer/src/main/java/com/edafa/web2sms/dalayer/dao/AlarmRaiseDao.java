package com.edafa.web2sms.dalayer.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.edafa.jee.alarms.vfeg.AlarmConfiguration;
import com.edafa.jee.alarms.vfeg.exception.AlarmRaiseLocallyException;
import com.edafa.jee.apperr.alarm.Alarm;
import com.edafa.jee.apperr.exceptions.AlarmRaiseException;
import com.edafa.web2sms.dalayer.constants.PersistenceUnits;
import com.edafa.web2sms.dalayer.constants.StoredProcedurs;
import com.edafa.web2sms.dalayer.dao.interfaces.AlarmConfigurationDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AlarmDefinitionsDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.AlarmRaiseDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.FtpServerAuthenticationDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.FtpServerDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ProcessEntityDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.AlarmDefinitions;
import com.edafa.web2sms.dalayer.model.Categories;
import com.edafa.web2sms.dalayer.model.Component;
import com.edafa.web2sms.dalayer.model.FtpServer;
import com.edafa.web2sms.dalayer.model.Node;
import com.edafa.web2sms.dalayer.model.ProcessEntity;

/**
 * Session Bean implementation class AlarmRaiseDao
 */
@Stateless
@LocalBean
public class AlarmRaiseDao implements AlarmRaiseDaoLocal {

    @PersistenceContext(unitName = PersistenceUnits.ALARMS_DB)
    private EntityManager em;

    @EJB
    FtpServerAuthenticationDaoLocal ftpServerAuthenticationDao;

    @EJB
    ProcessEntityDaoLocal processEntityDao;
    @EJB
    AlarmDefinitionsDaoLocal alarmDefinitionsDao;
    @EJB
    AlarmConfigurationDaoLocal alarmConfigurationDao;
    @EJB
    FtpServerDaoLocal ftpServerDao;

    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void raiseAlarm(Object processId, Alarm alarm) throws DBException, AlarmRaiseException {
        int result;
        int resultWrite;
        DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS");
        try {
            Connection con = (Connection) getEntityManager().unwrap(Connection.class);

            CallableStatement callableStatement = con.prepareCall("{ call " + StoredProcedurs.RAISE_ALARM
                    + "(?,?,?,?,?,?,?) }");

            callableStatement.setString(1, df.format(alarm.getTimestamp()));//
            callableStatement.setInt(2, Integer.valueOf((String) processId));//
            callableStatement.setInt(3, alarm.getAlarmId());//
            callableStatement.setInt(4, alarm.getSeverity().getId());//
            callableStatement.setString(5, "Source:" + alarm.getSource() + ": " + alarm.getInfo());//

            callableStatement.registerOutParameter(6, Types.NUMERIC);
            callableStatement.registerOutParameter(7, Types.NUMERIC);

            callableStatement.executeUpdate();

            result = callableStatement.getInt(6);
            resultWrite = callableStatement.getInt(7);
        } catch (Exception exception) {
            throw new DBException(exception);
        }

        if (result != 0) {
            throw new AlarmRaiseException("Can NOT raise alarm via DB, return result (" + result + ")");
        } else if (resultWrite != 0) {
            if (resultWrite > 0) {
                throw new AlarmRaiseException("Can NOT ftp alarm via DB, return result (" + resultWrite + ")");
            } else {
                throw new AlarmRaiseException("Can NOT write alarm via DB, return result (" + resultWrite + ")");
            }
        }
    }

    @Override
    public AlarmConfiguration getAlarmConfiguration(Object processId, Alarm alarm) throws AlarmRaiseLocallyException {
        ProcessEntity process = processEntityDao.getCachedObjectById(Integer.valueOf((String) processId));
        if (process == null) {
            throw new AlarmRaiseLocallyException("Can NOT find cached ProcessEntity with id = "
                    + (Integer.valueOf((String) processId)));
        }
        Node node = process.getNodeId();
        if (node == null) {
            throw new AlarmRaiseLocallyException("Can NOT find cached NodeEntity relative to ProcessId = " + process.getProcessId());
        }
        Component component = process.getComponentId();
        if (component == null) {
            throw new AlarmRaiseLocallyException("Can NOT find cached ComponentEntity relative to ProcessId = " + process.getProcessId());
        }

        AlarmDefinitions alarmDefinition = alarmDefinitionsDao.getCachedObjectById(alarm.getAlarmId());
        if (alarmDefinition == null) {
            throw new AlarmRaiseLocallyException("Can NOT find cached AlarmDefinitionsEntity with id = "
                    + alarm.getAlarmId());
        }
        Categories category = alarmDefinition.getCategoryId();
        if (category == null) {
            throw new AlarmRaiseLocallyException("Can NOT find cached CategoryEntity relative to AlarmDefinitionsId = " + alarmDefinition.getAlarmId());
        }

        int maxAlarmDescriptionLength = alarmConfigurationDao.getPropertyInt("MAX_ALARM_DESCRIPTION_LENGTH");

        String alarmFtpPath = alarmConfigurationDao.getProperty("ALARM_FTP_PATH");

        String alarmLocationOfNode = alarmConfigurationDao.getProperty("LOCATION_OF_NODE");
        if (alarmLocationOfNode == null) {
            alarmLocationOfNode = "";
        }

        String alarmOthersInAlarmTxt = alarmConfigurationDao.getProperty("OTHERS");
        if (alarmOthersInAlarmTxt == null) {
            alarmOthersInAlarmTxt = "";
        }
        int serverId = alarmConfigurationDao.getPropertyInt("USED_SERVER");
        FtpServer ftpServer = ftpServerDao.getCachedObjectById(serverId);
        if (ftpServer == null) {
            throw new AlarmRaiseLocallyException("Can NOT find cached FtpServerEntity with id = " + serverId);
        }

        int timeOut = alarmConfigurationDao.getPropertyInt("CONNECTION_TIME_OUT");

        String hostname = ftpServer.getRealIp();
        String username = ftpServerAuthenticationDao.getUsername();
        String password = ftpServerAuthenticationDao.getPassword();
        int port = ftpServer.getFtpPort();

        AlarmConfiguration alarmConfig = new AlarmConfiguration(processId, alarm, node.getDescription(), component.getComponentName(),
                category.getCategoryName(), alarm.getSeverity().getId(), maxAlarmDescriptionLength, alarmFtpPath, alarmLocationOfNode,
                alarmOthersInAlarmTxt, timeOut, hostname, username, password, port);

        return alarmConfig;
    }
}
