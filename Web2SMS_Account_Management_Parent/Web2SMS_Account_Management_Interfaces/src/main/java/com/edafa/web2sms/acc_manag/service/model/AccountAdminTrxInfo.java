package com.edafa.web2sms.acc_manag.service.model;

import com.edafa.web2sms.dalayer.model.Admin;
import java.io.Serializable;

public class AccountAdminTrxInfo extends AccountTrxInfo implements Serializable {
	
	Admin admin;
	
	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	
	public String logId() {
		StringBuilder sb = new StringBuilder();
		sb.append("UserTrx(");
		sb.append(trxId);
		sb.append("): ");
		return sb.toString();
	}

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
}
