package com.edafa.web2sms.dalayer.dao.interfaces;

import javax.ejb.Local;

import com.edafa.jee.alarms.vfeg.exception.AlarmRaiseLocallyException;
import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.AlarmConfiguration;

/**
 *
 * @author loay
 */
@Local
public interface AlarmConfigurationDaoLocal extends Cachable<AlarmConfiguration, String> {

    public String getProperty(String propertyName) throws AlarmRaiseLocallyException;

    public int getPropertyInt(String propertyName) throws AlarmRaiseLocallyException;
}
