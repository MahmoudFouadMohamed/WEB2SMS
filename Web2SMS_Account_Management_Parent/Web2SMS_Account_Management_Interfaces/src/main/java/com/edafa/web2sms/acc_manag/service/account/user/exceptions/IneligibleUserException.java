/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.account.user.exceptions;

/**
 *
 * @author mahmoud
 */
public class IneligibleUserException extends Exception {
	
	public IneligibleUserException(String message) {
		super(message);
	}

	@Override
	public synchronized Throwable fillInStackTrace() {
		return this;
	}
}
