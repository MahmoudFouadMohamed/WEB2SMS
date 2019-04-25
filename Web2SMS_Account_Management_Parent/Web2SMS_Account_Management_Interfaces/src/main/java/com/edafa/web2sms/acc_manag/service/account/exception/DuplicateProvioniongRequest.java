package com.edafa.web2sms.acc_manag.service.account.exception;

import com.edafa.web2sms.dalayer.enums.ProvRequestTypeName;

public class DuplicateProvioniongRequest extends Exception {

	private static final long serialVersionUID = -1020950811583469357L;
	String requestId;
	ProvRequestTypeName type;

	public DuplicateProvioniongRequest(String requestId) {
		super("Duplicate provisioning request with id (" + requestId + ")");
		this.requestId = requestId;
	}

	public DuplicateProvioniongRequest(ProvRequestTypeName type) {
		super("Duplicate provisioning request with type (" + type + ")");
		this.type = type;
	}

	public String getRequestId() {
		return requestId;
	}

	public ProvRequestTypeName getType() {
		return type;
	}

}
