package com.edafa.web2sms.service.model;

import com.edafa.web2sms.acc_manag.service.model.AccountAdminTrxInfo;
import com.edafa.web2sms.dalayer.model.Admin;

public class AdminTrxInfo extends TrxInfo{
	
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
        
    public AccountAdminTrxInfo getAccountAdminTrxInfo() {
        AccountAdminTrxInfo accountAdminTrxInfo = new AccountAdminTrxInfo();
        accountAdminTrxInfo.setTrxId(this.trxId);
        accountAdminTrxInfo.setAdmin(this.admin);
        return accountAdminTrxInfo;
    }
}
