package com.edafa.web2sms.service.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import com.edafa.web2sms.acc_manag.service.model.AccountUserTrxInfo;
import com.edafa.web2sms.dalayer.enums.ActionName;

/**
 * @author akhalifah
 * 
 */
/**
 * @author akhalifah
 * 
 */
@XmlType(name = "UserTrxInfo", namespace = "http://www.edafa.com/web2sms/reporting/service/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class UserTrxInfo extends TrxInfo {

	@XmlElement(required = true, nillable = false)
	private UserModel user;

	@XmlTransient
	private List<ActionName> userActions = new ArrayList<>();

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

	@Override
	@XmlTransient
	public boolean isValid() {
		if (super.isValid() && user.isValid()) {
			return true;
		}
		return false;
	}
	@XmlTransient
	public AccManagUserModel getAccManagUserModel() {
		AccManagUserModel accManagUserModel = new AccManagUserModel();
		accManagUserModel.setAccountId(user.getAccountId());
		accManagUserModel.setUsername(user.getUsername());
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
		StringBuilder builder = new StringBuilder();
		builder.append("{user=").append(user).append(", userActions=").append(userActions).append(", trxId=")
				.append(trxId).append("}");
		return builder.toString();
	}

}
