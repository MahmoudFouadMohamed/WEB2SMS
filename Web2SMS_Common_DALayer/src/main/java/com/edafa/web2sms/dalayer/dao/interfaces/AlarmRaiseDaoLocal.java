package com.edafa.web2sms.dalayer.dao.interfaces;

import javax.ejb.Local;

import com.edafa.jee.alarms.vfeg.AlarmConfiguration;
import com.edafa.jee.alarms.vfeg.exception.AlarmRaiseLocallyException;
import com.edafa.jee.apperr.alarm.Alarm;
import com.edafa.jee.apperr.exceptions.AlarmRaiseException;
import com.edafa.web2sms.dalayer.exception.DBException;

@Local
public interface AlarmRaiseDaoLocal {

    public void raiseAlarm(Object processId, Alarm alarm) throws DBException, AlarmRaiseException;

    public AlarmConfiguration getAlarmConfiguration(Object processId, Alarm alarm) throws AlarmRaiseLocallyException;
}
