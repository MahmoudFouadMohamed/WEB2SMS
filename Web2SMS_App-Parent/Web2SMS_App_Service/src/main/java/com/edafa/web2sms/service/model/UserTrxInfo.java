package com.edafa.web2sms.service.model;

import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.dalayer.enums.ActionName;
import java.util.ArrayList;
import java.util.List;

/**
 * @author akhalifah
 * 
 */
/**
 * @author akhalifah
 * 
 */
@XmlType(name = "UserTrxInfo", namespace = "http://www.edafa.com/web2sms/service/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserTrxInfo extends TrxInfo {

	@XmlElement(required = true, nillable = false)
	UserModel user;

	@XmlTransient
	List<ActionName> userActions = new ArrayList<>();

	public UserModel getUser() {
		return user;
	}

	public void setUser(UserModel user) {
		this.user = user;
	}

	public List<ActionName> getUserActions() {
		return userActions;
	}

	public void addUserAction(ActionName userAction) {
		this.userActions.add(userAction);
	}

	@XmlTransient
	public boolean isValid() {
		if (super.isValid() && user.isValid()) {
			return true;
		}
		return false;
	}
        @XmlTransient
        public AccManagUserModel getAccManagUserModel(){
            AccManagUserModel accManagUserModel = new AccManagUserModel();
            accManagUserModel.setAccountId(user.accountId);
            accManagUserModel.setUsername(user.username);
            accManagUserModel.setEmail(user.getEmail());
            accManagUserModel.setPhoneNumber(user.getPhoneNumber());
            return accManagUserModel;
        }
        
        
	@XmlTransient
	@Override
	public String logId() {
		return "UserTrx(" + trxId + "): ";
	}

	@XmlTransient
	@Override
	public String logInfo() {
		String log = "UserTrx(" + trxId + "): User(" + user.getUsername() + ") ";

		if (user != null)
			log += "AccountId(" + user.getAccountId() + ") ";

		if (userActions != null) {
			log += "Action(" + userActions + ") ";
		}
		return log;
	}
        
        
        
        @XmlTransient
        public AccountUserTrxInfo getAccountUserTrxInfo() {
            AccountUserTrxInfo accountUserTrxInfo = new AccountUserTrxInfo();
            accountUserTrxInfo.setTrxId(this.getTrxId());
            accountUserTrxInfo.setUser(this.getAccManagUserModel());
            accountUserTrxInfo.setUserActions(this.getUserActions());
            return accountUserTrxInfo;
        }

	@Override
	public String toString() {
		return "UserTrxInfo (" + super.toString() + ",user=" + user + ", userActions=" + userActions + ")";
	}

}