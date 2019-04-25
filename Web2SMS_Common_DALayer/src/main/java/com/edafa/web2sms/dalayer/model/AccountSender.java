/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.model.constants.AccountSenderConst;

/**
 * 
 * @author yyaseen
 */
@Entity
@Table(name = "ACCOUNT_SENDERS")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "AccountSender.findAll", query = "SELECT a FROM AccountSender a"),
		@NamedQuery(name = "AccountSender.removeAllByAccountId", query = "DELETE FROM AccountSender a WHERE a.accountSendersPK.accountId = :accountId"),
		@NamedQuery(name = "AccountSender.findByAccountId", query = "SELECT a FROM AccountSender a WHERE a.accountSendersPK.accountId = :accountId"),
		@NamedQuery(name = "AccountSender.countByAccountId", query = "SELECT COUNT(a) FROM AccountSender a WHERE a.accountSendersPK.accountId = :accountId"),
//		@NamedQuery(name = "AccountSender.findBySenderName", query = "SELECT a FROM AccountSender a WHERE a.accountSendersPK.senderName = :senderName "),
		@NamedQuery(name = "AccountSender.findByAccountIdAndSenderName", query = "SELECT a FROM AccountSender a WHERE a.accountSendersPK.senderName = :senderName AND a.accountSendersPK.accountId = :accountId"),
//		@NamedQuery(name = "AccountSender.countBySenderName", query = "SELECT COUNT(a) FROM AccountSender a WHERE a.accountSendersPK.senderName = :senderName") 
		})
public class AccountSender implements Serializable, AccountSenderConst {
	private static final long serialVersionUID = 1L;
	@EmbeddedId
	protected AccountSenderPK accountSendersPK;

	@JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID", insertable = false, updatable = false)
	@ManyToOne(targetEntity = Account.class, fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
	Account account;
	

	public AccountSender() {
	}

	public AccountSender(AccountSenderPK accountSendersPK) {
		this.accountSendersPK = accountSendersPK;
	}

	public AccountSender(String accountId, String senderName) {
		this.accountSendersPK = new AccountSenderPK(accountId, senderName);
	}
	

	public AccountSenderPK getAccountSendersPK() {
		return accountSendersPK;
	}

	public void setAccountSendersPK(AccountSenderPK accountSendersPK) {
		this.accountSendersPK = accountSendersPK;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (accountSendersPK != null ? accountSendersPK.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof AccountSender)) {
			return false;
		}
		AccountSender other = (AccountSender) object;
		if ((this.accountSendersPK == null && other.accountSendersPK != null)
				|| (this.accountSendersPK != null && !this.accountSendersPK.equals(other.accountSendersPK))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "AccountSenders[ " + accountSendersPK + " ]";
	}

}
