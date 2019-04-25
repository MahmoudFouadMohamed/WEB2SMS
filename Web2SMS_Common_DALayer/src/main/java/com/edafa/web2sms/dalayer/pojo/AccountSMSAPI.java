
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.pojo;

import java.util.ArrayList;
import java.util.List;

public class AccountSMSAPI {
    private String password;
    private String secureKey;
    private List<String> accountIPs;

	public AccountSMSAPI() {
		accountIPs = new ArrayList<String>();
    }

    public AccountSMSAPI( String password, String secureKey) {
        this.password = password;
        this.secureKey = secureKey;
        accountIPs = new ArrayList<String>();
    }


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecureKey() {
        return secureKey;
    }

    public void setSecureKey(String secureKey) {
        this.secureKey = secureKey;
    }
    public List<String> getAccountIPs() {
		return accountIPs;
	}

	public void setAccountIPs(List<String> accountIPs) {
		this.accountIPs = accountIPs;
	}
	public void addAccountIP(String accountIP) {
		this.accountIPs.add(accountIP);
	}

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("AccountSMSAPI{");
        str = str.append("password=").append(password).append(", secureKey=").append(secureKey).append(", accountIPs=").append(accountIPs).append('}');
        return str.toString();
    }
}

