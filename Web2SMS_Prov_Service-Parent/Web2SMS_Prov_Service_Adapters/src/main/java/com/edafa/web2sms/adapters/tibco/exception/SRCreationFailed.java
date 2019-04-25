package com.edafa.web2sms.adapters.tibco.exception;

import eg.com.vfe.xmlns.eai.celfocus.setservice.reply.SetServiceReply;

public class SRCreationFailed extends Exception {
	String URL;
	int returnStatus;
	String returnCode;
	SetServiceReply reply;

	private static final long serialVersionUID = -7632637346436703264L;

	public SRCreationFailed(String URL, int returnResult) {
		super("Failed to create SR, URL: " + URL + ", returnStatus: " + returnResult);
		this.URL = URL;
		this.returnStatus = returnResult;
	}

	public SRCreationFailed(String URL, String returnCode) {
		super("Failed to create SR, URL: " + URL + ", returnCode: " + returnCode);
		this.URL = URL;
		this.returnCode = returnCode;
	}

	public SRCreationFailed(String URL, Throwable cause) {
		super("Failed to create SR, URL: " + URL + ", " + cause.getMessage(), cause);
		this.URL = URL;
	}

	public SRCreationFailed(String URL, SetServiceReply reply) {
		super("Failed to create SR, URL: " + URL + ", received non-success status \"" + reply.getECode()
				+ "\", errorDesc: " + reply.getEDescription());
		this.URL = URL;
		this.reply = reply;
	}

	public int getReturnStatus() {
		return returnStatus;
	}

	public String getURL() {
		return URL;
	}
}
