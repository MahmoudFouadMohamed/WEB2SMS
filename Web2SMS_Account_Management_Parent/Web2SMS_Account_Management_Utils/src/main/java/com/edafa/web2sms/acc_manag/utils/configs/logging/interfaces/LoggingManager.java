package com.edafa.web2sms.acc_manag.utils.configs.logging.interfaces;

import javax.ejb.Local;

@Local
public interface LoggingManager {
	public void init();

	public void close();
}
