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
public class LoginEvent {
    private UserLoginEventsEnum loginEventName;
    private String userLang;

    public LoginEvent(UserLoginEventsEnum loginEventName, String userLang) {
        this.loginEventName = loginEventName;
        this.userLang = userLang;
    }      

    public UserLoginEventsEnum getLoginEventName() {
        return loginEventName;
    }

    public void setLoginEventName(UserLoginEventsEnum loginEventName) {
        this.loginEventName = loginEventName;
    }    

    public String getUserLang() {
        return userLang;
    }

    public void setUserLang(String userLang) {
        this.userLang = userLang;
    }
           
}
