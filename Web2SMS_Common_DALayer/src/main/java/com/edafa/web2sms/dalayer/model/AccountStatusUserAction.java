package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.edafa.web2sms.dalayer.model.constants.AccountStatusUserActionConst;

/**
 * The persistent class for the ACCOUNT_STATUS_USER_ACTIONS database table.
 * 
 */
@Entity
@Table(name = "ACCOUNT_STATUS_USER_ACTIONS")
@NamedQuery(name = "AccountStatusUserAction.findAll", query = "SELECT a FROM AccountStatusUserAction a")
public class AccountStatusUserAction implements Serializable, AccountStatusUserActionConst {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private AccountStatusUserActionPK id;

	// bi-directional many-to-one association to UserActionName
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "USER_ACTION_ID")
	private Action action;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ACCT_STATUS_ID")
	private AccountStatus accountStatus;

	public AccountStatusUserAction() {
	}

	public AccountStatusUserActionPK getId() {
		return this.id;
	}

	public void setId(AccountStatusUserActionPK id) {
		this.id = id;
	}

	public Action getAction() {
		return this.action;
	}

	public void setAction(Action userAction) {
		this.action = userAction;
	}

	public AccountStatus getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(AccountStatus accountStatus) {
		this.accountStatus = accountStatus;
	}
}