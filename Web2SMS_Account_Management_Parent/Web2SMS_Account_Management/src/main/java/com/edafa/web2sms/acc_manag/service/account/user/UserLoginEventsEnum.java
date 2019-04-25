/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.account.user;

/**
 *
 * @author mahmoud
 */
public enum UserLoginEventsEnum {
    
    USER_CHECK("userCheck"), USER_LOGIN("userLogin"), GENERATE_PASSWORD("generatePassword"),
    CHANGE_PASSWORD("changePassword"), FORGET_PASSWORD("forgetPassword"), USER_UNLOCK("userUnlock");
    
    String eventName;

    private UserLoginEventsEnum(String eventName) {
        this.eventName = eventName;
    }   

    public String getEventName() {
        return eventName;
    }    
    
}
