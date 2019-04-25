/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author yyaseen
 */
@Embeddable
public class AccountSenderPK implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "ACCOUNT_ID")
    private String accountId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 23)
    @Column(name = "SENDER_NAME")
    private String senderName;

    public AccountSenderPK() {
    }

    public AccountSenderPK(String accountId, String senderName) {
        this.accountId = accountId;
        this.senderName = senderName;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (accountId != null ? accountId.hashCode() : 0);
        hash += (senderName != null ? senderName.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AccountSenderPK)) {
            return false;
        }
        AccountSenderPK other = (AccountSenderPK) object;
        if ((this.accountId == null && other.accountId != null) || (this.accountId != null && !this.accountId.equals(other.accountId))) {
            return false;
        }
        if ((this.senderName == null && other.senderName != null) || (this.senderName != null && !this.senderName.equals(other.senderName))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AccountSendersPK[ accountId=" + accountId + ", senderName=" + senderName + " ]";
    }
    
}
