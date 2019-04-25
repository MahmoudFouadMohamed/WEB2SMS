/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.enums;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author mahmoud
 */
@XmlType(name = "UserLoginStatus", namespace = "http://www.edafa.com/web2sms/service/model/enums")
@XmlEnum
@XmlAccessorType(XmlAccessType.FIELD)
public enum UserLoginStatusName {

    INITIAL("initial"), TEMP("temp"), ACTIVE("active"), BLOCKED("blocked"), UNKNOWN("unknown");
    String statusName;

    private UserLoginStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String name) {
        this.statusName = name;
    }

    public String getName() {
        return this.name();
    }

    @Override
    public String toString() {
        return getStatusName();
    }
}
