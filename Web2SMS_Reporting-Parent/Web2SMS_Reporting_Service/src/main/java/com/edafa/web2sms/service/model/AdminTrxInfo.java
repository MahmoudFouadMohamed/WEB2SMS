package com.edafa.web2sms.service.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.acc_manag.service.model.AccountAdminTrxInfo;
import com.edafa.web2sms.dalayer.model.Admin;

@XmlType(name = "AdminTrxInfo", namespace = "http://www.edafa.com/web2sms/reporting/service/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class AdminTrxInfo extends TrxInfo {

	@XmlElement(required = true, nillable = false)
	private Admin admin;

	public AdminTrxInfo() {}

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	@Override
	public String logId() {
		StringBuilder sb = new StringBuilder();
		sb.append("UserTrx(");
		sb.append(trxId);
		sb.append("): ");
		return sb.toString();
	}

	@Override
	public String logInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("UserTrx");
		sb.append("(");
		sb.append(trxId);
		sb.append("): ");
		sb.append("User(");
		sb.append(admin.getUsername());
		sb.append("). ");

		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{admin=").append(admin).append(", trxId=").append(trxId).append("}");
		return builder.toString();
	}

	public AccountAdminTrxInfo getAccountAdminTrxInfo() {
		AccountAdminTrxInfo accountAdminTrxInfo = new AccountAdminTrxInfo();
		accountAdminTrxInfo.setTrxId(this.trxId);
		accountAdminTrxInfo.setAdmin(this.admin);
		return accountAdminTrxInfo;
	}
}
