/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.campaign.execution.exception;

/**
 *
 * @author mahmoud
 */
public class RetrieveContactsException extends Exception {

    /**
     * Creates a new instance of <code>RetrieveContactsException</code> without
     * detail message.
     */
    public RetrieveContactsException() {
    }

    public RetrieveContactsException(String message) {
        super(message);
    }

    public RetrieveContactsException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetrieveContactsException(Throwable cause) {
        super(cause);
    }
}
