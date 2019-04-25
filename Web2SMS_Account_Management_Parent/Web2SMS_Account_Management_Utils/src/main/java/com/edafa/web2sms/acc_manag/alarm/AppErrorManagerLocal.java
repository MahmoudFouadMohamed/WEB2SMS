package com.edafa.web2sms.acc_manag.alarm;

import javax.ejb.Local;

@Local
public interface AppErrorManagerLocal {

	boolean refreshAlarmDefinitions();

}
