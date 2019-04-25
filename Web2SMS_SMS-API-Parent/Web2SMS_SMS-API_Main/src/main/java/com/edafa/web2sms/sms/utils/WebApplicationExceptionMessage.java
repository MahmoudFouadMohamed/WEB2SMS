package com.edafa.web2sms.sms.utils;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

/**
 * Workaround for JAX_RS_SPEC-312
 * 
 */
public class WebApplicationExceptionMessage extends WebApplicationException {

	private static final long serialVersionUID = -4769075497000714813L;

	WebApplicationExceptionMessage(Response response) {
		super(response);
	}

	/**
	 * Workaround for JAX_RS_SPEC-312
	 * 
	 */
	public String getMessage() {
		Response response = getResponse();
		Response.Status status = Response.Status.fromStatusCode(response.getStatus());
		if (status != null) {
			return (response.getStatus() + (" " + status.getReasonPhrase()));
		} else {
			return Integer.toString(response.getStatus());
		}
	}

	public String toString() {
		String s = "javax.ws.rs.WebApplicationException";
		String message = getLocalizedMessage();
		return (s + (": " + message));
	}

}