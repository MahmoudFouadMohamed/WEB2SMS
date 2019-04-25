/**
 * 
 */
package com.edafa.web2sms.acc_manag.service.account.group.exception;

/**
 * @author memad
 *
 */
public class GroupAlreadyExistException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -770149881013472222L;

	/**
	 * 
	 */
	public GroupAlreadyExistException() {

	}

	/**
	 * @param message
	 */
	public GroupAlreadyExistException(String message) {
		super(message);
	}

	/**
	 * @param message
	 */
	public GroupAlreadyExistException(Throwable message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public GroupAlreadyExistException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public GroupAlreadyExistException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
