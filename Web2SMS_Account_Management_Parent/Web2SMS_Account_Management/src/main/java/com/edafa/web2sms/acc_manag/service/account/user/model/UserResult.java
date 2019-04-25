package com.edafa.web2sms.acc_manag.service.account.user.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;

@XmlType(name = "UserResult", namespace = "http://www.edafa.com/web2sms/service/acc_manag/account/user/model/")
public class UserResult extends ResultStatus {

    @XmlElement(name = "User", required = true, nillable = false)
    AccManagUserModel user;
    @XmlElement
    Integer numberOfAccountUsers;

    public UserResult() {
    }

    public UserResult(ResponseStatus status, String errorMessage) {
        super(status, errorMessage);
    }

    public UserResult(ResponseStatus status) {
        super(status);
    }

    public UserResult(ResponseStatus status, AccManagUserModel user) {
        super(status);
        this.user = user;
    }

    public AccManagUserModel getUser() {
        return user;
    }

    public void setUser(AccManagUserModel user) {
        this.user = user;
    }

    public Integer getNumberOfAccountUsers() {
        return numberOfAccountUsers;
    }

    public void setNumberOfAccountUsers(Integer numberOfAccountUsers) {
        this.numberOfAccountUsers = numberOfAccountUsers;
    }

}
