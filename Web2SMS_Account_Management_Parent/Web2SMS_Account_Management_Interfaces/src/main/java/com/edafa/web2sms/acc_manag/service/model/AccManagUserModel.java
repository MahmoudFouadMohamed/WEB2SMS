package com.edafa.web2sms.acc_manag.service.model;

import com.edafa.web2sms.dalayer.enums.ActionName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "AccManagUser", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
public class AccManagUserModel implements Serializable {

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
        List<ActionName> userActions = new ArrayList<ActionName>();

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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
        

    public List<ActionName> getUserActions() {
        return userActions;
    }

    public void setUserActions(List<ActionName> userActions) {
        this.userActions = userActions;
    }        

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    

	@XmlTransient
	public boolean isValid() {
		boolean valid = true;

		if (username == null || accountId == null)
			valid = false;

		return valid;
	}

    @Override
    public String toString() {
        return "UserModel{" + "username=" + username + ", accountId=" + accountId + ", email=" + email + ", phoneNumber=" + phoneNumber + ", userActions=" + userActions + '}';
    }

        
        


}
