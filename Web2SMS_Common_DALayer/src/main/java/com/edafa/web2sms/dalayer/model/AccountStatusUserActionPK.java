package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the ACCOUNT_STATUS_USER_ACTIONS database table.
 * 
 */
@Embeddable
public class AccountStatusUserActionPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="ACCT_STATUS_ID", insertable=false, updatable=false)
	private long acctStatusId;

	@Column(name="USER_ACTION_ID", insertable=false, updatable=false)
	private long userActionId;

	public AccountStatusUserActionPK() {
	}
	public long getAcctStatusId() {
		return this.acctStatusId;
	}
	public void setAcctStatusId(long acctStatusId) {
		this.acctStatusId = acctStatusId;
	}
	public long getUserActionId() {
		return this.userActionId;
	}
	public void setUserActionId(long userActionId) {
		this.userActionId = userActionId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AccountStatusUserActionPK)) {
			return false;
		}
		AccountStatusUserActionPK castOther = (AccountStatusUserActionPK)other;
		return 
			(this.acctStatusId == castOther.acctStatusId)
			&& (this.userActionId == castOther.userActionId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.acctStatusId ^ (this.acctStatusId >>> 32)));
		hash = hash * prime + ((int) (this.userActionId ^ (this.userActionId >>> 32)));
		
		return hash;
	}
}