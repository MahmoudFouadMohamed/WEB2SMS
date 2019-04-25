package com.edafa.web2sms.alarm;

import javax.ejb.Local;

@Local
public interface AppErrorManagerLocal {

	boolean refreshAlarmDefinitions();

}
