
package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Mahmoud Fouad <mahmoud.fouad@edafa.com>
 */
@Embeddable
public class SmsApiDailySmsStatsPK implements Serializable {

	private static final long serialVersionUID = 5243916396767701133L;

	@Basic(optional = false)
	@Column(name = "PROCESSING_DATE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date processingDate;

	@Basic(optional = false)
	@Column(name = "SENDER_NAME", nullable = false, length = 150)
	private String senderName;

	@Basic(optional = false)
	@Column(name = "OWNER_ID", nullable = false, length = 150)
	private String ownerId;

	public SmsApiDailySmsStatsPK() {}

	public SmsApiDailySmsStatsPK(Date processingDate, String senderName, String ownerId) {
		super();
		this.processingDate = processingDate;
		this.senderName = senderName;
		this.ownerId = ownerId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public Date getProcessingDate() {
		return processingDate;
	}

	public void setProcessingDate(Date processingDate) {
		this.processingDate = processingDate;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
		result = prime * result + ((processingDate == null) ? 0 : processingDate.hashCode());
		result = prime * result + ((senderName == null) ? 0 : senderName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SmsApiDailySmsStatsPK other = (SmsApiDailySmsStatsPK) obj;
		if (ownerId == null) {
			if (other.ownerId != null)
				return false;
		} else if (!ownerId.equals(other.ownerId))
			return false;
		if (processingDate == null) {
			if (other.processingDate != null)
				return false;
		} else if (!processingDate.equals(other.processingDate))
			return false;
		if (senderName == null) {
			if (other.senderName != null)
				return false;
		} else if (!senderName.equals(other.senderName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{processingDate=").append(processingDate).append(", senderName=").append(senderName)
				.append(", ownerId=").append(ownerId).append("}");
		return builder.toString();
	}

}
