package com.edafa.web2sms.acc_manag.service.model;

import java.util.Date;

import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import com.edafa.web2sms.acc_manag.service.model.TierModel;
import java.io.Serializable;

public class ProvisioningRequestInfo implements Serializable {

	private String accountId;
	private Date timestamp;
	private AccountStatusName oldStatus;
	private AccountStatusName newStatus;
	private TierModel oldTier;
	private TierModel newTier;

	/**
	 * @return the accountId
	 */
	public String getAccountId() {
		return accountId;
	}

	/**
	 * @param accountId
	 *            the accountId to set
	 */
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the oldStatus
	 */
	public AccountStatusName getOldStatus() {
		return oldStatus;
	}

	/**
	 * @param oldStatus
	 *            the oldStatus to set
	 */
	public void setOldStatus(AccountStatusName oldStatus) {
		this.oldStatus = oldStatus;
	}

	/**
	 * @return the newStatus
	 */
	public AccountStatusName getNewStatus() {
		return newStatus;
	}

	/**
	 * @param newStatus
	 *            the newStatus to set
	 */
	public void setNewStatus(AccountStatusName newStatus) {
		this.newStatus = newStatus;
	}

	/**
	 * @return the oldTier
	 */
	public TierModel getOldTier() {
		return oldTier;
	}

	/**
	 * @param oldTier
	 *            the oldTier to set
	 */
	public void setOldTier(TierModel oldTier) {
		this.oldTier = oldTier;
	}

	/**
	 * @return the newTier
	 */
	public TierModel getNewTier() {
		return newTier;
	}

	/**
	 * @param newTier
	 *            the newTier to set
	 */
	public void setNewTier(TierModel newTier) {
		this.newTier = newTier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */

	public String logEvent() {
		return "accountId=" + accountId + ", timestamp=" + timestamp + ", oldStatus=" + oldStatus + ", newStatus="
				+ newStatus + ", oldTier=" + oldTier + ", newTier=" + newTier;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProvisioningEvent (accountId=" + accountId + ", timestamp=" + timestamp + ", oldStatus=" + oldStatus
				+ ", newStatus=" + newStatus + ", oldTier=" + oldTier + ", newTier=" + newTier + ")";
	}

}
