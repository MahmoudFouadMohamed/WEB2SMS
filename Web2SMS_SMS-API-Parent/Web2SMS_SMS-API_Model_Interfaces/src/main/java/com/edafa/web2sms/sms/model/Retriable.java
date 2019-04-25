package com.edafa.web2sms.sms.model;

public interface Retriable {

	int getRetryCount();

	int incrementRetryCount();
}