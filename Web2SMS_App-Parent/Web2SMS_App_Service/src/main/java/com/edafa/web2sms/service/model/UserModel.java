package com.edafa.web2sms.service.model;

import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import com.edafa.web2sms.dalayer.enums.ActionName;
import java.io.Serializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "User", namespace = "http://www.edafa.com/web2sms/service/model/")
public class UserModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2494245869961024708L;

    @XmlElement(required = true, nillable = false)
    String username;

    @XmlElement(required = true, nillable = false)
    String accountId;

    @XmlElement(required = false, nillable = false)
    String email;

    @XmlElement(required = false, nillable = false)
    String phoneNumber;

    @XmlElement(required = false, nillable = true)
    List<ActionName> userActions;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public AccManagUserModel getAccManagUserModel() {
        AccManagUserModel accManagUserModel = new AccManagUserModel();
        accManagUserModel.setUsername(username);
        accManagUserModel.setAccountId(accountId);
        accManagUserModel.setUserActions(userActions);
        accManagUserModel.setEmail(email);
        accManagUserModel.setPhoneNumber(phoneNumber);
        return accManagUserModel;
    }

    public List<ActionName> getUserActions() {
        return userActions;
    }

    public void setUserActions(List<ActionName> userActions) {
        this.userActions = userActions;
    }

    @XmlTransient
    public boolean isValid() {
        boolean valid = true;

        if (username == null || accountId == null) {
            valid = false;
        }

        return valid;
    }

    @Override
    public String toString() {
        return "UserModel{" + "username=" + username + ", accountId=" + accountId + ", email=" + email + ", phoneNumber=" + phoneNumber + ", userActions=" + userActions + '}';
    }

}
