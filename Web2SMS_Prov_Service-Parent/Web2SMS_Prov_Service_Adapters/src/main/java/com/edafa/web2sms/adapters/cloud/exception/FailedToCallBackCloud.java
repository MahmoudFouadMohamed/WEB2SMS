package com.edafa.web2sms.adapters.cloud.exception;

public class FailedToCallBackCloud extends Exception {

	String URL;
	int returnStatus;

	private static final long serialVersionUID = -7632637346436703264L;

	public FailedToCallBackCloud(String URL, int returnResult) {
		super("Failed to call the cloud back URL: " + URL + ", returnStatus: " + returnResult);
		this.URL = URL;
		this.returnStatus = returnResult;
	}

	public FailedToCallBackCloud(String URL, Throwable cause) {
		super("Failed to call the cloud back URL: " + URL + ", " + cause.getMessage(), cause);
		this.URL = URL;
	}

	public int getReturnStatus() {
		return returnStatus;
	}

	public String getURL() {
		return URL;
	}
}
