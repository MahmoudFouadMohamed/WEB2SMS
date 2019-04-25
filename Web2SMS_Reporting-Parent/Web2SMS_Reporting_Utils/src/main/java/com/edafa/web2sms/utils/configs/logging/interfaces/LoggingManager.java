package com.edafa.web2sms.utils.configs.logging.interfaces;

import javax.ejb.Local;

@Local
public interface LoggingManager {

	public void init();

	public void close();
}
