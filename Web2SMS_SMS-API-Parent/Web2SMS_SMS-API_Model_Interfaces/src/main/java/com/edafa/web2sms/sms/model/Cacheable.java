package com.edafa.web2sms.sms.model;

import java.util.Date;

public interface Cacheable extends Retriable {

	int getValidityMinutes();

	void setValidityMinutes(int validityMinutes);

	Date getCacheDate();

	void setCacheDate(Date cacheDate);

}