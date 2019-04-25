package com.edafa.web2sms.acc_manag.service.account.exception;

public class TierNotFoundException extends Exception {

	Integer tierId;

	private static final long serialVersionUID = -396837114114445355L;

	public TierNotFoundException(Integer tierId) {
		super("Tier with id " + tierId + " not found ");
		this.tierId = tierId;
	}

	public TierNotFoundException(Integer tierId, String message) {
		super(message);
		this.tierId = tierId;
	}

	public TierNotFoundException(Throwable cause) {
		super(cause);
	}

	public TierNotFoundException(String message) {
		super(message);
	}

	public Integer getTierId() {
		return tierId;
	}
}
