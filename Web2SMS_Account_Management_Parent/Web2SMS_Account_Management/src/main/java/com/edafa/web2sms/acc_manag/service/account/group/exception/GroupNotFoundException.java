package com.edafa.web2sms.acc_manag.service.account.group.exception;

public class GroupNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5550887531810243806L;
        public String accountId;
	public String group;

	public GroupNotFoundException() {
	}

	public GroupNotFoundException(String message) {
		super(message);
	}
        
        public GroupNotFoundException(String accountId, String group) {
		this.accountId = accountId;
                this.group = group;
	}

	public GroupNotFoundException(Throwable cause) {
		super(cause);
	}

	public GroupNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public GroupNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
        
    @Override
    public String getMessage() {
        if (group != null && accountId != null) {
            return "Account(" + accountId + ") has no group(" + group + ")";
        } else {
            return super.getMessage();
        }
    }

}
