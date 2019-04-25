package com.edafa.web2sms.acc_manag.service.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.dalayer.enums.ActionName;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author akhalifah
 * 
 */
@XmlType(name = "AccountUserTrxInfo", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountUserTrxInfo extends AccountTrxInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5014274495342526304L;

	@XmlElement(required = true, nillable = false)
	AccManagUserModel user;

	@XmlTransient
	List<ActionName> userActions = new ArrayList<ActionName>();

	public AccManagUserModel getUser() {
		return user;
	}

	public void setUser(AccManagUserModel user) {
		this.user = user;
	}

	public List<ActionName> getUserActions() {
		return userActions;
	}

	public void setUserActions(List<ActionName> userActions) {
		this.userActions = userActions;
	}

	@XmlTransient
	public boolean isValid() {
		if (super.isValid() && user.isValid()) {
			return true;
		}
		return false;
	}

	@XmlTransient
	@Override
	public String logId() {
		return "UserTrx(" + trxId + "): ";
	}

	@XmlTransient
	@Override
	public String logInfo() {
		String log = "UserTrx(" + trxId + ")";

            if (user != null) {
                log += ": User(" + user.getUsername() + ") ";
                if (user.getAccountId() != null && !user.getAccountId().isEmpty()) {
                    log += "AccountId(" + user.getAccountId() + ") ";
                }
            }
		return log;
	}

	@Override
	public String toString() {
		return "UserTrxInfo (" + super.toString() + ",user=" + user + ", userActions=" + userActions + ")";
	}

}